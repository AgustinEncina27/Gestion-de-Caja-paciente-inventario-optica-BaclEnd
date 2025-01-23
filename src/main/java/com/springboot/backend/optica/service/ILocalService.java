package com.springboot.backend.optica.service;

import java.util.List;

import com.springboot.backend.optica.modelo.Local;

public interface ILocalService {
	public List<Local> findAllLocal();
		
	public Local findById(Long id);
	
	public Local save(Local local);
	
	public void deleteLocal(Long id);
}
