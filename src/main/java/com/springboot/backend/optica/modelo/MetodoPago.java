package com.springboot.backend.optica.modelo;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "metodos_pago")
@Data
public class MetodoPago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nombre; // Ejemplo: Efectivo, Tarjeta, Transferencia
    
    private static final long serialVersionUID = 1L;
}
