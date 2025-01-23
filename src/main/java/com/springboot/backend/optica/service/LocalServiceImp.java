package com.springboot.backend.optica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.dao.ILocalDao;
import com.springboot.backend.optica.modelo.Local;

@Service
public class LocalServiceImp implements ILocalService {
	
	@Autowired
	private ILocalDao localDao;

	@Override
	@Transactional(readOnly = true)
	public List<Local> findAllLocal() {
		return (List<Local>) localDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Local findById(Long id) {
		return localDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Local save(Local local) {
		return localDao.save(local);
	}

	@Override
	@Transactional
	public void deleteLocal(Long id) {
		Local localAEliminar=localDao.findById(id).orElse(null);
		localDao.delete(localAEliminar);
	}

	

}
