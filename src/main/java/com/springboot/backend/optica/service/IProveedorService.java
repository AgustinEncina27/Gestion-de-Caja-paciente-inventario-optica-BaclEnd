package com.springboot.backend.optica.service;

import java.util.List;


import com.springboot.backend.optica.modelo.Proveedor;

public interface IProveedorService {
	
	public List<Proveedor> findAllProveedor();
		
	public Proveedor findById(Long id);
	
	public Proveedor save(Proveedor proveedor);
	
	public void deleteProveedor(Long id);
}
