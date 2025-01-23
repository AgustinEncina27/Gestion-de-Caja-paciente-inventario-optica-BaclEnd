package com.springboot.backend.optica.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RestController;

import com.springboot.backend.optica.modelo.Categoria;
import com.springboot.backend.optica.modelo.Producto;
import com.springboot.backend.optica.service.ICategoriaService;


@CrossOrigin
@RestController
@RequestMapping("/api")
public class CategoriaController {

	@Autowired
	private ICategoriaService categoriaService;
	
	@GetMapping("/categoria")
	public List<Categoria> index() {
		return categoriaService.findAllCategory();
	}
	
	@GetMapping("/categoria/page/{id}/{page}")
	public Page<Producto> index(@PathVariable Long id,@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page,12);
		return categoriaService.getProductosPorCategoria(pageable,id);
	}
	
	@GetMapping("/categoria/page/{id}/{genero}/{page}")
	public Page<Producto> index(@PathVariable Long id,@PathVariable String generoSeleccionado,@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page,12);
		return categoriaService.findByGeneroAndCetegoria(generoSeleccionado,id,pageable);
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/categoria")
	public ResponseEntity<?> createCategory(@Valid @RequestBody Categoria categoria, BindingResult result) {
		
		Categoria categoriaNew = null;
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
			categoriaNew = categoriaService.save(categoria);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la inserción en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "La categoria ha sido creada con éxito!");
		response.put("category", categoriaNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured("ROLE_ADMIN")
	@PutMapping("/categoria/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Categoria categoria, BindingResult result, @PathVariable Long id) {

		Categoria currentCategoria = categoriaService.findById(id);

		Categoria categoriaUpdated = null;

		Map<String, Object> response = new HashMap<>();

		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El archivo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if (currentCategoria == null) {
			response.put("mensaje", "Error: No se pudo editar la categoria ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {

			currentCategoria.setNombre(categoria.getNombre());
					
			categoriaUpdated = categoriaService.save(currentCategoria);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la categoria en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "la categoria ha sido actualizada!");
		response.put("categoria", categoriaUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	
	@Secured("ROLE_ADMIN")
	@DeleteMapping("/categoria/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			categoriaService.deleteCategory(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar la categoria en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "La categoria ha sido eliminada");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}