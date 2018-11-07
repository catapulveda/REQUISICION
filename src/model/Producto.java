package model;

import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Producto {
    
    private IntegerProperty idproducto;
    private SimpleStringProperty codigoproducto;
    private SimpleStringProperty nombreproducto;
    private StringProperty medida;
    private byte[] imagen;
    private ObjectProperty<LocalDate> fechaderegistro;
    private Usuario usuario; 
    private Categoria categoria;
    private BooleanProperty selected;      

    public Producto(){
    }
    
    public Integer getIdproducto() {
        return idproducto.get();
    }

    public void setIdproducto(Integer idproducto) {
        this.idproducto = new SimpleIntegerProperty(idproducto);
    }

    public String getCodigoproducto() {
        return codigoproducto.get();
    }

    public void setCodigoproducto(String codigoproducto) {
        this.codigoproducto = new SimpleStringProperty(codigoproducto);
    }

    public String getNombreproducto() {
        return nombreproducto.get();
    }

    public void setNombreproducto(String nombreproducto) {
        this.nombreproducto = new SimpleStringProperty(nombreproducto);
    }

    public String getMedida() {
        return medida.get();
    }

    public void setMedida(String medida) {
        this.medida = new SimpleStringProperty(medida);
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public LocalDate getFechaderegistro() {
        return fechaderegistro.get();
    }

    public void setFechaderegistro(LocalDate fechaderegistro) {
        this.fechaderegistro = new SimpleObjectProperty<>(fechaderegistro);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }  

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public final boolean isSelected() {
        return selected.get();
    }

    public final void setSelected(boolean value) {
        selected = new SimpleBooleanProperty(value);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    @Override
    public String toString() {
        return nombreproducto.get();
    }
    
    
}