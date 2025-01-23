package com.springboot.backend.optica.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.backend.optica.modelo.Paciente;

public interface IPacienteService {
	
	public List<Paciente> findAllPaciente();
	
	public Page<Paciente> findAllPaciente(Pageable pageable);
		
	public Paciente findById(Long id);
	
	public Page<Paciente> findByNombre(String nombre,Pageable pageable);	
	
	public Page<Paciente> findByDocumento(String documento,Pageable pageable);	
	
	public Paciente save(Paciente paciente);
	
	public void deletePaciente(Long id);
	
	Optional<Paciente> findByFicha(Long ficha);

	public boolean existsByDocumento(String documento);
		
}
