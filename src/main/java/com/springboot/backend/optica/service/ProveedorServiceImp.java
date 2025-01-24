package com.springboot.backend.optica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.dao.IProveedorDao;
import com.springboot.backend.optica.modelo.Proveedor;

@Service
public class ProveedorServiceImp implements IProveedorService{

	@Autowired
	private IProveedorDao proveedorDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Proveedor> findAllProveedor() {
		return  proveedorDao.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
	}
	
	@Override
	@Transactional(readOnly = true)
	public Proveedor findById(Long id) {
		return proveedorDao.findById(id).orElse(null);
	}
	
		
	@Override
	@Transactional
	public Proveedor save(Proveedor proveedor) {
		return proveedorDao.save(proveedor);
	}
	
	@Override
	@Transactional
	public void deleteProveedor(Long id) {
		proveedorDao.desasociarProductosDelProveedor(id);
		Proveedor proveedorEliminar= proveedorDao.findById(id).orElse(null);
		proveedorDao.delete(proveedorEliminar);
	}

	

	

	
}
