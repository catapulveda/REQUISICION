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

public class RecepcionDePedido {
    
    private IntegerProperty idrecepciondepedido;
    private Pedido pedido;
    private ObjectProperty<LocalDate> fechaderecibido;
    private DoubleProperty cantidadrecibida;
    private DoubleProperty preciofinal;
    private StringProperty factura;
    private StringProperty remision;
    private StringProperty observaciones;
    private Usuario usuario;
    private ObjectProperty<LocalDate> fechaderegistro;
    private byte[] archivo;
    private StringProperty formato;
    private StringProperty nombrearchivo;        
    private final BooleanProperty seleccionado = new SimpleBooleanProperty(false);

    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    public void setSeleccionado(boolean value) {
        seleccionado.set(value);
    }

    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }

    public RecepcionDePedido() {
    }        

    public final int getIdrecepciondepedido() {
        return idrecepciondepedido.get();
    }

    public final void setIdrecepciondepedido(int value) {
        idrecepciondepedido = new SimpleIntegerProperty(value);
    }

    public IntegerProperty idrecepciondepedidoProperty() {
        return idrecepciondepedido;
    }

    public final LocalDate getFechaderecibido() {
        return fechaderecibido.get();
    }

    public final void setFechaderecibido(LocalDate value) {
        fechaderecibido = new SimpleObjectProperty<>(value);
    }

    public ObjectProperty<LocalDate> fechaderecibidoProperty() {
        return fechaderecibido;
    }

    public final double getCantidadrecibida() {
        return cantidadrecibida.get();
    }

    public final void setCantidadrecibida(double value) {
        cantidadrecibida = new SimpleDoubleProperty(value);
    }

    public DoubleProperty cantidadrecibidaProperty() {
        return cantidadrecibida;
    }

    public final double getPreciofinal() {
        return preciofinal.get();
    }

    public final void setPreciofinal(double value) {
        preciofinal = new SimpleDoubleProperty(value);
    }

    public DoubleProperty preciofinalProperty() {
        return preciofinal;
    }

    public final String getFactura() {
        return factura.get();
    }

    public final void setFactura(String value) {
        factura = new SimpleStringProperty(value);
    }

    public StringProperty facturaProperty() {
        return factura;
    }

    public final String getRemision() {
        return remision.get();
    }

    public final void setRemision(String value) {
        remision = new SimpleStringProperty(value);
    }

    public StringProperty remisionProperty() {
        return remision;
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
    
    public final String getFormato() {
        return formato.get();
    }

    public final void setFormato(String value) {
        formato = new SimpleStringProperty(value);
    }

    public StringProperty formatoProperty() {
        return formato;
    }
    
    public final String getNombreArchivo() {
        return nombrearchivo.get();
    }

    public final void setNombreArchivo(String value) {
        nombrearchivo = new SimpleStringProperty(value);
    }

    public StringProperty nombreArchivoProperty() {
        return nombrearchivo;
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

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }        
    
}
