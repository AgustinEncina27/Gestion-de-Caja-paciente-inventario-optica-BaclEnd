package com.springboot.backend.optica.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.backend.optica.modelo.Movimiento;

@Repository
public interface IMovimientoDao extends JpaRepository<Movimiento, Long> {
	
	Page<Movimiento> findByTipoMovimiento(String tipo, Pageable pageable);
    
	Page<Movimiento> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);
	
	@Query("SELECT m FROM Movimiento m WHERE m.local.id = :idLocal")
	List<Movimiento> findByLocalId(Long idLocal);
	
	@Query("SELECT m FROM Movimiento m WHERE m.local.id = :idLocal")
	Page<Movimiento> findByLocalId(Long idLocal, Pageable pageable);
	
	@Query("SELECT DISTINCT m FROM Movimiento m " +
		       "LEFT JOIN m.cajaMovimientos p " +
		       "LEFT JOIN p.metodoPago metodo " +
		       "LEFT JOIN m.paciente paciente " +
		       "WHERE (:idLocal IS NULL OR m.local.id = :idLocal) AND " +
		       "(:tipoMovimiento IS NULL OR m.tipoMovimiento = :tipoMovimiento) AND " +
		       "(:nroFicha IS NULL OR paciente.ficha = :nroFicha) AND " +
		       "(cast(:fecha as date) IS NULL OR  m.fecha = :fecha ) AND " +
		       "(:metodoPago IS NULL OR metodo.nombre = :metodoPago OR p IS NULL) " +
		       "ORDER BY m.fecha DESC")
		Page<Movimiento> filtrarMovimientos(
		        @Param("idLocal") Long idLocal,
		        @Param("tipoMovimiento") String tipoMovimiento,
		        @Param("nroFicha") Long nroFicha,
		        @Param("fecha") LocalDate fecha,
		        @Param("metodoPago") String metodoPago,
		        Pageable pageable
		);
}
