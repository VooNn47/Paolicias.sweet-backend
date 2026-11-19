package com.paolicias.insumos.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.insumos.dto.CalculoInsumos;
import com.paolicias.insumos.dto.LineaProducto;
import com.paolicias.insumos.entities.Consumo;
import com.paolicias.insumos.entities.Insumo;
import com.paolicias.insumos.entities.Receta;
import com.paolicias.insumos.repository.ConsumoRepository;
import com.paolicias.insumos.repository.InsumoRepository;
import com.paolicias.insumos.repository.RecetaRepository;

// Cálculo de insumos de una reserva:
//   necesario(insumo) = Σ por cada producto: unidades × cantidad del insumo por unidad (receta)
// Si para algún insumo necesario > stock, la reserva no se puede realizar con el inventario actual.
@Service
public class InsumoService {

    private final InsumoRepository insumoRepo;
    private final RecetaRepository recetaRepo;
    private final ConsumoRepository consumoRepo;

    public InsumoService(InsumoRepository insumoRepository,
                         RecetaRepository recetaRepository,
                         ConsumoRepository consumoRepository) {
        this.insumoRepo = insumoRepository;
        this.recetaRepo = recetaRepository;
        this.consumoRepo = consumoRepository;
    }

    // Redondeo a 2 decimales (evita resultados como 1.2000000000000002)
    static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public List<Insumo> listar() {
        return insumoRepo.findAllByOrderByNombreAsc();
    }

    public Insumo actualizarStock(Long id, double stock) {
        Insumo insumo = insumoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El insumo no existe"));
        insumo.setStock(redondear(stock));
        return insumoRepo.save(insumo);
    }

    public CalculoInsumos calcular(List<LineaProducto> lineas) {
        // Suma por insumo, conservando el orden en que aparecen
        Map<Long, Insumo> insumos = new LinkedHashMap<>();
        Map<Long, Double> necesario = new LinkedHashMap<>();
        if (lineas != null) {
            for (LineaProducto linea : lineas) {
                for (Receta receta : recetaRepo.findByProductoId(linea.productoId())) {
                    Insumo insumo = receta.getInsumo();
                    insumos.putIfAbsent(insumo.getId(), insumo);
                    necesario.merge(insumo.getId(), linea.unidades() * receta.getCantidadPorUnidad(), Double::sum);
                }
            }
        }

        List<CalculoInsumos.InsumoNecesario> detalle = new ArrayList<>();
        for (Map.Entry<Long, Double> e : necesario.entrySet()) {
            Insumo insumo = insumos.get(e.getKey());
            double requerido = redondear(e.getValue());
            double stock = redondear(insumo.getStock());
            double faltante = redondear(Math.max(0, requerido - stock));
            detalle.add(new CalculoInsumos.InsumoNecesario(
                    insumo.getId(), insumo.getNombre(), insumo.getUnidad(), requerido, stock, faltante));
        }
        // Primero los que faltan, después por nombre
        detalle.sort(Comparator.comparing((CalculoInsumos.InsumoNecesario i) -> i.faltante() == 0)
                .thenComparing(CalculoInsumos.InsumoNecesario::nombre));

        boolean alcanza = detalle.stream().allMatch(i -> i.faltante() == 0);
        return new CalculoInsumos(alcanza, detalle);
    }

    // Descuenta del stock los insumos de una reserva. Si falta algo, no descuenta nada (409).
    // Si la reserva ya se descontó antes, no vuelve a descontar.
    @Transactional
    public CalculoInsumos consumir(Long reservaId, List<LineaProducto> lineas) {
        CalculoInsumos calculo = calcular(lineas);
        if (consumoRepo.existsById(reservaId)) {
            return calculo;
        }
        if (!calculo.alcanza()) {
            String faltan = calculo.insumos().stream()
                    .filter(i -> i.faltante() > 0)
                    .map(i -> i.nombre() + " (" + formatear(i.faltante()) + " " + i.unidad() + ")")
                    .collect(Collectors.joining(", "));
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Faltan insumos: " + faltan);
        }
        for (CalculoInsumos.InsumoNecesario i : calculo.insumos()) {
            Insumo insumo = insumoRepo.findById(i.insumoId()).orElseThrow();
            insumo.setStock(redondear(insumo.getStock() - i.necesario()));
            insumoRepo.save(insumo);
        }
        String detalle = calculo.insumos().stream()
                .map(i -> i.nombre() + " " + formatear(i.necesario()) + " " + i.unidad())
                .collect(Collectors.joining("; "));
        consumoRepo.save(new Consumo(reservaId, LocalDateTime.now(), detalle));
        return calculo;
    }

    private static String formatear(double valor) {
        // Con coma decimal, como se escribe en Chile: 0.5 -> "0,5"
        return valor == Math.floor(valor) ? String.valueOf((long) valor) : String.valueOf(valor).replace('.', ',');
    }
}
