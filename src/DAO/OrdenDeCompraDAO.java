package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javax.swing.JOptionPane;
import model.CentroDeCostos;
import model.Conexion;
import model.OrdenDeCompra;
import model.Proveedor;

public class OrdenDeCompraDAO {

    private Conexion con;
    
    public OrdenDeCompraDAO(Conexion cx) {
        this.con = cx;
    }

    public ObservableList<OrdenDeCompra> getOrdenes(int idordendecompra) throws SQLException {
        ObservableList<OrdenDeCompra> listaOrdenes = FXCollections.observableArrayList();
        Task<ObservableList<OrdenDeCompra>> task = new Task<ObservableList<OrdenDeCompra>>() {
            @Override
            protected ObservableList<OrdenDeCompra> call() throws Exception {
                String sql = "select oc.*, pv.*, sum(ped.cantidadsolicitada) as totales, sum(r.recibidos) as recibidos \n"
                        + "from ordendecompra oc \n"
                        + "inner join proveedor pv on pv.idproveedor=oc.idproveedor\n"
                        + "LEFT join pedidos ped on ped.idordendecompra=oc.idordendecompra\n"
                        + "left join \n"
                        + "(	select rp.idpedido, sum(rp.cantidadrecibida) as recibidos \n"
                        + "	from recepciondepedidos rp \n"
                        + "	group by rp.idpedido order by 1 asc\n"
                        + ") as r on r.idpedido=ped.idpedido\n"
                        + " "+((idordendecompra>0)?"WHERE oc.idordendecompra="+idordendecompra:" ")+" "
                        + " group by oc.idordendecompra, pv.idproveedor\n"
                        + " order by oc.numerodeorden desc;";
                
                ResultSet rs = con.CONSULTAR(sql);
                while (rs.next()) {
                    Proveedor pv = new Proveedor();
                    pv.setIdproveedor(rs.getInt("idproveedor"));
                    pv.setNombreprovee(rs.getString("nombreprovee"));
                    pv.setNitprovee(rs.getString("nitprovee"));
                    pv.setDireccionprovee(rs.getString("direccionprovee"));
                    pv.setTelefonofijoprovee(rs.getString("telefonofijoprovee"));
                    pv.setCelularprovee(rs.getString("celularprovee"));
                    pv.setCorreoprovee(rs.getString("correoprovee"));
                    pv.setPaginawebprovee(rs.getString("paginawebprovee"));
                    pv.setFechaderegistro(rs.getTimestamp("fechaderegistro").toLocalDateTime());
                    if (rs.getTimestamp("fechaactualizado") != null) {
                        pv.setFechaactualizado(rs.getTimestamp("fechaactualizado").toLocalDateTime());
                    }
                    pv.setCiudad(rs.getString("ciudad"));

//                    CentroDeCostos cc = new CentroDeCostos();
//                    cc.setIdcentro(rs.getInt("idcentrodecostos"));
//                    cc.setCentrodecostos(rs.getString("centrodecostos"));
                    OrdenDeCompra oc = new OrdenDeCompra();
                    oc.setIdordendecompra(rs.getInt("idordendecompra"));
                    oc.setNumerodeorden(rs.getInt("numerodeorden"));
                    oc.setCargoflete(rs.getString("concargoa"));
                    oc.setContacto(rs.getString("contacto"));
                    oc.setExentodeiva(rs.getBoolean("exentodeiva"));
                    oc.setFechadeorden(rs.getDate("fechadeorden").toLocalDate());
                    oc.setFormadepago(rs.getString("formadepago"));
                    oc.setIva(rs.getInt("iva"));
                    oc.setNumerodeguia(rs.getString("numerodeguia"));
                    oc.setObservaciones(rs.getString("observaciones"));
                    oc.setTransportador(rs.getString("transportador"));
                    oc.setCentrodecostos(rs.getString("centrodecostos"));
                    oc.setProveedor(pv);
                    oc.setSolicitados(rs.getDouble("totales"));
                    oc.setRecibidos(rs.getDouble("recibidos"));

                    listaOrdenes.add(oc);
                }
                return listaOrdenes;
            }
        };
        Thread t = new Thread(task);
        t.start();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(OrdenDeCompraDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int guardar(OrdenDeCompra oc) throws SQLException {
        String sql = null;
        if (oc.getIdordendecompra() == 0) {
            sql = "INSERT INTO public.ordendecompra(\n"
                    + "	fechadeorden, idproveedor, centrodecostos, "
                    + "contacto, concargoa, transportador, "
                    + "numerodeguia, formadepago, "
                    + "idusuario, observaciones, "
                    + "iva, exentodeiva, valoriva)\n"
                    + "	VALUES ('" + oc.getFechadeorden() + "', " + oc.getProveedor().getIdproveedor() + ", '" + oc.getCentrodecostos() + "', "
                    + " '" + oc.getContacto() + "', '" + oc.getCargoflete() + "', '" + oc.getTransportador() + "', "
                    + " '" + oc.getNumerodeguia() + "', '" + oc.getFormadepago() + "', "
                    + " " + model.Usuario.getInstanceUser(0, null, null, null).getIdusuario() + ", '" + oc.getObservaciones() + "', "
                    + " " + oc.getIva() + ", '" + oc.isExentodeiva() + "' , "+oc.getValoriva()+");";
        } else {
            sql = "UPDATE public.ordendecompra SET "
                    + "	fechadeorden='" + oc.getFechadeorden() + "', idproveedor=" + oc.getProveedor().getIdproveedor() + ", "
                    + "centrodecostos='" + oc.getCentrodecostos() + "', "
                    + "contacto='" + oc.getContacto() + "', concargoa='" + oc.getCargoflete() + "', "
                    + "transportador='" + oc.getTransportador() + "', "
                    + "numerodeguia='" + oc.getNumerodeguia() + "', formadepago='" + oc.getFormadepago() + "', "
                    + "idusuario=" + model.Usuario.getInstanceUser(0, null, null, null).getIdusuario() + ", "
                    + "observaciones='" + oc.getObservaciones() + "', "
                    + "iva=" + oc.getIva() + ", exentodeiva='"+oc.isExentodeiva()+"' , valoriva="+oc.getValoriva()+" "
                    + "WHERE idordendecompra=" + oc.getIdordendecompra();
        }
        System.out.println(sql);
        PreparedStatement pst = con.getCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        if (pst.executeUpdate() > 0) {
            ResultSet rs = pst.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }

        return 0;
    }
}
