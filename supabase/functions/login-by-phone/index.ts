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

const logAccess = async (
  service: ReturnType<typeof createClient>,
  profile: Record<string, unknown> | null,
  origin: string,
  req: Request,
) => {
  if (!profile?.id) return;
  await service.from("logs_accesos").insert({
    usuario_id: profile.id,
    email: profile.email ?? "",
    rol: profile.rol ?? "",
    origen: origin || "app_cliente",
    evento: "login",
    user_agent: req.headers.get("user-agent") ?? "",
  });
};

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: cors });
  if (req.method !== "POST") return json({ error: "Metodo no permitido" }, 405);

  try {
    const { phone, password, origin } = await req.json();
    const normalized = String(phone ?? "").replace(/\D/g, "");
    if (normalized.length < 8 || typeof password !== "string" || password.length < 1) {
      return json({ error: "Credenciales invalidas" }, 400);
    }

    const url = Deno.env.get("SUPABASE_URL")!;
    const service = createClient(url, Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!, {
      auth: { persistSession: false },
    });
    const { data: profile } = await service
      .from("perfiles")
      .select("id,email,nombre_completo,telefono,rol,avatar_url,descripcion,latitud,longitud,direccion")
      .eq("telefono_normalizado", normalized)
      .maybeSingle();

    if (!profile?.email) return json({ error: "Correo/telefono o contrasena incorrectos" }, 401);

    const publicClient = createClient(url, Deno.env.get("SUPABASE_ANON_KEY")!, {
      auth: { persistSession: false },
    });
    const { data, error } = await publicClient.auth.signInWithPassword({
      email: String(profile.email),
      password,
    });

    if (error || !data.session) return json({ error: "Correo/telefono o contrasena incorrectos" }, 401);

    await logAccess(service, profile, String(origin ?? "app_cliente"), req);
    return json({ ...data.session, user: data.user, profile, rol: profile.rol ?? null });
  } catch {
    return json({ error: "No se pudo iniciar sesion" }, 500);
  }
});
