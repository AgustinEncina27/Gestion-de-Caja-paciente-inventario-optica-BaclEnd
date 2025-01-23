package com.springboot.backend.optica.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "detalle_adionales")
@Data
public class DetalleAdicional {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@Column
    private String descripcion;

    @Column(nullable = false)
    private float subtotal;
	
	// Relación con el movimiento (encabezado)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id", nullable = false)
    @JsonIgnoreProperties({"detallesAdicionales", "cajaMovimientos", "hibernateLazyInitializer", "handler"})
    private Movimiento movimiento;
}
