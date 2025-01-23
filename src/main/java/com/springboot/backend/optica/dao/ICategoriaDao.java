package com.springboot.backend.optica.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.modelo.Categoria;
import com.springboot.backend.optica.modelo.Producto;

@Repository
public interface ICategoriaDao extends JpaRepository<Categoria, Long> {
	
	@Query("SELECT p FROM Categoria c JOIN c.productos p WHERE c.id = :categoriaId")
	public Page<Producto> findProductosByCategoriaId(Long categoriaId, Pageable pageable);
	
	@Query("SELECT p FROM Categoria c JOIN c.productos p WHERE p.genero LIKE :generoSeleccionado and c.id = :categoriaId")
	public Page<Producto> findByGenero(String generoSeleccionado,Long categoriaId, Pageable pageable);
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM producto_categoria WHERE categoria_id = :categoriaId", nativeQuery = true)
	public void desasociarProductosDeLaCategoria(@Param("categoriaId") long categoriaId);
}
