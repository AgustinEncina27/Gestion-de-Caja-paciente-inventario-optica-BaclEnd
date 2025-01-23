package com.springboot.backend.optica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import com.springboot.backend.optica.service.ProductoServiceImp;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ProductoServiceImp productoService;

    @GetMapping("/stock/{localId}")
    public ResponseEntity<byte[]> exportStockToExcel(@PathVariable Long localId) throws IOException {
        // Llama al servicio para generar el Excel
        byte[] excelData = productoService.exportStockToExcel(localId);

        // Configurar la respuesta HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=stock_local.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}