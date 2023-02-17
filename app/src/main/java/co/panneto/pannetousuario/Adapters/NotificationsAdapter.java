package co.panneto.pannetousuario.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

import co.panneto.pannetousuario.Activities.MenuActivity;
import co.panneto.pannetousuario.Fragments.ReservationsFragment;
import co.panneto.pannetousuario.R;
import co.panneto.pannetousuario.Response.NotificationsData;
import co.panneto.pannetousuario.Response.ReserveResponse;
import co.panneto.pannetousuario.Service.ApiUtils;
import co.panneto.pannetousuario.Utils.ErrorManager;
import co.panneto.pannetousuario.Service.ManagerInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<NotificationsData> notificationsData;
    private Context context;
    private ManagerInterface managerInterface;
    private ErrorManager errorManager;
    private String token;
    private EditText cantidad;
    private Button btnEnviarReserva, btnCancelar;
    private ViewPager vista;

    private static final String TAG = "NOTIFICATIONS ADAPTER";

    public NotificationsAdapter(Context context, List<NotificationsData> notificationsData) {
        this.context = context;
        this.notificationsData = notificationsData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvId, tvProducto, tvEmpresa, tvDescripcion, tvPrecio, tvFecha;
        private ImageView ivImagenProducto;
        private Button btnReservar;

        public ViewHolder(final View view) {
            super(view);

            tvId = itemView.findViewById(R.id.tvIdPublicacion);
            tvProducto = itemView.findViewById(R.id.tvNombreProducto);
            tvEmpresa = itemView.findViewById(R.id.tvNombreEmpresa);
            tvDescripcion = itemView.findViewById(R.id.tvNotaPublicacion);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            tvFecha = itemView.findViewById(R.id.tvFechaPublicacion);
            ivImagenProducto = itemView.findViewById(R.id.ivProducto);
            btnReservar = itemView.findViewById(R.id.btnReservar);
            btnReservar.setOnClickListener(this);
            managerInterface = ApiUtils.getService(context);
            errorManager = new ErrorManager(context);
        }

        @Override
        public void onClick(View v) {
            reservar(tvId.getText().toString(), getLayoutPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_notifications, viewGroup, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        final String imagenRuta = notificationsData.get(i).getImagen();
        holder.tvId.setText(String.valueOf(notificationsData.get(i).getId()));
        holder.tvEmpresa.setText(notificationsData.get(i).getNombreEmpresa());
        holder.tvProducto.setText(notificationsData.get(i).getNombreProducto());
        holder.tvDescripcion.setText(notificationsData.get(i).getNota());
        holder.tvPrecio.setText(notificationsData.get(i).getPrecio());
        holder.tvFecha.setText(notificationsData.get(i).getFecha());

        String ruta = context.getString(R.string.urlApiRoute) + context.getString(R.string.urlNoImg);

        if (imagenRuta != null && !imagenRuta.equals(ruta)) {
            Glide.with(context).load(imagenRuta).into(holder.ivImagenProducto);
        } else if (imagenRuta.equals(ruta)) {
            holder.ivImagenProducto.setImageResource(R.drawable.agregar_imagen);
        }
    }

    private void reservar(final String id, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_reservation_notification, null);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        cantidad = view.findViewById(R.id.etCantidad);
        btnEnviarReserva = view.findViewById(R.id.btnEnviar);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        btnEnviarReserva.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(final View v) {

                final String cant = cantidad.getText().toString().trim();

                if (!cant.isEmpty() && Integer.parseInt(cant) > 0) {

                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(context.getString(R.string.generandoReserva));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new AsyncTask<Void, Void, String>() {

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
                        protected void onPostExecute(String token) {

                            managerInterface.sendReservation(token, id, Integer.parseInt(cant)).enqueue(new Callback<ReserveResponse>() {
                                @Override
                                public void onResponse(Call<ReserveResponse> call, Response<ReserveResponse> response) {

                                    try {

                                        if (response.body().getCode() == 200) {
                                            Toast.makeText(context, context.getString(R.string.productoReservado), Toast.LENGTH_SHORT).show();

                                            progressDialog.dismiss();
                                            dialog.dismiss();

                                            MenuActivity activity = (MenuActivity) v.getContext();
                                            ReservationsFragment fragment2 = new ReservationsFragment();

                                            android.support.v4.app.FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                                            fragmentTransaction.replace(R.id.frReservas, fragment2);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();

                                            vista = activity.findViewById(R.id.pvMenu);
                                            vista.setCurrentItem(2);

                                        } else if (response.body().getCode() == 502) {
                                            progressDialog.dismiss();
                                            cantidad.setError(response.body().getMessage());
                                            cantidad.requestFocus();


                                        } else {
                                            Toast.makeText(context, context.getString(R.string.errorReserva), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }

                                        Integer cantidadDisponible = response.body().getData().getProductPublication().getCantidadDisponible();
                                        if (cantidadDisponible != null && cantidadDisponible == 0) {
                                            removeItem(position);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ReserveResponse> call, Throwable t) {
                                    t.printStackTrace();
                                    ErrorManager.commonErrors(context, t);
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }.execute();

                } else if (cant.isEmpty()) {
                    cantidad.setError(context.getString(R.string.cantidadObligatoria));
                    cantidad.requestFocus();
                } else if (Integer.parseInt(cant) <= 0) {
                    cantidad.setError(context.getString(R.string.cantidadMayorCero));
                    cantidad.requestFocus();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsData.size();
    }

    private void removeItem(int position) {
        this.notificationsData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

}
