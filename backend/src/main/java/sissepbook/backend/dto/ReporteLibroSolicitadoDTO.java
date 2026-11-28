package sissepbook.backend.dto;

/** Fila del reporte "libros más solicitados". */
public class ReporteLibroSolicitadoDTO {

    private int idLibro;
    private String titulo;
    private String autor;
    private String categoria;
    private int totalPrestamos;
    private int lectoresDistintos;
    private int ejemplaresDisponibles;

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getTotalPrestamos() { return totalPrestamos; }
    public void setTotalPrestamos(int totalPrestamos) { this.totalPrestamos = totalPrestamos; }

    public int getLectoresDistintos() { return lectoresDistintos; }
    public void setLectoresDistintos(int lectoresDistintos) { this.lectoresDistintos = lectoresDistintos; }

    public int getEjemplaresDisponibles() { return ejemplaresDisponibles; }
    public void setEjemplaresDisponibles(int ejemplaresDisponibles) { this.ejemplaresDisponibles = ejemplaresDisponibles; }
}
