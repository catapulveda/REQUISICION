package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CentroDeCostos {
    
    private IntegerProperty idcentro;
    private StringProperty centrodecostos;
    private Cliente cliente;

    public CentroDeCostos() {
        
    }   
    
    public final int getIdcentro() {
        return idcentro.get();
    }

    public final void setIdcentro(int value) {
        idcentro = new SimpleIntegerProperty(value);
    }

    public IntegerProperty idcentroProperty() {
        return idcentro;
    }

    public final String getCentrodecostos() {
        return centrodecostos.get();
    }

    public final void setCentrodecostos(String value) {
        centrodecostos = new SimpleStringProperty(value);
    }

    public StringProperty centrodecostosProperty() {
        return centrodecostos;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return centrodecostos.get()+" / "+getCliente().getNombrecliente();
    }
    
    
    
}
