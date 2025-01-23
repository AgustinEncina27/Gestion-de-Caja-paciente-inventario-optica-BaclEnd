package com.springboot.backend.optica.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.backend.optica.modelo.Categoria;
import com.springboot.backend.optica.modelo.Producto;

public interface ICategoriaService {
	public List<Categoria> findAllCategory();
	
	public Page<Producto> getProductosPorCategoria(Pageable pageable,Long categoriaId);
	
	public Page<Producto> findByGeneroAndCetegoria(String generoSeleccionado,Long categoriaId, Pageable pageable);
	
	public Categoria findById(Long id);
	
	public Categoria save(Categoria categoria);
	
	public void deleteCategory(Long id);
}
