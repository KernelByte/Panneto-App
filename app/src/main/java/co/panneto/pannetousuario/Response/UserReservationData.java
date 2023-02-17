package co.panneto.pannetousuario.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserReservationData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nombreProducto")
    @Expose
    private String nombreProducto;
    @SerializedName("cantidadReserva")
    @Expose
    private String cantidadReserva;
    @SerializedName("imagen")
    @Expose
    private String imagen;
    @SerializedName("codigoReserva")
    @Expose
    private String codigoReserva;
    @SerializedName("precioTotal")
    @Expose
    private String precioTotal;
    @SerializedName("nombreEmpresa")
    @Expose
    private String nombreEmpresa;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCantidadReserva() {
        return cantidadReserva;
    }

    public void setCantidadReserva(String cantidadReserva) {
        this.cantidadReserva = cantidadReserva;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
    }

    public String getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(String precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }
}
