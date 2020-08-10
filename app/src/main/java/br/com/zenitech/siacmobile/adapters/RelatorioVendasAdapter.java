package br.com.zenitech.siacmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.RelatorioVendasCliente;
import br.com.zenitech.siacmobile.domains.VendasDomain;

public class RelatorioVendasAdapter extends RecyclerView.Adapter<RelatorioVendasAdapter.ViewHolder> {

    private ClassAuxiliar classAuxiliar;
    private Context context;
    private ArrayList<VendasDomain> elementos;

    public RelatorioVendasAdapter(Context context, ArrayList<VendasDomain> elementos) {
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
        View view = inflater.inflate(R.layout.item_relatorio_vendas, parent, false);

        //
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //
        final VendasDomain vendasDomain = elementos.get(position);
        classAuxiliar = new ClassAuxiliar();

        //
        TextView produto = holder.txtProduto;
        produto.setText(vendasDomain.getProduto_venda());
        //
        TextView codigo = holder.txtQuantidade;
        codigo.setText(vendasDomain.getQuantidade_venda());
        //
        //TextView valor = holder.txtValor;
        //valor.setText(classAuxiliar.maskMoney(new BigDecimal(vendasDomain.getPreco_unitario())));
        //
        String[] vls_media = {vendasDomain.getValor_total(), vendasDomain.getQuantidade_venda()};
        String media = String.valueOf(classAuxiliar.dividir(vls_media));
        TextView total = holder.txtTotal;
        total.setText("R$ " + classAuxiliar.maskMoney(new BigDecimal(media)));//vendasDomain.getValor_total()

        holder.llRelatorioVendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, RelatorioVendasCliente.class);
                i.putExtra("produto", vendasDomain.getProduto_venda());
                i.putExtra("quantidade", vendasDomain.getQuantidade_venda());
                i.putExtra("total", classAuxiliar.maskMoney(new BigDecimal(vendasDomain.getValor_total())));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llRelatorioVendas;
        TextView txtProduto, txtQuantidade, txtValor, txtTotal;
        ImageButton btnExcluirVenda;

        public ViewHolder(View itemView) {
            super(itemView);

            //
            llRelatorioVendas = (LinearLayout) itemView.findViewById(R.id.llRelatorioVendas);
            txtProduto = (TextView) itemView.findViewById(R.id.txtProduto);
            txtQuantidade = (TextView) itemView.findViewById(R.id.txtQuantidade);
            txtValor = (TextView) itemView.findViewById(R.id.txtValor);
            txtTotal = (TextView) itemView.findViewById(R.id.txtTotal);
            btnExcluirVenda = (ImageButton) itemView.findViewById(R.id.btnExcluirVenda);
        }
    }
}
