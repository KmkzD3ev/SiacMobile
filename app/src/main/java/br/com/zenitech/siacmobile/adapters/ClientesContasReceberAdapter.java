package br.com.zenitech.siacmobile.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.zenitech.siacmobile.ContasReceberCliente;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.domains.FinanceiroReceberClientes;

public class ClientesContasReceberAdapter extends RecyclerView.Adapter<ClientesContasReceberAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FinanceiroReceberClientes> elementos;

    public ClientesContasReceberAdapter(Context context, ArrayList<FinanceiroReceberClientes> elementos) {
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

        //
        View view = inflater.inflate(R.layout.item_cliente, parent, false);

        //
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //
        final FinanceiroReceberClientes clientes = elementos.get(position);

        //
        TextView codigo = holder.tvCodigo;
        codigo.setText(clientes.getCodigo_cliente());

        //
        TextView nome = holder.tvNome;
        nome.setText(clientes.getNome_cliente());

        holder.LlList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, ContasReceberCliente.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("codigo", clientes.getCodigo_cliente());
                in.putExtra("nome", clientes.getNome_cliente());
                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout LlList;
        TextView tvCodigo, tvNome;

        public ViewHolder(View itemView) {
            super(itemView);

            //
            LlList = (LinearLayout) itemView.findViewById(R.id.LlList);
            tvCodigo = (TextView) itemView.findViewById(R.id.codCliente);
            tvNome = (TextView) itemView.findViewById(R.id.nomeCliente);
        }
    }

    public void setFilter(ArrayList<FinanceiroReceberClientes> newlist){
        elementos = new ArrayList<>();
        elementos.addAll(newlist);
        notifyDataSetChanged();
    }
}
