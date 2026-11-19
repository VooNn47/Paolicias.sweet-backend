package com.paolicias.reservas.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.reservas.dto.CalculoInsumos;
import com.paolicias.reservas.dto.LineaProducto;

// Comunicación entre microservicios: calcula y descuenta los insumos de una reserva en insumos-service.
@Component
public class InsumosClient {

    private final RestClient restClient;

    public InsumosClient(@Value("${insumos.url}") String insumosUrl) {
        this.restClient = RestClient.create(insumosUrl);
    }

    // Cuánto se necesita y si alcanza (no cambia el inventario)
    public CalculoInsumos calcular(List<LineaProducto> lineas) {
        try {
            return restClient.post()
                    .uri("/api/insumos/calculo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("lineas", lineas))
                    .retrieve()
                    .body(CalculoInsumos.class);
        } catch (RestClientException e) {
            throw noDisponible();
        }
    }

    // Descuenta los insumos de la reserva. Si falta algo, responde 409 con el detalle de lo que falta.
    public void consumir(Long reservaId, List<LineaProducto> lineas) {
        try {
            restClient.post()
                    .uri("/api/insumos/consumos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("reservaId", reservaId, "lineas", lineas))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, mensajeDe(e));
        } catch (RestClientException e) {
            throw noDisponible();
        }
    }

    // Toma el texto "mensaje" del JSON de error de insumos-service (ej: "Faltan insumos: Harina (2 kg)")
    private static String mensajeDe(HttpClientErrorException e) {
        try {
            Map<?, ?> cuerpo = e.getResponseBodyAs(Map.class);
            if (cuerpo != null && cuerpo.get("mensaje") != null) {
                return cuerpo.get("mensaje").toString();
            }
        } catch (RuntimeException ignorada) {
            // Cuerpo vacío o no es JSON
        }
        return "Faltan insumos para realizar esta reserva";
    }

    private static ResponseStatusException noDisponible() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "El inventario de insumos no está disponible, intenta más tarde");
    }
}
