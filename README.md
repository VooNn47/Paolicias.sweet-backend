# Paolicias.Sweet – Backend

Microservicios Spring Boot del proyecto Paolicias.Sweet (DSY1105, Duoc UC). Datos ficticios.

| Servicio | Puerto | Tabla | Estado |
|---|---|---|---|
| `catalogo-service` | 8081 | `productos` | En desarrollo |
| `reservas-service` | 8082 | `reservas` | En desarrollo |

Todos usan un mismo Postgres (`paolicias_db`) y cada servicio es dueño de sus tablas.

## Cómo ejecutar (Docker)

1. Instalar y abrir **Docker Desktop**.
2. Copiar `.env.example` como `.env` y cambiar la clave. **El `.env` no se sube a GitHub.**
3. En esta carpeta: `docker compose up -d --build`
4. Probar en Postman o en el navegador: `http://localhost:8081/api/productos` (debe devolver 11 productos).
5. Para detener: `docker compose down`

## Endpoints de catalogo-service

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/productos` | Lista de productos |
| GET | `/api/productos/{id}` | Un producto, o 404 |
| POST | `/api/productos` | Crea un producto (201) |

## Estructura

```
catalogo-service/src/main/java/com/paolicias/catalogo
├── entities     Producto
├── repository   ProductoRepository
├── services     ProductoService, ProductoServiceImpl
├── controller   ProductoController
└── seed         ProductoSeeder (11 productos iniciales)
```

## Endpoints de reservas-service

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/reservas` | Lista de reservas |
| GET | `/api/reservas/{id}` | Una reserva, o 404 |
| POST | `/api/reservas` | Crea una reserva (201) |
