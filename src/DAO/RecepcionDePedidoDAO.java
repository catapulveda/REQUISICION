package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Conexion;
import model.Pedido;
import model.RecepcionDePedido;

public class RecepcionDePedidoDAO {

    public RecepcionDePedidoDAO() {
    }
    
    public ObservableList<RecepcionDePedido> getPedidos(Pedido pedido, Conexion con) throws SQLException{
        ObservableList<RecepcionDePedido> recepciones = FXCollections.observableArrayList();        
        String sql = "SELECT rp.*, u.nombreusuario FROM recepciondepedidos rp\n" +
        "INNER JOIN pedidos ped ON ped.idpedido=rp.idpedido\n" +
        "INNER JOIN usuario u ON u.idusuario=rp.idusuario\n" +
        "WHERE rp.idpedido="+pedido.getIdpedido()+";";
        ResultSet rs = con.CONSULTAR(sql);
        while(rs.next()){
            RecepcionDePedido rp = new RecepcionDePedido();
            rp.setIdrecepciondepedido(rs.getInt("idrecepciondepedido"));
            rp.setCantidadrecibida(rs.getInt("cantidadrecibida"));
            rp.setFactura(rs.getString("factura"));
            rp.setFechaderecibido(rs.getDate("fechaderecibido").toLocalDate());
            rp.setFechaderegistro(rs.getDate("fechaderegistro").toLocalDate());
            rp.setObservaciones(rs.getString("observaciones"));
            rp.setPedido(pedido);
            rp.setPreciofinal(rs.getDouble("preciofinal"));
            rp.setRemision(rs.getString("remision"));

            recepciones.add(rp);
        }        
        return recepciones;
    }
    
    public int guardar(RecepcionDePedido rp, Conexion con) throws SQLException{
        String sql = "INSERT INTO recepciondepedidos (idpedido,fechaderecibido,cantidadrecibida,preciofinal, ";
               sql += "factura,remision,observaciones,idusuario)";
               sql += "VALUES ("+rp.getPedido().getIdpedido()+" , '"+rp.getFechaderecibido()+"' , "+rp.getCantidadrecibida()+" , "+rp.getPreciofinal()+" , ";
               sql += " '"+rp.getFactura()+"' , '"+rp.getRemision()+"' , '"+rp.getObservaciones()+"' , ";
               sql += " "+model.Usuario.getInstanceUser(0, null, null, null).getIdusuario()+" ) ";        
        PreparedStatement pst = con.getCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        if(pst.executeUpdate()>0){
            ResultSet rs = pst.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
        return 0;
    }
}
