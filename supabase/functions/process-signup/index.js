// Backend logic in Javascript for Supabase Edge Functions
// This function can be used to process data after a user signs up

const { createClient } = require('@supabase/supabase-js');

exports.handler = async (event, context) => {
  const { record } = event;

  console.log('Procesando nuevo usuario:', record.email);

  // Aquí puedes agregar lógica adicional como:
  // - Enviar un correo de bienvenida personalizado
  // - Validar el formato del teléfono
  // - Notificar a un canal de Slack/Telegram sobre el nuevo registro

  if (record.telefono && record.telefono.length < 8) {
    console.warn('Advertencia: El teléfono parece ser demasiado corto:', record.telefono);
  }

  return {
    status: 200,
    body: { message: "Perfil procesado correctamente" }
  };
};
