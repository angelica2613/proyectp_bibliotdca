package com.example.Entrepaginas.repository;
import com.example.Entrepaginas.model.Permiso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    List<Permiso> findByNombre(String nombre);

}
