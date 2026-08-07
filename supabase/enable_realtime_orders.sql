-- Ejecutar en Supabase SQL Editor si el cPanel no recibe actualizaciones en tiempo real.
-- Esto agrega las tablas de pedidos a la publicacion usada por Supabase Realtime.

do $$
begin
  if not exists (
    select 1
    from pg_publication_tables
    where pubname = 'supabase_realtime'
      and schemaname = 'public'
      and tablename = 'pedidos'
  ) then
    alter publication supabase_realtime add table public.pedidos;
  end if;

  if not exists (
    select 1
    from pg_publication_tables
    where pubname = 'supabase_realtime'
      and schemaname = 'public'
      and tablename = 'pedido_items'
  ) then
    alter publication supabase_realtime add table public.pedido_items;
  end if;
end $$;
