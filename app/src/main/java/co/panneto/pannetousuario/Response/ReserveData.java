package co.panneto.pannetousuario.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReserveData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_publication")
    @Expose
    private ProductPublication productPublication;
    @SerializedName("cantidad_reserva")
    @Expose
    private Integer cantidadReserva;
    @SerializedName("estado")
    @Expose
    private Boolean estado;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("fecha_reserva")
    @Expose
    private String fechaReserva;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCantidadReserva() {
        return cantidadReserva;
    }

    public void setCantidadReserva(Integer cantidadReserva) {
        this.cantidadReserva = cantidadReserva;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ProductPublication getProductPublication() {
        return productPublication;
    }

    public void setProductPublication(ProductPublication productPublication) {
        this.productPublication = productPublication;
    }
}
