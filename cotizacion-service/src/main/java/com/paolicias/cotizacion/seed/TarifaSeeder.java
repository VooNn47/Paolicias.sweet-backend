package com.paolicias.cotizacion.seed;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.paolicias.cotizacion.entities.Tarifa;
import com.paolicias.cotizacion.repository.TarifaRepository;

// Carga los 10 tramos de tarifa la primera vez (tabla vacía).
// 1-10 -> $30.000, 11-20 -> $50.000, ... 91-100 -> $220.000
@Component
public class TarifaSeeder implements CommandLineRunner {

    private static final int[] MONTOS = {
            30000, 50000, 80000, 100000, 120000,
            140000, 160000, 180000, 200000, 220000
    };

    private final TarifaRepository tarifaRepo;

    public TarifaSeeder(TarifaRepository tarifaRepository) {
        this.tarifaRepo = tarifaRepository;
    }

    @Override
    public void run(String... args) {
        if (tarifaRepo.count() == 0) {
            List<Tarifa> tarifas = new ArrayList<>();
            for (int i = 0; i < MONTOS.length; i++) {
                tarifas.add(new Tarifa(null, i * 10 + 1, (i + 1) * 10, MONTOS[i]));
            }
            tarifaRepo.saveAll(tarifas);
        }
    }
}
