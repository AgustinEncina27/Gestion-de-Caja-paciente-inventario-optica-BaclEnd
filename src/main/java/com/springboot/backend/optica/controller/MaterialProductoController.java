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

import com.springboot.backend.optica.modelo.MaterialProducto;
import com.springboot.backend.optica.service.IMaterialProductoService;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class MaterialProductoController {
	@Autowired
	private IMaterialProductoService materialProductoService;
	
	@GetMapping("/materialProducto")
	public List<MaterialProducto> index() {
		return materialProductoService.findAllMarca();
	}
		
	@Secured("ROLE_ADMIN")
	@PostMapping("/materialProducto")
	public ResponseEntity<?> createMaterialProducto(@Valid @RequestBody MaterialProducto materialProducto, BindingResult result) {
		
		MaterialProducto materialProductoNew = null;
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
			materialProductoNew = materialProductoService.save(materialProducto);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la inserción en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El material ha sido creado con éxito!");
		response.put("materialProducto", materialProductoNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured("ROLE_ADMIN")
	@PutMapping("/materialProducto/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody MaterialProducto materialProducto, BindingResult result, @PathVariable Long id) {

		MaterialProducto currentMaterialProducto = materialProductoService.findById(id);

		MaterialProducto materialProductoUpdated = null;

		Map<String, Object> response = new HashMap<>();

		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El archivo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if (currentMaterialProducto == null) {
			response.put("mensaje", "Error: No se pudo editar el material ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {

			currentMaterialProducto.setNombre(materialProducto.getNombre());
					
			materialProductoUpdated = materialProductoService.save(currentMaterialProducto);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el material en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El material ha sido actualizado!");
		response.put("materialProducto", materialProductoUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	
	@Secured("ROLE_ADMIN")
	@DeleteMapping("/materialProducto/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			materialProductoService.deleteMaterialProducto(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el material en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El material ha sido eliminado");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
