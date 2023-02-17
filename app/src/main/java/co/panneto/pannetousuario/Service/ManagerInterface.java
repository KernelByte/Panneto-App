package co.panneto.pannetousuario.Service;

import co.panneto.pannetousuario.Response.CompanyResponse;
import co.panneto.pannetousuario.Response.FavoriteResponse;
import co.panneto.pannetousuario.Response.LoginResponse;
import co.panneto.pannetousuario.Response.NotificationsResponse;
import co.panneto.pannetousuario.Response.ReserveResponse;
import co.panneto.pannetousuario.Response.UserReservationResponse;
import co.panneto.pannetousuario.Response.SignupResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ManagerInterface {

    @FormUrlEncoded
    @POST("security/login_check")
    Call<LoginResponse> login(
            @Field("_username") String username,
            @Field("_password") String password);

    @FormUrlEncoded
    @POST("security/register")
    Call<SignupResponse> registerUser(
            @Field("email") String email,
            @Field("name") String name,
            @Field("password") String password);

    @GET("v1/product/publish")
    Call<NotificationsResponse> getNotifications(
            @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("v1/publication-reserve/reserve")
    Call<ReserveResponse> sendReservation(
            @Header("Authorization") String token,
            @Field("idPublicacion") String id,
            @Field("cantidad") int cantidad);

    @GET("v1/publication-reserve/user-reserves")
    Call<UserReservationResponse> getUserReservations(
            @Header("Authorization") String token);

    @GET("v1/company/companies")
    Call<CompanyResponse> getCompaniesLocation(
            @Header("Authorization") String token);

    @GET("v1/favorite/companies")
    Call<CompanyResponse> getFavorites(
            @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("v1/favorite/favorite")
    Call<FavoriteResponse> postFavorite(
            @Header("Authorization") String token,
            @Field("company_id") String id);

    @POST("v1/favorite/companies")
    Call<CompanyResponse> deleteFavorite(
            @Header("Authorization") String token);

}
