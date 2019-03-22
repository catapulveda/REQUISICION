package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cotizacion {
    
    private int id;
    private String nombre;
    private String formato;
    private byte[] archivo;
    private LocalDateTime fecha;

    public Cotizacion(int id, String nombre, String formato, byte[] archivo) {
        this.id = id;
        this.nombre = nombre;
        this.formato = formato;
        this.archivo = archivo;
    }   

    public Cotizacion() {
    }    
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }        

    @Override
    public String toString() {
        return nombre+"."+formato; //To change body of generated methods, choose Tools | Templates.
    }       

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public Path getFile() throws IOException{        
        return Files.write(File.createTempFile(this.nombre, "."+this.formato).toPath(), getArchivo());
    }

}