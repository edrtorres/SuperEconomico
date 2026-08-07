-- Bootstrap del primer usuario administrador del cPanel.
--
-- Uso:
-- 1. En Supabase Dashboard ve a Authentication > Users > Add user.
-- 2. Crea el usuario con email y password, marcando "Auto Confirm User" si aparece.
-- 3. Cambia el valor de admin_email abajo y ejecuta este SQL en SQL Editor.
--
-- IMPORTANTE: no pongas service_role keys en docs/Admin.html ni en la app Android.

begin;

do $$
declare
  admin_email text := 'admin@supereconomico.hn';
  admin_id uuid;
begin
  select id
    into admin_id
  from auth.users
  where lower(email) = lower(admin_email)
  limit 1;

  if admin_id is null then
    raise exception 'No existe un usuario Auth con email %. Crealo primero en Authentication > Users.', admin_email;
  end if;

  update auth.users
  set raw_app_meta_data = coalesce(raw_app_meta_data, '{}'::jsonb) || jsonb_build_object('app_role', 'encargado'),
      email_confirmed_at = coalesce(email_confirmed_at, now()),
      updated_at = now()
  where id = admin_id;

  insert into public.perfiles (
    id,
    email,
    nombre_completo,
    telefono,
    rol,
    acepto_politicas_registro,
    actualizado_at
  )
  values (
    admin_id,
    lower(admin_email),
    'Administrador cPanel',
    null,
    'encargado'::public.rol_usuario,
    true,
    now()
  )
  on conflict (id) do update
  set email = excluded.email,
      nombre_completo = coalesce(nullif(public.perfiles.nombre_completo, ''), excluded.nombre_completo),
      rol = 'encargado'::public.rol_usuario,
      acepto_politicas_registro = true,
      actualizado_at = now();
end
$$;

commit;
