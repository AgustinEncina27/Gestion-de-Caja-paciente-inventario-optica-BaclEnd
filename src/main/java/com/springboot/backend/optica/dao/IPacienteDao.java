package com.springboot.backend.optica.dao;



import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.backend.optica.modelo.Paciente;

public interface IPacienteDao extends JpaRepository<Paciente, Long> {
	
	public Page<Paciente> findByNombreCompleto (String nombreCompleto, Pageable pageable);
	
	public Page<Paciente> findByDocumento (String documento, Pageable pageable);
	
    Optional<Paciente> findByFicha(Long ficha);
    
    @Query("SELECT MAX(p.ficha) FROM Paciente p")
    Long findMaxFicha();
    
    @Query("SELECT COUNT(p) > 0  FROM Paciente p WHERE p.documento = :documento")
	boolean existsByDocumento(@Param("documento") String documento);

}
