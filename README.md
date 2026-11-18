# Paolicias.Sweet – Backend

Microservicios Spring Boot del proyecto Paolicias.Sweet (DSY1105, Duoc UC). Datos ficticios.

| Servicio | Puerto | Tabla | Qué hace |
|---|---|---|---|
| `catalogo-service` | 8081 | `productos` | Catálogo de productos |
| `reservas-service` | 8082 | `reservas` | Reservas, cotización, comprobantes y flujo del abono |

Los dos usan el mismo Postgres (`paolicias_db`) y cada servicio es dueño de su tabla.
Un contenedor por microservicio. El **login y registro** viven en la app (SQLite), como pide la rúbrica.

`reservas-service` le pide los precios a `catalogo-service` por HTTP (`CatalogoClient`) para calcular el total:
así el precio nunca depende de lo que mande la app.

## Cómo ejecutar (local, con Docker)

1. Instalar y abrir **Docker Desktop**.
2. Copiar `.env.example` como `.env` y cambiar la clave. **El `.env` no se sube a GitHub.**
3. En esta carpeta: `docker compose up -d --build`
4. Probar: `http://localhost:8081/api/productos` (debe devolver 11 productos).
5. Para detener: `docker compose down`

> Si venías de la versión anterior, la tabla `reservas` cambió (fecha y horario ahora son tipos de fecha reales).
> Como los datos son ficticios, borra la base y créala de nuevo: `docker compose down -v` y luego `up -d --build`.

## Endpoints de catalogo-service (8081)

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/productos` | Lista de productos (por categoría y nombre) |
| GET | `/api/productos/{id}` | Un producto, o 404 |
| POST | `/api/productos` | Crea un producto (201). Valida los datos e ignora el `id` del JSON |

## Endpoints de reservas-service (8082)

| Método | Ruta | Quién lo usa | Qué hace |
|---|---|---|---|
| POST | `/api/cotizacion` | Cliente | Desglose: tarifa + productos + total + abono |
| POST | `/api/reservas` | Cliente | Crea la reserva. El total lo calcula el servidor. 409 si es duplicada |
| GET | `/api/reservas?correo=...` | Cliente | Mi Agenda, ordenada por fecha y horario |
| GET | `/api/reservas?estado=En revisión` | Admin | Panel admin (sin filtros o `Todas` = todas) |
| GET | `/api/reservas/{id}` | Ambos | Una reserva, o 404 |
| POST | `/api/reservas/{id}/comprobante?correo=...` | Cliente dueño | Sube la imagen (multipart, campo `archivo`, JPG/PNG/WEBP, máx. 5 MB) → *En revisión* |
| GET | `/api/reservas/{id}/comprobante` | Admin | La imagen del comprobante |
| PATCH | `/api/reservas/{id}/validar` | Admin | *En revisión* → *Pagado 50%* y guarda la fecha de pago |
| PATCH | `/api/reservas/{id}/rechazar` | Admin | *En revisión* → *Pendiente* y borra el comprobante |
| PATCH | `/api/reservas/{id}/completar` | Admin | *Pagado 50%* → *Completado* |

```json
POST /api/reservas
{ "nombreEvento": "Cumpleaños Ana", "fecha": "20/12/2026", "horario": "20:00", "invitados": 25,
  "observaciones": "Sin nueces", "correoCliente": "ana@correo.cl", "productoIds": [1, 7] }
```

```json
POST /api/cotizacion
{ "invitados": 25, "productoIds": [1] }
→ { "invitados": 25, "servicio": 80000, "lineas": [ { "productoId": 1, "nombre": "Torta Frutos Rojos",
    "unidades": 3, "precioUnitario": 45000, "subtotal": 135000 } ], "total": 215000, "abono": 107500 }
```

Fechas en `dd/MM/yyyy` y horario en `HH:mm`, igual que en la app. La respuesta de una reserva incluye
`total`, `abono`, `estadoAbono`, `tieneComprobante` y `fechaPago`.

### Flujo del abono

```
Pendiente --(cliente adjunta)--> En revisión --(admin valida)--> Pagado 50% --(admin)--> Completado
    ^                                 |
    +--------(admin rechaza)----------+
```

### Errores

Todos responden JSON `{ "mensaje": "..." }`; los de validación agregan `"errores": { "campo": "mensaje" }`.

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos (fecha pasada, invitados fuera de 1–100, producto inexistente...) |
| 403 | Subir comprobante a una reserva de otro cliente |
| 404 | No existe |
| 409 | Reserva duplicada o cambio de estado no permitido |
| 503 | reservas-service no pudo hablar con catalogo-service |

## Estructura

```
catalogo-service/src/main/java/com/paolicias/catalogo
├── entities     Producto
├── repository   ProductoRepository
├── services     ProductoService, ProductoServiceImpl
├── controller   ProductoController, ManejadorErrores
└── seed         ProductoSeeder (11 productos iniciales)

reservas-service/src/main/java/com/paolicias/reservas
├── entities     Reserva, EstadoAbono
├── repository   ReservaRepository
├── dto          ReservaRequest, CotizacionRequest, Cotizacion, ProductoCatalogo
├── client       CatalogoClient (llama a catalogo-service)
├── services     ReservaService(Impl), CotizacionService, AlmacenComprobantes
└── controller   ReservaController, CotizacionController, ManejadorErrores
```

## Pruebas (JUnit 5 + Mockito)

En cada carpeta: `./mvnw test` (no necesitan base de datos).

- `catalogo-service`: 8 pruebas (servicio y controller).
- `reservas-service`: 23 pruebas (reservas, cotización con el catálogo simulado, controller).

## Despliegue en AWS EC2 (1 GB de RAM)

1. Crear la instancia (Ubuntu), instalar Docker: `sudo apt update && sudo apt install -y docker.io docker-compose-v2 git`
2. Activar 1 GB de *swap* (recomendado con 1 GB de RAM):
   ```bash
   sudo fallocate -l 1G /swapfile && sudo chmod 600 /swapfile && sudo mkswap /swapfile && sudo swapon /swapfile
   echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
   ```
3. Clonar este repositorio, crear el `.env` y levantar:
   - **Con HTTPS (recomendado, lo pide el diagrama de la rúbrica):** crear un dominio gratis en
     [duckdns.org](https://www.duckdns.org) que apunte a la IP pública de la EC2, ponerlo en `DOMINIO` del `.env` y ejecutar
     `sudo docker compose --profile https up -d --build`. Caddy obtiene el certificado solo.
     La app usa una sola dirección: `https://DOMINIO/`.
   - **Sin dominio (solo pruebas):** `sudo docker compose up -d --build` y usar `http://IP:8081/` y `http://IP:8082/`
     (solo funciona con el APK *debug*).
4. *Security Group*: abrir 22 (SSH), 80 y 443 (HTTPS). No abrir el 5434 de Postgres.

Memoria medida con Docker: catálogo ~190 MB, reservas ~205 MB, Postgres ~55 MB (≈450 MB en total).
`docker-compose.yml` limita cada contenedor para que no se pasen.
