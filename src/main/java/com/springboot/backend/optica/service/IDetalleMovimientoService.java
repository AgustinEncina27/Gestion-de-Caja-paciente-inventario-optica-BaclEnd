package com.springboot.backend.optica.service;

import java.util.List;

import com.springboot.backend.optica.modelo.DetalleMovimiento;

public interface IDetalleMovimientoService {
	List<DetalleMovimiento> findAll();
    DetalleMovimiento findById(Long id);
    DetalleMovimiento save(DetalleMovimiento detalleMovimiento);
    void delete(Long id);
}
