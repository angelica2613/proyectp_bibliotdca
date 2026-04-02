package com.example.Entrepaginas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set; // Importa java.util.Set

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String imagen; // Guarda la ruta relativa de la imagen

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "perfil_permiso",
        joinColumns = @JoinColumn(name = "perfil_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos; // Colección de permisos asociados
}