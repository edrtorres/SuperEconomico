begin;

create schema if not exists private;
revoke all on schema private from public, anon, authenticated;
grant usage on schema private to authenticated;

create or replace function private.es_encargado()
returns boolean
language sql
stable
security definer
set search_path = ''
as $$
  select exists (
    select 1
    from public.perfiles
    where id = (select auth.uid())
      and rol = 'encargado'::public.rol_usuario
  );
$$;
revoke all on function private.es_encargado() from public, anon;
grant execute on function private.es_encargado() to authenticated;

alter table public.categorias enable row level security;
alter table public.productos enable row level security;
alter table public.perfiles enable row level security;
alter table public.direcciones enable row level security;
alter table public.metodos_pago enable row level security;
alter table public.pedidos enable row level security;
alter table public.pedido_items enable row level security;
alter table public.aceptaciones_login enable row level security;
alter table public.logs_errores enable row level security;

do $$
declare policy_record record;
begin
  for policy_record in
    select schemaname, tablename, policyname
    from pg_policies
    where schemaname = 'public'
      and tablename in (
        'categorias', 'productos', 'perfiles', 'direcciones',
        'metodos_pago', 'pedidos', 'pedido_items',
        'aceptaciones_login', 'logs_errores'
      )
  loop
    execute format(
      'drop policy if exists %I on %I.%I',
      policy_record.policyname,
      policy_record.schemaname,
      policy_record.tablename
    );
  end loop;
end
$$;

create policy categorias_lectura_publica
on public.categorias for select
to anon, authenticated
using (true);

create policy categorias_gestion_encargados
on public.categorias for all
to authenticated
using ((select private.es_encargado()))
with check ((select private.es_encargado()));

create policy productos_lectura_publica
on public.productos for select
to anon, authenticated
using (true);

create policy productos_gestion_encargados
on public.productos for all
to authenticated
using ((select private.es_encargado()))
with check ((select private.es_encargado()));

create policy perfiles_lectura_propietario_o_encargado
on public.perfiles for select
to authenticated
using ((select auth.uid()) = id or (select private.es_encargado()));

create policy perfiles_actualizacion_propia
on public.perfiles for update
to authenticated
using ((select auth.uid()) = id)
with check ((select auth.uid()) = id);

create policy direcciones_lectura_propietario_o_encargado
on public.direcciones for select
to authenticated
using ((select auth.uid()) = perfil_id or (select private.es_encargado()));

create policy direcciones_insercion_propia
on public.direcciones for insert
to authenticated
with check ((select auth.uid()) = perfil_id);

create policy direcciones_actualizacion_propia
on public.direcciones for update
to authenticated
using ((select auth.uid()) = perfil_id)
with check ((select auth.uid()) = perfil_id);

create policy direcciones_eliminacion_propia
on public.direcciones for delete
to authenticated
using ((select auth.uid()) = perfil_id);

create policy metodos_pago_lectura_propietario_o_encargado
on public.metodos_pago for select
to authenticated
using ((select auth.uid()) = perfil_id or (select private.es_encargado()));

create policy metodos_pago_insercion_propia
on public.metodos_pago for insert
to authenticated
with check ((select auth.uid()) = perfil_id);

create policy metodos_pago_actualizacion_propia
on public.metodos_pago for update
to authenticated
using ((select auth.uid()) = perfil_id)
with check ((select auth.uid()) = perfil_id);

create policy metodos_pago_eliminacion_propia
on public.metodos_pago for delete
to authenticated
using ((select auth.uid()) = perfil_id);

create policy pedidos_lectura_propietario_o_encargado
on public.pedidos for select
to authenticated
using ((select auth.uid()) = perfil_id or (select private.es_encargado()));

create policy pedidos_actualizacion_encargado
on public.pedidos for update
to authenticated
using ((select private.es_encargado()))
with check ((select private.es_encargado()));

create policy pedidos_eliminacion_propietario_pendiente_o_encargado
on public.pedidos for delete
to authenticated
using (
  ((select auth.uid()) = perfil_id and estado = 'pendiente')
  or (select private.es_encargado())
);

create policy pedido_items_lectura_propietario_o_encargado
on public.pedido_items for select
to authenticated
using (
  exists (
    select 1 from public.pedidos
    where pedidos.id = pedido_items.pedido_id
      and (pedidos.perfil_id = (select auth.uid()) or (select private.es_encargado()))
  )
);

create policy pedido_items_actualizacion_propietario_pendiente
on public.pedido_items for update
to authenticated
using (
  exists (
    select 1 from public.pedidos
    where pedidos.id = pedido_items.pedido_id
      and pedidos.perfil_id = (select auth.uid())
      and pedidos.estado = 'pendiente'
  )
)
with check (
  exists (
    select 1 from public.pedidos
    where pedidos.id = pedido_items.pedido_id
      and pedidos.perfil_id = (select auth.uid())
      and pedidos.estado = 'pendiente'
  )
);

create policy pedido_items_eliminacion_propietario_pendiente
on public.pedido_items for delete
to authenticated
using (
  exists (
    select 1 from public.pedidos
    where pedidos.id = pedido_items.pedido_id
      and pedidos.perfil_id = (select auth.uid())
      and pedidos.estado = 'pendiente'
  )
);

create policy aceptaciones_insercion_propia
on public.aceptaciones_login for insert
to authenticated
with check ((select auth.uid()) = usuario_id);

create policy aceptaciones_lectura_propia_o_encargado
on public.aceptaciones_login for select
to authenticated
using ((select auth.uid()) = usuario_id or (select private.es_encargado()));

create policy logs_insercion_autenticada
on public.logs_errores for insert
to authenticated
with check (usuario_id is null or usuario_id = (select auth.uid()));

drop policy if exists logs_insercion_publica on public.logs_errores;
create policy logs_insercion_publica
on public.logs_errores for insert
to anon, authenticated
with check (usuario_id is null or usuario_id = (select auth.uid()));

create policy logs_lectura_encargado
on public.logs_errores for select
to authenticated
using ((select private.es_encargado()));

revoke all on all tables in schema public from anon, authenticated;
revoke all on all sequences in schema public from anon, authenticated;

grant select on public.categorias, public.productos to anon;
grant select on public.categorias, public.productos to authenticated;
grant insert, update, delete on public.categorias, public.productos to authenticated;
grant usage, select on all sequences in schema public to authenticated;

grant select on public.perfiles to authenticated;
grant update (nombre_completo, telefono, direccion, avatar_url, descripcion, latitud, longitud, fcm_token, actualizado_at)
on public.perfiles to authenticated;

grant select, insert, update, delete on public.direcciones, public.metodos_pago to authenticated;
grant select, delete on public.pedidos to authenticated;
grant update (estado) on public.pedidos to authenticated;
grant select, delete on public.pedido_items to authenticated;
grant update (cantidad) on public.pedido_items to authenticated;
grant select, insert on public.aceptaciones_login to authenticated;
grant insert on public.logs_errores to anon, authenticated;
grant select on public.logs_errores to authenticated;

alter default privileges for role postgres in schema public
  revoke select, insert, update, delete on tables from anon, authenticated;
alter default privileges for role postgres in schema public
  revoke usage, select on sequences from anon, authenticated;
alter default privileges for role postgres in schema public
  revoke execute on functions from public, anon, authenticated;

create unique index if not exists perfiles_telefono_normalizado_unique
on public.perfiles ((regexp_replace(telefono, '[^0-9]', '', 'g')))
where telefono is not null and regexp_replace(telefono, '[^0-9]', '', 'g') <> '';

create or replace function private.manejar_nuevo_usuario()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
declare
  direccion_item jsonb;
begin
  insert into public.perfiles (
    id, email, nombre_completo, telefono, rol,
    acepto_politicas_registro, actualizado_at
  ) values (
    new.id,
    new.email,
    coalesce(new.raw_user_meta_data->>'nombre_completo', ''),
    nullif(new.raw_user_meta_data->>'telefono', ''),
    'cliente'::public.rol_usuario,
    true,
    now()
  );

  if jsonb_typeof(new.raw_user_meta_data->'direcciones') = 'array' then
    for direccion_item in
      select value
      from jsonb_array_elements(new.raw_user_meta_data->'direcciones') with ordinality as item(value, position)
      where position <= 3
    loop
      insert into public.direcciones (
        perfil_id, etiqueta, direccion_texto, latitud, longitud
      ) values (
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

drop trigger if exists al_crear_usuario_auth on auth.users;
create trigger al_crear_usuario_auth
after insert on auth.users
for each row execute function private.manejar_nuevo_usuario();

create or replace function private.recalcular_total_pedido()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
declare
  objetivo bigint := coalesce(new.pedido_id, old.pedido_id);
begin
  update public.pedidos
  set total = (
    select coalesce(sum(cantidad * precio_unitario), 0)
    from public.pedido_items
    where pedido_id = objetivo
  )
  where id = objetivo;
  return null;
end;
$$;
revoke all on function private.recalcular_total_pedido() from public, anon, authenticated;

drop trigger if exists tr_recalcular_total on public.pedido_items;
create trigger tr_recalcular_total
after insert or update or delete on public.pedido_items
for each row execute function private.recalcular_total_pedido();

create or replace function private.notificar_cambio_estado()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  return new;
end;
$$;
revoke all on function private.notificar_cambio_estado() from public, anon, authenticated;

drop trigger if exists tr_notificar_pedido on public.pedidos;
create trigger tr_notificar_pedido
after update of estado on public.pedidos
for each row
when (old.estado is distinct from new.estado)
execute function private.notificar_cambio_estado();

create or replace function public.asignar_repartidor_pedido(
  p_pedido_id bigint,
  p_repartidor_id uuid
)
returns void
language plpgsql
security definer
set search_path = ''
as $$
begin
  if not (select private.es_encargado()) then
    raise exception 'No tiene permiso para asignar repartidores';
  end if;

  if p_repartidor_id is not null and not exists (
    select 1
    from public.perfiles
    where id = p_repartidor_id
      and rol = 'repartidor'::public.rol_usuario
  ) then
    raise exception 'El usuario asignado no es repartidor';
  end if;

  update public.pedidos
  set repartidor_id = p_repartidor_id
  where id = p_pedido_id;

  if not found then
    raise exception 'Pedido no encontrado';
  end if;
end;
$$;
revoke all on function public.asignar_repartidor_pedido(bigint, uuid) from public, anon;
grant execute on function public.asignar_repartidor_pedido(bigint, uuid) to authenticated;

create or replace function public.crear_pedido_seguro(
  p_direccion_id bigint,
  p_metodo_pago text,
  p_items jsonb
)
returns bigint
language plpgsql
security definer
set search_path = ''
as $$
declare
  usuario_id uuid := (select auth.uid());
  pedido_id bigint;
  item jsonb;
  producto public.productos%rowtype;
  cantidad integer;
  precio numeric;
  total_calculado numeric := 0;
begin
  if usuario_id is null then
    raise exception 'Se requiere una sesion autenticada';
  end if;

  if p_items is null or jsonb_typeof(p_items) <> 'array' or jsonb_array_length(p_items) = 0 then
    raise exception 'El pedido debe contener productos';
  end if;

  if p_direccion_id is not null and not exists (
    select 1 from public.direcciones
    where id = p_direccion_id and perfil_id = usuario_id
  ) then
    raise exception 'La direccion no pertenece al usuario';
  end if;

  insert into public.pedidos (perfil_id, estado, total, direccion_id, metodo_pago)
  values (usuario_id, 'pendiente', 0, p_direccion_id, coalesce(nullif(trim(p_metodo_pago), ''), 'Efectivo'))
  returning id into pedido_id;

  for item in select value from jsonb_array_elements(p_items)
  loop
    cantidad := (item->>'cantidad')::integer;
    if cantidad is null or cantidad < 1 or cantidad > 99 then
      raise exception 'Cantidad de producto invalida';
    end if;

    select * into producto
    from public.productos
    where id = (item->>'producto_id')::bigint
      and esta_activo is true;

    if not found then
      raise exception 'Producto inexistente o inactivo';
    end if;

    precio := case
      when producto.es_oferta is true and producto.precio_oferta is not null and producto.precio_oferta > 0
        then producto.precio_oferta
      else producto.precio
    end;

    insert into public.pedido_items (
      pedido_id, producto_id, cantidad, precio_unitario, nombre, imagen_url
    ) values (
      pedido_id, producto.id, cantidad, precio, producto.nombre, producto.imagen_url
    );

    total_calculado := total_calculado + (cantidad * precio);
  end loop;

  update public.pedidos set total = total_calculado where id = pedido_id;
  return pedido_id;
end;
$$;
revoke all on function public.crear_pedido_seguro(bigint, text, jsonb) from public, anon;
grant execute on function public.crear_pedido_seguro(bigint, text, jsonb) to authenticated;

notify pgrst, 'reload schema';

drop function if exists public.manejar_nuevo_usuario();
drop function if exists public.recalcular_total_pedido();
drop function if exists public.notificar_cambio_estado();

do $$
begin
  if to_regprocedure('public.rls_auto_enable()') is not null then
    alter function public.rls_auto_enable() set schema private;
  end if;
exception when duplicate_function then
  null;
end
$$;

alter function private.rls_auto_enable() set search_path = 'pg_catalog';
revoke all on function private.rls_auto_enable() from public, anon, authenticated;

commit;
