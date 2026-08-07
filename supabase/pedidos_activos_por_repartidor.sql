create or replace function public.pedidos_activos_por_repartidor(
  p_repartidor_id uuid
)
returns setof public.pedidos
language sql
stable
security invoker
set search_path = ''
as $$
  select p.*
  from public.pedidos p
  where p.repartidor_id = p_repartidor_id
    and p.estado not in ('entregado', 'cancelado')
  order by p.creado_at desc;
$$;

revoke all on function public.pedidos_activos_por_repartidor(uuid) from public, anon;
grant execute on function public.pedidos_activos_por_repartidor(uuid) to authenticated;
