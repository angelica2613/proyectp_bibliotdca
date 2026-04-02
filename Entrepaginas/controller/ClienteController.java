package com.example.Entrepaginas.controller;

import com.example.Entrepaginas.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Importar Value
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.Entrepaginas.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate; // Importar RestTemplate

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
 //solo dices que consumes un api a un sitio web donde envias el dni, 8 digitos, y te devuelve nombres y apellidos de la persona y ya  la configuracione esta aqui
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RestTemplate restTemplate; // Inyectar RestTemplate

    @Value("${reniec.api.url}") // Inyectar la URL de la API desde application.properties
    private String reniecApiUrl;

    @Value("${reniec.api.token}") // Inyectar el token de la API desde application.properties
    private String reniecApiToken;

    @GetMapping
    public String listarClientes(Model model, HttpSession session) {
        // Check session
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        // Add data to model
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("usuarioNombre", nombre.toString());
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        
        return "clientes";
    }

    @GetMapping("/nuevo")
    public String formularioNuevoCliente(Model model, HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        model.addAttribute("cliente", new Cliente());
        model.addAttribute("usuarioNombre", nombre.toString());
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        return "nuevo-cliente";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditarCliente(@PathVariable Long id, Model model, HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/entrepaginas/login";
        }

        Cliente cliente = clienteService.obtenerPorId(id);
        if (cliente == null) {
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", cliente);
        model.addAttribute("usuarioNombre", nombre.toString());
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        return "editar-cliente";
    }

    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable Long id, @ModelAttribute Cliente cliente) {
        Cliente clienteExistente = clienteService.obtenerPorId(id);
        if (clienteExistente == null) {
            return "redirect:/clientes";
        }

        // Actualizar los campos del cliente existente
        clienteExistente.setNombre(cliente.getNombre());
        clienteExistente.setCorreo(cliente.getCorreo());
        clienteExistente.setTelefono(cliente.getTelefono());
        clienteExistente.setDni(cliente.getDni());
        clienteExistente.setDireccion(cliente.getDireccion());

        clienteService.guardar(clienteExistente);
        return "redirect:/clientes";
    }//muy bien, te ganaste una papa rellena xdnajajajajajajja

    @PostMapping
    public String guardarCliente(@ModelAttribute Cliente cliente) {
        clienteService.guardar(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, HttpSession session) {
        Object nombre = session.getAttribute("usuarioNombre");
        if (nombre == null) {
            return "redirect:/clientes";
        }

        clienteService.eliminar(id);
        return "redirect:/clientes";
    }

    @GetMapping("/consultar-dni/{dni}") // Modificado para llamar a la API de RENIEC
    public ResponseEntity<Map<String, Object>> consultarDni(@PathVariable String dni) {
        Map<String, Object> response = new HashMap<>();

        if (dni == null || dni.length() != 8 || !dni.matches("\\d+")) {
            response.put("success", false);
            response.put("message", "El DNI debe tener 8 dígitos numéricos.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.set("Authorization", "Bearer " + reniecApiToken); // Usar el token inyectado

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = reniecApiUrl + dni; // Construir la URL con el DNI
            
            System.out.println("DEBUG: Llamando a RENIEC API con URL: " + url); // Depuración
            
            ResponseEntity<Map> reniecResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
            );

            if (reniecResponse.getStatusCode().is2xxSuccessful() && reniecResponse.getBody() != null) {
                Map<String, Object> reniecData = reniecResponse.getBody();
                response.put("success", true);
                response.put("data", reniecData);
                System.out.println("DEBUG: Respuesta de RENIEC: " + reniecData); // Depuración
            } else {
                response.put("success", false);
                response.put("message", "No se pudo obtener información de RENIEC. Código: " + reniecResponse.getStatusCode());
                System.err.println("ERROR: Respuesta no exitosa de RENIEC: " + reniecResponse.getStatusCode() + " - " + reniecResponse.getBody()); // Depuración
            }

        } catch (HttpClientErrorException e) {
            response.put("success", false);
            response.put("message", "Error al consultar RENIEC: " + e.getResponseBodyAsString());
            System.err.println("ERROR: HttpClientErrorException al consultar RENIEC: " + e.getMessage() + " - " + e.getResponseBodyAsString()); // Depuración
            return ResponseEntity.status(e.getStatusCode()).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al consultar RENIEC: " + e.getMessage());
            System.err.println("ERROR: Excepción general al consultar RENIEC: " + e.getMessage()); // Depuración
        }
        return ResponseEntity.ok(response);
    }
}
