package com.example.Entrepaginas.repository;

import com.example.Entrepaginas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByCorreo(String correo);
}