package com.example.Entrepaginas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

//metodo get para mostrar la vista del login ok angelica ensalada
    @GetMapping("/login")
    public String login(Model model, @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("exito", "Sesión cerrada correctamente");
        }
        return "login";
    }
}