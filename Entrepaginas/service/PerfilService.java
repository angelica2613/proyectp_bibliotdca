package com.example.Entrepaginas.service;

import com.example.Entrepaginas.model.Perfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Entrepaginas.repository.PerfilRepository;

import java.util.List;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;

    public List<Perfil> obtenerTodos() {
        return perfilRepository.findAll();
    }

    public Perfil obtenerPorId(Long id) {
        return perfilRepository.findById(id).orElse(null);
    }

    public Perfil guardar(Perfil perfil) {
        return perfilRepository.save(perfil);
    }

    public void eliminar(Long id) {
        perfilRepository.deleteById(id);
    }
}