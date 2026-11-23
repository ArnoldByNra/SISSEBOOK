package sissepbook.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Representa un libro (título) del catálogo. */
public class LibroDTO {

    private Integer idLibro;

    @NotBlank(message = "El ISBN es obligatorio.")
    private String isbn;

    @NotBlank(message = "El título es obligatorio.")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio.")
    private String autor;

    @NotBlank(message = "La editorial es obligatoria.")
    private String editorial;

    @NotNull(message = "La categoría es obligatoria.")
    private Integer idCategoria;

    private String nombreCategoria;

    @NotNull(message = "El año de publicación es obligatorio.")
    private Integer anioPublicacion;

    private boolean activo = true;

    private int ejemplaresDisponibles;

    public Integer getIdLibro() { return idLibro; }
    public void setIdLibro(Integer idLibro) { this.idLibro = idLibro; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public Integer getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(Integer anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public int getEjemplaresDisponibles() { return ejemplaresDisponibles; }
    public void setEjemplaresDisponibles(int ejemplaresDisponibles) { this.ejemplaresDisponibles = ejemplaresDisponibles; }
}
