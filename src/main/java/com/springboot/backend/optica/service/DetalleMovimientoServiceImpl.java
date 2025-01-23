package com.springboot.backend.optica.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.dao.IDetalleMovimientoDao;
import com.springboot.backend.optica.modelo.DetalleMovimiento;

@Service
public class DetalleMovimientoServiceImpl implements IDetalleMovimientoService {

	@Autowired
    private IDetalleMovimientoDao detalleMovimientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DetalleMovimiento> findAll() {
        return detalleMovimientoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleMovimiento findById(Long id) {
        Optional<DetalleMovimiento> result = detalleMovimientoRepository.findById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public DetalleMovimiento save(DetalleMovimiento detalleMovimiento) {
        return detalleMovimientoRepository.save(detalleMovimiento);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        detalleMovimientoRepository.deleteById(id);
    }

}
