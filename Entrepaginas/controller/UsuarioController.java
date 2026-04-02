package com.example.Entrepaginas.controller;

import com.example.Entrepaginas.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.Entrepaginas.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model, HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        return "usuarios";
    }

    @PostMapping("/registrar")
    @ResponseBody
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            usuarioService.guardar(usuario);
            return ResponseEntity.ok().body(Map.of("message", "Usuario registrado con éxito"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "El correo electrónico ya está registrado."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Error al registrar usuario."));
        }
    }

    @GetMapping("/nuevo")
    public String formularioNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "nuevo-usuario";
    }

    @PostMapping
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        model.addAttribute("usuario", usuario);
        return "editar-usuario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute Usuario usuario) {
        Usuario usuarioExistente = usuarioService.obtenerPorId(id);
        usuarioExistente.setCorreo(usuario.getCorreo());
        if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
            usuarioExistente.setContrasena(usuario.getContrasena());
        }
        usuarioExistente.setRol(usuario.getRol());
        usuarioService.guardar(usuarioExistente);
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("usuarioRol");
        if (rol == null || !rol.equalsIgnoreCase("ADMIN")) {
             redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar usuarios.");
             return "redirect:/usuarios";
        }
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }
}