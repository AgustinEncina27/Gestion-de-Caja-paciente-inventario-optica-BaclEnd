package com.springboot.backend.optica.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.dao.IPacienteDao;
import com.springboot.backend.optica.modelo.Paciente;

@Service
public class PacienteServiceImp implements IPacienteService {
	
	@Autowired
	private IPacienteDao pacienteDao;
		
	@Override
	@Transactional(readOnly = true)
	public List<Paciente> findAllPaciente() {
		return pacienteDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Paciente> findAllPaciente(Pageable pageable) {
		return pacienteDao.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Paciente findById(Long id) {
		return pacienteDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
    public Optional<Paciente> findByFicha(Long ficha) {
        return pacienteDao.findByFicha(ficha);
    }
	
	@Override
	@Transactional(readOnly = true)
	public Page<Paciente> findByNombre(String nombre,Pageable pageable) {
		return pacienteDao.findByNombreCompleto(nombre,pageable);
	}
		
	@Override
	@Transactional(readOnly = true)
	public Page<Paciente> findByDocumento(String documento, Pageable pageable) {
		return pacienteDao.findByDocumento(documento,pageable);
	}
	
	@Override
	@Transactional
	public Paciente save(Paciente paciente) {
        if (paciente.getFicha() == 0) {
            Long maxFicha = pacienteDao.findMaxFicha();
            paciente.setFicha(maxFicha != null ? maxFicha + 1 : 1);
        }
        return pacienteDao.save(paciente);
    }

	@Override
	@Transactional
	public void deletePaciente(Long id) {
		Paciente paciente = pacienteDao.findById(id).get();
		pacienteDao.delete(paciente);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByDocumento(String documento) {
		return pacienteDao.existsByDocumento(documento);
	}

	

}
