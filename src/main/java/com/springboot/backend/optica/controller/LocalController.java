package com.springboot.backend.optica.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import org.springframework.web.bind.annotation.RestController;

import com.springboot.backend.optica.modelo.Local;
import com.springboot.backend.optica.service.ILocalService;



@CrossOrigin
@RestController
@RequestMapping("/api")
public class LocalController {
	@Autowired
	private ILocalService localService;
	
	@GetMapping("/local")
	public List<Local> index() {
		return localService.findAllLocal();
	}
		
	@Secured("ROLE_ADMIN")
	@PostMapping("/local")
	public ResponseEntity<?> createLocal(@Valid @RequestBody Local local, BindingResult result) {
		
		Local localNew = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El archivo'" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			localNew = localService.save(local);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la inserción en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El local ha sido creado con éxito!");
		response.put("local", localNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured("ROLE_ADMIN")
	@PutMapping("/local/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Local local, BindingResult result, @PathVariable Long id) {

		Local currentLocal = localService.findById(id);

		Local localUpdated = null;

		Map<String, Object> response = new HashMap<>();

		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El archivo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if (currentLocal == null) {
			response.put("mensaje", "Error: No se pudo editar el local ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {

			currentLocal.setNombre(local.getNombre());
			currentLocal.setDireccion(local.getDireccion());
					
			localUpdated = localService.save(currentLocal);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el local en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El local ha sido actualizada!");
		response.put("local", localUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	
	@Secured("ROLE_ADMIN")
	@DeleteMapping("/local/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			localService.deleteLocal(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el local en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El local ha sido eliminado");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
