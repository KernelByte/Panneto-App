package co.panneto.pannetousuario.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import co.panneto.pannetousuario.Activities.LoginActivity;
import co.panneto.pannetousuario.Response.LoginResponse;
import co.panneto.pannetousuario.Service.ApiUtils;
import co.panneto.pannetousuario.Service.ManagerInterface;
import co.panneto.pannetousuario.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ErrorManager {

    private Context mContext;

    public ErrorManager(Context context) {
        this.mContext = context;
    }

    public static void commonErrors(Throwable t, Activity activity) {

        TextView tvReservations, tvNotifications, tvTitleReservations, tvTitleNotifications;
        ImageView ivReservations, ivNotifications;
        String mensaje, titulo;

        tvTitleReservations = activity.findViewById(R.id.tvEmptyTitleReservations);
        tvTitleNotifications = activity.findViewById(R.id.tvEmptyTitleNotifications);

        tvReservations = activity.findViewById(R.id.tvEmptyReservations);
        tvNotifications = activity.findViewById(R.id.tvEmptyNotifications);

        ivReservations = activity.findViewById(R.id.ivEmptyReservations);
        ivNotifications = activity.findViewById(R.id.ivEmptyNotifications);

        if (t instanceof IOException) {
            titulo = activity.getString(R.string.titleErrorConexion);
            mensaje = activity.getString(R.string.txtErrorConexion);

            ivNotifications.setImageResource(R.drawable.ic_disconnected);
            ivReservations.setImageResource(R.drawable.ic_disconnected);

        } else {
            titulo = activity.getString(R.string.titleError);
            mensaje = activity.getString(R.string.txtError);

            ivNotifications.setImageResource(R.drawable.ic_error);
            ivReservations.setImageResource(R.drawable.ic_error);
        }

        tvTitleReservations.setText(titulo);
        tvTitleNotifications.setText(titulo);
        tvNotifications.setText(mensaje);
        tvReservations.setText(mensaje);
    }

    public static void commonErrors(Context context, Throwable t) {

        if (t instanceof IOException) {
            Toast.makeText(context, context.getString(R.string.txtErrorConexion), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getString(R.string.titleError), Toast.LENGTH_LONG).show();
        }
    }

    public static void successfulResponse(Activity activity) {
        TextView tvReservations, tvNotifications, tvTitleReservations, tvTitleNotifications;
        ImageView ivReservations, ivNotifications;

        tvTitleReservations = activity.findViewById(R.id.tvEmptyTitleReservations);
        tvTitleNotifications = activity.findViewById(R.id.tvEmptyTitleNotifications);

        tvReservations = activity.findViewById(R.id.tvEmptyReservations);
        tvNotifications = activity.findViewById(R.id.tvEmptyNotifications);

        ivReservations = activity.findViewById(R.id.ivEmptyReservations);
        ivNotifications = activity.findViewById(R.id.ivEmptyNotifications);

        tvTitleReservations.setText("");
        tvTitleNotifications.setText("");

        tvReservations.setText(activity.getString(R.string.tvNoReservas));
        tvNotifications.setText(activity.getString(R.string.tvNoNotifications));

        ivReservations.setImageResource(R.drawable.logopaneto);
        ivNotifications.setImageResource(R.drawable.logopaneto);
    }

    public String decryptPass() {
        String result = "";

        SharedPreferences sPref = mContext.getSharedPreferences("encr", Context.MODE_PRIVATE);
        String encryptedPass = sPref.getString("encryptedPass", "");

        try {
            KeyHelper keyHelper = new KeyHelper(mContext);
            String decr;

            if (!TextUtils.isEmpty(encryptedPass)) {

                //Envío la contraseña encriptada y me la devuelve desencriptada
                //Luego, debo enviar esta contraseña al método getToken()
                decr = keyHelper.decrypt(mContext, encryptedPass);
                result = decr;
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.sinCredenciales), Toast.LENGTH_LONG).show();
                newLogin();
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException | BadPaddingException |
                IllegalBlockSizeException | InvalidAlgorithmParameterException | KeyStoreException | CertificateException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getToken(String contrasena) {

        String token = "";

        SharedPreferences sPref = mContext.getSharedPreferences("encr", Context.MODE_PRIVATE);
        String user = sPref.getString("userEmail", "");

        ManagerInterface managerInterface;
        managerInterface = ApiUtils.getService(mContext);

        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(contrasena)) {
            //Con el usuario almacenado y la contraseña desencriptada, se generará un nuevo token
            //Para mantener la sesión iniciada

            try {
                Call<LoginResponse> call = managerInterface.login(user, contrasena);
                Response<LoginResponse> response = call.execute();

                if (response.isSuccessful()) {
                    token = "Bearer " + response.body().getToken();

                    SharedPreferences preferencias = mContext.getSharedPreferences("datos", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putString("token", token);
                    editor.apply();
                }
                else {
                    Toast.makeText(mContext, mContext.getString(R.string.sinCredenciales), Toast.LENGTH_LONG).show();
                    newLogin();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(mContext, mContext.getString(R.string.sinCredenciales), Toast.LENGTH_LONG).show();
                newLogin();
            }

        } else {
            Toast.makeText(mContext, mContext.getString(R.string.sinCredenciales), Toast.LENGTH_LONG).show();
            newLogin();
        }
        return token;
    }

    private void newLogin() {
        SharedPreferences sPref = mContext.getSharedPreferences("encr", Context.MODE_PRIVATE);
        sPref.edit().clear().apply();

        SharedPreferences preferencias = mContext.getSharedPreferences("datos", Context.MODE_PRIVATE);
        preferencias.edit().clear().apply();

        Intent login = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(login);
    }

    public String getNewToken() {
        SharedPreferences pref = mContext.getSharedPreferences("datos", Context.MODE_PRIVATE);
        String newToken = pref.getString("token", "");

        return newToken;
    }
}
