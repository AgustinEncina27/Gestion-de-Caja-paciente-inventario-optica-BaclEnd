package com.springboot.backend.optica.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "graduaciones")
@Data
public class Graduacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal esferico;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal cilindrico;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal eje;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal adicion;
    
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal cerca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Ojo ojo; // Enum para diferenciar entre ojo derecho e izquierdo

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
    
    @Column(nullable = false, name = "fecha_graduacion")
    private LocalDate fechaGraduacion;
    
    private static final long serialVersionUID = 1L;

    public enum Ojo {
        DERECHO,
        IZQUIERDO
    }
}
