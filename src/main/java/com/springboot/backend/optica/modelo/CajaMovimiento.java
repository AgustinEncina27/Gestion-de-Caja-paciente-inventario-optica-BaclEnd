package com.springboot.backend.optica.modelo;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "caja_movimientos")
@Data
public class CajaMovimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double monto;

    @Column(nullable = false)
    private double montoImpuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MetodoPago metodoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id", nullable = false)
    @JsonIgnoreProperties({"detalles", "cajaMovimientos", "hibernateLazyInitializer", "handler"})
    private Movimiento movimiento;

    private static final long serialVersionUID = 1L;
}
