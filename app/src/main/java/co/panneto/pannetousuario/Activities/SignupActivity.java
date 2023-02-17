package co.panneto.pannetousuario.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.panneto.pannetousuario.Response.SignupResponse;
import co.panneto.pannetousuario.Service.ApiUtils;
import co.panneto.pannetousuario.Utils.ErrorManager;
import co.panneto.pannetousuario.Service.ManagerInterface;
import co.panneto.pannetousuario.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCorreo, etNombreUsuario, etContrasena, etContrasena2;
    private String correo, nombreUsuario, contrasena;
    private ProgressDialog loadingDialog = null;
    private ManagerInterface managerInterface;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_signup);

        etCorreo = findViewById(R.id.txtCorreoRegistro);
        etNombreUsuario = findViewById(R.id.txtNombreUsuario);
        etContrasena = findViewById(R.id.txtPasswordRegistro);
        etContrasena2 = findViewById(R.id.txtPasswordRegistro2);
        Button btnRegistrar = findViewById(R.id.btnRegistrarCliente);
        btnRegistrar.setOnClickListener(this);
        loadingDialog = new ProgressDialog(this);
        managerInterface = ApiUtils.getService(getApplicationContext());
    }

    boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onClick(View v) {

        correo = etCorreo.getText().toString().trim();
        nombreUsuario = etNombreUsuario.getText().toString().trim();
        contrasena = etContrasena.getText().toString().trim();
        String contrasena2 = etContrasena2.getText().toString().trim();

        if (correo.isEmpty()) {
            etCorreo.setError(getString(R.string.emailObligatorio));
            etCorreo.requestFocus();
        } else if (!isEmailValid(correo)) {
            etCorreo.setError(getString(R.string.emailInvalido));
            etCorreo.requestFocus();
        } else if (nombreUsuario.isEmpty()) {
            etNombreUsuario.setError(getString(R.string.usuarioObligatorio));
            etNombreUsuario.requestFocus();
        } else if (contrasena.isEmpty()) {
            etContrasena.setError(getString(R.string.contrasenaObligatoria));
            etContrasena.requestFocus();
        } else if (contrasena2.isEmpty()) {
            etContrasena2.setError(getString(R.string.contrasenaConfirmar));
            etContrasena2.requestFocus();
        } else if (!contrasena.equals(contrasena2)) {
            etContrasena2.setError(getString(R.string.contrasenasDiferentes));
            etContrasena2.requestFocus();
        } else {
            registrarUsuario();
        }
    }

    private void registrarUsuario() {

        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage(getString(R.string.registrandoUsuario));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        managerInterface.registerUser(correo, nombreUsuario, contrasena).enqueue(new Callback<SignupResponse>() {

            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                try {
                    if (response.body().getCode() == 200) {
                        Toast.makeText(getApplication(), response.body().getData(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.putExtra("correo", correo);
                        startActivity(intent);

                    } else if (response.body().getCode() == 500) {
                        etCorreo.requestFocus();
                        Toast.makeText(getApplicationContext(), response.body().getData(), Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.errorRegistroUsuario), Toast.LENGTH_LONG).show();
                    }
                    loadingDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                loadingDialog.dismiss();
                ErrorManager.commonErrors(getApplicationContext(), t);
            }
        });
    }
}
