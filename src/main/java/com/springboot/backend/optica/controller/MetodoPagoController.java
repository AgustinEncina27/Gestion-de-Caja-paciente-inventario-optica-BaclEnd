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

import com.springboot.backend.optica.modelo.MetodoPago;
import com.springboot.backend.optica.service.IMetodoPagoService;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class MetodoPagoController {
	@Autowired
	private IMetodoPagoService merodoPagoService;
	
	@GetMapping("/metodoPago")
	public List<MetodoPago> index() {
		return merodoPagoService.findAll();
	}
		
	@Secured("ROLE_ADMIN")
	@PostMapping("/metodoPago")
	public ResponseEntity<?> createCategory(@Valid @RequestBody MetodoPago metodoPago, BindingResult result) {
		
		MetodoPago metodoPagoNew = null;
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
			metodoPagoNew = merodoPagoService.save(metodoPago);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la inserción en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "La categoria ha sido creada con éxito!");
		response.put("metodoPago", metodoPagoNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured("ROLE_ADMIN")
	@PutMapping("/metodoPago/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody MetodoPago metodoPago, BindingResult result, @PathVariable Long id) {

		MetodoPago currentMetodoPago = merodoPagoService.findById(id);

		MetodoPago metodoPagoUpdated = null;

		Map<String, Object> response = new HashMap<>();

		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El archivo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if (currentMetodoPago == null) {
			response.put("mensaje", "Error: No se pudo editar la marca ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {

			currentMetodoPago.setNombre(metodoPago.getNombre());
					
			metodoPagoUpdated = merodoPagoService.save(currentMetodoPago);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la marca en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "la marca ha sido actualizada!");
		response.put("metodoPago", metodoPagoUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	
	@Secured("ROLE_ADMIN")
	@DeleteMapping("/metodoPago/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			merodoPagoService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el método de pago en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El método de pago ha sido eliminado");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
