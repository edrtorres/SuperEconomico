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

  try {
    const { email, password } = await req.json();
    const cleanEmail = String(email ?? "").trim().toLowerCase();
    if (!cleanEmail.includes("@") || typeof password !== "string" || password.length < 1) {
      return json({ error: "Credenciales invalidas" }, 400);
    }

    const url = Deno.env.get("SUPABASE_URL")!;
    const publicClient = createClient(url, Deno.env.get("SUPABASE_ANON_KEY")!, {
      auth: { persistSession: false },
    });

    const { data, error } = await publicClient.auth.signInWithPassword({
      email: cleanEmail,
      password,
    });

    if (error || !data.session || !data.user) {
      return json({ error: "Correo o contrasena incorrectos" }, 401);
    }

    const service = createClient(url, Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!, {
      auth: { persistSession: false },
    });
    const { data: profile } = await service
      .from("perfiles")
      .select("id,email,nombre_completo,telefono,rol,avatar_url,descripcion,latitud,longitud,direccion")
      .eq("id", data.user.id)
      .maybeSingle();

    return json({
      ...data.session,
      user: data.user,
      profile,
      rol: profile?.rol ?? null,
    });
  } catch {
    return json({ error: "No se pudo iniciar sesion" }, 500);
  }
});
