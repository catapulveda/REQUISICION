package model;

public class BuscarTodo {

    private Requisicion req;
    private OrdenDeCompra oc;
    private Proveedor pv;
    private Producto p;

    public BuscarTodo() {
    }

    public Requisicion getReq() {
        return req;
    }

    public void setReq(Requisicion req) {
        this.req = req;
    }

    public OrdenDeCompra getOc() {
        return oc;
    }

    public void setOc(OrdenDeCompra oc) {
        this.oc = oc;
    }

    public Proveedor getPv() {
        return pv;
    }

    public void setPv(Proveedor pv) {
        this.pv = pv;
    }

    public Producto getP() {
        return p;
    }

    public void setP(Producto p) {
        this.p = p;
    }
    
    
}