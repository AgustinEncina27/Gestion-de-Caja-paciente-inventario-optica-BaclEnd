package com.springboot.backend.optica.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "movimientos")
@Data
public class Movimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String tipoMovimiento; // ENTRADA o SALIDA

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private double total;
    
    @Column
    private Double descuento;
    
    @Column(nullable = false)
    private double totalImpuesto;

    @Column
    private String descripcion;

    // Relación con Paciente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = true)
    @JsonIgnoreProperties({"movimientos", "hibernateLazyInitializer", "handler"})
    private Paciente paciente;

    // Relación con DetalleMovimiento
    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"movimiento", "hibernateLazyInitializer", "handler"})
    private List<DetalleMovimiento> detalles;

    // Relación con DetalleAdicional
    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"movimiento", "hibernateLazyInitializer", "handler"})
    private List<DetalleAdicional> detallesAdicionales;
    
    // Lista de pagos realizados
    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"movimiento", "hibernateLazyInitializer", "handler"})
    private List<CajaMovimiento> cajaMovimientos;
    
    // Relación con Local
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    @JsonIgnoreProperties({"productoLocales","hibernateLazyInitializer", "handler"})
    private Local local;
    
    @Enumerated(EnumType.STRING)
    private EstadoMovimiento estadoMovimiento;
    
    public enum EstadoMovimiento {
        PEDIDO_CRISTALES,
        ARMANDO_PEDIDO,
        ANTEOJO_TERMINADO
    }

    private static final long serialVersionUID = 1L;
}
