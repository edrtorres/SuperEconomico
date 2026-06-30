-- CONFIGURACIÓN DE BASE DE DATOS PARA SUPERMERCADO SUPERECOECONÓMICO

-- 1. TIPOS Y ENUMS
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'rol_usuario') THEN
        CREATE TYPE public.rol_usuario AS ENUM ('cliente', 'encargado');
    END IF;
END $$;

-- 2. TABLA DE PERFILES
CREATE TABLE IF NOT EXISTS public.perfiles (
    id UUID REFERENCES auth.users ON DELETE CASCADE PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    nombre_completo TEXT,
    telefono TEXT,
    direccion TEXT,
    rol public.rol_usuario DEFAULT 'cliente' NOT NULL,
    actualizado_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

-- Asegurar columnas si la tabla ya existe
ALTER TABLE public.perfiles ADD COLUMN IF NOT EXISTS telefono TEXT;
ALTER TABLE public.perfiles ADD COLUMN IF NOT EXISTS direccion TEXT;

-- 3. SEGURIDAD (RLS)
ALTER TABLE public.perfiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Ver propio perfil" ON public.perfiles;
CREATE POLICY "Ver propio perfil" ON public.perfiles FOR SELECT TO authenticated USING (auth.uid() = id);

DROP POLICY IF EXISTS "Actualizar propio perfil" ON public.perfiles;
CREATE POLICY "Actualizar propio perfil" ON public.perfiles FOR UPDATE TO authenticated USING (auth.uid() = id);

-- 4. FUNCIÓN PARA EL TRIGGER (PLPGSQL)
CREATE OR REPLACE FUNCTION public.manejar_nuevo_usuario()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.perfiles (id, email, nombre_completo, telefono, direccion, rol)
  VALUES (
    new.id,
    new.email,
    new.raw_user_meta_data->>'nombre_completo',
    new.raw_user_meta_data->>'telefono',
    new.raw_user_meta_data->>'direccion',
    COALESCE((new.raw_user_meta_data->>'rol')::public.rol_usuario, 'cliente')
  );
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 5. TRIGGER
DROP TRIGGER IF EXISTS al_crear_usuario_auth ON auth.users;
CREATE TRIGGER al_crear_usuario_auth
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE PROCEDURE public.manejar_nuevo_usuario();
