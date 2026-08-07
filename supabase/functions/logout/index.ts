import { createClient } from "https://esm.sh/@supabase/supabase-js@2.57.4";

const cors = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

const json = (body: unknown, status = 200) =>
  new Response(JSON.stringify(body), {
    status,
    headers: { ...cors, "Content-Type": "application/json" },
  });

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: cors });
  if (req.method !== "POST") return json({ error: "Metodo no permitido" }, 405);

  const authorization = req.headers.get("Authorization") ?? "";
  const url = Deno.env.get("SUPABASE_URL")!;
  const anon = Deno.env.get("SUPABASE_ANON_KEY")!;

  try {
    const body = await req.json().catch(() => ({}));
    const origin = String(body?.origin ?? "app_cliente");
    const caller = createClient(url, anon, {
      global: { headers: { Authorization: authorization } },
      auth: { persistSession: false },
    });

    const {
      data: { user },
      error: authError,
    } = await caller.auth.getUser();

    if (authError || !user) return json({ error: "Sesion invalida" }, 401);

    const service = createClient(url, Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!, {
      auth: { persistSession: false },
    });
    const { data: profile } = await service
      .from("perfiles")
      .select("id,email,rol")
      .eq("id", user.id)
      .maybeSingle();

    try {
      await service.from("logs_accesos").insert({
        usuario_id: user.id,
        email: profile?.email ?? user.email ?? "",
        rol: profile?.rol ?? "",
        origen: origin || "app_cliente",
        evento: "logout",
        user_agent: req.headers.get("user-agent") ?? "",
      });
    } catch (error) {
      console.error("No se pudo registrar la salida", error);
    }

    await caller.auth.signOut({ scope: "local" }).catch(() => null);
    return json({ ok: true });
  } catch {
    return json({ error: "No se pudo cerrar sesion" }, 500);
  }
});
