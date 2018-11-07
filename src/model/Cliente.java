package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cliente {
    
    private IntegerProperty idcliente;
    private StringProperty nombrecliente;

    public final int getIdcliente() {
        return idcliente.get();
    }

    public final void setIdcliente(int value) {
        idcliente = new SimpleIntegerProperty(value);
    }

    public IntegerProperty idclienteProperty() {
        return idcliente;
    }

    public final String getNombrecliente() {
        return nombrecliente.get();
    }

    public final void setNombrecliente(String value) {
        nombrecliente = new SimpleStringProperty(value);
    }

    public StringProperty nombreclienteProperty() {
        return nombrecliente;
    }

    @Override
    public String toString() {
        return nombrecliente.get();
    }
    
    
}
