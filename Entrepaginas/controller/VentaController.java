package com.example.Entrepaginas.controller;

import com.example.Entrepaginas.model.Venta;
import com.example.Entrepaginas.model.Cliente;
import com.example.Entrepaginas.model.Libro;
import com.example.Entrepaginas.model.Usuario;
import com.example.Entrepaginas.service.VentaService;
import com.example.Entrepaginas.model.DetalleVenta;

import jakarta.servlet.http.HttpSession;

import com.example.Entrepaginas.service.ClienteService;
import com.example.Entrepaginas.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.Entrepaginas.repository.UsuarioRepository;
import com.example.Entrepaginas.repository.ClienteRepository;
import com.example.Entrepaginas.repository.LibroRepository;
import com.example.Entrepaginas.dto.VentaWebDTO;
import com.example.Entrepaginas.dto.DetalleVentaWebDTO;
import com.example.Entrepaginas.repository.VentaRepository;


import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ClienteService clienteService;

     @Autowired
    private LibroService libroService;    

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    // Asumimos que tienes un servicio de usuario o una forma de obtener el usuario logueado
    // private UserService userService;

    // ====================================================================
    // 1. LISTAR VENTAS
    // ====================================================================

    @GetMapping
    public String listarVentas(Model model) {
        // En un entorno real, podrías necesitar implementar paginación aquí
        List<Venta> ventas = ventaService.listarTodasLasVentas(); 
        model.addAttribute("ventas", ventas);
        
        // Simulación de usuario logueado para el navbar (ajústalo según tu Auth)
        model.addAttribute("loggedInUser", "JuanPerez"); 
        model.addAttribute("userRole", "Administrador");

        return "ventas";
    }
    
    // ====================================================================
    // 2. NUEVA VENTA (Formulario GET)
    // ====================================================================

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaVenta(Model model) {
        List<Cliente> clientes = clienteService.listarClientes(); // Clientes para el <select>
        List<Libro> libros = libroService.obtenerTodos(); 

        model.addAttribute("clientes", clientes);
        model.addAttribute("libros", libros);
        Venta venta = new Venta();
        venta.setTipoComprobante("BOLETA");
        model.addAttribute("venta", venta);

        return "nueva-venta";
    }

    // ====================================================================
    // 3. GUARDAR VENTA (Formulario POST)
    // ====================================================================

    /**
     * Procesa el formulario de la nueva venta.
     * La clase Venta recibida debe contener la lista de DetallesVenta.
     */
    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute Venta venta, 
                               @RequestParam(value = "clienteId", required = false) Long clienteId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            // Asignar Cliente (si se seleccionó uno)
            if (clienteId != null) {
                Cliente cliente = clienteService.obtenerPorId(clienteId);
                // validacion factura, ruc si o si
                if ("FACTURA".equalsIgnoreCase(venta.getTipoComprobante())) {
                    if (cliente.getRuc() == null || cliente.getRuc().trim().isEmpty()) {
                         throw new Exception("Para emitir FACTURA, el cliente debe tener RUC registrado.");
                    }
                }
                
                venta.setCliente(cliente);
            }
            
            // Asignar el usuario que realiza la venta (simulado)
            // Asignar el usuario que realiza la venta desde la sesión
            String correoUsuario = (String) session.getAttribute("usuarioNombre");
            if (correoUsuario != null) {
                Usuario usuario = usuarioRepository.findByCorreo(correoUsuario);
                venta.setUsuario(usuario);
            } 

            // Llama al servicio para guardar la venta y ACTUALIZAR EL STOCK
            Venta ventaGuardada = ventaService.guardarVentaYActualizarStock(venta);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                                                "Venta N° " + ventaGuardada.getId() + " registrada con éxito.");
            
            return "redirect:/ventas";
            
        } catch (Exception e) {
            // Manejo de errores (ej: stock insuficiente, datos inválidos)
            redirectAttributes.addFlashAttribute("errorMessage", 
                                                "Error al registrar la venta: " + e.getMessage());
            return "redirect:/ventas/nueva";
        }
    }
    
    // ====================================================================
    // 4. VER DETALLE DE VENTA (Voucher POS)
    // ====================================================================
    
    @GetMapping("/detalle/{id}")
    public String verDetalleVenta(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Venta venta = ventaService.buscarVentaPorId(id);
            if (venta == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Venta no encontrada.");
                return "redirect:/ventas";
            }
            model.addAttribute("venta", venta);
            return "DetalleVenta";
        } catch (Exception e) {
             redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el detalle: " + e.getMessage());
             return "redirect:/ventas";
        }
    }
    
    // ====================================================================
    // 5. ANULAR VENTA (Eliminar/Cancelar Lógico)
    // ====================================================================

    @GetMapping("/anular/{id}")
    public String anularVenta(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Llama al servicio para marcar como anulada y REACOMODAR EL STOCK
            ventaService.anularVentaYReponerStock(id);
            
            redirectAttributes.addFlashAttribute("successMessage", "Venta N° " + id + " ha sido ANULADA y el stock repuesto.");
            return "redirect:/ventas";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al anular la venta: " + e.getMessage());
            return "redirect:/ventas";
        }
    }

    // ====================================================================
    // 6. API PARA VENTA WEB (BIBLIOTECA)
    // ====================================================================
    
    @PostMapping("/api/crear")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> crearVentaWeb(@RequestBody com.example.Entrepaginas.dto.VentaWebDTO ventaWebDTO) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            // 1. Crear o Buscar Cliente "Web" (Simplificado para este ejemplo)
            // Podrías buscar por nombre o crear uno genérico si no existe.
            // Por simplicidad, crearemos un cliente "Invitado" o usaremos los datos si son nuevos.
            
            Cliente cliente = new Cliente();
            cliente.setNombre(ventaWebDTO.getClienteNombre());
            cliente.setDireccion(ventaWebDTO.getClienteDireccion());
            cliente.setTelefono(ventaWebDTO.getClienteTelefono());
            cliente.setCorreo("web_" + System.currentTimeMillis() + "@entrepaginas.com"); // Correo ficticio único
            cliente.setDni("00000000"); // DNI Genérico
            cliente = clienteRepository.save(cliente);

            // 2. Crear Objeto Venta
            Venta venta = new Venta();
            venta.setCliente(cliente);
            venta.setFechaVenta(java.time.LocalDateTime.now());
            venta.setTipoComprobante("BOLETA"); // Por defecto
            venta.setTotal(0.0); // Se recalculará en el servicio

            // 3. Crear Detalles
            java.util.List<DetalleVenta> detalles = new java.util.ArrayList<>();
            
            for (DetalleVentaWebDTO item : ventaWebDTO.getItems()) {
                // 1. Intentar buscar uno con stock primero (para evitar duplicados sin stock)
                Libro libro = libroRepository.findFirstByTituloAndStockGreaterThan(item.getTitulo(), 0);
                
                // 2. Si no hay con stock, buscar cualquiera por título (para que el error sea "Stock insuficiente" y no "No encontrado")
                if (libro == null) {
                    libro = libroRepository.findFirstByTitulo(item.getTitulo());
                }

                if (libro == null) {
                    throw new Exception("Libro no encontrado en base de datos: " + item.getTitulo());
                }
                
                DetalleVenta detalle = new DetalleVenta();
                detalle.setLibro(libro);
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(libro.getPrecio());
                detalle.setSubtotal(libro.getPrecio() * item.getCantidad());
                
                detalles.add(detalle);
            }
            
            venta.setDetallesVenta(detalles);

            // 4. Guardar
            Venta ventaGuardada = ventaService.guardarVentaYActualizarStock(venta);
            
            response.put("success", true);
            response.put("message", "Venta registrada con ID: " + ventaGuardada.getId());
            response.put("orderId", ventaGuardada.getId());
            return org.springframework.http.ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al procesar la venta: " + e.getMessage());
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}