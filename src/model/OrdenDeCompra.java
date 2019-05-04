package model;

import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OrdenDeCompra {
    
    private IntegerProperty idordendecompra;
    private IntegerProperty numerodeorden;
    private ObjectProperty<LocalDate> fechadeorden;
    private Proveedor proveedor;
    private StringProperty centrodecostos;
    private StringProperty contacto;
    private StringProperty cargoflete;
    private StringProperty transportador;
    private StringProperty numerodeguia;
    private StringProperty formadepago;
    private StringProperty observaciones;
    private IntegerProperty iva;
    private BooleanProperty exentodeiva;
    private final DoubleProperty valoriva = new SimpleDoubleProperty();   
    
    private Usuario idusuario;
    private ObjectProperty<LocalDate> fechaderegistro;
    private final DoubleProperty solicitados = new SimpleDoubleProperty();
    private final DoubleProperty recibidos = new SimpleDoubleProperty();   
        
    public OrdenDeCompra() {
    }
    
    public double getValoriva() {
        return valoriva.get();
    }

    public void setValoriva(double value) {
        valoriva.set(value);
    }

    public DoubleProperty valorivaProperty() {
        return valoriva;
    }

    public final int getIdordendecompra() {
        return idordendecompra.get();
    }

    public final void setIdordendecompra(int value) {
        idordendecompra = new SimpleIntegerProperty(value);
    }

    public IntegerProperty idordendecompraProperty() {
        return idordendecompra;
    }

    public final int getNumerodeorden() {
        return numerodeorden.get();
    }

    public final void setNumerodeorden(int value) {
        numerodeorden = new SimpleIntegerProperty(value);
    }

    public IntegerProperty numerodeordenProperty() {
        return numerodeorden;
    }

    public final LocalDate getFechadeorden() {
        return fechadeorden.get();
    }

    public final void setFechadeorden(LocalDate value) {
        fechadeorden = new SimpleObjectProperty<>(value);
    }

    public ObjectProperty<LocalDate> fechadeordenProperty() {
        return fechadeorden;
    }

    public final String getContacto() {
        return contacto.get();
    }

    public final void setContacto(String value) {
        contacto = new SimpleStringProperty(value);
    }

    public StringProperty contactoProperty() {
        return contacto;
    }

    public final String getCargoflete() {
        return cargoflete.get();
    }

    public final void setCargoflete(String value) {
        cargoflete = new SimpleStringProperty(value);
    }

    public StringProperty cargofleteProperty() {
        return cargoflete;
    }

    public final String getTransportador() {
        return transportador.get();
    }

    public final void setTransportador(String value) {
        transportador = new SimpleStringProperty(value);
    }

    public StringProperty transportadorProperty() {
        return transportador;
    }

    public final String getNumerodeguia() {
        return numerodeguia.get();
    }

    public final void setNumerodeguia(String value) {
        numerodeguia = new SimpleStringProperty(value);
    }

    public StringProperty numerodeguiaProperty() {
        return numerodeguia;
    }

    public final String getFormadepago() {
        return formadepago.get();
    }

    public final void setFormadepago(String value) {
        formadepago = new SimpleStringProperty(value);
    }

    public StringProperty formadepagoProperty() {
        return formadepago;
    }

    public final String getObservaciones() {
        return observaciones.get();
    }

    public final void setObservaciones(String value) {
        observaciones = new SimpleStringProperty(value);
    }

    public StringProperty observacionesProperty() {
        return observaciones;
    }

    public final int getIva() {
        return iva.get();
    }

    public final void setIva(int value) {
        iva = new SimpleIntegerProperty(value);
    }

    public IntegerProperty ivaProperty() {
        return iva;
    }

    public final boolean isExentodeiva() {
        return exentodeiva.get();
    }

    public final void setExentodeiva(boolean value) {
        exentodeiva = new SimpleBooleanProperty(value);
    }

    public BooleanProperty exentodeivaProperty() {
        return exentodeiva;
    }

    public final LocalDate getFechaderegistro() {
        return fechaderegistro.get();
    }

    public final void setFechaderegistro(LocalDate value) {
        fechaderegistro = new SimpleObjectProperty<>(value);
    }

    public ObjectProperty<LocalDate> fechaderegistroProperty() {
        return fechaderegistro;
    }   

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }             

    public String getCentrodecostos() {
        return centrodecostos.get();
    }

    public void setCentrodecostos(String centrodecostos) {
        this.centrodecostos = new SimpleStringProperty(centrodecostos);
    }   
    
    public double getRecibidos() {
        return recibidos.get();
    }

    public void setRecibidos(double value) {
        recibidos.set(value);
    }

    public DoubleProperty recibidosProperty() {
        return recibidos;
    }    

    public double getSolicitados() {
        return solicitados.get();
    }

    public void setSolicitados(double value) {
        solicitados.set(value);
    }

    public DoubleProperty solicitadosProperty() {
        return solicitados;
    }
    
    @Override
    public String toString() {
        return ""+numerodeorden.get();
    }
}
