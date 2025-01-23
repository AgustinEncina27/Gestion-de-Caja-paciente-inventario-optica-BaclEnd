package com.springboot.backend.optica.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.dao.IMetodoPagoDao;
import com.springboot.backend.optica.modelo.MetodoPago;

@Service
public class MetodoPagoServiceImpl implements IMetodoPagoService {

	 @Autowired
    private IMetodoPagoDao metodoPagoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPago> findAll() {
        return metodoPagoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPago findById(Long id) {
        Optional<MetodoPago> result = metodoPagoRepository.findById(id);
        return result.orElse(null);
    }

    @Override
	@Transactional
    public MetodoPago save(MetodoPago metodoPago) {
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
	@Transactional
    public void delete(Long id) {
        metodoPagoRepository.deleteById(id);
    }

}
