package com.springboot.backend.optica.modelo;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "producto_local")
@Data
public class ProductoLocal implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnoreProperties(value = {"productoLocales","hibernateLazyInitializer", "handler"}, allowSetters = true)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    @JsonIgnoreProperties(value = {"productoLocales","hibernateLazyInitializer", "handler"}, allowSetters = true)
    private Local local;

    @Column(nullable = false)
    private int stock;

    private static final long serialVersionUID = 1L;
}
