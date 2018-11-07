package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Conexion;
import model.Cotizacion;

public class CotizacionDAO {

    public CotizacionDAO() {
    }
    
    public ObservableList<Cotizacion> getCotizaciones(int idrequisicion, Conexion con) throws SQLException{
        ObservableList<Cotizacion> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM cotizaciones WHERE idrequisicion="+idrequisicion;
        ResultSet rs = con.CONSULTAR(sql);
        while(rs.next()){
            Cotizacion cot = new Cotizacion();
            cot.setId(rs.getInt("idcotizacion"));
            cot.setArchivo(rs.getBytes("archivo"));
            cot.setFormato(rs.getString("formato"));
            cot.setNombre(rs.getString("nombrearchivo"));
            cot.setFecha(rs.getTimestamp("fechaderegistro").toLocalDateTime());
            
            lista.add(cot);
        }
        return lista;
    }
    
    public int delete(Cotizacion c, Conexion con) throws SQLException{
        return con.GUARDAR("DELETE FROM cotizaciones WHERE idcotizacion="+c.getId());
    }
}
