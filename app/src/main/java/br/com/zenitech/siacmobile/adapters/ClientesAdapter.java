package br.com.zenitech.siacmobile.adapters;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.Vendas;
import br.com.zenitech.siacmobile.VendasConsultarClientes;
import br.com.zenitech.siacmobile.domains.Clientes;

public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Clientes> elementos;
    private SharedPreferences prefs;

    public ClientesAdapter(Context context, ArrayList<Clientes> elementos) {
        this.context = context;
        this.elementos = elementos;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        prefs = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE);

        //
        View view = inflater.inflate(R.layout.item_cliente, parent, false);

        //
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //
        final Clientes clientes = elementos.get(position);

        //
        TextView codigo = holder.tvCodigo;
        codigo.setText(clientes.getCodigo_cliente());

        // + " " + clientes.getSaldo() + ", " + clientes.getLatitude_cliente() + ", " + clientes.getLongitude_cliente()
        TextView nome = holder.tvNome;
        nome.setText(clientes.getNome_cliente());

        //
        TextView apelido = holder.tvApelido;
        apelido.setText(clientes.getApelido_cliente());

        //
        holder.enderecoCliente.setText(clientes.getEndereco());

        holder.LlList.setOnClickListener(v -> {

            Log.e("Pos_Cliente", clientes.getLatitude_cliente() + ", " + clientes.getLongitude_cliente());

            // Salva a posição nas preferencias do app
            prefs.edit().putString("latitude_cliente", clientes.getLatitude_cliente()).apply();
            prefs.edit().putString("longitude_cliente", clientes.getLongitude_cliente()).apply();

            Intent in = new Intent(context, Vendas.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            in.putExtra("id_venda", "");
            in.putExtra("codigo", clientes.getCodigo_cliente());
            in.putExtra("nome", clientes.getNome_cliente());
            in.putExtra("latitude_cliente", clientes.getLatitude_cliente());
            in.putExtra("longitude_cliente", clientes.getLongitude_cliente());
            in.putExtra("saldo", clientes.getSaldo());
            in.putExtra("cpfcnpj", clientes.getCpfcnpj());
            in.putExtra("endereco", clientes.getEndereco());
            context.startActivity(in);

            ((VendasConsultarClientes) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout LlList;
        TextView tvCodigo, tvNome, tvApelido, enderecoCliente;

        public ViewHolder(View itemView) {
            super(itemView);

            //
            LlList = itemView.findViewById(R.id.LlList);
            tvCodigo = itemView.findViewById(R.id.codCliente);
            tvNome = itemView.findViewById(R.id.nomeCliente);
            tvApelido =  itemView.findViewById(R.id.apelidoCliente);
            enderecoCliente = itemView.findViewById(R.id.enderecoCliente);
        }
    }

    public void setFilter(ArrayList<Clientes> newlist) {
        elementos = new ArrayList<>();
        elementos.addAll(newlist);
        notifyDataSetChanged();
    }
}
