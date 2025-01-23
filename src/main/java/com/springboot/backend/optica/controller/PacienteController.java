package com.springboot.backend.optica.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.backend.optica.modelo.Paciente;
import com.springboot.backend.optica.service.IPacienteService;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class PacienteController {
	
	@Autowired
	private IPacienteService pacienteService;
		
	// Obtener todos los pacientes
	@GetMapping("/pacientes")
	public List<Paciente> getAllPacientes() {
	    return pacienteService.findAllPaciente();
	}

	// Obtener pacientes paginados
	@GetMapping("/pacientes/paginado/{page}")
	public Page<Paciente> getAllPacientesPaginados(@PathVariable Integer page) {
	    Pageable pageable = PageRequest.of(page, 12);
	    return pacienteService.findAllPaciente(pageable);
	}

	// Buscar pacientes por nombre con paginación
	@GetMapping("/pacientes/buscar/nombre")
	public Page<Paciente> buscarPacientesPorNombre(
	        @RequestParam String nombre,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "12") int size) {

	    Pageable pageable = PageRequest.of(page, size);
	    return pacienteService.findByNombre(nombre, pageable);
	}

	// Buscar pacientes por documento con paginación
	@GetMapping("/pacientes/buscar/documento/{documento}/page/{page}")
	public Page<Paciente> buscarPacientesPorDocumento(
	        @PathVariable String documento,
	        @PathVariable Integer page) {

	    Pageable pageable = PageRequest.of(page, 12);
	    return pacienteService.findByDocumento(documento, pageable);
	}
	
	@GetMapping("/pacientes/buscar-por-ficha/{ficha}")
	public ResponseEntity<Paciente> getPacientePorFicha(@PathVariable Long ficha) {
	    Optional<Paciente> paciente = pacienteService.findByFicha(ficha);
	    return paciente.map(ResponseEntity::ok)
	                   .orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping("/pacientes/id/{id}")
	public ResponseEntity<?> getPacientePorId(@PathVariable Long id) {
	    Paciente paciente = null;
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        paciente = pacienteService.findById(id);
	    } catch(DataAccessException e) {
	        response.put("mensaje", "Error al consultar la base de datos");
	        response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
	    if(paciente == null) {
	        response.put("mensaje", "El paciente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	    }
	    return new ResponseEntity<>(paciente, HttpStatus.OK);
	}

	
	@Secured("ROLE_ADMIN")
	@PostMapping("/pacientes")
	public ResponseEntity<?> create(@Valid @RequestBody Paciente paciente, BindingResult result) {
		Paciente pacienteNew = null;
		Map<String, Object> response = new HashMap<>();
		
		 // Actualizar la colección productoLocales sin reemplazarla
        if (paciente.getGraduaciones() != null) {
        	paciente.getGraduaciones().forEach(graduacion -> {
        		graduacion.setPaciente(paciente);
            });
        }
		
		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El archivo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// Validar existencia de producto por modelo y marca
	    if (pacienteService.existsByDocumento(paciente.getDocumento())) {
	        response.put("mensaje", "Ya existe un paciente con el mismo documento");
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
		
		try {
			pacienteNew = pacienteService.save(paciente);
			
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la inserción en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El paciente ha sido creado exitosamente.!");
		response.put("paciente", pacienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
		
	@Secured("ROLE_ADMIN")
	@PutMapping("/pacientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Paciente paciente, BindingResult result, @PathVariable Long id) {
		Paciente currentPaciente = pacienteService.findById(id);

		Paciente pacienteUpdated = null;

		Map<String, Object> response = new HashMap<>();

		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El archivo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if (currentPaciente == null) {
			response.put("mensaje", "Error: No se pudo editar el paciente ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentPaciente.setNombreCompleto(paciente.getNombreCompleto());
			currentPaciente.setDireccion(paciente.getDireccion());
			currentPaciente.setCelular(paciente.getCelular());
			currentPaciente.setGenero(paciente.getGenero());
			currentPaciente.setLocal(paciente.getLocal());
			currentPaciente.setDocumento(paciente.getDocumento());
			currentPaciente.setCorreo(paciente.getCorreo());
			currentPaciente.setMedico(paciente.getMedico());
			currentPaciente.setCreadoEn(paciente.getCreadoEn());
			currentPaciente.setUltimaActualizacion(paciente.getUltimaActualizacion());
			
			 // Limpiar y actualizar graduaciones
		    currentPaciente.getGraduaciones().clear();
		    currentPaciente.getGraduaciones().addAll(paciente.getGraduaciones());
			
		    // Actualizar la colección productoLocales sin reemplazarla
	        if (paciente.getGraduaciones() != null) {
	        	currentPaciente.getGraduaciones().clear();
	        	paciente.getGraduaciones().forEach(graduacion -> {
	        		graduacion.setPaciente(currentPaciente);
	        		currentPaciente.getGraduaciones().add(graduacion);
	            });
	        }
		    
			pacienteUpdated = pacienteService.save(currentPaciente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el paciente en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "el paciente ha sido actualizado!");
		response.put("paciente", pacienteUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured("ROLE_ADMIN")
	@DeleteMapping("/pacientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			pacienteService.deletePaciente(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error: no se pudo eliminar el paciente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "Se elimino el paciente con éxito!");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
}
