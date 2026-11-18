package com.paolicias.reservas.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.reservas.dto.ProductoCatalogo;

// Comunicación entre microservicios: pide los precios a catalogo-service por HTTP.
// Así el total de la reserva siempre usa los precios reales del catálogo.
@Component
public class CatalogoClient {

    private final RestClient restClient;

    public CatalogoClient(@Value("${catalogo.url}") String catalogoUrl) {
        this.restClient = RestClient.create(catalogoUrl);
    }

    // Devuelve el producto, o vacío si no existe (404)
    public Optional<ProductoCatalogo> obtenerProducto(Long id) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/api/productos/{id}", id)
                    .retrieve()
                    .body(ProductoCatalogo.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "El catálogo no está disponible, intenta más tarde");
        }
    }
}
