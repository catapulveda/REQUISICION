package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.BuscarTodo;
import model.Conexion;
import model.OrdenDeCompra;
import model.Producto;
import model.Proveedor;
import model.Requisicion;

public class BuscarTodoDAO {

    private Conexion con;

    public BuscarTodoDAO(Conexion con) {
        this.con = con;
    }

    public ObservableList<BuscarTodo> getTodo() {
        ObservableList<BuscarTodo> lista = FXCollections.observableArrayList();
        String sql = "SELECT r.referencia, r.numerorequisicion, oc.numerodeorden, pv.nombreprovee, p.nombreproducto FROM pedidos ped\n"
                + "INNER JOIN requisicion r ON r.idrequisicion=ped.idrequisicion\n"
                + "LEFT JOIN ordendecompra oc ON oc.idordendecompra=ped.idordendecompra\n"
                + "INNER JOIN producto p ON p.idproducto=ped.idproducto\n"
                + "LEFT JOIN proveedor pv ON pv.idproveedor=oc.idproveedor "
                + "ORDER BY numerorequisicion DESC, numerodeorden DESC;";
        con = new Conexion();
        try {
            ResultSet rs = con.CONSULTAR(sql);
            while (rs.next()) {

                Requisicion req = new Requisicion();
                req.setNumerorequisicion(rs.getInt("numerorequisicion"));
                req.setReferencia(rs.getString("referencia"));

                OrdenDeCompra oc = new OrdenDeCompra();
                oc.setNumerodeorden(rs.getInt("numerodeorden"));

                Producto p = new Producto();
                p.setNombreproducto(rs.getString("nombreproducto"));

                Proveedor pv = new Proveedor();
                if(rs.getString("nombreprovee")==null){
                    pv.setNombreprovee("***PENDIENTE***");
                }else{
                    pv.setNombreprovee(rs.getString("nombreprovee"));
                }

                BuscarTodo bt = new BuscarTodo();
                bt.setReq(req);
                bt.setOc(oc);
                bt.setP(p);
                bt.setPv(pv);

                lista.add(bt);
            }
        } catch (SQLException e) {
            System.err.println(0);
        }finally{
            con.CERRAR();
        }
        return lista;
    }
}
