package com.example.Entrepaginas.controller;

import com.example.Entrepaginas.model.Libro;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.Entrepaginas.service.LibroService;
import com.example.Entrepaginas.service.CategoriaService;
import com.example.Entrepaginas.model.Categoria;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private CategoriaService categoriaService;

    @Value("${file.upload-dir-libros}")
    private String uploadDirLibrosPhysical;

    private final String uploadDirLibrosWebPath = "/uploads/libros/";

    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<List<Libro>> listarLibrosJson() {
        List<Libro> libros = libroService.obtenerTodos();
        return ResponseEntity.ok(libros);
    }

    @GetMapping
    public String listarLibros(Model model, HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        List<Libro> libros = libroService.obtenerTodos();
        System.out.println("Número de libros obtenidos del servicio: " + libros.size());

        model.addAttribute("libros", libros);
        model.addAttribute("usuarioNombre", nombre.toString());
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));

        return "libros";
    }

    @GetMapping("/nuevo")
    public String formularioNuevoLibro(Model model, HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }
        List<Categoria> categorias = categoriaService.obtenerTodos();
        model.addAttribute("libro", new Libro());
        model.addAttribute("categorias", categorias);
        model.addAttribute("usuarioNombre", nombre.toString());
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        return "nuevo-libro";
    }

    @PostMapping
    public String guardarLibro(@ModelAttribute Libro libro,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        if (!imagenFile.isEmpty()) {
            try {
                String originalFilename = imagenFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                Path rutaFisica = Paths.get(uploadDirLibrosPhysical, uniqueFileName);
                Files.write(rutaFisica, imagenFile.getBytes());

                libro.setImagen(uploadDirLibrosWebPath + uniqueFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            libro.setImagen(null);
        }
        libroService.guardar(libro);
        return "redirect:/libros";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditarLibro(@PathVariable Long id, Model model, HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        Libro libro = libroService.obtenerPorId(id);
        model.addAttribute("libro", libro);
        model.addAttribute("usuarioNombre", nombre.toString());
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        return "editar-libro";
    }

    @PostMapping("/editar/{id}")
    public String actualizarLibro(@PathVariable Long id,
            @ModelAttribute Libro libro,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        Libro libroExistente = libroService.obtenerPorId(id);
        if (libroExistente == null) {
            return "redirect:/libros";
        }

        libroExistente.setTitulo(libro.getTitulo());
        libroExistente.setAutor(libro.getAutor());
        libroExistente.setGenero(libro.getGenero());
        libroExistente.setDisponible(libro.isDisponible());
        libroExistente.setIsbn(libro.getIsbn());
        libroExistente.setDescripcion(libro.getDescripcion());
        libroExistente.setStock(libro.getStock());
        libroExistente.setPrecio(libro.getPrecio());

        if (!imagenFile.isEmpty()) {
            try {
                if (libroExistente.getImagen() != null && !libroExistente.getImagen().isEmpty()) {
                    String fileName = Paths.get(libroExistente.getImagen()).getFileName().toString();
                    Path oldImagePath = Paths.get(uploadDirLibrosPhysical, fileName);
                    Files.deleteIfExists(oldImagePath);
                }

                String originalFilename = imagenFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                Path rutaFisica = Paths.get(uploadDirLibrosPhysical, uniqueFileName);
                Files.write(rutaFisica, imagenFile.getBytes());
                libroExistente.setImagen(uploadDirLibrosWebPath + uniqueFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        libroService.guardar(libroExistente);
        return "redirect:/libros";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id, HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        Libro libro = libroService.obtenerPorId(id);
        if (libro != null) {
            if (libro.getImagen() != null && !libro.getImagen().isEmpty()) {
                try {
                    String fileName = Paths.get(libro.getImagen()).getFileName().toString();
                    Path imagePath = Paths.get(uploadDirLibrosPhysical, fileName);
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    System.err.println("Error al eliminar la imagen del libro: " + libro.getImagen() + " - " + e.getMessage());
                }
            }
            libroService.eliminar(id);
        }
        return "redirect:/libros";
    }
}