package com.springboot.backend.optica.service;

import java.util.List;

import com.springboot.backend.optica.modelo.Marca;
import com.springboot.backend.optica.modelo.MaterialProducto;

public interface IMaterialProductoService {
	public List<MaterialProducto> findAllMarca();
		
	public MaterialProducto findById(Long id);
	
	public MaterialProducto save(MaterialProducto materialProducto);
	
	public void deleteMaterialProducto(Long id);
}
