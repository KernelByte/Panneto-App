package co.panneto.pannetousuario.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import co.panneto.pannetousuario.Adapters.SectionsPageAdapter;
import co.panneto.pannetousuario.Fragments.NotificationsFragment;
import co.panneto.pannetousuario.R;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NotificationsFragment.OnFragmentInteractionListener {

    private ViewPager viewPager;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        TextView tvUsuario = view.findViewById(R.id.tvNombreUsuarioLogeado);
        TextView tvCorreo = view.findViewById(R.id.tvCorreoUsuarioLogeado);
        ImageView ivPerfil = view.findViewById(R.id.imageView);

        Drawable drawable = getDrawable(R.drawable.perfil);
        Bitmap bitmap = ((BitmapDrawable) Objects.requireNonNull(drawable)).getBitmap();

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory
                .create(getResources(), bitmap);

        roundedBitmapDrawable.setCornerRadius(bitmap.getHeight());
        ivPerfil.setImageDrawable(roundedBitmapDrawable);

        TabLayout tabLayout = findViewById(R.id.tbMenu);
        TabItem tabMap = findViewById(R.id.tabMap);
        TabItem tabNotifications = findViewById(R.id.tabNotifications);
        TabItem tabReservations = findViewById(R.id.tabReservations);
        viewPager = findViewById(R.id.pvMenu);

        SectionsPageAdapter sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(sectionsPageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(3);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    setTitle(getResources().getText(R.string.tvInicio));
                } else if (tab.getPosition() == 1) {
                    setTitle(getResources().getText(R.string.tvNotificaciones));
                } else {
                    setTitle(getResources().getText(R.string.tv_Reservas));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        SharedPreferences myPref = getSharedPreferences("datos", Context.MODE_PRIVATE);
        String user = myPref.getString("user", "");
        String email = myPref.getString("email", "");

        if (!Objects.equals(user, "") && !Objects.equals(email, "")) {
            String bienvenido = "¡" + getString(R.string.hola) + " " + user + "!";
            tvUsuario.setText(bienvenido);
            tvCorreo.setText(email);
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        this.exit = true;
        Toast.makeText(this, getString(R.string.atrasParaSalir), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            id = item.getItemId();

            if (id == R.id.action_settings) {
                SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
                preferencias.edit().clear().apply();
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                finish();
            }
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.soporte) {
            mostrarVentanaSoporte();

        } else if (id == R.id.inicio) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.propiedadIntelectual) {
            Intent copyright = new Intent(this, CopyrightActivity.class);
            startActivity(copyright);
        } else if (id == R.id.politicaPrivacidad) {
            Intent privacyPolicy = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(privacyPolicy);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mostrarVentanaSoporte() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_soporte, null);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btnOk = view.findViewById(R.id.btnCloseDialog);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
