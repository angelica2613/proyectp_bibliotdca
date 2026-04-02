package com.example.Entrepaginas.controller;

import com.example.Entrepaginas.service.LibroService;
import com.example.Entrepaginas.service.ClienteService;
import com.example.Entrepaginas.service.PrestamoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PrestamoService prestamoService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Verifica la sesión del usuario
        Object nombre = session.getAttribute("usuarioNombre");
        Object rol = session.getAttribute("usuarioRol");

        if (nombre == null) {
            return "redirect:/entrepaginas/login"; // Redirige si no hay sesión
        }

        model.addAttribute("loggedInUser", nombre.toString());
        model.addAttribute("userRole", rol != null ? rol.toString() : "Desconocido");

        // --- Obtener y añadir los datos numéricos al modelo ---
        // Asegúrate de que estos métodos existan en tus servicios y devuelvan un Long o Integer
        model.addAttribute("totalLibros", libroService.contarTodosLosLibros());
        model.addAttribute("totalLibrosDisponibles", libroService.contarLibrosDisponibles());
        model.addAttribute("totalLibrosNoDisponibles", libroService.contarLibrosNoDisponibles());
        model.addAttribute("totalClientes", clienteService.contarTodosLosClientes());
        model.addAttribute("totalPrestamos", prestamoService.contarTodosLosPrestamos());
        model.addAttribute("totalPrestamosActivos", prestamoService.contarPrestamosActivos());
        model.addAttribute("totalPrestamosVencidos", prestamoService.contarPrestamosVencidos());

        return "Dashboard"; // Nombre de tu plantilla HTML
    }
}
