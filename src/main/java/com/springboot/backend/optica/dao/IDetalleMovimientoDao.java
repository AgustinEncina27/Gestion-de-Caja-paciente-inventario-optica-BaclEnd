package com.springboot.backend.optica.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.backend.optica.modelo.DetalleMovimiento;

public interface IDetalleMovimientoDao extends JpaRepository<DetalleMovimiento, Long> {
}
