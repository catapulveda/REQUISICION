package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Conexion;
import model.OrdenDeCompra;
import model.Pedido;
import model.Producto;

public class PedidoDAO {

    public PedidoDAO() {
    }
    
    public ObservableList<Pedido> getPedidos(int idrequisicion, Conexion con) throws SQLException{
        ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();
        
        String sql = "select pr.idproducto, pr.nombreproducto, pr.medida, "
        + "ped.idpedido, ped.cantidadsolicitada, ped.precioinicial, ped.observaciones, "+ 
        "oc.idordendecompra, oc.numerodeorden from pedidos ped\n" +
        "inner join requisicion req on req.idrequisicion=ped.idrequisicion\n" +
        "inner join producto pr on pr.idproducto=ped.idproducto\n" +
        "left join ordendecompra oc on oc.idordendecompra=ped.idordendecompra\n" +
        ""+((idrequisicion>0)?"WHERE ped.idrequisicion="+idrequisicion+"":"")+" ORDER BY idpedido ASC";
        ResultSet rs = con.CONSULTAR(sql);
        while(rs.next()){
            
            Producto p = new Producto();
            p.setIdproducto(rs.getInt("idproducto"));
            p.setNombreproducto(rs.getString("nombreproducto"));
            p.setMedida(rs.getString("medida"));
            
            OrdenDeCompra oc = new OrdenDeCompra();
            oc.setIdordendecompra(rs.getInt("idordendecompra"));
            oc.setNumerodeorden(rs.getInt("numerodeorden"));            
            
            Pedido ped = new Pedido();
            ped.setIdpedido(rs.getInt("idpedido"));
            ped.setProducto(p);
            ped.setCantidadsolicitada(rs.getInt("cantidadsolicitada"));
            ped.setPrecioinicial(rs.getDouble("precioinicial"));
            ped.setObservaciones((rs.getString("observaciones")!=null)?rs.getString("observaciones"):"GG");
            ped.setOc(oc);
            ped.setSelected(false);
            
            listaPedidos.add(ped);
        }
        
        return listaPedidos;
    }
}
