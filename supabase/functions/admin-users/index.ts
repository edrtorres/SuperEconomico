import { createClient } from "https://esm.sh/@supabase/supabase-js@2.57.4";

const cors = { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type" };
const json = (body: unknown, status = 200) => new Response(JSON.stringify(body), { status, headers: { ...cors, "Content-Type": "application/json" } });
const validRoles = new Set(["cliente", "repartidor", "encargado"]);

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: cors });
  if (req.method !== "POST") return json({ error: "Método no permitido" }, 405);
  const authorization = req.headers.get("Authorization") ?? "";
  try {
    const url = Deno.env.get("SUPABASE_URL")!;
    const anon = Deno.env.get("SUPABASE_ANON_KEY")!;
    const caller = createClient(url, anon, { global: { headers: { Authorization: authorization } }, auth: { persistSession: false } });
    const { data: { user } } = await caller.auth.getUser();
    if (!user) return json({ error: "Sesión inválida" }, 401);

    const service = createClient(url, Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!, { auth: { persistSession: false } });
    const { data: admin } = await service.from("perfiles").select("rol").eq("id", user.id).single();
    if (admin?.rol !== "encargado") return json({ error: "Acceso denegado" }, 403);

    const body = await req.json();
    const action = String(body.action ?? "");
    if (action === "list") {
      const { data, error } = await service.from("perfiles").select("id,email,nombre_completo,telefono,rol,actualizado_at").order("nombre_completo");
      if (error) throw error;
      return json({ users: data });
    }
    if (action === "create") {
      const role = String(body.role ?? "cliente");
      if (!validRoles.has(role) || !body.email || !body.password || String(body.password).length < 6) return json({ error: "Datos de usuario inválidos" }, 400);
      const email = String(body.email).trim().toLowerCase();
      const name = String(body.name ?? "").trim();
      const phone = String(body.phone ?? "").trim();
      const { data, error } = await service.auth.admin.createUser({
        email, password: String(body.password), email_confirm: true,
        user_metadata: { nombre_completo: name, telefono: phone },
        app_metadata: { app_role: role }
      });
      if (error) return json({ error: error.message }, 400);
      const { error: profileError } = await service.from("perfiles").upsert({
        id: data.user.id,
        email,
        nombre_completo: name,
        telefono: phone,
        rol: role
      }, { onConflict: "id" });
      if (profileError) return json({ error: profileError.message }, 400);
      return json({ user: data.user }, 201);
    }
    if (action === "update") {
      const id = String(body.id ?? "");
      const role = String(body.role ?? "");
      if (!id || !validRoles.has(role)) return json({ error: "Datos inválidos" }, 400);
      const email = body.email ? String(body.email).trim().toLowerCase() : "";
      const name = String(body.name ?? "").trim();
      const phone = String(body.phone ?? "").trim();
      const authChanges: Record<string, unknown> = {
        app_metadata: { app_role: role },
        user_metadata: { nombre_completo: name, telefono: phone }
      };
      if (email) authChanges.email = email;
      if (body.password) authChanges.password = String(body.password);
      const { error: authError } = await service.auth.admin.updateUserById(id, authChanges);
      if (authError) return json({ error: authError.message }, 400);
      const { error } = await service.from("perfiles").upsert({ id, email, nombre_completo: name, telefono: phone, rol: role }, { onConflict: "id" });
      if (error) throw error;
      return json({ ok: true });
    }
    if (action === "delete") {
      const id = String(body.id ?? "");
      if (!id || id === user.id) return json({ error: "No puedes eliminar tu propia cuenta" }, 400);
      const { error } = await service.auth.admin.deleteUser(id);
      if (error) return json({ error: error.message }, 400);
      return json({ ok: true });
    }
    return json({ error: "Acción inválida" }, 400);
  } catch (error) {
    return json({ error: error instanceof Error ? error.message : "Error interno" }, 500);
  }
});
