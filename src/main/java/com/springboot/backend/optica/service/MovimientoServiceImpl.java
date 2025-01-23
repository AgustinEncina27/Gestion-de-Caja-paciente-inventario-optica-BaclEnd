package com.springboot.backend.optica.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.optica.dao.IMetodoPagoDao;
import com.springboot.backend.optica.dao.IMovimientoDao;
import com.springboot.backend.optica.dao.IProductoLocalDao;
import com.springboot.backend.optica.modelo.MetodoPago;
import com.springboot.backend.optica.modelo.Movimiento;
import com.springboot.backend.optica.modelo.ProductoLocal;
import com.springboot.backend.optica.modelo.CajaMovimiento;
import com.springboot.backend.optica.modelo.DetalleAdicional;
import com.springboot.backend.optica.modelo.DetalleMovimiento;

@Service
public class MovimientoServiceImpl implements IMovimientoService {

    @Autowired
    private IMovimientoDao movimientoRepository;
    
    @Autowired
    private IMetodoPagoDao metodoPagoRepository;
    
    @Autowired
    private IPdfService pdfService;
    
    @Autowired
    private IProductoLocalDao  productoLocalRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> findAll() {
        return movimientoRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Movimiento> filtrarMovimientos(Long idLocal, String tipoMovimiento, Long nroFicha, LocalDate fecha, String metodoPago, Pageable pageable) {
        if(idLocal== null || idLocal==0)idLocal=null;
    	return movimientoRepository.filtrarMovimientos(idLocal,tipoMovimiento,nroFicha,fecha,metodoPago, pageable);
    }
    
    @Override
	@Transactional(readOnly = true)
	public Page<Movimiento> findAllMovimiento(Pageable pageable) {
		return movimientoRepository.findAll(pageable);
	}
    
    @Override
    @Transactional(readOnly = true)
    public Page<Movimiento> findByLocalIdPaginated(Long idLocal, Pageable pageable) {
        return movimientoRepository.findByLocalId(idLocal, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> calcularTotales(Long idLocal) {
        // Obtener todos los métodos de pago
        List<MetodoPago> metodosPago = metodoPagoRepository.findAll();
        Map<String, Double> totales = new HashMap<>();

        // Inicializar los totales en 0 para cada método de pago
        for (MetodoPago metodoPago : metodosPago) {
            totales.put(metodoPago.getNombre(), 0.0);
        }

        // Obtener movimientos según el local seleccionado
        List<Movimiento> movimientos;
        if (idLocal == 0) {
            movimientos = movimientoRepository.findAll();
        } else {
            movimientos = movimientoRepository.findByLocalId(idLocal);
        }

        // Calcular los totales para cada método de pago teniendo en cuenta el tipo de movimiento
        for (Movimiento movimiento : movimientos) {
            // Iterar sobre los pagos del movimiento
            for (CajaMovimiento movimientoCaja : movimiento.getCajaMovimientos()) {
                String metodoPagoNombre = movimientoCaja.getMetodoPago().getNombre();
                if (totales.containsKey(metodoPagoNombre)) {
                    double totalActual = totales.get(metodoPagoNombre);

                    // Sumar o restar según el tipo de movimiento
                    if ("ENTRADA".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
                        totalActual += movimientoCaja.getMontoImpuesto();
                    } else if ("SALIDA".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
                        totalActual -= movimientoCaja.getMontoImpuesto();
                    }

                    totales.put(metodoPagoNombre, totalActual);
                }
            }
        }

        return totales;
    }


    @Override
    @Transactional(readOnly = true)
    public Movimiento findById(Long id) {
        Optional<Movimiento> result = movimientoRepository.findById(id);
        return result.orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Movimiento> findByTipoMovimiento(String tipo, Pageable pageable) {
        return movimientoRepository.findByTipoMovimiento(tipo, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Movimiento> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        return movimientoRepository.findByFechaBetween(fechaInicio, fechaFin, pageable);
    }

    @Override
	@Transactional
    public Movimiento create(Movimiento movimiento) {
    	// Asignar el movimiento en cada DetalleMovimiento
        if (movimiento.getDetalles() != null) {
            for (DetalleMovimiento detalle : movimiento.getDetalles()) {
                detalle.setMovimiento(movimiento);
            }
        }
        
        // Asignar el movimiento en cada DetalleMovimiento
        if (movimiento.getDetallesAdicionales() != null) {
            for (DetalleAdicional detalleAdicional : movimiento.getDetallesAdicionales()) {
            	detalleAdicional.setMovimiento(movimiento);
            }
        }

        // Asignar el movimiento en cada CajaMovimientos
        if (movimiento.getCajaMovimientos() != null) {
            for (CajaMovimiento pago : movimiento.getCajaMovimientos()) {
                pago.setMovimiento(movimiento);
            }
        }
        
     // Verificar el tipo de movimiento y descontar stock si es SALIDA
        if ("ENTRADA".equalsIgnoreCase(movimiento.getTipoMovimiento()) && movimiento.getDetalles() != null) {
            for (DetalleMovimiento detalle : movimiento.getDetalles()) {
                ProductoLocal productoLocal = productoLocalRepository.findByProductoIdAndLocalId(
                        detalle.getProducto().getId(), movimiento.getLocal().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el local."));

                // Verificar si hay suficiente stock
                if (productoLocal.getStock() < detalle.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto: " + detalle.getProducto().getModelo());
                }

                // Descontar el stock
                productoLocal.setStock(productoLocal.getStock() - detalle.getCantidad());
                productoLocalRepository.save(productoLocal);
            }
        }
        
        return movimientoRepository.save(movimiento);
    }
    
    @Transactional
    public Movimiento update(Long id, Movimiento movimiento) {
        Movimiento currentMovimiento = movimientoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

        // Restaurar stock si el movimiento original es de tipo "SALIDA"
        if ("ENTRADA".equalsIgnoreCase(currentMovimiento.getTipoMovimiento())) {
            for (DetalleMovimiento detalle : currentMovimiento.getDetalles()) {
                ProductoLocal productoLocal = productoLocalRepository.findByProductoIdAndLocalId(
                        detalle.getProducto().getId(), currentMovimiento.getLocal().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el local."));

                productoLocal.setStock(productoLocal.getStock() + detalle.getCantidad()); // Restaurar stock
                productoLocalRepository.save(productoLocal);
            }
        }
        
        // Asignar el movimiento en cada DetalleMovimiento
        if (movimiento.getDetalles() != null) {
            for (DetalleMovimiento detalle : movimiento.getDetalles()) {
                detalle.setMovimiento(movimiento);
            }
        }
        
        // Asignar el movimiento en cada DetalleMovimiento
        if (movimiento.getDetallesAdicionales() != null) {
            for (DetalleAdicional detalle : movimiento.getDetallesAdicionales()) {
                detalle.setMovimiento(movimiento);
            }
        }

        // Asignar el movimiento en cada CajaMovimientos
        if (movimiento.getCajaMovimientos() != null) {
            for (CajaMovimiento pago : movimiento.getCajaMovimientos()) {
                pago.setMovimiento(movimiento);
            }
        }
        
     
        // Ajustar el stock si el nuevo movimiento es de tipo "SALIDA"
        if ("ENTRADA".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
            for (DetalleMovimiento detalle : movimiento.getDetalles()) {
                ProductoLocal productoLocal = productoLocalRepository.findByProductoIdAndLocalId(
                        detalle.getProducto().getId(), movimiento.getLocal().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el local."));

                // Verificar si hay suficiente stock
                if (productoLocal.getStock() < detalle.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto: " + detalle.getProducto().getModelo());
                }

                // Descontar el stock
                productoLocal.setStock(productoLocal.getStock() - detalle.getCantidad());
                productoLocalRepository.save(productoLocal);
            }
        }

        // Guardar el movimiento actualizado
        return movimientoRepository.save(movimiento);
    }


    @Override
	@Transactional
    public void delete(Long id) {
    	Movimiento currentMovimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

        // Restaurar stock si el movimiento original es de tipo "SALIDA"
        if ("ENTRADA".equalsIgnoreCase(currentMovimiento.getTipoMovimiento())) {
            for (DetalleMovimiento detalle : currentMovimiento.getDetalles()) {
                ProductoLocal productoLocal = productoLocalRepository.findByProductoIdAndLocalId(
                        detalle.getProducto().getId(), currentMovimiento.getLocal().getId())
                		.orElseGet(() -> {
                            // Crear el producto en el local si no existe
                            ProductoLocal nuevoProductoLocal = new ProductoLocal();
                            nuevoProductoLocal.setProducto(detalle.getProducto());
                            nuevoProductoLocal.setLocal(currentMovimiento.getLocal());
                            nuevoProductoLocal.setStock(0); 
                            return productoLocalRepository.save(nuevoProductoLocal);
                        });

                productoLocal.setStock(productoLocal.getStock() + detalle.getCantidad()); // Restaurar stock
                productoLocalRepository.save(productoLocal);
            }
        }
        
        movimientoRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public byte[] generarReporteMovimiento(Long idMovimiento) throws IOException {
        Movimiento movimiento = movimientoRepository.findById(idMovimiento)
            .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

		return pdfService.generarReporteMovimiento(movimiento);
		
    }
}
