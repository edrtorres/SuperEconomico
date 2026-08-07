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
  if (req.method !== "GET") return json({ error: "Metodo no permitido" }, 405);

  const authorization = req.headers.get("Authorization") ?? "";

  try {
    const url = Deno.env.get("SUPABASE_URL")!;
    const anon = Deno.env.get("SUPABASE_ANON_KEY")!;
    const caller = createClient(url, anon, {
      global: { headers: { Authorization: authorization } },
      auth: { persistSession: false },
    });

    const {
      data: { user },
      error: authError,
    } = await caller.auth.getUser();

    if (authError || !user) return json({ error: "Sesion invalida" }, 401);

    const { data: profile, error } = await caller
      .from("perfiles")
      .select("id,email,nombre_completo,telefono,rol,avatar_url,descripcion,latitud,longitud,direccion")
      .eq("id", user.id)
      .single();

    if (error || !profile) return json({ error: "No se pudo cargar el perfil" }, 404);

    return json({
      user: {
        id: user.id,
        email: user.email,
      },
      profile,
      rol: profile.rol,
    });
  } catch {
    return json({ error: "No se pudo cargar la sesion" }, 500);
  }
});
