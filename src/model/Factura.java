package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Factura {
    
    private int idfactura;
    private byte[] archivo;
    private String formato;
    private String nombrearchivo;
    private LocalDateTime fecharegistro;
    private int idordendecompra;

    public Factura() {
    }        

    public int getIdfactura() {
        return idfactura;
    }

    public void setIdfactura(int idfactura) {
        this.idfactura = idfactura;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getNombrearchivo() {
        return nombrearchivo;
    }

    public void setNombrearchivo(String nombrearchivo) {
        this.nombrearchivo = nombrearchivo;
    }

    public LocalDateTime getFecharegistro() {
        return fecharegistro;
    }

    public void setFecharegistro(LocalDateTime fecharegistro) {
        this.fecharegistro = fecharegistro;
    }

    public int getIdordendecompra() {
        return idordendecompra;
    }

    public void setIdordendecompra(int idordendecompra) {
        this.idordendecompra = idordendecompra;
    }
    
    public Path getFile() throws IOException{        
        return Files.write(File.createTempFile(this.nombrearchivo, "."+this.formato).toPath(), getArchivo());
    }

    @Override
    public String toString() {
        return  nombrearchivo+"."+formato;
    }
    
    
}
