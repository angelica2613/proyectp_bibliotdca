package com.example.Entrepaginas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // This annotation generates the correct getRol(), setRol(), etc.
@Table(name = "usuario") // Good practice to explicitly name your table
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    // REMOVED: public String getRol; (Incorrect field)
    // REMOVED: public Object getRol() { return null; } (Incorrect manual method)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena; // Note: In a real app, this should be HASHED.

    @Column(nullable = false)
    private String rol; // Lombok will generate getRol() and setRol() for this field.

}