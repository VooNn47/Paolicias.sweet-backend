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

import com.paolicias.reservas.dto.Cotizacion;

// Comunicación entre microservicios: pide el total del evento a cotizacion-service.
// Así el precio de la reserva siempre lo calcula el servidor con las tarifas y precios reales.
@Component
public class CotizacionClient {

    private final RestClient restClient;

    public CotizacionClient(@Value("${cotizacion.url}") String cotizacionUrl) {
        this.restClient = RestClient.create(cotizacionUrl);
    }

    public Cotizacion cotizar(int invitados, List<Long> productoIds) {
        try {
            return restClient.post()
                    .uri("/api/cotizacion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("invitados", invitados, "productoIds", productoIds == null ? List.of() : productoIds))
                    .retrieve()
                    .body(Cotizacion.class);
        } catch (HttpClientErrorException.BadRequest e) {
            // Por ejemplo, un producto que no existe en el catálogo
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hay productos que ya no están en el catálogo");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "La cotización no está disponible, intenta más tarde");
        }
    }
}
