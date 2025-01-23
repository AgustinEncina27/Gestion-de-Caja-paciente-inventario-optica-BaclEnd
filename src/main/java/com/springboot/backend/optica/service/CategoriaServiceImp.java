package com.springboot.backend.optica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.dao.ICategoriaDao;
import com.springboot.backend.optica.modelo.Categoria;
import com.springboot.backend.optica.modelo.Producto;

@Service
public class CategoriaServiceImp implements ICategoriaService{

	@Autowired
	private ICategoriaDao categoriaDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Categoria> findAllCategory() {
		return (List<Categoria>) categoriaDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Categoria findById(Long id) {
		return categoriaDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Producto> getProductosPorCategoria(Pageable pageable,Long categoriaId){
		return categoriaDao.findProductosByCategoriaId(categoriaId, pageable);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Producto> findByGeneroAndCetegoria(String generoSeleccionado, Long categoriaId, Pageable pageable) {
		return categoriaDao.findByGenero(generoSeleccionado,categoriaId, pageable);
	}
	
	@Override
	@Transactional
	public Categoria save(Categoria category) {
		return categoriaDao.save(category);
	}
	
	@Override
	@Transactional
	public void deleteCategory(Long id) {
		categoriaDao.desasociarProductosDeLaCategoria(id);
		Categoria categoriaEliminar= categoriaDao.findById(id).orElse(null);
		categoriaDao.delete(categoriaEliminar);
	}

	

	

	
}
