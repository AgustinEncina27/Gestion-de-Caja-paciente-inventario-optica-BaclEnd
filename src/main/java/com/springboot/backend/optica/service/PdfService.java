package com.springboot.backend.optica.service;

import org.springframework.stereotype.Service;

import com.springboot.backend.optica.modelo.CajaMovimiento;
import com.springboot.backend.optica.modelo.DetalleAdicional;
import com.springboot.backend.optica.modelo.DetalleMovimiento;
import com.springboot.backend.optica.modelo.Graduacion;
import com.springboot.backend.optica.modelo.Movimiento;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

@Service
public class PdfService implements IPdfService {
	
	@Override
	public byte[] generarReporteMovimiento(Movimiento movimiento) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            double deuda=0;
            double total=0;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            	//Parte del paciente
            	PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/static/images/logo2.png", document);
                contentStream.drawImage(logo, 40, 730, 100, 50);
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.setLeading(20f);
                contentStream.newLineAtOffset(50, 750);
                
                contentStream.showText("                                                                                Fecha: " + movimiento.getFecha());
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Local: " + movimiento.getLocal().getNombre()+"     Dirección: "+movimiento.getLocal().getDireccion()+"     Celular: "+movimiento.getLocal().getCelular());
                contentStream.newLine();
                if(movimiento.getPaciente() != null) {
                	contentStream.showText("Número de Ficha:"+movimiento.getPaciente().getFicha()+"     Paciente: " + (movimiento.getPaciente() != null ? movimiento.getPaciente().getNombreCompleto() : "No especificado"));
                    contentStream.newLine();	
                }
                
                
                //DETALLE DE LA COMPRA
                if(!movimiento.getDetalles().isEmpty()) {
	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                contentStream.showText("Detalle de la compra:");
	                contentStream.newLine();
	                contentStream.setFont(PDType1Font.HELVETICA, 12);
	                for (DetalleMovimiento detalle : movimiento.getDetalles()) {
	                    contentStream.showText(detalle.getProducto().getModelo() +" - Marca: "+detalle.getProducto().getMarca().getNombre()+ " - Cantidad: " + detalle.getCantidad() + " - Precio: " + detalle.getSubtotal());
	                    contentStream.newLine();
	                    total+=detalle.getSubtotal();
	                }
                }
                
                //DETALLE ADIOCIONAL
                if(!movimiento.getDetallesAdicionales().isEmpty()) {
	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                contentStream.showText("Detalle adicionales:");
	                contentStream.newLine();
	                contentStream.setFont(PDType1Font.HELVETICA, 12);
	                for (DetalleAdicional detalle : movimiento.getDetallesAdicionales()) {
	                    contentStream.showText(detalle.getDescripcion() +" - Precio: " + detalle.getSubtotal());
	                    contentStream.newLine();
	                    total+=detalle.getSubtotal();
	                }
                }
                
                //DETALLE DEL PAGO
                if(!movimiento.getCajaMovimientos().isEmpty()) {

	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                contentStream.showText("Pagos Realizados:");
	                contentStream.newLine();
	                contentStream.setFont(PDType1Font.HELVETICA, 12);
	                for (CajaMovimiento pago : movimiento.getCajaMovimientos()) {
	                    contentStream.showText("Método: " + pago.getMetodoPago().getNombre() + " - Monto: " + pago.getMonto());
	                    deuda+=pago.getMonto();
	                    contentStream.newLine();
	                }
	            }
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Total: " + total);
                if(movimiento.getDescuento() !=null ) {
                	contentStream.showText("       Total con descuento: " + movimiento.getTotal());
                    contentStream.showText("       Descuento: " + movimiento.getDescuento());
                }
                deuda= movimiento.getTotal()-deuda;
                contentStream.showText("       Adeuda: " + deuda);
                contentStream.newLine();
                
                //Parte para la optica
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText("------------------------------------------------------------------------------------------------------------------------------------");
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Reporte de Movimiento");
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText("Local: " + movimiento.getLocal().getNombre()+"                                                Fecha: " + movimiento.getFecha());
                contentStream.newLine();
                
                //INFORMACION DEL PACIENTE
                if(movimiento.getPaciente() != null) {
                	contentStream.showText("Número de Ficha:"+movimiento.getPaciente().getFicha());
                    contentStream.newLine();	
                }
                contentStream.showText("Paciente: " + (movimiento.getPaciente() != null ? movimiento.getPaciente().getNombreCompleto() : "No especificado"));
                contentStream.newLine();
                contentStream.showText("Celular: " + (movimiento.getPaciente() != null ? movimiento.getPaciente().getCelular() : "No especificado"));
                contentStream.newLine();
                contentStream.showText("Domicilio: " + (movimiento.getPaciente() != null ? movimiento.getPaciente().getDireccion() : "No especificado"));
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Graduaciones");
                contentStream.newLine();
                if (movimiento.getPaciente() != null) {
                    // Obtener graduaciones más recientes por ojo
                    Optional<Graduacion> graduacionDerecho = movimiento.getPaciente().getGraduaciones().stream()
                        .filter(g -> g.getOjo() == Graduacion.Ojo.DERECHO)
                        .max(Comparator.comparing(Graduacion::getFechaGraduacion));

                    Optional<Graduacion> graduacionIzquierdo = movimiento.getPaciente().getGraduaciones().stream()
                        .filter(g -> g.getOjo() == Graduacion.Ojo.IZQUIERDO)
                        .max(Comparator.comparing(Graduacion::getFechaGraduacion));

                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.showText("Graduación Ojo Derecho:");
                    contentStream.newLine();
                    if (graduacionDerecho.isPresent()) {
                        Graduacion g = graduacionDerecho.get();
                        contentStream.showText("Esférico: " + g.getEsferico() + ", Cilíndrico: " + g.getCilindrico() +
                            ", Eje: " + g.getEje() + ", Adición: " + g.getAdicion() + ", Fecha: " + g.getFechaGraduacion());
                    } else {
                        contentStream.showText("No especificado");
                    }
                    contentStream.newLine();

                    contentStream.showText("Graduación Ojo Izquierdo:");
                    contentStream.newLine();
                    if (graduacionIzquierdo.isPresent()) {
                        Graduacion g = graduacionIzquierdo.get();
                        contentStream.showText("Esférico: " + g.getEsferico() + ", Cilíndrico: " + g.getCilindrico() +
                            ", Eje: " + g.getEje() + ", Adición: " + g.getAdicion() + ", Fecha: " + g.getFechaGraduacion());
                    } else {
                        contentStream.showText("No especificado");
                    }
                    contentStream.newLine();
                } else {
                    contentStream.showText("Graduaciones: No especificadas");
                    contentStream.newLine();
                }
                
                //DETALLE DE LA COMPRA
                if(!movimiento.getDetalles().isEmpty()) {
	                contentStream.newLine();
	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                contentStream.showText("Detalle de la compra:");
	                contentStream.setFont(PDType1Font.HELVETICA, 12);
	                contentStream.newLine();
	                for (DetalleMovimiento detalle : movimiento.getDetalles()) {
	                    contentStream.showText(detalle.getProducto().getModelo() +" - Marca:"+detalle.getProducto().getMarca().getNombre()+ " - Cantidad: " + detalle.getCantidad() + " - Precio: " + detalle.getSubtotal());
	                    contentStream.newLine();
	                }
                }
                
            	//DETALLE ADIOCIONAL
                if(!movimiento.getDetallesAdicionales().isEmpty()) {
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.showText("Detalle adicionales:");
                    contentStream.newLine();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    for (DetalleAdicional detalle : movimiento.getDetallesAdicionales()) {
                        contentStream.showText(detalle.getDescripcion() +" - Precio: " + detalle.getSubtotal());
                        contentStream.newLine();
                    }
                }
                
                //DETALLE DEL PAGO
                if(!movimiento.getCajaMovimientos().isEmpty()) {

	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                contentStream.showText("Pagos Realizados:");
	                contentStream.newLine();
	                contentStream.setFont(PDType1Font.HELVETICA, 12);
	                for (CajaMovimiento pago : movimiento.getCajaMovimientos()) {
	                    contentStream.showText("Método: " + pago.getMetodoPago().getNombre() + " - Monto: " + pago.getMonto());
	                    deuda+=pago.getMonto();
	                    contentStream.newLine();
	                }
	            }
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Total: " + total);
                if(movimiento.getDescuento() !=null ) {
                	contentStream.showText("       Total con descuento: " + movimiento.getTotal());
                    contentStream.showText("       Descuento: " + movimiento.getDescuento());
                }
                deuda= movimiento.getTotal()-deuda;
                contentStream.showText("       Adeuda: " + deuda);
                contentStream.newLine();
                
                //INFORMACION DEL MEDICO
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Informaciòn del Médico:");
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                if(movimiento.getPaciente() != null) {
                    contentStream.showText("Médico: " + (movimiento.getPaciente().getMedico() != null ? movimiento.getPaciente().getMedico() : "No especificado"));
                }
                contentStream.newLine();
                contentStream.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}
