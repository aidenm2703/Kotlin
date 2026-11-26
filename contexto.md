# Contexto: Sistema pequeño con IA en un stack desconocido (Kotlin + Ktor)

> Uso: pegar este archivo al inicio de cualquier chat con la IA para que tenga todo el contexto.
> Estudiante: Aiden Montero Pérez

## 1. Qué es el ejercicio
Construir un sistema web muy pequeño, con **100 % del código generado por IA**, en un lenguaje que **no se enseña en el programa**, y dejarlo funcionando en una **URL pública**. El reto no es programar: es investigar un stack desconocido, dirigir a la IA con buenos prompts, verificar lo que produce y llevarlo a producción.

## 2. Reglas clave (del enunciado)
1. Stack ajeno al programa; se registra con el docente antes de empezar y no se repite dentro del grupo.
2. Todo el código, configuración y scripts los escribe la IA. Yo puedo pegar, ejecutar, mover archivos y pedir cambios, **pero no editar código a mano**. Si algo falla, se lo pido a la IA.
3. Se puede usar cualquier IA (Claude, ChatGPT, Gemini, Copilot, Cursor...), pero todo queda en la bitácora.
4. Sistema muy pequeño: 1 a 3 pantallas, una entidad principal con CRUD y persistencia.
5. En producción: URL pública durante el periodo de evaluación. Localhost no cuenta.
6. Repositorio público (GitHub/GitLab) con historial de commits que muestre el avance.
7. Seguridad mínima: nada de contraseñas ni llaves en el repo; variables de entorno.
8. Transparencia: bitácora de prompts completa. Ocultar conversaciones o presentar código a mano como generado anula el ejercicio.

## 3. Decisiones tomadas
| Elemento | Decisión |
|---|---|
| Lenguaje | Kotlin |
| Framework | Ktor (backend) |
| Base de datos | PostgreSQL (Neon, plan gratis permanente) |
| Hosting | Render (web service gratis con Docker) |
| Editor | **Visual Studio Code** (no Android Studio ni IntelliJ) |
| Tipo de proyecto | **Sistema web** con URL pública. **Sin app Android** (el enunciado pide sistema web; una app no se abre con un enlace) |
| Dificultad según enunciado | **Alta** (puede sumar puntos extra según rúbrica) |
| Sistema | **Reservas de laboratorio**, ampliado y con diseño muy cuidado |
| Control de versiones | Commits frecuentes con sentido a la rama de GitHub (ver sección 7) |

Pendiente: confirmar con el docente que Kotlin no se ve en la carrera y registrar el stack.

## 4. Investigación de despliegue gratuito (verificada el 26-nov-2026)
Los planes gratuitos cambian con frecuencia; **re-confirmar el día del despliegue** (el enunciado lo exige como parte de la investigación).

### Render (la app) + Neon (la base de datos)
**Render, web service gratis**
- Se despliega desde un repo de GitHub/GitLab/Bitbucket con un Dockerfile; se elige "Docker", la ruta del Dockerfile y las variables de entorno. Cada commit a la rama redespliega automáticamente.
- 750 horas gratis por mes, sin tarjeta para empezar.
- Se **duerme tras 15 minutos sin tráfico**; al despertar tarda cerca de un minuto (con JVM puede ser más). Antes de la demo, abrir la URL con tiempo.
- **Sistema de archivos efímero**: se pierde en cada redeploy o reinicio. Por eso no usar SQLite local.
- Puede reiniciar el servicio en cualquier momento.

**Render PostgreSQL gratis: NO usar en este ejercicio**
- Una sola base gratis por cuenta y **expira a los 30 días**; luego queda inaccesible salvo que se pague.

**Neon PostgreSQL gratis (usar este)**
- Plan gratuito permanente, sin tarjeta: 1 GB por proyecto y 100 horas de cómputo por proyecto al mes.
- El cómputo se apaga tras 5 minutos de inactividad (la primera consulta después puede tardar un poco).
- Se conecta con una `DATABASE_URL` guardada como variable de entorno en Render.

### Alternativas (no recomendadas)
- **Railway**: crédito de prueba de $5 por 30 días y luego ~$1/mes; no es gratis a largo plazo. Verificar.
- **Fly.io**: sin plan gratuito continuo, solo crédito de prueba corto. Verificar.
- **Supabase (solo base de datos)**: gratis con 500 MB, pero se pausa por inactividad.

### Viabilidad
**Sí es posible gratis con Kotlin/Ktor**: Dockerfile (lo genera la IA) → Render + Neon. Límites a asumir: arranque en frío lento y memoria limitada para la JVM (pedir a la IA un Dockerfile multi-stage con imagen ligera y verificar el límite de RAM del plan).

## 5. El sistema: Reservas de laboratorio (versión ampliada)
**Catálogo original:** entidad Reserva; mínimo = crear y cancelar reservas por fecha y hora; extra = evitar choques de horario.

**Meta:** cumplir todo lo mínimo, hacer el extra y añadir funciones y diseño que impresionen **sin pasar de 3 pantallas** (regla 4). Mejor poco y muy pulido que mucho y a medias.

**Pantallas (3):**
1. **Panel / calendario semanal**: vista de disponibilidad por laboratorio, tarjetas de resumen (reservas de hoy, laboratorios libres ahora, ocupación).
2. **Reservas**: listado con búsqueda y filtros (laboratorio, fecha, estado), crear, editar y cancelar.
3. **Laboratorios**: alta/edición de laboratorios (nombre, capacidad, ubicación, equipo, color).

**Nota:** la entidad principal es Reserva; Laboratorio es una tabla de apoyo necesaria. Confirmar con el docente que esto cuenta como un sistema pequeño.

**Frontend:** HTML, CSS y JavaScript generados por la IA y servidos por Ktor; el backend y el lenguaje principal siguen siendo Kotlin. Confirmar con el docente que un frontend en JS es aceptable.

## 6. Fases y cronograma (2 semanas sugeridas)
1. **Investigación (días 1–2):** stack registrado, sistema elegido, ficha de 1 página.
2. **Diseño con IA (día 3):** modelo de datos, pantallas, estructura de carpetas, línea visual.
3. **Construcción (días 4–7):** generar por partes (modelo, rutas, vistas, conexión a datos); ejecutar en local tras cada parte; **commit y push en cada avance**.
4. **Despliegue (días 8–10):** Dockerfile, config de Render, variables de entorno; probar todo en la URL pública.
5. **Documentación y presentación (días 11–14):** README, bitácora, reflexión, demo en vivo de 5 min.

## 7. Flujo de Git (obligatorio)
- Repositorio público en GitHub desde el día 1, con `.gitignore` y sin secretos.
- Hacer **commit después de cada avance que funcione** y hacer push a la rama de trabajo de GitHub, para que el historial cuente la historia del proyecto.
- **Mensajes con sentido**, en un formato consistente (por ejemplo `feat:`, `fix:`, `docs:`, `chore:`):
  - `chore: crea proyecto Ktor base con Gradle`
  - `feat: agrega modelo y CRUD de laboratorios`
  - `feat: valida choques de horario en reservas`
  - `fix: corrige zona horaria al guardar reservas`
  - `docs: agrega README con pasos de despliegue`
- Nada de commits tipo "cambios", "asdf" o "final2".
- Pedir a la IA que sugiera el mensaje del commit después de cada paso. Cada commit debe poder explicarse en una línea.
- Nunca subir `.env`, contraseñas ni la `DATABASE_URL` real.

## 8. Notas
- El documento recibido solo incluye las páginas 1–4 de 7: **falta la rúbrica y las preguntas de la reflexión**. Pedirlas al docente.
- Para la demo, el docente elegirá un fragmento de código que debo saber explicar aunque lo haya escrito la IA: pedirle a la IA que explique cada archivo mientras se construye.