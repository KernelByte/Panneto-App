package co.panneto.pannetousuario.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import co.panneto.pannetousuario.Response.LoginResponse;
import co.panneto.pannetousuario.Service.ApiUtils;
import co.panneto.pannetousuario.Utils.ErrorManager;
import co.panneto.pannetousuario.Service.ManagerInterface;
import co.panneto.pannetousuario.R;
import co.panneto.pannetousuario.Utils.KeyHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvRegistrar;
    private EditText etCorreo, etContrasena;
    private Button btnLogin;
    private ProgressBar progressBar;
    private ManagerInterface managerInterface;
    private ErrorManager errorManager;
    private Intent getCorreo;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = findViewById(R.id.txtEmailIngreso);
        etContrasena = findViewById(R.id.txtPasswordIngreso);
        tvRegistrar = findViewById(R.id.tvRegistrarse);
        ImageView ivVer = findViewById(R.id.imVer);
        btnLogin = findViewById(R.id.btnIngresar);
        progressBar = findViewById(R.id.psProgreso);
        managerInterface = ApiUtils.getService(getApplicationContext());
        errorManager = new ErrorManager(getApplicationContext());

        ivVer.setOnClickListener(this);
        tvRegistrar.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        if (!Objects.requireNonNull(preferencias.getString("token", "")).equals("")) {
            Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(menu);
        }

        getCorreo = getIntent();
        if (getCorreo.getExtras() != null) {
            etCorreo.setText((String) getCorreo.getExtras().get("correo"));
        }

        ivVer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        etContrasena.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        etContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnIngresar:
                String correo = etCorreo.getText().toString().trim();
                String contrasena = etContrasena.getText().toString().trim();
                iniciarSesion(correo, contrasena);
                break;

            case R.id.tvRegistrarse:
                registrarUsuario();
                break;
        }
    }

    private void registrarUsuario() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    public void iniciarSesion(String correo, String contrasena) {

        if (!correo.isEmpty() && !contrasena.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            tvRegistrar.setEnabled(false);
            etCorreo.setEnabled(false);
            etContrasena.setEnabled(false);

            managerInterface.login(correo, contrasena).enqueue(new Callback<LoginResponse>() {

                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                    try {
                        if (response.isSuccessful()) {

                            SharedPreferences pref = getSharedPreferences("datos", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("token", "Bearer " + response.body().getToken());
                            editor.putString("user", response.body().getData().getNombreUsuario());
                            editor.putString("email", response.body().getData().getCorreoUsuario());
                            editor.apply();

                            KeyHelper keyHelper = new KeyHelper(getApplicationContext());
                            String encr = keyHelper.encrypt(getApplicationContext(), contrasena);

                            SharedPreferences sPref = getSharedPreferences("encr", Context.MODE_PRIVATE);
                            SharedPreferences.Editor spEditor = sPref.edit();
                            spEditor.putString("userEmail", correo);
                            spEditor.putString("encryptedPass", encr);
                            spEditor.apply();

                            Toast.makeText(getApplication(), getString(R.string.bienvenido), Toast.LENGTH_SHORT).show();
                            getCorreo.removeExtra("correo");

                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);

                        } else {
                            if (response.code() == 401) {
                                Toast.makeText(getApplicationContext(), getString(R.string.credencialesInvalidas), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.errorLogin), Toast.LENGTH_LONG).show();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        tvRegistrar.setEnabled(true);
                        etCorreo.setEnabled(true);
                        etContrasena.setEnabled(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    tvRegistrar.setEnabled(true);
                    etCorreo.setEnabled(true);
                    etContrasena.setEnabled(true);
                    ErrorManager.commonErrors(getApplicationContext(), t);
                }
            });

        } else {
            if (correo.isEmpty()) {
                etCorreo.setError(getString(R.string.correoObligatorio));
                etCorreo.requestFocus();
            } else if (contrasena.isEmpty()) {
                etContrasena.setError(getString(R.string.contraObligatoria));
                etContrasena.requestFocus();
            }
        }
    }
}
