# Bitácora del proyecto: Reservas de laboratorio

Este documento acompaña todo el desarrollo del sistema. Se actualizará después de cada avance que funcione y servirá como evidencia del proceso de investigación, construcción, pruebas y despliegue.

## 1. Contexto y objetivo

El proyecto pertenece a **Aiden Montero Pérez**. Consiste en una aplicación web para reservar laboratorios por fecha y hora.

La fuente de verdad del alcance es [requisitos.md](./requisitos.md). Las decisiones generales y el contexto académico están en [contexto.md](./contexto.md).

Reglas principales:

- Todo el código, configuración y scripts deben ser generados por IA.
- El estudiante puede copiar, ejecutar, probar y pedir correcciones, pero no editar código manualmente.
- Debe existir un repositorio público con commits pequeños y explicables.
- Los datos deben persistir en PostgreSQL y la aplicación debe funcionar en una URL pública.
- El sistema tendrá como máximo tres pantallas: Panel, Reservas y Laboratorios.

## 2. Decisiones técnicas

| Área | Decisión | Motivo |
|---|---|---|
| Lenguaje | Kotlin | Lenguaje JVM moderno, seguro frente a muchos errores de valores nulos y adecuado para backend. |
| Framework web | Ktor | Framework ligero de Kotlin para crear servidores y APIs. |
| Base de datos | PostgreSQL en Neon | Persistencia para que los datos sobrevivan a reinicios y despliegues. |
| Hosting | Render | Publicación de la aplicación mediante Docker. |
| Frontend | HTML, CSS y JavaScript | Ktor servirá la interfaz web y los recursos estáticos. |
| Editor | Visual Studio Code | Herramienta indicada por el contexto del ejercicio. |
| Construcción | Gradle Wrapper | Permite ejecutar Gradle sin instalarlo globalmente. |
| Control de versiones | Git y GitHub | Registra la historia y publica el código. |

## 3. Estado actual

- [x] Requisitos y contexto recibidos y revisados.
- [x] Confirmado que la carpeta inicial solo contiene los documentos de requisitos y contexto.
- [x] Creada esta bitácora de trabajo.
- [ ] Confirmar con el docente que Kotlin no se enseña en la carrera.
- [ ] Registrar el stack con el docente.
- [ ] Crear el proyecto base de Ktor y verificar que arranca.
- [ ] Crear el repositorio público y hacer el primer push.
- [ ] Implementar laboratorios y reservas.
- [ ] Implementar validación de choques.
- [ ] Crear las tres pantallas y sus estados visuales.
- [ ] Crear pruebas, seed, Dockerfile y despliegue.
- [ ] Completar README, ficha de investigación y reflexión.

## 4. Plan de construcción

Cada fase termina con una ejecución o prueba local. Si funciona, se registra un commit y se hace push.

1. Preparación: JDK, Git, VS Code, Kotlin y Gradle Wrapper.
2. Proyecto base: Ktor, ruta de salud y configuración inicial.
3. Persistencia: PostgreSQL mediante `DATABASE_URL`, tablas y datos de ejemplo.
4. Laboratorios: CRUD con validación del servidor.
5. Reservas: CRUD, estados confirmada/cancelada y filtros.
6. Regla de negocio: impedir reservas solapadas en el mismo laboratorio.
7. Interfaz: Panel, Reservas y Laboratorios con HTML, CSS y JavaScript.
8. Calidad visual: responsive, modo claro/oscuro, carga, vacíos, errores, toasts y accesibilidad.
9. Pruebas: endpoints y flujos principales en local y URL pública.
10. Despliegue: Dockerfile con IA, Render y Neon.
11. Entrega: README, bitácora, reflexión y guion de demo.

## 5. Herramientas y extensiones

Se distingue entre herramientas **usadas**, **planificadas** y **alternativas**, para no afirmar que algo se utilizó antes de tiempo.

### Herramientas usadas hasta ahora

| Herramienta | Para qué sirve |
|---|---|
| Visual Studio Code | Editor del proyecto, tareas y revisión de archivos. |
| Codex/IA | Genera código, configuración, documentación, pruebas y correcciones; también explica cada decisión. |
| PowerShell | Terminal de Windows usada para inspeccionar archivos y ejecutar comandos. |

### Extensiones requeridas

| Extensión | Identificador | Para qué sirve |
|---|---|---|
| Kotlin by JetBrains | `JetBrains.kotlin-server` | Autocompletado, errores, navegación e indexación de Kotlin. |
| Docker | `ms-azuretools.vscode-docker` | Editar, inspeccionar y ejecutar imágenes y contenedores. |
| DotENV | `mikestead.dotenv` | Colorear archivos `.env`; no protege secretos ni reemplaza `.gitignore`. |

### Extensiones recomendadas

| Extensión | Identificador | Para qué sirve |
|---|---|---|
| GitLens | `eamodio.gitlens` | Consultar historial, commits y cambios por línea. |
| SQLTools | `mtxr.sqltools` | Explorar y consultar PostgreSQL desde VS Code. |
| Driver PostgreSQL de SQLTools | `mtxr.sqltools-driver-pg` | Conectar SQLTools con PostgreSQL. |
| REST Client | `humao.rest-client` | Probar endpoints HTTP desde archivos `.http`. |
| Prettier | `esbenp.prettier-vscode` | Formatear HTML, CSS y JavaScript. |
| Error Lens | `usernamehw.errorlens` | Mostrar errores junto a la línea afectada. |
| Gradle for Java | `vscjava.vscode-gradle` | Ejecutar tareas de Gradle desde VS Code. |

Solo se instalará una extensión de Kotlin: la oficial o, si falla, `fwcd.kotlin`. No se deben instalar ambas.

### Programas externos

- **JDK 21 LTS:** Kotlin se ejecuta sobre la JVM y necesita el Java Development Kit.
- **Git:** registra versiones y cambios.
- **GitHub:** aloja el repositorio público.
- **Docker Desktop:** prueba el empaquetado antes de Render.
- **Neon:** proporciona PostgreSQL persistente.
- **Render:** publica el servicio web.

## 6. Kotlin explicado desde cero

Kotlin es un lenguaje creado por JetBrains que normalmente se compila para la **JVM** (Java Virtual Machine). La JVM ejecuta el programa y permite usar muchas bibliotecas del ecosistema Java. Kotlin también se usa en Android, backend, escritorio y proyectos multiplataforma.

### Diferencias básicas frente a Java

- Kotlin suele necesitar menos código para expresar lo mismo.
- El sistema de tipos distingue valores que pueden ser `null` de los que no.
- Las funciones pueden existir fuera de una clase.
- Las `data class` generan operaciones útiles para transportar datos.
- `val` representa valores que no se reasignan; `var` permite reasignarlos.
- La sintaxis de funciones, lambdas y colecciones es más concisa.

Ejemplo:

```kotlin
val nombre = "Laboratorio 1"
var capacidad = 24
val descripcion = "$nombre tiene $capacidad puestos"
```

`val` no permite reasignar esa referencia. `var` sí. El signo `$` inserta una variable dentro de un texto.

### El problema de `null`

En Kotlin un texto normal no puede ser nulo:

```kotlin
val obligatorio: String = "Siempre tiene texto"
val opcional: String? = null
```

El signo `?` indica que el valor puede faltar. Para usarlo de forma segura se usa `?.`, `?:` o una validación explícita. Esto será importante cuando lleguen datos incompletos desde una petición HTTP.

### Clases de datos

```kotlin
data class Reserva(
    val id: Long,
    val responsable: String,
    val motivo: String,
    val estado: EstadoReserva
)
```

Una `data class` describe datos. No significa por sí sola que estén guardados en PostgreSQL; la persistencia se implementará aparte.

### Ktor en palabras simples

- **Application:** configuración general del servidor.
- **Plugin:** funcionalidad agregable, como JSON, CORS o manejo de errores.
- **Routing:** define qué ocurre para cada método y URL, por ejemplo `GET /api/reservas`.
- **Request:** datos que llegan del navegador o de otra herramienta.
- **Response:** resultado que el servidor devuelve, normalmente JSON.

Kotlin es el lenguaje; Ktor es el framework web construido con Kotlin; Gradle es la herramienta que descarga dependencias, compila y ejecuta tareas.

### Curiosidades

- El nombre Kotlin viene de una isla cercana a San Petersburgo.
- Kotlin fue creado por JetBrains y recibió apoyo oficial de Google para Android.
- Kotlin puede llamar código Java existente.
- Ktor no es lo mismo que Kotlin: uno es framework y el otro es lenguaje.

## 7. API REST prevista

| Método | Ruta | Propósito |
|---|---|---|
| `GET` | `/api/laboratorios` | Listar laboratorios. |
| `POST` | `/api/laboratorios` | Crear un laboratorio. |
| `PUT` | `/api/laboratorios/{id}` | Editar un laboratorio. |
| `DELETE` | `/api/laboratorios/{id}` | Eliminarlo si es seguro. |
| `GET` | `/api/reservas` | Listar y filtrar reservas. |
| `POST` | `/api/reservas` | Crear una reserva. |
| `PUT` | `/api/reservas/{id}` | Editar una reserva. |
| `DELETE` | `/api/reservas/{id}` | Cancelar o eliminar según la regla definida. |

## 8. Seguridad y secretos

La aplicación leerá `DATABASE_URL` desde variables de entorno. Se incluirá un `.env.example` sin valores reales y `.env` estará en `.gitignore`. Nunca se subirá una contraseña, una llave ni la cadena real de Neon. Si un secreto se publica por error, se debe rotar la credencial; borrarlo del archivo no basta porque puede quedar en el historial.

## 9. Bitácora de prompts

| # | Fecha | Herramienta | Fase | Prompt o solicitud | Resultado | ¿Funcionó? | Aprendizaje | Commit sugerido |
|---|---|---|---|---|---|---|---|---|
| 1 | 2026-11-26 | Codex | Preparación | Revisar `contexto.md` y `requisitos.md`, explicar el alcance y comenzar sin inventar requisitos. | Se confirmó el alcance, las restricciones y que aún no había código. | Sí | Los documentos son la fuente de verdad. | `docs: crea bitácora y guía del proyecto` |
| 2 | 2026-11-26 | Codex | Documentación | Crear una bitácora Markdown con herramientas, extensiones, commits, pruebas y explicaciones de Kotlin/Ktor. | Se creó este documento. | Pendiente de verificar en Git | La documentación debe avanzar junto con el código. | `docs: crea bitácora y guía del proyecto` |

En cada avance se añadirá una fila con el error encontrado, la corrección solicitada a la IA y la prueba ejecutada.

## 10. Registro de pruebas

| Fecha | Prueba | Procedimiento | Resultado | Corrección |
|---|---|---|---|---|
| 2026-11-26 | Reconocimiento inicial | Revisar archivos del proyecto. | Solo existían `contexto.md` y `requisitos.md`; no había código ni Git inicializado. | Crear la base del proyecto en el siguiente paso. |

## 11. Commits y pushes

Después de cada avance funcional se revisará el diff y se usará:

```powershell
git status
git add .
git commit -m "tipo: descripción clara"
git push origin <rama>
```

Mensaje sugerido para este avance documental:

```text
docs: crea bitácora y guía del proyecto
```

Antes del commit se confirmará que no aparece `.env` ni ningún secreto.

## 12. Pendientes para la entrega

- URL pública funcionando.
- Repositorio público con historial comprensible.
- README con instalación, ejecución local y despliegue.
- Ficha de investigación del stack de una página.
- Bitácora completa de prompts y correcciones.
- Reflexión de una o dos páginas; faltan las páginas 5–7 del enunciado y sus preguntas.
- Demo de cinco minutos con explicación de un fragmento de código.
- Reconfirmación de límites gratuitos de Render y Neon el día del despliegue.
