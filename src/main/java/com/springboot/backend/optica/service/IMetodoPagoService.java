package com.springboot.backend.optica.service;

import java.util.List;

import com.springboot.backend.optica.modelo.MetodoPago;

public interface IMetodoPagoService {
	List<MetodoPago> findAll();
    MetodoPago findById(Long id);
    MetodoPago save(MetodoPago metodoPago);
    void delete(Long id);
}
