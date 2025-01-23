package com.springboot.backend.optica.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.backend.optica.modelo.Producto;



public interface IProductoService {
	
	public List<Producto> findAllProducto();
	
	public Page<Producto> findAllProducto(Pageable pageable);
	
	public Page<Producto> findByGeneroAndMarcaAndCategoria(String genero, Long marca, Long categoria, Pageable pageable);
	
	public Producto findById(Long id);
		
	public Producto save(Producto producto);
	
	public void deleteNote(Long id);
	
	List<Producto> findByModelo(String modelo);
    
    public boolean setStockByLocal(Long productoId, Long localId, Integer stock);
        
    public boolean existsByModeloAndMarca(String modelo, Long marcaId, Long id);
		
}
