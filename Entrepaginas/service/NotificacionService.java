package com.example.Entrepaginas.service;

import com.example.Entrepaginas.model.Libro;
import com.example.Entrepaginas.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    @Autowired
    private LibroRepository libroRepository;

    public void verificarStockBajo() {
        List<Libro> librosBajoStock = libroRepository.findAll().stream()
                .filter(l -> l.getStock() < 5)
                .collect(Collectors.toList());

        if (!librosBajoStock.isEmpty()) {
            System.out.println("=== ALERTA DE STOCK BAJO ===");
            for (Libro libro : librosBajoStock) {
                System.out.println("Libro: " + libro.getTitulo() + " | Stock actual: " + libro.getStock());
            }
            System.out.println("============================");
        }
    }
}
