package com.springboot.backend.optica.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnoreProperties(value = {"productos", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ManyToOne
    private Marca marca;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"producto", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    private List<ProductoLocal> productoLocales;

    @Column(length = 50)
    private String modelo;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private float precio;

    @Column
    private Float costo;

    @JsonIgnoreProperties(value = {"productos", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ManyToOne
    private MaterialProducto material;

    @Column(length = 20)
    private String genero;

    @JsonIgnoreProperties(value = {"productos", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "producto_categoria",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias;
    
    @JsonIgnoreProperties(value = {"productos", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "producto_proveedor",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "proveedor_id")
    )
    private List<Proveedor> proveedores;

    @Column(nullable = false, name = "creado_en")
    private LocalDate creadoEn;

    @Column(nullable = false, name = "ultima_actualizacion")
    private LocalDate ultimaActualizacion;

    
    private static final long serialVersionUID = 1L;
}
