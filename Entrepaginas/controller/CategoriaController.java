package com.example.Entrepaginas.controller;

import com.example.Entrepaginas.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Importar RedirectAttributes
import com.example.Entrepaginas.service.CategoriaService;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.obtenerTodos());
        return "categorias";
    }

    @GetMapping("/nuevo")
    public String formularioNuevoCategoria(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "nuevo-categoria";
    }

    @PostMapping
    public String guardarCategoria(@ModelAttribute Categoria categoria, RedirectAttributes redirectAttributes) {
        categoriaService.guardar(categoria);
        redirectAttributes.addFlashAttribute("mensajeExito", "Categoría guardada exitosamente.");
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editarCategoria(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Categoria categoria = categoriaService.obtenerPorId(id);
        if (categoria == null) {
            redirectAttributes.addFlashAttribute("error", "Categoría no encontrada.");
            return "redirect:/categorias";
        }
        model.addAttribute("categoria", categoria);
        return "editar-categoria";
    }

    @PostMapping("/editar/{id}")
    public String actualizarCategoria(@PathVariable Long id, @ModelAttribute Categoria categoria, RedirectAttributes redirectAttributes) {
        Categoria categoriaExistente = categoriaService.obtenerPorId(id);
        if (categoriaExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Categoría no encontrada para actualizar.");
            return "redirect:/categorias";
        }
        // Actualizar solo los campos que pueden ser editados (ej. nombre)
        categoriaExistente.setNombre(categoria.getNombre());
        categoriaService.guardar(categoriaExistente);
        redirectAttributes.addFlashAttribute("mensajeExito", "Categoría actualizada exitosamente.");
        return "redirect:/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoriaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Categoría eliminada exitosamente.");
        return "redirect:/categorias";
    }
}