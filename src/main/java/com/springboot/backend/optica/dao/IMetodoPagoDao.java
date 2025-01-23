package com.springboot.backend.optica.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.backend.optica.modelo.MetodoPago;

@Repository
public interface IMetodoPagoDao extends JpaRepository<MetodoPago, Long> {
}
