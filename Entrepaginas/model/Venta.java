package com.example.Entrepaginas.model;



import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente; // Puede ser null si es una venta a "Mostrador"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // El empleado que realizó la venta

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "tipo_comprobante")
    private String tipoComprobante; // FACTURA o BOLETA

    @Column(name = "anulada", nullable = false)
    private boolean anulada = false; // Estado lógico para anulación

    // Relación Uno a Muchos con DetalleVenta
    // mappedBy indica el campo en la clase DetalleVenta que mapea esta relación.
    // CascadeType.ALL asegura que si guardamos/eliminamos la Venta, sus detalles también se manejen.
    // orphanRemoval = true es útil para eliminar detalles al editar una venta.
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detallesVenta = new ArrayList<>();

    // --- Constructores ---
    
    public Venta() {
        // Inicializa la fecha al crear el objeto, si el constructor de Spring lo usa
        this.fechaVenta = LocalDateTime.now(); 
    }

    // Constructor con campos esenciales (opcional)
    public Venta(Cliente cliente, Usuario usuario, Double total) {
        this.cliente = cliente;
        this.usuario = usuario;
        this.fechaVenta = LocalDateTime.now();
        this.total = total;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public boolean isAnulada() {
        return anulada;
    }

    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }

    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        // Al establecer los detalles, también nos aseguramos de asignar la Venta a cada detalle
        this.detallesVenta.clear(); // Limpia la lista existente para orphanRemoval = true
        if (detallesVenta != null) {
            for (DetalleVenta detalle : detallesVenta) {
                detalle.setVenta(this);
                this.detallesVenta.add(detalle);
            }
        }
    }
    
    // --- Métodos Auxiliares para DetalleVenta (Buena Práctica JPA) ---
    
    public void addDetalleVenta(DetalleVenta detalle) {
        detallesVenta.add(detalle);
        detalle.setVenta(this);
    }

    public void removeDetalleVenta(DetalleVenta detalle) {
        detallesVenta.remove(detalle);
        detalle.setVenta(null);
    }
}