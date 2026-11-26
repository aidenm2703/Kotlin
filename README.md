# LabReserve

Aplicación web modular para reservar laboratorios por fecha y hora. El backend está construido con Kotlin y Ktor; el frontend usa HTML, CSS y JavaScript servido por Ktor; PostgreSQL se utiliza cuando existe `DATABASE_URL` y, si no existe, se usa memoria para la demostración local.

## Ejecutar en local

Requiere JDK 21 LTS. En PowerShell:

```powershell
.\gradlew.bat test
.\gradlew.bat run
```

Abrir `http://localhost:8080`. Para PostgreSQL, definir `DATABASE_URL` antes de arrancar; nunca guardar la cadena real en Git.

## API principal

- `GET/POST/PUT/DELETE /api/laboratorios`
- `GET/POST/PUT/DELETE /api/reservas`
- `GET /api/dashboard`
- `GET /api/health`

Las reservas se validan en el servidor: campos obligatorios, fecha no pasada, orden de horas y solapamientos.

## Docker

```powershell
docker build -t labreserve .
docker run --rm -p 8080:8080 --env-file .env labreserve
```

El archivo `.env` es local y está ignorado por Git. Usar [.env.example](./.env.example) como plantilla sin credenciales reales.

## Estructura

- `domain/`: modelos y validaciones de negocio.
- `persistence/`: repositorios en memoria y PostgreSQL mediante JDBC.
- `service/`: casos de uso.
- `routes/`: API y recursos estáticos.
- `src/main/resources/static/`: las tres pantallas del frontend.
- `BITACORA_PROYECTO.md`: decisiones, herramientas, prompts, pruebas y pendientes.

## Despliegue

El destino previsto es Render mediante Docker y Neon como PostgreSQL persistente. La configuración final de cuentas, variables de entorno y URL pública debe completarse desde los paneles de esos servicios; esas acciones no pueden hacerse desde el código local.
