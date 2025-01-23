package com.springboot.backend.optica.dao;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.backend.optica.modelo.Producto;

public interface IProductoDao extends JpaRepository<Producto, Long> {
	
	@Query("SELECT p FROM Producto p WHERE p.modelo = :modelo")
	List<Producto> findByModelo(@Param("modelo") String modelo);
    
    @Query("SELECT p FROM Producto p ORDER BY p.marca.nombre ASC")
    Page<Producto> findAllProducto(Pageable pageable);
	
	@Query("SELECT DISTINCT p FROM Producto p WHERE p.genero LIKE :generoSeleccionado")
	public Page<Producto> findByGenero(String generoSeleccionado, Pageable pageable);
	
	@Query("SELECT DISTINCT p FROM Producto p " +
		       "LEFT JOIN p.categorias c " +
		       "WHERE (:genero IS NULL OR p.genero = :genero) " +
		       "AND (:marca IS NULL OR p.marca.id = :marca) " +
		       "AND (:categoria IS NULL OR c.id = :categoria) ORDER BY p.marca.nombre ASC")
	Page<Producto> findByGeneroAndMarcaAndCategoria(String genero, Long marca, Long categoria, Pageable pageable);
	
	@Query("SELECT COUNT(p) > 0 FROM Producto p " +
	       "WHERE p.modelo = :modelo " + 
	       "AND p.marca.id = :marcaId " +
	       "AND p.id != :productoId")
	boolean existsByModeloAndMarca(@Param("modelo") String modelo, @Param("marcaId") Long marcaId, @Param("productoId") Long productoId);
}
