package com.example.Entrepaginas.controller;


import com.example.Entrepaginas.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.Entrepaginas.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/acceder")
    public String processLogin(@RequestParam String correo,
                           @RequestParam String contrasena,
                           Model model,
                           HttpSession session) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario != null && passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            // Guardar datos en sesión
            session.setAttribute("usuarioNombre", usuario.getCorreo());
            session.setAttribute("usuarioRol", usuario.getRol());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
            return "login";
        }
    }


    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
    }

}