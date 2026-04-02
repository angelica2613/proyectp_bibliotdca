package com.example.Entrepaginas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.example.Entrepaginas.service.LibroService;

@Controller
public class BibliotecaController {

    @Autowired
    private LibroService libroService;

    @GetMapping({"/", "/biblioteca", "/index"})
    public String mostrarBiblioteca(Model model) {
        model.addAttribute("libros", libroService.obtenerTodos());
        return "biblioteca";
    }
}