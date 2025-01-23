package com.springboot.backend.optica.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.backend.optica.modelo.ProductoLocal;

public interface IProductoLocalDao extends JpaRepository<ProductoLocal, Long> {
    Optional<ProductoLocal> findByProductoIdAndLocalId(Long productoId, Long localId);
    
    List<ProductoLocal> findByLocalId(Long localId);

}
