package com.springboot.backend.optica.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.modelo.Proveedor;


@Repository
public interface IProveedorDao extends JpaRepository<Proveedor, Long> {
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM producto_proveedor WHERE proveedor_id = :proveedorId", nativeQuery = true)
	public void desasociarProductosDelProveedor(@Param("proveedorId") long proveedorId);
}
