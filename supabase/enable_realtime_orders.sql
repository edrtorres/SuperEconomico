-- Ejecutar en Supabase SQL Editor si el cPanel no recibe actualizaciones en tiempo real.
-- Esto agrega las tablas de pedidos a Realtime y crea un canal Broadcast para el cPanel.
-- Verificacion esperada:
-- select schemaname, tablename from pg_publication_tables
-- where pubname = 'supabase_realtime' and tablename in ('pedidos', 'pedido_items');
-- select tgname from pg_trigger where tgname in ('tr_cpanel_broadcast_pedidos', 'tr_cpanel_broadcast_pedido_items');

do $$
begin
  alter table public.pedidos replica identity full;
  alter table public.pedido_items replica identity full;

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

create schema if not exists private;

create or replace function private.cpanel_broadcast_pedidos()
returns trigger
security definer
set search_path = ''
language plpgsql
as $$
begin
  perform realtime.send(
    jsonb_build_object(
      'schema', TG_TABLE_SCHEMA,
      'table', TG_TABLE_NAME,
      'operation', TG_OP,
      'record', to_jsonb(NEW),
      'old_record', to_jsonb(OLD)
    ),
    TG_OP,
    'cpanel:pedidos',
    false
  );
  return null;
end;
$$;
revoke all on function private.cpanel_broadcast_pedidos() from public, anon, authenticated;

drop trigger if exists tr_cpanel_broadcast_pedidos on public.pedidos;
create trigger tr_cpanel_broadcast_pedidos
after insert or update or delete on public.pedidos
for each row execute function private.cpanel_broadcast_pedidos();

drop trigger if exists tr_cpanel_broadcast_pedido_items on public.pedido_items;
create trigger tr_cpanel_broadcast_pedido_items
after insert or update or delete on public.pedido_items
for each row execute function private.cpanel_broadcast_pedidos();
