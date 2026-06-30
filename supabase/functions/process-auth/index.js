// BACKEND EN JAVASCRIPT - SUPABASE EDGE FUNCTION
// Este código se despliega en Supabase Functions
import { serve } from "https://deno.land/std@0.168.0/http/server.ts"

serve(async (req) => {
  try {
    const { record, type } = await req.json()

    // Lógica para procesar registros de usuarios
    if (type === 'INSERT') {
      const { nombre_completo, telefono, direccion } = record.raw_user_meta_data || {}

      console.log(`Nuevo usuario: ${record.email}`);
      console.log(`Nombre: ${nombre_completo}`);
      console.log(`Teléfono: ${telefono}`);
      console.log(`Dirección: ${direccion}`);

      // Aquí se puede agregar lógica adicional en JS como:
      // - Integración con CRM
      // - Validación de zona de entrega
    }

    return new Response(JSON.stringify({ success: true }), {
      headers: { "Content-Type": "application/json" },
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      status: 400,
      headers: { "Content-Type": "application/json" },
    })
  }
})
