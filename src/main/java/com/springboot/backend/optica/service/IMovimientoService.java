package com.springboot.backend.optica.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.backend.optica.modelo.Movimiento;

public interface IMovimientoService {
	List<Movimiento> findAll();
    Movimiento findById(Long id);
    Movimiento create(Movimiento movimiento);
    Movimiento update(Long id,Movimiento movimiento);
    void delete(Long id);
    Page<Movimiento> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);
    Page<Movimiento> findByTipoMovimiento(String tipo, Pageable pageable);
	Page<Movimiento> findAllMovimiento(Pageable pageable);
	Map<String, Double> calcularTotales(Long idLocal);
	Page<Movimiento> findByLocalIdPaginated(Long idLocal, Pageable pageable);
	Page<Movimiento> filtrarMovimientos(Long idLocal, String tipoMovimiento, Long nroFicha, LocalDate fecha, String metodoPago, Pageable pageable);
	byte[] generarReporteMovimiento(Long idMovimiento)throws IOException;
}
