package com.example.Entrepaginas.service;

import com.example.Entrepaginas.model.Venta;
import com.example.Entrepaginas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private VentaRepository ventaRepository;

    public String generarReporteDiario() {
        LocalDate hoy = LocalDate.now();
        List<Venta> ventasHoy = ventaRepository.findAll().stream()
                .filter(v -> v.getFechaVenta().toLocalDate().equals(hoy))
                .collect(Collectors.toList());

        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE VENTAS DIARIO (" + hoy + ") ===\n");
        
        double totalDia = 0.0;
        for (Venta venta : ventasHoy) {
            if (!venta.isAnulada()) {
                reporte.append(String.format("Venta ID: %d | Total: %.2f | Cliente: %s\n", 
                        venta.getId(), 
                        venta.getTotal(),
                        venta.getCliente() != null ? venta.getCliente().getNombre() : "Mostrador"));
                totalDia += venta.getTotal();
            }
        }
        
        reporte.append("Total de ventas activas hoy: ").append(ventasHoy.size()).append("\n");
        reporte.append("Monto Total recaudado: ").append(totalDia).append("\n");
        reporte.append("=========================================\n");

        return reporte.toString();
    }
}
