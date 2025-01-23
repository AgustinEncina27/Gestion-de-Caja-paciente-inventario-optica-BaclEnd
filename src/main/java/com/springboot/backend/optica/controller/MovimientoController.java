package com.springboot.backend.optica.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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

import com.springboot.backend.optica.modelo.Movimiento;
import com.springboot.backend.optica.service.IMovimientoService;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = {"http://localhost:4200"})
public class MovimientoController {

	@Autowired
    private IMovimientoService movimientoService;

    @GetMapping
    public List<Movimiento> getAllMovimientos() {
        return movimientoService.findAll();
    }
    
 // Obtener pacientes paginados
 	@GetMapping("/paginado/{page}")
 	public Page<Movimiento> getAllMovimientosPaginados(@PathVariable Integer page) {
 	    Pageable pageable = PageRequest.of(page, 12);
 	    return movimientoService.findAllMovimiento(pageable);
 	}
 	
 	@GetMapping("/filtrar")
 	public ResponseEntity<Page<Movimiento>> filtrarMovimientos(
 	        @RequestParam(required = false) Long idLocal,
 	        @RequestParam(required = false) String tipoMovimiento,
 	        @RequestParam(required = false) Long nroFicha,
 	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
 	        @RequestParam(required = false) String metodoPago,
 	        @RequestParam(defaultValue = "0") int page,
 	        @RequestParam(defaultValue = "10") int size) {
 		 		
 	    Pageable pageable = PageRequest.of(page, size);

 	    // Valida que todos los parámetros opcionales sean manejados correctamente
 	    Page<Movimiento> movimientos = movimientoService.filtrarMovimientos(idLocal, tipoMovimiento, nroFicha, fecha, metodoPago, pageable);
 	    return ResponseEntity.ok(movimientos);
 	}

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> getMovimientoById(@PathVariable Long id) {
        Movimiento movimiento = movimientoService.findById(id);
        return movimiento != null ? ResponseEntity.ok(movimiento) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/totales")
    public Map<String, Double> calcularTotales(@RequestParam(required = false) Long idLocal) {
        return movimientoService.calcularTotales(idLocal);
    }
    
    @GetMapping("/local/{idLocal}/page/{page}")
    public ResponseEntity<?> getMovimientosPorLocalPaginados(
            @PathVariable Long idLocal,
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movimiento> movimientos = movimientoService.findByLocalIdPaginated(idLocal, pageable);
        return ResponseEntity.ok(movimientos);
    }
    
    @GetMapping("/buscar/entre-fechas")
    public ResponseEntity<Page<Movimiento>> getMovimientosEntreFechas(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam int page,
            @RequestParam int size) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        PageRequest pageable = PageRequest.of(page, size);
        Page<Movimiento> movimientos = movimientoService.findByFechaBetween(inicio, fin, pageable);
        return ResponseEntity.ok(movimientos);
    }
    
    @GetMapping("/buscar/tipo")
    public ResponseEntity<Page<Movimiento>> getMovimientosPorTipo(
            @RequestParam String tipo,
            @RequestParam int page,
            @RequestParam int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Movimiento> movimientos = movimientoService.findByTipoMovimiento(tipo, pageable);
        return ResponseEntity.ok(movimientos);
    }
    
    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<Movimiento> createMovimiento(@RequestBody Movimiento movimiento) {
        Movimiento newMovimiento = movimientoService.create(movimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMovimiento);
    }
    
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<Movimiento> updateMovimiento(@PathVariable Long id, @RequestBody Movimiento movimiento) {
    	try {
            Movimiento updatedMovimiento = movimientoService.update(id, movimiento);
            return ResponseEntity.ok(updatedMovimiento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Manejo de errores
        }
    }
    
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovimiento(@PathVariable Long id) {
        movimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
