begin;

alter type public.rol_usuario add value if not exists 'repartidor';

commit;
begin;

alter table public.perfiles
  add column if not exists telefono_normalizado text
  generated always as (regexp_replace(coalesce(telefono, ''), '[^0-9]', '', 'g')) stored;

create unique index if not exists perfiles_telefono_normalizado_uidx
  on public.perfiles (telefono_normalizado)
  where telefono_normalizado <> '';

alter table public.pedidos
  add column if not exists repartidor_id uuid references public.perfiles(id) on delete set null,
  add column if not exists asignado_at timestamptz,
  add column if not exists entregado_at timestamptz;

create index if not exists pedidos_repartidor_estado_idx
  on public.pedidos (repartidor_id, estado, creado_at desc);

create or replace function private.proteger_identidad_perfil()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  if (select auth.uid()) is not null
     and (new.rol is distinct from old.rol or new.email is distinct from old.email)
     and not exists (select 1 from public.perfiles p where p.id = (select auth.uid()) and p.rol = 'encargado') then
    raise exception 'No tienes permiso para cambiar correo o rol';
  end if;
  return new;
end;
$$;
revoke all on function private.proteger_identidad_perfil() from public, anon, authenticated;
drop trigger if exists tr_proteger_identidad_perfil on public.perfiles;
create trigger tr_proteger_identidad_perfil before update on public.perfiles
for each row execute function private.proteger_identidad_perfil();

create or replace function private.tiene_rol(p_rol public.rol_usuario)
returns boolean
language sql
stable
security definer
set search_path = ''
as $$
  select exists (
    select 1 from public.perfiles
    where id = (select auth.uid()) and rol = p_rol
  );
$$;
revoke all on function private.tiene_rol(public.rol_usuario) from public, anon;
grant execute on function private.tiene_rol(public.rol_usuario) to authenticated;

create or replace function private.manejar_nuevo_usuario()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
declare
  direccion_item jsonb;
  rol_solicitado text := coalesce(new.raw_app_meta_data->>'app_role', 'cliente');
  rol_nuevo public.rol_usuario;
begin
  rol_nuevo := case rol_solicitado
    when 'repartidor' then 'repartidor'::public.rol_usuario
    when 'encargado' then 'encargado'::public.rol_usuario
    else 'cliente'::public.rol_usuario
  end;

  insert into public.perfiles (id, email, nombre_completo, telefono, rol, acepto_politicas_registro)
  values (
    new.id,
    new.email,
    coalesce(nullif(new.raw_user_meta_data->>'nombre_completo', ''), split_part(coalesce(new.email, ''), '@', 1)),
    nullif(new.raw_user_meta_data->>'telefono', ''),
    rol_nuevo,
    true
  )
  on conflict (id) do update set
    email = excluded.email,
    nombre_completo = excluded.nombre_completo,
    telefono = excluded.telefono,
    rol = excluded.rol;

  if jsonb_typeof(new.raw_user_meta_data->'direcciones') = 'array' then
    for direccion_item in select value from jsonb_array_elements(new.raw_user_meta_data->'direcciones')
    loop
      insert into public.direcciones (perfil_id, etiqueta, direccion_texto, latitud, longitud)
      values (
        new.id,
        coalesce(nullif(direccion_item->>'etiqueta', ''), 'Casa'),
        coalesce(nullif(direccion_item->>'direccion_texto', ''), 'Sin direccion'),
        (direccion_item->>'latitud')::double precision,
        (direccion_item->>'longitud')::double precision
      );
    end loop;
  end if;
  return new;
end;
$$;
revoke all on function private.manejar_nuevo_usuario() from public, anon, authenticated;

drop policy if exists "repartidor ve su perfil" on public.perfiles;
create policy "repartidor ve su perfil" on public.perfiles for select to authenticated
using (id = (select auth.uid()));

drop policy if exists "repartidor ve clientes asignados" on public.perfiles;
create policy "repartidor ve clientes asignados" on public.perfiles for select to authenticated
using (
  private.tiene_rol('repartidor') and exists (
    select 1 from public.pedidos p
    where p.repartidor_id = (select auth.uid()) and p.perfil_id = perfiles.id
  )
);

drop policy if exists "repartidor ve pedidos asignados" on public.pedidos;
create policy "repartidor ve pedidos asignados" on public.pedidos for select to authenticated
using (repartidor_id = (select auth.uid()) and private.tiene_rol('repartidor'));

drop policy if exists "repartidor actualiza pedidos asignados" on public.pedidos;
create policy "repartidor actualiza pedidos asignados" on public.pedidos for update to authenticated
using (repartidor_id = (select auth.uid()) and private.tiene_rol('repartidor'))
with check (repartidor_id = (select auth.uid()) and private.tiene_rol('repartidor'));

drop policy if exists "repartidor ve items asignados" on public.pedido_items;
create policy "repartidor ve items asignados" on public.pedido_items for select to authenticated
using (exists (
  select 1 from public.pedidos p
  where p.id = pedido_items.pedido_id
    and p.repartidor_id = (select auth.uid())
    and private.tiene_rol('repartidor')
));

drop policy if exists "repartidor ve direcciones asignadas" on public.direcciones;
create policy "repartidor ve direcciones asignadas" on public.direcciones for select to authenticated
using (exists (
  select 1 from public.pedidos p
  where p.direccion_id = direcciones.id
    and p.repartidor_id = (select auth.uid())
    and private.tiene_rol('repartidor')
));

create or replace function private.validar_cambio_pedido()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
declare rol_actual public.rol_usuario;
begin
  select rol into rol_actual from public.perfiles where id = (select auth.uid());
  if rol_actual = 'repartidor' then
    if new.perfil_id is distinct from old.perfil_id
       or new.repartidor_id is distinct from old.repartidor_id
       or new.total is distinct from old.total
       or new.direccion_id is distinct from old.direccion_id
       or new.metodo_pago is distinct from old.metodo_pago then
      raise exception 'El repartidor solo puede cambiar el estado';
    end if;
    if not ((old.estado = 'preparando' and new.estado = 'en_camino')
         or (old.estado = 'en_camino' and new.estado = 'entregado')
         or old.estado = new.estado) then
      raise exception 'Cambio de estado no permitido para repartidor';
    end if;
  end if;
  if new.repartidor_id is distinct from old.repartidor_id and new.repartidor_id is not null then
    if not exists (select 1 from public.perfiles where id = new.repartidor_id and rol = 'repartidor') then
      raise exception 'El usuario asignado no es repartidor';
    end if;
    new.asignado_at := coalesce(new.asignado_at, now());
  end if;
  if new.estado = 'entregado' and old.estado is distinct from 'entregado' then new.entregado_at := now(); end if;
  return new;
end;
$$;
revoke all on function private.validar_cambio_pedido() from public, anon, authenticated;

drop trigger if exists tr_validar_cambio_pedido on public.pedidos;
create trigger tr_validar_cambio_pedido before update on public.pedidos
for each row execute function private.validar_cambio_pedido();

grant select on public.perfiles, public.pedidos, public.pedido_items, public.direcciones to authenticated;
grant update (estado) on public.pedidos to authenticated;

commit;
