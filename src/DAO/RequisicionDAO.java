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
        String sql = "select req.idrequisicion, req.numerorequisicion, req.referencia, req.fechaderegistro, count(ped.*), count(ped.*)-count(oc.*) as pendientes\n"
                + "from requisicion req \n"
                + "inner join pedidos ped on ped.idrequisicion=req.idrequisicion\n"
                + "left join ordendecompra oc on oc.idordendecompra=ped.idordendecompra\n"
                + "group by req.idrequisicion\n"
                + "order by req.numerorequisicion desc;";
        Conexion con = new Conexion();
        try {
            ResultSet rs = con.CONSULTAR(sql);
            Requisicion r;
            while (rs.next()) {
                r = new Requisicion();
                r.setIdrequisicion(rs.getInt("idrequisicion"));
                r.setNumerorequisicion(rs.getInt("numerorequisicion"));
                r.setReferencia(rs.getString("referencia"));
                r.setTotalProductos(rs.getInt("count"));
                r.setFechaderegistro(rs.getTimestamp("fechaderegistro").toLocalDateTime());
                r.setTotalpendientes(rs.getInt("pendientes"));
                lista.add(r);
            }
        } catch (SQLException e) {
            Logger.getLogger(ProveedorDAO.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            con.CERRAR();
        }
        return lista;
    }

}
