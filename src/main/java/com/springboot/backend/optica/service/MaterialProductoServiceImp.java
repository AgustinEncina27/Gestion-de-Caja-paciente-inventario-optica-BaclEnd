package com.springboot.backend.optica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.dao.IMaterialProductoDao;
import com.springboot.backend.optica.modelo.MaterialProducto;

@Service
public class MaterialProductoServiceImp implements IMaterialProductoService {
	
	@Autowired
	private IMaterialProductoDao materialProductoDao;

	@Override
	@Transactional(readOnly = true)
	public List<MaterialProducto> findAllMarca() {
		return materialProductoDao.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
	}

	@Override
	@Transactional(readOnly = true)
	public MaterialProducto findById(Long id) {
		return materialProductoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public MaterialProducto save(MaterialProducto materialProducto) {
		return materialProductoDao.save(materialProducto);
	}

	@Override
	@Transactional
	public void deleteMaterialProducto(Long id) {
		MaterialProducto materialProductoAEliminar=materialProductoDao.findById(id).orElse(null);
		materialProductoDao.delete(materialProductoAEliminar);
	}

	

}
