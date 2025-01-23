package com.springboot.backend.optica.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.dao.ILocalDao;
import com.springboot.backend.optica.dao.IProductoDao;
import com.springboot.backend.optica.dao.IProductoLocalDao;
import com.springboot.backend.optica.modelo.Local;
import com.springboot.backend.optica.modelo.Producto;
import com.springboot.backend.optica.modelo.ProductoLocal;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.springboot.backend.optica.service.ProductoServiceImp;

@Service
public class ProductoServiceImp implements IProductoService {
	
	@Autowired
	private IProductoDao productoDao;
	
	@Autowired
	private IProductoLocalDao productoLocalRepository; 
	
	@Autowired
	private ILocalDao localRepository; 
		
	@Override
	@Transactional(readOnly = true)
	public List<Producto> findAllProducto() {
		return productoDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Producto> findAllProducto(Pageable pageable) {
		Page<Producto> productos = productoDao.findAllProducto(pageable);
	    // Cargar relaciones de productoLocales manualmente
	    productos.getContent().forEach(producto -> producto.getProductoLocales().size());
	    return productos;
	}
	
	@Transactional(readOnly = true)
	public List<ProductoLocal> getStockByLocal(Long localId) {
        return productoLocalRepository.findByLocalId(localId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Producto> findByModelo(String modelo) {
	    return productoDao.findByModelo(modelo);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Producto> findByGeneroAndMarcaAndCategoria(String genero, Long marca, Long categoria, Pageable pageable) {
		return productoDao.findByGeneroAndMarcaAndCategoria(genero,marca,categoria,pageable);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Producto findById(Long id) {
		return productoDao.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public byte[] exportStockToExcel(Long localId) throws IOException {
	    // Obtener todos los productos y el stock del local
	    List<ProductoLocal> productoLocales = productoLocalRepository.findByLocalId(localId);

	    // Crear el archivo Excel
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Workbook workbook = new XSSFWorkbook();

	    // Crear la hoja de Excel
	    Sheet sheet = workbook.createSheet("Stock Local");

	    // Crear encabezados
	    Row headerRow = sheet.createRow(0);
	    headerRow.createCell(0).setCellValue("Modelo");
	    headerRow.createCell(1).setCellValue("Marca");
	    headerRow.createCell(2).setCellValue("Costo");
	    headerRow.createCell(3).setCellValue("Precio");
	    headerRow.createCell(4).setCellValue("Stock");

	    // Llenar datos
	    int rowNum = 1;
	    for (ProductoLocal productoLocal : productoLocales) {
	        
	    	if(productoLocal.getStock()>0) {
	    		 Producto producto = productoLocal.getProducto();
	 	        
	 	        Row row = sheet.createRow(rowNum++);
	 	        row.createCell(0).setCellValue(producto.getModelo());
	 	        row.createCell(1).setCellValue(producto.getMarca().getNombre());
	 	        if(producto.getCosto()!=null) {
	 		        row.createCell(2).setCellValue(producto.getCosto());
	 	        }else {
	 		        row.createCell(2).setCellValue(0);
	 	        }

	 	        row.createCell(3).setCellValue(producto.getPrecio());
	 	        row.createCell(4).setCellValue(productoLocal.getStock());
	    	}
	       
	    }

	    // Ajustar tamaño de columnas
	    for (int i = 0; i < 5; i++) {
	        sheet.autoSizeColumn(i);
	    }

	    // Escribir en el output stream
	    workbook.write(out);
	    workbook.close();

	    return out.toByteArray();
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean existsByModeloAndMarca(String modelo, Long marcaId, Long id) {
	    return productoDao.existsByModeloAndMarca(modelo, marcaId,id);
	}

	@Override
	@Transactional
	public boolean setStockByLocal(Long productoId, Long localId, Integer stock) {
	    Optional<ProductoLocal> productoLocal = productoLocalRepository.findByProductoIdAndLocalId(productoId, localId);
	    if (productoLocal.isPresent()) {
	        ProductoLocal existing = productoLocal.get();
	        existing.setStock(stock);
	        productoLocalRepository.save(existing);
	        return true;
	    } else {
	        Optional<Producto> producto = productoDao.findById(productoId);
	        Optional<Local> local = localRepository.findById(localId);
	        if (producto.isPresent() && local.isPresent()) {
	            ProductoLocal newProductoLocal = new ProductoLocal();
	            newProductoLocal.setProducto(producto.get());
	            newProductoLocal.setLocal(local.get());
	            newProductoLocal.setStock(stock);
	            productoLocalRepository.save(newProductoLocal);
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	@Transactional
	public Producto save(Producto producto) {
		return productoDao.save(producto);
	}

	@Override
	@Transactional
	public void deleteNote(Long id) {
		Producto note = productoDao.findById(id).get();
		productoDao.delete(note);
	}

}
