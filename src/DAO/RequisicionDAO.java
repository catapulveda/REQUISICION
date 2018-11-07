package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Conexion;
import model.Requisicion;

public class RequisicionDAO {

    public ObservableList<Requisicion> getRequisiciones() {
        ObservableList<Requisicion> lista = FXCollections.observableArrayList();
        String sql = "select count(ped.idrequisicion), r.idrequisicion, r.numerorequisicion, r.referencia, r.fechaderegistro \n" +
        "from requisicion r \n" +
        "left join pedidos ped on ped.idrequisicion=r.idrequisicion\n" +
        "group by r.idrequisicion\n" +
        "order by r.idrequisicion DESC ;";
        Conexion con = new Conexion();
        try {
            ResultSet rs = con.CONSULTAR(sql);
            Requisicion r;
            while(rs.next()){
                r = new Requisicion();
                r.setIdrequisicion(rs.getInt("idrequisicion"));
                r.setNumerorequisicion(rs.getInt("numerorequisicion"));
                r.setReferencia(rs.getString("referencia"));
                r.setTotalProductos(rs.getInt("count"));
                r.setFechaderegistro(rs.getTimestamp("fechaderegistro").toLocalDateTime());
                lista.add(r);
            }
        } catch (SQLException e) {
            Logger.getLogger(ProveedorDAO.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            con.CERRAR();
        }
        return lista;
    }

    
    
}
