package com.example.Entrepaginas.service;

import com.example.Entrepaginas.model.Libro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Entrepaginas.repository.LibroRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CatalagoService {
    @Autowired
    private LibroRepository libroRepository;

    public List<Libro> getAllLibros() {
        return libroRepository.findAll();
    }

    public List<Libro> getLibrosByGenero(String genero) {
        if (genero == null || genero.isEmpty()) {
            return getAllLibros();
        }
        return libroRepository.findByGenero(genero);
    }

    public List<Libro> buscarLibros(String query) {
        if (query == null || query.isEmpty()) {
            return getAllLibros();
        }
        return libroRepository.buscarPorQuery(query);
    }

    public Optional<Libro> getLibroById(Long id) {
        return libroRepository.findById(id);
    }

    public Libro prestarLibro(Long id) {
        Optional<Libro> optionalLibro = getLibroById(id);
        if (optionalLibro.isPresent())
            if (optionalLibro.get().isDisponible()) {
                Libro libro = optionalLibro.get();
                libro.setDisponible(false);
                return libroRepository.save(libro);
            }
        return null;
    }

    public Libro devolverLibro(Long id) {
        Optional<Libro> optionalLibro = getLibroById(id);
        if (optionalLibro.isPresent()) {
            Libro libro = optionalLibro.get();
            libro.setDisponible(true);
            return libroRepository.save(libro);
        }
        return null;
    }

    public Libro saveLibro(Libro libro) {
        return libroRepository.save(libro);
    }
}