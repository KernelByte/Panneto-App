package co.panneto.pannetousuario.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import co.panneto.pannetousuario.Adapters.EmptyRecyclerView;
import co.panneto.pannetousuario.Adapters.ReservationsAdapter;
import co.panneto.pannetousuario.R;
import co.panneto.pannetousuario.Response.UserReservationData;
import co.panneto.pannetousuario.Response.UserReservationResponse;
import co.panneto.pannetousuario.Service.ApiUtils;
import co.panneto.pannetousuario.Utils.ErrorManager;
import co.panneto.pannetousuario.Service.ManagerInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<UserReservationData> userReservationData;
    private EmptyRecyclerView recyclerView;
    private ReservationsAdapter adapter;
    private ManagerInterface managerInterface;
    public SwipeRefreshLayout refresh;
    private ErrorManager errorManager;
    private String token;

    private Boolean isStarted = false;
    private Boolean isVisible = false;

    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;

        if (isVisible) {
            asyncGetReservations async = new asyncGetReservations();
            async.execute();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;

        if (isStarted && isVisible) {
            asyncGetReservations async = new asyncGetReservations();
            async.execute();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservations, container, false);
        managerInterface = ApiUtils.getService(Objects.requireNonNull(getContext()));
        errorManager = new ErrorManager(getContext());

        try {
            refresh = view.findViewById(R.id.spRefreshReservations);
            recyclerView = view.findViewById(R.id.rvReservas);
            recyclerView.setEmptyView(view.findViewById(R.id.emptyViewReservations));
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            refresh.setOnRefreshListener(this);
            refresh.setColorSchemeResources(R.color.colorAccent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private class asyncGetReservations extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
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
        protected void onPostExecute(String newToken) {
            token = newToken;
            getReservations(token);
        }
    }

    public void getReservations(String token) {

        if (!TextUtils.isEmpty(token)) {

            managerInterface.getUserReservations(token).enqueue(new Callback<UserReservationResponse>() {
                @Override
                public void onResponse(Call<UserReservationResponse> call, Response<UserReservationResponse> response) {

                    try {
                        userReservationData = new ArrayList<>();
                        if (response.isSuccessful()) {

                            if (getActivity() != null) {
                                ErrorManager.successfulResponse(getActivity());
                            }

                            for (int i = 0; i < response.body().getData().size(); i++) {

                                UserReservationData j = response.body().getData().get(i);

                                UserReservationData reservations = new UserReservationData();
                                reservations.setId(j.getId());
                                reservations.setNombreProducto(j.getNombreProducto());
                                reservations.setNombreEmpresa(j.getNombreEmpresa());
                                reservations.setCodigoReserva(j.getCodigoReserva());
                                reservations.setCantidadReserva(j.getCantidadReserva());
                                reservations.setPrecioTotal(j.getPrecioTotal());
                                reservations.setImagen(j.getImagen());

                                userReservationData.add(reservations);
                            }

                            if (response.body().getCode() != 200) {
                                Toast.makeText(getContext(), getString(R.string.titleError), Toast.LENGTH_SHORT).show();
                            }

                            adapter = new ReservationsAdapter(getContext(), userReservationData);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            refresh.setRefreshing(false);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<UserReservationResponse> call, Throwable t) {
                    t.getCause();
                    if (getActivity() != null) {
                        ErrorManager.commonErrors(t, getActivity());
                    } else {
                        ErrorManager.commonErrors(getContext(), t);
                    }
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        asyncGetReservations async = new asyncGetReservations();
        async.execute();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
