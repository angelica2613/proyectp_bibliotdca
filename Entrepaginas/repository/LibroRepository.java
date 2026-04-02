package com.example.Entrepaginas.repository;

import com.example.Entrepaginas.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByGenero(String genero);

    @Query("SELECT l FROM Libro l WHERE "
            + "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR "
            + "LOWER(l.autor) LIKE LOWER(CONCAT('%', :query, '%')) OR "
            + "LOWER(l.genero) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Libro> buscarPorQuery(@Param("query") String query);

    Long countByDisponibleTrue();

    Long countByDisponibleFalse();

    Long countByDisponible(boolean disponible);

    //metodo para listar libros con stock
    List<Libro> findByStockGreaterThan(int stock);

    Libro findFirstByTitulo(String titulo);

    Libro findFirstByTituloAndStockGreaterThan(String titulo, int stock);
}
