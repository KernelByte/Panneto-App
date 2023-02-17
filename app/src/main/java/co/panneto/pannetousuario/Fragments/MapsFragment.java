package co.panneto.pannetousuario.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.panneto.pannetousuario.Activities.InfoWindowListener;
import co.panneto.pannetousuario.Activities.MapWrapperLayout;
import co.panneto.pannetousuario.R;
import co.panneto.pannetousuario.Response.CompanyData;
import co.panneto.pannetousuario.Response.CompanyResponse;
import co.panneto.pannetousuario.Response.FavoriteResponse;
import co.panneto.pannetousuario.Service.ApiUtils;
import co.panneto.pannetousuario.Utils.ErrorManager;
import co.panneto.pannetousuario.Service.ManagerInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private List<Marker> markers = new ArrayList<>();
    private GoogleMap googleMap;
    private String token;
    private ManagerInterface managerInterface;
    private ErrorManager errorManager;

    private ViewGroup infoWindow;
    private Marker mMarker;
    private Boolean bFavorite;
    private InfoWindowListener infoButtonListener;
    private MapWrapperLayout mapWrapperLayout;

    private TextView tvNegocio, tvDireccion, tvDir, tvTelefono, tvTel;
    private ImageView ivImgNegocio;
    private ImageButton btnClic;
    private String sDireccion, sTelefono, sCompany, sId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_window, container, false);
        managerInterface = ApiUtils.getService(getContext());
        errorManager = new ErrorManager(getContext());

        asyncGetCompaniesLocation async = new asyncGetCompaniesLocation();
        async.execute();

        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapp);
        mapWrapperLayout = (MapWrapperLayout) view.findViewById(R.id.map_relative_layout);

        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.map_info_window, null);
        this.tvNegocio = infoWindow.findViewById(R.id.tvInfoNegocio);
        this.tvDireccion = infoWindow.findViewById(R.id.tvInfoDireccion);
        this.tvDir = infoWindow.findViewById(R.id.tvInfoDir);
        this.tvTelefono = infoWindow.findViewById(R.id.tvInfoTelefono);
        this.tvTel = infoWindow.findViewById(R.id.tvInfoTel);
        this.ivImgNegocio = infoWindow.findViewById(R.id.ivInfoImagen);
        this.btnClic = infoWindow.findViewById(R.id.btnClic);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapView mapView = view.findViewById(R.id.mapp);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    public static int getPixelsFromDp(Context context, float dp) {
        int num = 0;
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            num = (int) (dp * scale + 0.5f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    @Override
    public void onMapReady(GoogleMap googMap) {

        mapWrapperLayout.init(googMap, getPixelsFromDp(getContext(), 39 + 20));

        MapsInitializer.initialize(getContext());
        googleMap = googMap;
        googMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng latLng = new LatLng(3.454045, -76.530769);
        float zoom = 12;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                boolean openInfoWindow = false;

                for (int i = 0; i < markers.size(); i++) {
                    if (markers.get(i).isInfoWindowShown()) {
                        openInfoWindow = true;
                        break;
                    }
                }
                if (!openInfoWindow) {
                    asyncGetCompaniesLocation async = new asyncGetCompaniesLocation();
                    async.execute();
                }
            }
        });
    }

    private Bitmap getBitmapNoFavorite() {
        BitmapDrawable bitmapNoFavorite = (BitmapDrawable) Objects.requireNonNull(getContext()).getDrawable(R.drawable.marker_1);
        Bitmap bit = Objects.requireNonNull(bitmapNoFavorite).getBitmap();
        final Bitmap markerNoFavorite = Bitmap.createScaledBitmap(bit, 85, 85, false);

        return markerNoFavorite;
    }

    private Bitmap getBitmapFavorite() {

        BitmapDrawable bitmapFavorite = (BitmapDrawable) Objects.requireNonNull(getContext()).getDrawable(R.drawable.marker_2);
        Bitmap bitm = Objects.requireNonNull(bitmapFavorite).getBitmap();
        final Bitmap markerFavorite = Bitmap.createScaledBitmap(bitm, 100, 100, false);

        return markerFavorite;
    }

    private class asyncGetCompaniesLocation extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String newToken;

            //Se implementa un asyntask ya que si el token está vacío
            //este se genere nuevamente ANTES de ejecutar el método
            //getCompaniesLocation()
            token = errorManager.getNewToken();

            if (TextUtils.isEmpty(token)) {
                String decryptedPass = errorManager.decryptPass();
                newToken = errorManager.getToken(decryptedPass);
            } else {
                newToken = token;
            }
            return newToken;
        }

        @Override
        protected void onPostExecute(String token) {
            getCompaniesLocation(token, null, null);
        }
    }

    public void getCompaniesLocation(String token, Marker mark, Boolean favorite) {

        if (!TextUtils.isEmpty(token)) {

            managerInterface.getCompaniesLocation(token).enqueue(new Callback<CompanyResponse>() {
                @Override
                public void onResponse(Call<CompanyResponse> call, final Response<CompanyResponse> response) {

                    try {
                        if (response.isSuccessful()) {

                            googleMap.clear();

                            for (int i = 0; i < response.body().getData().size(); i++) {

                                CompanyData j = response.body().getData().get(i);

                                String dir = getString(R.string.mapDireccion);
                                String tel = getString(R.string.mapTelefono);
                                String companyInformation = dir + "!" + j.getDireccion() + "!\n" + tel + "!" + j.getTelefono();
                                String companyName = dir + "!" + j.getRazonSocial() + "!" + j.getId();
                                LatLng companyLocation = new LatLng(Double.parseDouble(j.getLatitud()), Double.parseDouble(j.getLongitud()));

                                Marker marker = null;
                                int btnImg;
                                int btnImgFav = 0;

                                if (j.getFavorite()) {
                                    btnImg = R.drawable.ic_fav;
                                    btnImgFav = R.drawable.ic_no_fav;

                                    marker = googleMap.addMarker(new MarkerOptions()
                                            .position(companyLocation)
                                            .title(companyName)
                                            .snippet(companyInformation + "!" + btnImg)
                                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFavorite()))
                                            .anchor(0.5f, 1));

                                } else if (!j.getFavorite()) {
                                    btnImg = R.drawable.ic_no_fav;
                                    btnImgFav = R.drawable.ic_fav;

                                    marker = googleMap.addMarker(new MarkerOptions()
                                            .position(companyLocation)
                                            .title(companyName)
                                            .snippet(companyInformation + "!" + btnImg)
                                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapNoFavorite()))
                                            .anchor(0.5f, 1));
                                }

                                if (mark != null) {
                                    bFavorite = favorite;
                                    if (favorite) {
                                        btnImg = R.drawable.ic_fav;
                                        mMarker = googleMap.addMarker(new MarkerOptions()
                                                .position(mark.getPosition())
                                                .title(mark.getTitle())
                                                .snippet(mark.getSnippet() + "!" + btnImg)
                                                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFavorite()))
                                                .anchor(0.5f, 1));
                                    } else {
                                        btnImg = R.drawable.ic_no_fav;
                                        mMarker = googleMap.addMarker(new MarkerOptions()
                                                .position(mark.getPosition())
                                                .title(mark.getTitle())
                                                .snippet(mark.getSnippet() + "!" + btnImg)
                                                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapNoFavorite()))
                                                .anchor(0.5f, 1));
                                    }
                                }

                                markers.add(marker);

                                setFavorite(btnImgFav);
                            }

                            if (mMarker != null) {
                                mMarker.showInfoWindow();
                            }

                            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                @Override
                                public View getInfoWindow(Marker marker) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {

                                    int img = 0;

                                    try {
                                        ivImgNegocio.setImageResource(R.drawable.pann1);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                    String[] arr = marker.getSnippet().split("!");
                                    for (int i = 0; i < arr.length; i++) {
                                        sDireccion = arr[1].trim();
                                        sTelefono = arr[3].trim();

                                        if (mMarker != null && mMarker.equals(marker)) {
                                            if (bFavorite) {
                                                img = R.drawable.ic_fav;
                                            } else {
                                                img = R.drawable.ic_no_fav;
                                            }
                                        } else {
                                            img = Integer.valueOf(arr[4].trim());
                                        }
                                    }

                                    String[] array = marker.getTitle().split("!");
                                    for (int i = 0; i < arr.length; i++) {
                                        sCompany = array[1].trim();
                                        sId = array[2].trim();
                                    }

                                    btnClic.setBackgroundResource(img);
                                    tvNegocio.setText(sCompany);
                                    tvDireccion.setText("Dirección: ");
                                    tvDir.setText(sDireccion);
                                    tvTelefono.setText("Teléfono: ");
                                    tvTel.setText(sTelefono);

                                    infoButtonListener.setMarker(marker);
                                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                                    return infoWindow;
                                }
                            });

                            if (response.body().getCode() != 200) {
                                Toast.makeText(getContext(), getString(R.string.titleError), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<CompanyResponse> call, Throwable t) {
                    ErrorManager.commonErrors(getContext(), t);
                }
            });
        }
    }

    private void setFavorite(int btnImgFav) {

        infoButtonListener = new InfoWindowListener(btnClic, btnImgFav, btnImgFav) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onClickConfirmed(View v, Marker marker) {

                String[] array = marker.getTitle().split("!");

                for (int x = 0; x < array.length; x++) {
                    sCompany = array[1].trim();
                    sId = array[2].trim();
                }

                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... voids) {
                        String newToken;

                        token = errorManager.getNewToken();

                        if (TextUtils.isEmpty(token)) {
                            String decryptedPass = errorManager.decryptPass();
                            newToken = errorManager.getToken(decryptedPass);
                        } else {
                            newToken = token;
                        }
                        return newToken;
                    }

                    @Override
                    protected void onPostExecute(String token) {

                        managerInterface.postFavorite(token, sId).enqueue(new Callback<FavoriteResponse>() {
                            @Override
                            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {

                                try {
                                    if (response.isSuccessful()) {

                                        if (response.body().getData().getFavorite()) {
                                            getCompaniesLocation(token, marker, response.body().getData().getFavorite());
                                            Toast.makeText(getContext(), getString(R.string.mapFavorito), Toast.LENGTH_SHORT).show();
                                        } else {
                                            getCompaniesLocation(token, marker, response.body().getData().getFavorite());
                                            Toast.makeText(getContext(), getString(R.string.mapNoFavorito), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                                ErrorManager.commonErrors(getContext(), t);
                            }
                        });
                    }
                }.execute();
            }
        };

        btnClic.setOnTouchListener(infoButtonListener);
    }

}
