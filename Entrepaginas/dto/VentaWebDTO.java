package com.example.Entrepaginas.dto;

import java.util.List;

public class VentaWebDTO {
    private String clienteNombre;
    private String clienteDireccion;
    private String clienteTelefono;
    private Double total;
    private List<DetalleVentaWebDTO> items;

    // Getters y Setters
    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteDireccion() {
        return clienteDireccion;
    }

    public void setClienteDireccion(String clienteDireccion) {
        this.clienteDireccion = clienteDireccion;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<DetalleVentaWebDTO> getItems() {
        return items;
    }

    public void setItems(List<DetalleVentaWebDTO> items) {
        this.items = items;
    }
}
