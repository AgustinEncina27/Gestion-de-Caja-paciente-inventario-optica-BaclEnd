package com.springboot.backend.optica.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.backend.optica.service.IMovimientoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api/movimientos")
public class PdfController {
	
	@Autowired
    private IMovimientoService movimientoService;


	@GetMapping("/reporte/{idMovimiento}")
	public ResponseEntity<byte[]> generarReporteMovimiento(@PathVariable Long idMovimiento) {
	    try {
	        byte[] pdf = movimientoService.generarReporteMovimiento(idMovimiento);

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDispositionFormData("attachment", "reporte_movimiento_" + idMovimiento + ".pdf");

	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(pdf);
	    } catch (IOException e) {
	        throw new RuntimeException("Error al generar el reporte PDF", e);
	    }
	}
}
