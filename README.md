# Creaciones Normita - App móvil

Aplicación móvil de ejemplo para Creaciones Normita, hecha en Android Studio con Jetpack Compose.

Por ahora el proyecto contiene las interfaces gráficas principales:

- Pantalla de inicio de sesión.
- Pantalla de registro.
- Pantalla principal según el mockup.
- Navegación inferior con secciones base: Inicio, Colección, Cotizar, Citas y Cuenta.
- Menú lateral tipo drawer.

La aplicación todavía no está conectada a una base de datos ni a un sistema real de usuarios. Los formularios sirven para mostrar el flujo visual y poder navegar entre pantallas.

## Requisitos

Para abrir y correr el proyecto se necesita:

- Android Studio instalado.
- Android SDK instalado.
- Un emulador Android o un celular conectado por USB.
- JDK incluido con Android Studio.

En esta computadora el SDK está configurado en:

```text
C:\Users\guerr\AppData\Local\Android\Sdk
```

## Cómo abrir el proyecto

1. Abrir Android Studio.
2. Seleccionar `Open`.
3. Buscar y abrir esta carpeta:

```text
C:\Users\guerr\Documents\CREACIONES-MOVIL
```

4. Esperar a que Android Studio sincronice el proyecto.
5. Elegir un emulador o dispositivo físico.
6. Presionar `Run`.

## Cómo correrlo desde terminal

Desde la carpeta del proyecto se puede compilar con:

```powershell
.\gradlew.bat :app:assembleDebug
```

Si la compilación termina bien, el APK se genera en:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## Estructura principal

```text
app/src/main/java/com/creacionesnormita/mobile
```

Archivos importantes:

- `MainActivity.kt`: punto de entrada de la aplicación.
- `navigation/AppRoot.kt`: decide si se muestra login/registro o la app principal.
- `features/auth/PantallaAutenticacion.kt`: interfaz de login y registro.
- `features/main/PantallaPrincipal.kt`: pantalla principal, colección, cotización, citas, cuenta y menú.
- `core/design/Theme.kt`: colores y tema visual.
- `core/model/Vestido.kt`: modelos de datos simples.
- `core/sample/DatosDeMuestra.kt`: datos de ejemplo para mostrar la interfaz.
- `ui/components`: componentes reutilizables como botones, logo, líneas de muestra y tarjetas.

## Colores usados

La app usa la paleta tomada del proyecto web de Creaciones Normita:

- Color principal: `#a83b78`
- Color principal oscuro: `#943066`
- Fondo claro: `#f8f3f6`

Esto ayuda a que la app móvil se vea relacionada con el sistema web existente.

## Estado actual

Hecho:

- Proyecto Android creado.
- Jetpack Compose configurado.
- Logo agregado.
- Login y registro como interfaces gráficas.
- Pantalla principal basada en el mockup.
- Navegación inferior.
- Menú lateral.
- Compilación probada correctamente.

Pendiente:

- Conectar login y registro a una base de datos.
- Agregar validaciones reales en los formularios.
- Reemplazar datos de muestra por datos reales.
- Crear las pantallas restantes con base en los demás mockups.
- Agregar imágenes reales de vestidos.

## Nota

Este proyecto está pensado como base ordenada para seguir agregando pantallas. La idea es que cada nueva parte de la app tenga su propia carpeta o archivo, para que el código no quede todo mezclado.
