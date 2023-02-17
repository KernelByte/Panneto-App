package co.panneto.pannetousuario.Service;

import android.content.Context;

import co.panneto.pannetousuario.R;

public class ApiUtils {

    public static ManagerInterface getService(Context context) {
        String url = context.getString(R.string.urlApiRoute) + context.getString(R.string.urlApi);
        return RetrofitClient.getClient(url).create(ManagerInterface.class);
    }
}
