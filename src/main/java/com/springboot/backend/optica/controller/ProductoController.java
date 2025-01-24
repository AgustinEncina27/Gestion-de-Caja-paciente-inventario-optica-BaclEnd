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

import com.springboot.backend.optica.modelo.Producto;
import com.springboot.backend.optica.service.IProductoService;



@CrossOrigin
@RestController
@RequestMapping("/api")
public class ProductoController {
	
	@Autowired
	private IProductoService productoService;
		
	@GetMapping("/productos")
	public List<Producto> index() {
		return productoService.findAllProducto();
	}
	
	@GetMapping("/productos/page/{page}")
	public Page<Producto> index(@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page,12);
		return productoService.findAllProducto(pageable);
	}
	
	@GetMapping("/productos/buscar-por-modelo/{modelo}")
	public ResponseEntity<List<Producto>> getProductosPorModelo(@PathVariable String modelo) {
	    List<Producto> productos = productoService.findByModelo(modelo);
	    if (productos.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }
	    return ResponseEntity.ok(productos);
	}
	
	@GetMapping("/productos/page/{genero}/{marca}/{categoria_id}/{page}")
	public Page<Producto> getProductosPorGenero(@PathVariable String genero,@PathVariable Long marca,@PathVariable Long categoria_id,@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page,12);
		if(genero.equals("-1")) {
			genero=null;	
		}
		if(marca == -1) {
			marca=null;		
		}
		if(categoria_id == -1) {
			categoria_id=null;
		}
		return productoService.findByGeneroAndMarcaAndCategoria(genero,marca,categoria_id,pageable);
	}
	
	@GetMapping("/productos/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Producto producto = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			producto = productoService.findById(id);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al consultar la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(producto == null) {
			response.put("mensaje", "El producto ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Producto>(producto, HttpStatus.OK);
	}
		
	@Secured("ROLE_ADMIN")
	@PostMapping("/productos")
	public ResponseEntity<?> create(@Valid @RequestBody Producto producto, BindingResult result) {
		Producto productoNew = null;
		Map<String, Object> response = new HashMap<>();
		
		 // Actualizar la colección productoLocales sin reemplazarla
        if (producto.getProductoLocales() != null) {
            producto.getProductoLocales().forEach(productoLocal -> {
                productoLocal.setProducto(producto);
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
	    if (productoService.existsByModeloAndMarca(producto.getModelo(), producto.getMarca().getId(),(long) 0)) {
	        response.put("mensaje", "Ya existe un producto con el mismo modelo y marca.");
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
		
		try {
			productoNew = productoService.save(producto);
			
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la inserción en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El producto ha sido creado exitosamente.!");
		response.put("producto", productoNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
		
	@Secured("ROLE_ADMIN")
	@PutMapping("/productos/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Producto producto, BindingResult result, @PathVariable Long id) {

		Producto currentProducto = productoService.findById(id);

		Producto productoUpdated = null;

		Map<String, Object> response = new HashMap<>();

		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El archivo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if (currentProducto == null) {
			response.put("mensaje", "Error: No se pudo editar el producto ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		 // Validar existencia de producto por modelo y marca (excluyendo el actual)
		if (productoService.existsByModeloAndMarca(producto.getModelo(), producto.getMarca().getId(),id) ) {
	        response.put("mensaje", "Ya existe otro producto con el mismo modelo y marca.");
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }

		try {

			currentProducto.setMarca(producto.getMarca());
			currentProducto.setMaterial(producto.getMaterial());
			currentProducto.setModelo(producto.getModelo());
			currentProducto.setDescripcion(producto.getDescripcion());
	        currentProducto.setPrecio(producto.getPrecio());
			currentProducto.setCosto(producto.getCosto());
			currentProducto.setGenero(producto.getGenero());
			currentProducto.setMaterial(producto.getMaterial());
			currentProducto.setCategorias(producto.getCategorias());
			currentProducto.setProveedores(producto.getProveedores());
			currentProducto.setCreadoEn(producto.getCreadoEn());
			currentProducto.setUltimaActualizacion(producto.getUltimaActualizacion());
			
			 // Actualizar la colección productoLocales sin reemplazarla
	        if (producto.getProductoLocales() != null) {
	            currentProducto.getProductoLocales().clear();
	            producto.getProductoLocales().forEach(productoLocal -> {
	                productoLocal.setProducto(currentProducto);
	                currentProducto.getProductoLocales().add(productoLocal);
	            });
	        }
			
			productoUpdated = productoService.save(currentProducto);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el producto en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "el producto ha sido actualizado!");
		response.put("producto", productoUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured("ROLE_ADMIN")
	@DeleteMapping("/productos/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			productoService.deleteNote(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error: no se pudo eliminar el producto en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "Se elimino el producto con éxito!");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
}
