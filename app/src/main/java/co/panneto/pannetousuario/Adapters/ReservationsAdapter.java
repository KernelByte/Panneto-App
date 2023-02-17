package co.panneto.pannetousuario.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.panneto.pannetousuario.R;
import co.panneto.pannetousuario.Response.UserReservationData;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder> {

    public List<UserReservationData> userReservationData;
    private Context context;

    public ReservationsAdapter(Context context, List<UserReservationData> userReservationData) {
        this.context = context;
        this.userReservationData = userReservationData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImagenProducto;
        private TextView tvId, tvProducto, tvEmpresa, tvCodigoReserva, tvCantidadReserva, tvPrecioTotal;

        public ViewHolder(final View view) {
            super(view);

            ivImagenProducto = itemView.findViewById(R.id.ivProductoReserva);
            tvId = itemView.findViewById(R.id.tvIdReserva);
            tvProducto = itemView.findViewById(R.id.tvNombreProductoReserva);
            tvEmpresa = itemView.findViewById(R.id.tvNombreEmpresa);
            tvCodigoReserva = itemView.findViewById(R.id.tvCodigoReserva);
            tvCantidadReserva = itemView.findViewById(R.id.tvCantidadReserva);
            tvPrecioTotal = itemView.findViewById(R.id.tvPrecioTotal);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_reservations, viewGroup, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String imagenRuta = userReservationData.get(position).getImagen();
        holder.tvId.setText(userReservationData.get(position).getId());
        holder.tvProducto.setText(userReservationData.get(position).getNombreProducto());
        holder.tvEmpresa.setText(userReservationData.get(position).getNombreEmpresa());
        String codigoReserva = context.getString(R.string.tvCodigoReserva) + " " + userReservationData.get(position).getCodigoReserva();
        String cantidadReserva = context.getString(R.string.tvCantidadReserva) + " " + userReservationData.get(position).getCantidadReserva();
        holder.tvCodigoReserva.setText(codigoReserva);
        holder.tvCantidadReserva.setText(cantidadReserva);
        holder.tvPrecioTotal.setText(userReservationData.get(position).getPrecioTotal());

        String ruta = context.getString(R.string.urlApiRoute) + context.getString(R.string.urlNoImg);

        if (imagenRuta != null && !imagenRuta.equals(ruta)) {
            Glide.with(context).load(imagenRuta).into(holder.ivImagenProducto);
        }
        else if (imagenRuta.equals(ruta)) {
            holder.ivImagenProducto.setImageResource(R.drawable.agregar_imagen);
        }

    }

    @Override
    public int getItemCount() {
        return userReservationData.size();
    }
}
