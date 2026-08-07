import { createClient } from "https://esm.sh/@supabase/supabase-js@2.57.4";

const cors = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" };
const json = (body: unknown, status = 200) => new Response(JSON.stringify(body), { status, headers: { ...cors, "Content-Type": "application/json" } });

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: cors });
  if (req.method !== "POST") return json({ error: "Método no permitido" }, 405);
  try {
    const { phone, password } = await req.json();
    const normalized = String(phone ?? "").replace(/\D/g, "");
    if (normalized.length < 8 || typeof password !== "string" || password.length < 1) return json({ error: "Credenciales inválidas" }, 400);

    const url = Deno.env.get("SUPABASE_URL")!;
    const service = createClient(url, Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!, { auth: { persistSession: false } });
    const { data: profile } = await service.from("perfiles").select("email").eq("telefono_normalizado", normalized).maybeSingle();
    if (!profile?.email) return json({ error: "Correo/teléfono o contraseña incorrectos" }, 401);

    const publicClient = createClient(url, Deno.env.get("SUPABASE_ANON_KEY")!, { auth: { persistSession: false } });
    const { data, error } = await publicClient.auth.signInWithPassword({ email: profile.email, password });
    if (error || !data.session) return json({ error: "Correo/teléfono o contraseña incorrectos" }, 401);
    return json({ ...data.session, user: data.user });
  } catch {
    return json({ error: "No se pudo iniciar sesión" }, 500);
  }
});
