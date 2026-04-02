package com.example.Entrepaginas.service;

import com.example.Entrepaginas.model.Libro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Entrepaginas.repository.LibroRepository;

import java.util.List;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public long contarTodosLosLibros() {
        return libroRepository.count();
    }

    public long contarLibrosDisponibles() {
        return libroRepository.countByDisponible(true); //seguro que si jajajja
    }

    //metodo para listar libros con stock 
    public List<Libro> listarLibrosConStock() {
        return libroRepository.findByStockGreaterThan(0);
    }

    public long contarLibrosNoDisponibles() {
        return libroRepository.countByDisponible(false);
    }

    public List<Libro> obtenerTodos() {
        return libroRepository.findAll();
    }

    public Libro obtenerPorId(Long id) {
        return libroRepository.findById(id).orElse(null);
    }

    public Libro guardar(Libro libro) {
        return libroRepository.save(libro);
    }

    public void eliminar(Long id) {
        libroRepository.deleteById(id);
    }
}
