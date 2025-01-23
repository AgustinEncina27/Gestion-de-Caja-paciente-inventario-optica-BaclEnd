package com.springboot.backend.optica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import com.springboot.backend.optica.modelo.DetalleMovimiento;
import com.springboot.backend.optica.service.IDetalleMovimientoService;

@RestController
@RequestMapping("/api/detalles-movimiento")
@CrossOrigin(origins = {"http://localhost:4200"})
public class DetalleMovimientoController {

    @Autowired
    private IDetalleMovimientoService detalleMovimientoService;

    @GetMapping
    public List<DetalleMovimiento> getAllDetallesMovimiento() {
        return detalleMovimientoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleMovimiento> getDetalleMovimientoById(@PathVariable Long id) {
        DetalleMovimiento detalleMovimiento = detalleMovimientoService.findById(id);
        return detalleMovimiento != null ? ResponseEntity.ok(detalleMovimiento) : ResponseEntity.notFound().build();
    }
    
    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<DetalleMovimiento> createDetalleMovimiento(@RequestBody DetalleMovimiento detalleMovimiento) {
        DetalleMovimiento newDetalleMovimiento = detalleMovimientoService.save(detalleMovimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDetalleMovimiento);
    }
    
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<DetalleMovimiento> updateDetalleMovimiento(@PathVariable Long id, @RequestBody DetalleMovimiento detalleMovimiento) {
        DetalleMovimiento currentDetalleMovimiento = detalleMovimientoService.findById(id);
        if (currentDetalleMovimiento == null) {
            return ResponseEntity.notFound().build();
        }
        detalleMovimiento.setId(id);
        return ResponseEntity.ok(detalleMovimientoService.save(detalleMovimiento));
    }
    
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalleMovimiento(@PathVariable Long id) {
        detalleMovimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
