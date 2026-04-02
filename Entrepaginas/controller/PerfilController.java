package com.example.Entrepaginas.controller;

import com.example.Entrepaginas.model.Perfil;
import com.example.Entrepaginas.repository.PerfilRepository;
import com.example.Entrepaginas.model.Permiso;
import com.example.Entrepaginas.repository.PermisoRepository;
import com.example.Entrepaginas.config.FileStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/perfiles")
public class PerfilController {

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired(required = false)
    private PermisoRepository permisoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String listarPerfiles(Model model) {
        model.addAttribute("perfiles", perfilRepository.findAll());
        return "perfiles";
    }

    @GetMapping("/nuevo")
    public String nuevoPerfil(Model model) {
        model.addAttribute("perfil", new Perfil());
        if (permisoRepository != null) {
            model.addAttribute("todosPermisos", permisoRepository.findAll());
        }
        return "nuevo-perfil";
    }

    @PostMapping
    public String guardarPerfil(@ModelAttribute Perfil perfil,
                                @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                @RequestParam(value = "permisoIds", required = false) List<Long> permisoIds,
                                RedirectAttributes redirectAttributes) {

        System.out.println("--- Intentando guardar perfil ---");
        System.out.println("Nombre del perfil: " + perfil.getNombre());

        if (imagenFile != null && !imagenFile.isEmpty()) {
            System.out.println("Se recibió un archivo de imagen.");
            System.out.println("Nombre original del archivo: " + imagenFile.getOriginalFilename());
            System.out.println("Tamaño del archivo: " + imagenFile.getSize() + " bytes");

            try {
                String filePath = fileStorageService.storeFile(imagenFile);
                perfil.setImagen(filePath); // Guarda la ruta web relativa
                System.out.println("Imagen guardada en la ruta: " + filePath);
            } catch (RuntimeException e) {
                e.printStackTrace();
                System.err.println("Error al procesar la imagen en el controlador: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
                return "redirect:/perfiles/nuevo";
            }
        } else {
            perfil.setImagen(null);
            System.out.println("No se recibió ningún archivo de imagen o estaba vacío.");
        }

        // Manejar los permisos
        if (permisoRepository != null && permisoIds != null && !permisoIds.isEmpty()) {
            Set<Permiso> permisos = new HashSet<>(permisoRepository.findAllById(permisoIds));
            perfil.setPermisos(permisos);
        } else {
            perfil.setPermisos(new HashSet<>());
        }

        perfilRepository.save(perfil);
        System.out.println("Perfil guardado en la base de datos con ID: " + perfil.getId());
        System.out.println("--- Fin de guardar perfil ---");
        redirectAttributes.addFlashAttribute("mensajeExito", "Perfil guardado exitosamente.");
        return "redirect:/perfiles";
    }

    @GetMapping("/editar/{id}")
    public String editarPerfil(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Perfil perfil = perfilRepository.findById(id).orElse(null);
        if (perfil == null) {
            redirectAttributes.addFlashAttribute("error", "Perfil no encontrado.");
            return "redirect:/perfiles";
        }
        model.addAttribute("perfil", perfil);
        if (permisoRepository != null) {
            model.addAttribute("todosPermisos", permisoRepository.findAll());
        }
        return "editar-perfil";
    }

    @PostMapping("/editar/{id}")
    public String actualizarPerfil(@PathVariable Long id,
                                   @ModelAttribute Perfil perfilActualizado,
                                   @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                   @RequestParam(value = "permisoIds", required = false) List<Long> permisoIds,
                                   RedirectAttributes redirectAttributes) {
        Perfil perfilExistente = perfilRepository.findById(id).orElse(null);
        if (perfilExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Perfil no encontrado para actualizar.");
            return "redirect:/perfiles";
        }

        // Actualizar campos básicos
        perfilExistente.setNombre(perfilActualizado.getNombre());
        perfilExistente.setDescripcion(perfilActualizado.getDescripcion());

        // Manejar la imagen: si se sube una nueva, la reemplaza; si no, mantiene la existente
        if (imagenFile != null && !imagenFile.isEmpty()) {
            // Eliminar la imagen antigua si existe
            if (perfilExistente.getImagen() != null && !perfilExistente.getImagen().isEmpty()) {
                fileStorageService.deleteFile(perfilExistente.getImagen());
            }
            try {
                String filePath = fileStorageService.storeFile(imagenFile);
                perfilExistente.setImagen(filePath);
            } catch (RuntimeException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Error al procesar la nueva imagen: " + e.getMessage());
                return "redirect:/perfiles/editar/" + id;
            }
        }
        // Si no se sube una nueva imagen, la imagen existente se mantiene.
        // Si se desea eliminar la imagen sin subir una nueva, se necesitaría un checkbox adicional.

        // Manejar los permisos
        if (permisoRepository != null) {
            if (permisoIds != null && !permisoIds.isEmpty()) {
                Set<Permiso> permisos = new HashSet<>(permisoRepository.findAllById(permisoIds));
                perfilExistente.setPermisos(permisos);
            } else {
                perfilExistente.setPermisos(new HashSet<>());
            }
        }

        perfilRepository.save(perfilExistente);
        redirectAttributes.addFlashAttribute("mensajeExito", "Perfil actualizado exitosamente.");
        return "redirect:/perfiles";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPerfil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Perfil perfil = perfilRepository.findById(id).orElse(null);
        if (perfil != null) {
            // Eliminar la imagen asociada antes de eliminar el perfil
            if (perfil.getImagen() != null && !perfil.getImagen().isEmpty()) {
                fileStorageService.deleteFile(perfil.getImagen());
            }
            perfilRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Perfil eliminado exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Perfil no encontrado para eliminar.");
        }
        return "redirect:/perfiles";
    }
    
    
}
