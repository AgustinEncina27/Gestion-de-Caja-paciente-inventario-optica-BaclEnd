package com.springboot.backend.optica.service;

import java.util.List;

import com.springboot.backend.optica.modelo.Marca;

public interface IMarcaService {
	public List<Marca> findAllMarca();
		
	public Marca findById(Long id);
	
	public Marca save(Marca marca);
	
	public void deleteMarca(Long id);
}
