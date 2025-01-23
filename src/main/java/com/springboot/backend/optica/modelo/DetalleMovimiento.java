package com.springboot.backend.optica.modelo;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "detalle_movimientos")
@Data
public class DetalleMovimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int cantidad; // Cantidad del producto

    @Column(nullable = false)
    private float precioUnitario;

    @Column(nullable = false)
    private float subtotal;

    // Relación con el movimiento (encabezado)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id", nullable = false)
    @JsonIgnoreProperties({"detalles", "cajaMovimientos", "hibernateLazyInitializer", "handler"})
    private Movimiento movimiento;

    // Relación con el producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonIgnoreProperties({"detalles","hibernateLazyInitializer", "handler"})
    private Producto producto;

    private static final long serialVersionUID = 1L;
}
