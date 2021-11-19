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
import br.com.zenitech.siacmobile.domains.VendasPedidosDomain;

public class RelatorioVendasPedidosAdapter extends RecyclerView.Adapter<RelatorioVendasPedidosAdapter.ViewHolder> {

    private ClassAuxiliar classAuxiliar;
    private Context context;
    private ArrayList<VendasPedidosDomain> elementos;

    public RelatorioVendasPedidosAdapter(Context context, ArrayList<VendasPedidosDomain> elementos) {
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
        View view = inflater.inflate(R.layout.item_relatorio_vendas_pedidos, parent, false);

        //
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //
        final VendasPedidosDomain vendasDomain = elementos.get(position);
        classAuxiliar = new ClassAuxiliar();

        //
        TextView produto = holder.txtProduto;
        produto.setText(vendasDomain.getProduto_venda());
        //
        TextView formasPagamento = holder.txtFormasPagamento;
        formasPagamento.setText(vendasDomain.getFormas_pagamento());

        //
        TextView codigo = holder.txtQuantidade;
        codigo.setText(vendasDomain.getQuantidade_venda());
        //
        TextView nomeCli = holder.txtNomeCliente;
        nomeCli.setText(vendasDomain.getCodigo_cliente());

        //
        //TextView valor = holder.txtValor;
        //valor.setText(classAuxiliar.maskMoney(new BigDecimal(vendasDomain.getPreco_unitario())));
        //
        /*String[] vls_media = {vendasDomain.getValor_total(), vendasDomain.getQuantidade_venda()};
        String media = String.valueOf(classAuxiliar.dividir(vls_media));
        TextView total = holder.txtTotal;
        total.setText("R$ " + classAuxiliar.maskMoney(new BigDecimal(media)));*///vendasDomain.getValor_total()

        TextView total = holder.txtTotal;
        total.setText("R$ " + classAuxiliar.maskMoney(new BigDecimal(vendasDomain.getValor_total())));

        holder.llRelatorioVendas.setOnClickListener(v -> {

            Intent i = new Intent(context, RelatorioVendasCliente.class);
            i.putExtra("produto", vendasDomain.getProduto_venda());
            i.putExtra("quantidade", vendasDomain.getQuantidade_venda());
            i.putExtra("total", classAuxiliar.maskMoney(new BigDecimal(vendasDomain.getValor_total())));
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llRelatorioVendas;
        TextView txtProduto, txtQuantidade, txtValor, txtTotal, txtNomeCliente, txtFormasPagamento;
        ImageButton btnExcluirVenda;

        public ViewHolder(View itemView) {
            super(itemView);

            //
            llRelatorioVendas = itemView.findViewById(R.id.llRelatorioVendas);
            txtProduto = itemView.findViewById(R.id.txtProduto);
            txtQuantidade = itemView.findViewById(R.id.txtQuantidade);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtNomeCliente = itemView.findViewById(R.id.txtNomeCliente);
            btnExcluirVenda = itemView.findViewById(R.id.btnExcluirVenda);
            txtFormasPagamento = itemView.findViewById(R.id.txtFormasPagamento);
        }
    }
}
