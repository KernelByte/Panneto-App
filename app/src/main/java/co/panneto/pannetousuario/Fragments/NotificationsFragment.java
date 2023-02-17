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
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.panneto.pannetousuario.Adapters.EmptyRecyclerView;
import co.panneto.pannetousuario.Adapters.NotificationsAdapter;
import co.panneto.pannetousuario.R;
import co.panneto.pannetousuario.Response.NotificationsData;
import co.panneto.pannetousuario.Response.NotificationsResponse;
import co.panneto.pannetousuario.Service.ApiUtils;
import co.panneto.pannetousuario.Utils.ErrorManager;
import co.panneto.pannetousuario.Service.ManagerInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private ArrayList<NotificationsData> notificationsData;
    private EmptyRecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private ManagerInterface managerInterface;
    private SwipeRefreshLayout refresh;
    private ErrorManager errorManager;
    private String token;

    private Boolean isStarted = false;
    private Boolean isVisible = false;

    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;

        if (isVisible) {
            asyncGetNotifications async = new asyncGetNotifications();
            async.execute();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;

        if (isStarted && isVisible) {
            asyncGetNotifications async = new asyncGetNotifications();
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
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        managerInterface = ApiUtils.getService(Objects.requireNonNull(getContext()));
        errorManager = new ErrorManager(getContext());
        setHasOptionsMenu(true);

        try {
            refresh = view.findViewById(R.id.spRefresh);
            recyclerView = view.findViewById(R.id.rvNotifications);
            recyclerView.setEmptyView(view.findViewById(R.id.emptyViewNotifications));
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            refresh.setOnRefreshListener(this);
            refresh.setColorSchemeResources(R.color.colorAccent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private class asyncGetNotifications extends AsyncTask<Void, Void, String> {

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
            getNotifications(token);
        }
    }

    public void getNotifications(String token) {

        if (!TextUtils.isEmpty(token)) {

            managerInterface.getNotifications(token).enqueue(new Callback<NotificationsResponse>() {
                @Override
                public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {

                    try {
                        notificationsData = new ArrayList<>();
                        if (response.isSuccessful()) {

                            if (getActivity() != null) {
                                ErrorManager.successfulResponse(getActivity());
                            }

                            for (int i = 0; i < response.body().getData().size(); i++) {

                                NotificationsData j = response.body().getData().get(i);

                                NotificationsData notifications = new NotificationsData();
                                notifications.setId(j.getId());
                                notifications.setNota(j.getNota());
                                notifications.setNombreProducto(j.getNombreProducto());
                                notifications.setNombreEmpresa(j.getNombreEmpresa());
                                notifications.setPrecio(j.getPrecio());
                                notifications.setImagen(j.getImagen());
                                notifications.setFecha(j.getFecha());

                                Log.e("DATA", ".");

                                notificationsData.add(notifications);
                            }

                            if (response.body().getCode() != 200) {
                                Toast.makeText(getContext(), getString(R.string.titleError), Toast.LENGTH_SHORT).show();
                            }

                            adapter = new NotificationsAdapter(getContext(), notificationsData);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            refresh.setRefreshing(false);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<NotificationsResponse> call, Throwable t) {
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
        asyncGetNotifications async = new asyncGetNotifications();
        async.execute();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.buscarProducto));

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        newText = newText.toLowerCase();
        List<NotificationsData> filteredValues = new ArrayList<NotificationsData>(notificationsData);
        for (NotificationsData value : notificationsData) {

            final String nombre = value.getNombreProducto().toLowerCase();
            final String nota = value.getNota().toLowerCase();

            if (!nombre.contains(newText.toLowerCase()) && !nota.contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }
        adapter = new NotificationsAdapter(getContext(), filteredValues);
        recyclerView.setAdapter(adapter);

        return true;
    }

    public void resetSearch() {
        adapter = new NotificationsAdapter(getContext(), notificationsData);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        adapter = new NotificationsAdapter(getContext(), notificationsData);
        recyclerView.setAdapter(adapter);
        return true;
    }

}
