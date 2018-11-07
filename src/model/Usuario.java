package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario {

    private IntegerProperty idusuario;
    private StringProperty nombreusuario;
    private StringProperty user;
    private StringProperty password;
    private static Usuario usuario;

    public static Usuario getInstanceUser(int id, String nombreusuario, String user, String password) {
        if (usuario==null) {
            usuario=new Usuario(id, nombreusuario, user, password);
        }
        return usuario;
    }

    public Usuario(Integer idusuario, String nombreusuario, String user, String password) {
        this.idusuario = new SimpleIntegerProperty(idusuario);
        this.nombreusuario = new SimpleStringProperty(nombreusuario);
        this.user = new SimpleStringProperty(user);
        this.password = new SimpleStringProperty(password);
    }

    public Usuario(String nombreusuario){
        this.nombreusuario = new SimpleStringProperty(nombreusuario);
    }    
        
    public final int getIdusuario() {
        return idusuario.get();
    }

    public final void setIdusuario(int value) {
        idusuario = new SimpleIntegerProperty(value);
    }

    public IntegerProperty idusuarioProperty() {
        return idusuario;
    }

    public final String getNombreusuario() {
        return nombreusuario.get();
    }

    public final void setNombreusuario(String value) {
        nombreusuario = new SimpleStringProperty(value);
    }

    public StringProperty nombreusuarioProperty() {
        return nombreusuario;
    }

    public final String getUsuario() {
        return user.get();
    }

    public final void setUsuario(String value) {
        user = new SimpleStringProperty(value);
    }

    public StringProperty usuarioProperty() {
        return user;
    }

    public final String getPassword() {
        return password.get();
    }

    public final void setPassword(String value) {
        password = new SimpleStringProperty(value);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    @Override
    public String toString() {
        return nombreusuario.get();
    }
    
    
       
}
