# Veterinaria PetWorld

Sistema de gestión para una veterinaria (Cliente, Empleado, Proveedor, Vacuna,
Veterinario) construido con JavaFX y MySQL. CRUD completo contra
procedimientos almacenados, formularios con validación (correo, teléfono) y
confirmación antes de eliminar.

## ⚠️ Antes de correrlo

Este proyecto se conecta a una base de datos MySQL. Las credenciales **no**
están en el código — se leen de variables de entorno (ver
`src/org/carloslopez/db/Conexion.java`):

| Variable  | Ejemplo                                                          |
|-----------|-------------------------------------------------------------------|
| `DB_URL`  | `jdbc:mysql://localhost:3306/DBVeterinaria2024242?useSSL=false`  |
| `DB_USER` | `root`                                                            |
| `DB_PASS` | tu contraseña de MySQL                                            |

Configúralas en tu sistema operativo o en la configuración de ejecución de tu
IDE (NetBeans: Project Properties → Run → VM Options, o variables de entorno
del sistema) antes de ejecutar. Si no las defines, el proyecto intenta
conectarse a `localhost` con usuario `root` y sin contraseña — solo pensado
para desarrollo local.

**Nunca vuelvas a escribir la contraseña real directamente en `Conexion.java`
ni en ningún archivo que subas al repositorio.**

## Base de datos

Necesitas una base de datos `DBVeterinaria2024242` en MySQL con los
procedimientos almacenados que usan los controladores (`sp_ListarClientes`,
`sp_AgregarCliente`, `sp_EditarCliente`, `sp_EliminarCliente`, y su
equivalente para Empleado, Proveedor, Vacuna y Veterinario). Si no conservas
el script de creación de la base, este es un buen siguiente paso: expórtalo
con `mysqldump --routines` y agrégalo a este repo como `database/schema.sql`
para que el proyecto sea reproducible por cualquiera que lo clone.

## Estructura

```
src/org/carloslopez/
├── bean/        → Cliente, Empleado, Proveedor, Vacuna, Veterinario
├── controller/  → un controlador JavaFX por entidad, CRUD completo
├── db/          → Conexion.java (singleton, credenciales por variable de entorno)
├── system/      → Principal.java (ventana principal)
├── view/        → .fxml de cada pantalla
└── resource/    → CSS de la interfaz
```

## Pendientes conocidos (para la próxima iteración)

- Las confirmaciones usan `JOptionPane` (Swing). Al ser una app JavaFX,
  conviene migrar a `javafx.scene.control.Alert` para no mezclar dos
  toolkits de UI.
- Hay literales con caracteres mal codificados (ej. tildes rotas en algunos
  mensajes de diálogo) — guarda los archivos como UTF-8 para corregirlo.
- `generarReporte()` en los controladores está sin implementar (solo actúa
  como botón de cancelar durante una edición).
