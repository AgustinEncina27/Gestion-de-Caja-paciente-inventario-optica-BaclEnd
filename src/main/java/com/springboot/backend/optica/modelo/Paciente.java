package com.springboot.backend.optica.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "pacientes")
@Data
public class Paciente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100)
    private String nombreCompleto;
    
    @Column(unique = true, nullable = false)
    private long ficha;
    
    @Column
    private String direccion;

    @Column(length = 20)
    private String celular;

    @Column(length = 20)
    private String genero;

 // Relación con Graduacion
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"paciente", "hibernateLazyInitializer", "handler"})
    private List<Graduacion> graduaciones;

    @Column(length = 20)
    private String documento;

    @Column(length = 100)
    private String correo;

    @Column(length = 100)
    private String medico;

    @Column(nullable = false, name = "creado_en")
    private LocalDate creadoEn;

    @Column(nullable = false, name = "ultima_actualizacion")
    private LocalDate ultimaActualizacion;

    // Relación con Local
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    @JsonIgnoreProperties({"productoLocales","hibernateLazyInitializer", "handler"})
    private Local local;

    private static final long serialVersionUID = 1L;
}
