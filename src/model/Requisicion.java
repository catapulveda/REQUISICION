package model;

import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Requisicion {

    private IntegerProperty idrequisicion;
    private IntegerProperty numerorequisicion;
    private StringProperty referencia;
    private ObjectProperty<LocalDateTime> fechaderegistro;
    private IntegerProperty totalProductos;
    private Usuario usuario;
    
    public Requisicion() {
        this.idrequisicion = new SimpleIntegerProperty(0);
        this.numerorequisicion = new SimpleIntegerProperty(0);
        this.referencia = new SimpleStringProperty();
        this.fechaderegistro = new SimpleObjectProperty<>(LocalDateTime.now());
        this.totalProductos = new SimpleIntegerProperty();
    }

    public final int getTotalProductos() {
        return totalProductos.get();
    }

    public final void setTotalProductos(int value) {
        totalProductos = new SimpleIntegerProperty(value);
    }

    public IntegerProperty totalProductosProperty() {
        return totalProductos;
    }
    
    public final int getIdrequisicion() {
        return idrequisicion.get();
    }

    public final void setIdrequisicion(int value) {
        idrequisicion = new SimpleIntegerProperty(value);
    }

    public IntegerProperty idrequisicionProperty() {
        return idrequisicion;
    }

    public final int getNumerorequisicion() {
        return numerorequisicion.get();
    }

    public final void setNumerorequisicion(int value) {
        numerorequisicion = new SimpleIntegerProperty(value);
    }

    public IntegerProperty numerorequisicionProperty() {
        return numerorequisicion;
    }

    public final String getReferencia() {
        return referencia.get();
    }

    public final void setReferencia(String value) {
        referencia = new SimpleStringProperty(value);
    }

    public StringProperty referenciaProperty() {
        return referencia;
    }

    public final LocalDateTime getFechaderegistro() {
        return fechaderegistro.get();
    }

    public final void setFechaderegistro(LocalDateTime value) {
        fechaderegistro = new SimpleObjectProperty<>(value);
    }

    public ObjectProperty<LocalDateTime> fechaderegistroProperty() {
        return fechaderegistro;
    }

    @Override
    public String toString() {
        return "# " + numerorequisicion.get() + " REF: " + referencia.get();
    }
    
    
}
