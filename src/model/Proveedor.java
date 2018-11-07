package model;

import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Proveedor {

    private IntegerProperty idproveedor;
    private StringProperty nombreprovee;
    private StringProperty nitprovee;
    private StringProperty direccionprovee;
    private StringProperty telefonofijoprovee;
    private StringProperty celularprovee;
    private StringProperty correoprovee;
    private StringProperty paginawebprovee;
    private ObjectProperty<LocalDateTime> fechaderegistro;
    private ObjectProperty<LocalDateTime> fechaactualizado;
    private StringProperty ciudad;
    private Usuario usuario;       

    public Proveedor() {        
    }
    
    public final int getIdproveedor() {
        return idproveedor.get();
    }

    public final void setIdproveedor(int value) {
        idproveedor = new SimpleIntegerProperty(value);
    }

    public IntegerProperty idproveedorProperty() {
        return idproveedor;
    }

    public final String getNombreprovee() {
        return nombreprovee.get();
    }

    public final void setNombreprovee(String value) {
        nombreprovee = new SimpleStringProperty(value);
    }

    public StringProperty nombreproveeProperty() {
        return nombreprovee;
    }

    public final String getNitprovee() {
        return nitprovee.get();
    }

    public final void setNitprovee(String value) {
        nitprovee = new SimpleStringProperty(value);
    }

    public StringProperty nitproveeProperty() {
        return nitprovee;
    }

    public final String getDireccionprovee() {
        return direccionprovee.get();
    }

    public final void setDireccionprovee(String value) {
        direccionprovee = new SimpleStringProperty(value);
    }

    public StringProperty direccionproveeProperty() {
        return direccionprovee;
    }

    public final String getTelefonofijoprovee() {
        return telefonofijoprovee.get();
    }

    public final void setTelefonofijoprovee(String value) {
        telefonofijoprovee = new SimpleStringProperty(value);
    }

    public StringProperty telefonofijoproveeProperty() {
        return telefonofijoprovee;
    }

    public final String getCelularprovee() {
        return celularprovee.get();
    }

    public final void setCelularprovee(String value) {
        celularprovee = new SimpleStringProperty(value);
    }

    public StringProperty celularproveeProperty() {
        return celularprovee;
    }
    
    public final String getCorreoprovee() {
        return correoprovee.get();
    }

    public final void setCorreoprovee(String value) {
        correoprovee = new SimpleStringProperty(value);
    }

    public StringProperty correoproveeProperty() {
        return correoprovee;
    }

    public final String getPaginawebprovee() {
        return paginawebprovee.get();
    }

    public final void setPaginawebprovee(String value) {
        paginawebprovee = new SimpleStringProperty(value);
    }

    public StringProperty paginawebproveeProperty() {
        return paginawebprovee;
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

    public final LocalDateTime getFechaactualizado() {
        return fechaactualizado.get();
    }

    public final void setFechaactualizado(LocalDateTime value) {
        fechaactualizado = new SimpleObjectProperty<>(value);
    }

    public ObjectProperty<LocalDateTime> fechaactualizadoProperty() {
        return fechaactualizado;
    }

    public final String getCiudad() {
        return ciudad.get();
    }

    public final void setCiudad(String value) {
        ciudad = new SimpleStringProperty(value);
    }

    public StringProperty ciudadProperty() {
        return ciudad;
    }        

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return nombreprovee.get();
    }
    
}
