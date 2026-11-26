# Requisitos: Reservas de laboratorio con Kotlin + Ktor (hecho con IA)

## A. Requisitos funcionales del sistema
**Mínimo del enunciado**
- [ ] **RF1.** Crear y cancelar reservas por fecha y hora.
- [ ] **RF2.** Evitar choques de horario (dos reservas no pueden solapar el mismo laboratorio).
- [ ] **RF3.** CRUD completo de la entidad principal (Reserva): crear, listar, editar, eliminar o cancelar.
- [ ] **RF4.** Persistencia en PostgreSQL (los datos sobreviven a reinicios y redeploys).
- [ ] **RF5.** Entre 1 y 3 pantallas.

**Ampliación (dentro de las 3 pantallas)**
- [ ] **RF6. Pantalla 1, Panel:** calendario semanal con disponibilidad por laboratorio y tarjetas de resumen (reservas de hoy, laboratorios libres, ocupación).
- [ ] **RF7. Pantalla 2, Reservas:** listado con búsqueda y filtros (laboratorio, fecha, estado), formulario de crear/editar y cancelar con confirmación.
- [ ] **RF8. Pantalla 3, Laboratorios:** CRUD de laboratorios (nombre, capacidad, ubicación, equipo, color).
- [ ] **RF9.** Estados de reserva: confirmada y cancelada.
- [ ] **RF10.** Validaciones con mensajes claros: hora fin posterior a la de inicio, no reservar en el pasado, campos obligatorios, mensaje cuando hay choque.
- [ ] **RF11 (opcional).** Exportar reservas a CSV.

**Sistema:** Reservas de laboratorio  
**Entidad principal:** Reserva (laboratorio, responsable, fecha, hora inicio, hora fin, motivo, estado)  
**Tabla de apoyo:** Laboratorio

## B. Requisitos de diseño (que impresione)
- [ ] **RD-UI1.** Identidad visual propia y coherente: paleta, tipografía, espaciado e iconos consistentes en las 3 pantallas.
- [ ] **RD-UI2.** Responsive: se ve bien en celular, tablet y PC.
- [ ] **RD-UI3.** Modo oscuro y claro.
- [ ] **RD-UI4.** Animaciones y transiciones suaves, sin exagerar; estados de carga, vacíos y error bien diseñados.
- [ ] **RD-UI5.** Avisos tipo toast al crear, editar o cancelar; confirmación antes de acciones destructivas.
- [ ] **RD-UI6.** Accesibilidad básica: buen contraste, foco visible, etiquetas en formularios.
- [ ] **RD-UI7.** Datos de ejemplo cargados (seed) para que la demo se vea viva desde el primer segundo.
- [ ] **RD-UI8.** Frontend en HTML, CSS y JavaScript generado por la IA y servido por Ktor.

Regla de oro: **pulido antes que cantidad**. Si el tiempo aprieta, se recortan extras, no calidad.

## C. Requisitos técnicos
- [ ] **RT1.** Kotlin con Ktor; stack registrado y aprobado por el docente.
- [ ] **RT2.** Todo el código, configuración y scripts generados por IA; ninguna edición manual de código.
- [ ] **RT3.** Dockerfile y configuración de despliegue generados por IA.
- [ ] **RT4.** Credenciales y llaves solo en variables de entorno (`DATABASE_URL`, etc.). Incluir `.gitignore` y un `.env.example` sin valores reales.
- [ ] **RT5.** Ejecutar en local y probar tras cada parte generada antes de pasar a la siguiente.
- [ ] **RT6.** API REST clara para reservas y laboratorios, con validación en el servidor (no solo en el navegador).

## D. Requisitos de Git y GitHub (siempre)
- [ ] **RG1.** Repositorio público en GitHub creado desde el inicio.
- [ ] **RG2.** **Hacer commit y push a la rama de GitHub después de cada avance que funcione.** Nunca acumular muchos cambios en un solo commit.
- [ ] **RG3.** **Mensajes de commit con sentido**: formato `tipo: qué se hizo` (`feat:`, `fix:`, `docs:`, `chore:`, `style:`, `refactor:`).
- [ ] **RG4.** Prohibidos mensajes vacíos o genéricos ("cambios", "update", "final2", "asdf").
- [ ] **RG5.** Pedir a la IA que sugiera el mensaje del commit al terminar cada paso, y registrarlo en la bitácora.
- [ ] **RG6.** Nunca subir `.env`, contraseñas ni la cadena de conexión real. Si se sube un secreto por error, **rotarlo** (cambiar la contraseña), no solo borrarlo.
- [ ] **RG7.** El historial final debe contar la historia: proyecto base → laboratorios → reservas → validación de choques → diseño → Docker → despliegue → documentación.

Instrucción para pegar a la IA en cada sesión: *"Después de cada cambio que funcione, dime los comandos git para hacer commit y push, con un mensaje claro en formato `tipo: descripción`."*

## E. Entorno de desarrollo: Visual Studio Code

### Programas a instalar (fuera de VS Code)
- [ ] **Visual Studio Code** (el editor).
- [ ] **JDK** (Java Development Kit) en versión LTS, por ejemplo 21 (Temurin). Kotlin corre sobre la JVM. Preguntar a la IA qué versión necesita el proyecto generado y verificar con `java -version`.
- [ ] **Git** y una cuenta de **GitHub**. Verificar con `git --version`.
- [ ] **Docker Desktop** (opcional pero recomendado, para probar el Dockerfile en local antes de desplegar).
- [ ] Cuentas gratis en **Render** y **Neon**.
- No hace falta instalar Gradle aparte: el proyecto trae su wrapper (`gradlew`).

### Extensiones de VS Code (ID para buscarlas en el panel de extensiones)
**Imprescindibles**
- [ ] **Kotlin by JetBrains** (`JetBrains.kotlin-server`): soporte oficial de Kotlin. Hoy está en **alpha**, con autocompletado, errores y navegación; abre una carpeta con un proyecto Gradle y entonces se activa al abrir un `.kt`. Esperar a que termine la importación e indexación.
- [ ] **Docker** (`ms-azuretools.vscode-docker`): para editar y probar el Dockerfile.
- [ ] **DotENV** (`mikestead.dotenv`): colores para archivos `.env` (recordar que nunca se suben).

**Recomendadas**
- [ ] **GitLens** (`eamodio.gitlens`): ver el historial de commits y quién cambió qué.
- [ ] **SQLTools** (`mtxr.sqltools`) + **SQLTools PostgreSQL/Cockroach Driver** (`mtxr.sqltools-driver-pg`): mirar las tablas de Neon desde VS Code.
- [ ] **REST Client** (`humao.rest-client`): probar los endpoints de la API sin salir del editor (alternativa a Postman).
- [ ] **Prettier** (`esbenp.prettier-vscode`): ordena HTML, CSS y JS del frontend.
- [ ] **Error Lens** (`usernamehw.errorlens`): muestra los errores en la misma línea.
- [ ] **Gradle for Java** (`vscjava.vscode-gradle`): ejecutar tareas de Gradle con clic.

**Alternativa si la oficial falla:** **Kotlin** de fwcd (`fwcd.kotlin`), comunitaria y más antigua. **Instalar solo una de las dos**, nunca ambas, porque chocan. Si en la página de la extensión oficial aparece un aviso de desinstalar una versión vieja de "Kotlin by JetBrains", aceptarlo y reiniciar la ventana.

**Primera prueba (antes de empezar):**
- [ ] Abrir la carpeta del proyecto, confiar en la carpeta cuando VS Code lo pida y abrir un archivo `.kt`.
- [ ] Ver que el autocompletado funciona y que `./gradlew run` (o `gradlew.bat run` en Windows) levanta el servidor en local.

## F. Requisitos de despliegue
- [ ] **RP1.** URL pública funcionando durante todo el periodo de evaluación.
- [ ] **RP2.** Confirmar las condiciones vigentes del plan gratuito el día del despliegue y documentarlas.
- [ ] **RP3.** Probar todas las funciones (incluido el choque de horarios) directamente en la URL pública.
- [ ] **RP4.** Base de datos en Neon (permanente), no el Postgres gratis de Render (expira a los 30 días).
- [ ] **RP5.** Antes de la demo, abrir la URL con tiempo para despertar el servicio.

## G. Entregables
- [ ] URL pública del sistema funcionando.
- [ ] Enlace al repositorio público con historial de commits claro.
- [ ] **README** con: descripción del sistema, stack usado, cómo ejecutarlo en local, cómo se desplegó.
- [ ] **Ficha de investigación del stack** (1 página).
- [ ] **Bitácora de prompts completa** + enlaces o exportaciones de las conversaciones.
- [ ] **Reflexión escrita** (1–2 páginas) respondiendo las preguntas del final del enunciado (pendientes: el documento recibido solo trae las páginas 1–4).
- [ ] **Demo en vivo de 5 minutos**, incluyendo explicar un fragmento de código elegido por el docente.

## H. Contenido sugerido de la ficha de investigación (1 página)
1. Qué es Kotlin y por qué se usa (JVM, backend, Android).
2. Qué es Ktor y cómo se estructura un proyecto (plugins, routing, módulos).
3. Cómo se instala y ejecuta (JDK, Gradle, VS Code con extensión de Kotlin).
4. Cómo se conecta a PostgreSQL.
5. Dónde se despliega gratis y con qué límites (Render + Neon, verificados).
6. Riesgos y dificultades esperadas.

## I. Plantilla de la bitácora de prompts
| # | Fecha | Herramienta de IA | Fase | Prompt (resumen o texto) | Resultado | ¿Funcionó? | Qué aprendí o qué corregí |
|---|---|---|---|---|---|---|---|
| 1 | | | Investigación | Explícame qué es Kotlin y cómo se crea un proyecto web mínimo con Ktor | | | |
| 2 | | | Diseño | | | | |
| 3 | | | Construcción | | | | |
| 4 | | | Despliegue | | | | |

Lo valioso es mostrar cómo corregí el rumbo cuando algo falló. Registrar cada error pegado a la IA y su corrección, y el mensaje de commit de cada paso.

### Ejemplos de prompts
- "Soy estudiante y nunca he usado Kotlin. Genera un proyecto Ktor mínimo para un sistema de reservas de laboratorio con entidades Laboratorio y Reserva (fecha, hora inicio, hora fin), CRUD completo y PostgreSQL. Explícame cada archivo y dime el mensaje de commit."
- "Al ejecutar obtengo este error: [pegar error]. ¿Qué significa y cómo lo corrijo?"
- "Agrega validación para que dos reservas no se solapen en el mismo laboratorio y devuelve un mensaje claro."
- "Diseña el frontend en HTML, CSS y JS con modo oscuro, responsive y animaciones suaves, para las 3 pantallas."
- "Quiero desplegar esta app en Render usando Docker. Genera el Dockerfile y dime paso a paso qué configurar en el panel. La base de datos está en Neon y se conecta con DATABASE_URL."

## J. Condiciones que anulan el ejercicio (evitar)
- Editar código a mano o presentar código escrito a mano como generado por IA.
- Ocultar conversaciones o dejar la bitácora incompleta.
- Subir contraseñas o llaves al repositorio.
- URL pública caída durante la evaluación.