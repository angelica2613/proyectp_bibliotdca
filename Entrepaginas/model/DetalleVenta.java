package com.example.Entrepaginas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación Muchos a Uno: Un detalle pertenece a una única venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    // Relación Muchos a Uno: Un detalle se refiere a un único libro
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false) 
    private Libro libro;
    
    // Campo para la cantidad vendida
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    // Campo para el precio unitario (Se guarda el precio al momento de la venta 
    // para proteger la contabilidad de futuros cambios de precio en el Libro)
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    // --- Constructores ---
    
    public DetalleVenta() {
    }

    // Constructor con campos esenciales (opcional)
    public DetalleVenta(Venta venta, Libro libro, Integer cantidad, Double precioUnitario) {
        this.venta = venta;
        this.libro = libro;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    @Transient // Indica que este campo no se mapea a la base de datos
    public Double getSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return 0.0;
        }
        return precioUnitario * cantidad;
    }

    public void setSubtotal(double d) {
    }
}