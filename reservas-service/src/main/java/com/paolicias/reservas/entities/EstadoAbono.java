package com.paolicias.reservas.entities;

// Textos de los estados del abono: son los mismos que muestra la app.
//
//   Pendiente --(cliente adjunta)--> En revisión --(admin valida)--> Pagado 50% --(admin)--> Completado
//       ^                                 |
//       +--------(admin rechaza)----------+
public final class EstadoAbono {

    public static final String PENDIENTE = "Pendiente";
    public static final String EN_REVISION = "En revisión";
    public static final String PAGADO = "Pagado 50%";
    public static final String COMPLETADO = "Completado";

    private EstadoAbono() {
    }
}
