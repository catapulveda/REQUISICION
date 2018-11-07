package model;

import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public class Categoria {
    
    private IntegerProperty idcategoria;
    private StringProperty nombreCategoria;
    private StringProperty descripcionCategoria;
    private ObjectProperty<LocalDate> fechaDeDegistro;
    private Usuario usuario;

    public Categoria() {
    }

    public Integer getIdcategoria() {
        return idcategoria.get();
    }

    public void setIdcategoria(Integer idcategoria) {
        this.idcategoria.set(idcategoria);
    }

    public String getNombreCategoria() {
        return nombreCategoria.get();
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria.set(nombreCategoria);
    }

    public String getDescripcionCategoria() {
        return descripcionCategoria.get();
    }

    public void setDescripcionCategoria(String descripcionCategoria) {
        this.descripcionCategoria.set(descripcionCategoria);
    }

    public LocalDate getFechaDeDegistro() {
        return fechaDeDegistro.get();
    }

    public void setFechaDeDegistro(LocalDate fechaDeDegistro) {
        this.fechaDeDegistro.set(fechaDeDegistro);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }    
    
}
