package com.example.Entrepaginas.repository;

import com.example.Entrepaginas.model.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}