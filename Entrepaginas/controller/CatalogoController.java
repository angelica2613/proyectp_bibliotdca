package com.example.Entrepaginas.controller;

import com.example.Entrepaginas.model.Libro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.Entrepaginas.service.CatalagoService;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/catalogos")
public class CatalogoController {

    @Autowired
    private CatalagoService catalogoService;

    @GetMapping
    public String mostrarCatalogo(
            @RequestParam(value = "genero", required = false) String genero,
            @RequestParam(value = "query", required = false) String query,
            Model model,
            HttpSession session) {

        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        List<Libro> libros;
        if (query != null && !query.isEmpty()) {
            libros = catalogoService.buscarLibros(query);
            if (genero != null && !genero.isEmpty()) {
                libros = libros.stream()
                        .filter(libro -> genero.equalsIgnoreCase(libro.getGenero()))
                        .toList();
            }
        } else if (genero != null && !genero.isEmpty()) {
            libros = catalogoService.getLibrosByGenero(genero);
        } else {
            libros = catalogoService.getAllLibros();
        }

        model.addAttribute("libros", libros);
        model.addAttribute("generoSeleccionado", genero);
        model.addAttribute("queryBusqueda", query);
        model.addAttribute("usuarioNombre", nombre.toString());
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));

        return "catalogo";
    }

    @GetMapping("/{id}")
    public String mostrarDetalleLibro(@PathVariable Long id, Model model, HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        Optional<Libro> libro = catalogoService.getLibroById(id);
        if (libro.isPresent()) {
            model.addAttribute("libro", libro.get());
            model.addAttribute("usuarioNombre", nombre.toString());
            model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
            return "detalle-libro";
        } else {
            return "redirect:/catalogo?error=LibroNoEncontrado";
        }
    }

    @PutMapping("/prestar/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> prestarLibro(@PathVariable Long id) {
        Libro prestado = catalogoService.prestarLibro(id);
        if (prestado != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Libro prestado exitosamente.");
            response.put("libro", prestado);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Libro no disponible o no encontrado."));
        }
    }

    @PutMapping("/devolver/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> devolverLibro(@PathVariable Long id) {
        Libro devuelto = catalogoService.devolverLibro(id);
        if (devuelto != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Libro devuelto exitosamente.");
            response.put("libro", devuelto);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Libro no encontrado."));
        }
    }

    // NUEVO ENDPOINT: Para cambiar la disponibilidad de un libro
    @PutMapping("/toggle-disponibilidad/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleDisponibilidad(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean nuevoEstado = payload.get("disponible");
            if (nuevoEstado == null) {
                response.put("success", false);
                response.put("message", "El estado de disponibilidad es requerido.");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<Libro> optionalLibro = catalogoService.getLibroById(id);
            if (optionalLibro.isPresent()) {
                Libro libro = optionalLibro.get();
                libro.setDisponible(nuevoEstado);
                catalogoService.saveLibro(libro); // Asumiendo que saveLibro actualiza si el ID existe

                response.put("success", true);
                response.put("message", "Disponibilidad del libro actualizada a " + (nuevoEstado ? "DISPONIBLE" : "NO DISPONIBLE") + ".");
                response.put("libro", libro);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Libro no encontrado con ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al actualizar disponibilidad: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}