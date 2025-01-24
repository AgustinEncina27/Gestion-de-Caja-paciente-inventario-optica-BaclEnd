package com.springboot.backend.optica.modelo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name="proveedores")
public class Proveedor implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@NotEmpty
	@Size(max=40,message = "the maximum is 40 characters")
	@Column(nullable=false)
	private String nombre;
	
	@Column(length = 20)
    private String celular;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "proveedores",fetch=FetchType.LAZY)
	private List<Producto> productos;
	
	private static final long serialVersionUID = 1L;
	
}
