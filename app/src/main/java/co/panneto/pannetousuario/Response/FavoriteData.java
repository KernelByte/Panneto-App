package co.panneto.pannetousuario.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoriteData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("company")
    @Expose
    private CompanyData data = null;
    @SerializedName("is_favorite")
    @Expose
    private Boolean favorite;

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CompanyData getData() {
        return data;
    }

    public void setData(CompanyData data) {
        this.data = data;
    }
}
