package model;

import java.time.LocalDateTime;
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

public class Pedido {
    
    private IntegerProperty idpedido;
    private Requisicion requisicion;
    private Producto producto;
    private IntegerProperty cantidadsolicitada;
    private DoubleProperty precioinicial;
    private StringProperty observaciones;
    private ObjectProperty<LocalDateTime> fechaRegistro;
    private OrdenDeCompra oc;
    private BooleanProperty selected; 

    public Pedido() {
    }    
    
    public final int getIdpedido() {
        return idpedido.get();
    }

    public final void setIdpedido(int value) {
        idpedido = new SimpleIntegerProperty(value);
    }

    public IntegerProperty idpedidoProperty() {
        return idpedido;
    }

    public final int getCantidadsolicitada() {
        return cantidadsolicitada.get();
    }

    public final void setCantidadsolicitada(int value) {
        cantidadsolicitada = new SimpleIntegerProperty(value);
    }

    public IntegerProperty cantidadsolicitadaProperty() {
        return cantidadsolicitada;
    }

    public final double getPrecioinicial() {
        return precioinicial.get();
    }

    public final void setPrecioinicial(double value) {
        precioinicial = new SimpleDoubleProperty(value);
    }

    public DoubleProperty precioinicialProperty() {
        return precioinicial;
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

    public final LocalDateTime getFechaRegistro() {
        return fechaRegistro.get();
    }

    public final void setFechaRegistro(LocalDateTime value) {
        fechaRegistro = new SimpleObjectProperty<>(value);
    }

    public ObjectProperty<LocalDateTime> fechaRegistroProperty() {
        return fechaRegistro;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Requisicion getRequisicion() {
        return requisicion;
    }

    public void setRequisicion(Requisicion requisicion) {
        this.requisicion = requisicion;
    }

    public OrdenDeCompra getOc() {
        return oc;
    }

    public void setOc(OrdenDeCompra oc) {
        this.oc = oc;
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
        return "Pedido{" + "idpedido=" + idpedido + ", cantidadsolicitada=" + cantidadsolicitada + ", precioinicial=" + precioinicial + ", observaciones=" + observaciones + '}';
    }        
}
