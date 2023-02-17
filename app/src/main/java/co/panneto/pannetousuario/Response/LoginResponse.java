package co.panneto.pannetousuario.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResponse implements Serializable {

    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private int responseCode;
    @SerializedName("token")
    private String token;
    @SerializedName("data")
    @Expose
    private LoginData data;

    public String getMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getToken() {
        return token;
    }

    public LoginData getData() {
        return data;
    }
}
