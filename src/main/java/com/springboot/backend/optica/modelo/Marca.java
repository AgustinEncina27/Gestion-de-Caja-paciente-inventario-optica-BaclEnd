package com.springboot.backend.optica.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


import lombok.Data;

@Data
@Entity
@Table(name="marcas")
public class Marca implements Serializable {
	 
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@NotEmpty
	@Size(max=40,message = "the maximum is 40 characters")
	@Column(nullable=false)
	private String nombre;
		
	private static final long serialVersionUID = 1L;
	
}
