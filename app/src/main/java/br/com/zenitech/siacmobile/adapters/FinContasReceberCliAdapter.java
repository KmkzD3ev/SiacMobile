package br.com.zenitech.siacmobile.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.domains.FormasPagamentoReceberTemp;

public class FinContasReceberCliAdapter extends RecyclerView.Adapter<FinContasReceberCliAdapter.ViewHolder> {

    private ClassAuxiliar classAuxiliar;
    private final Context context;
    private final ArrayList<FormasPagamentoReceberTemp> elementos;
    private final LinearLayout bgTotalReceber;
    private final TextView txtTotalFinanceiroReceber;
    private final TextView txtTotalItemFinanceiroReceber;
    private final EditText txtValorFormaPagamento;

    public FinContasReceberCliAdapter(
            Context context,
            ArrayList<FormasPagamentoReceberTemp> elementos,
            LinearLayout bgTotalReceber,
            TextView txtTotalFinanceiroReceber,
            TextView txtTotalItemFinanceiroReceber,
            EditText txtValorFormaPagamento
    ) {
        this.context = context;
        this.elementos = elementos;
        this.bgTotalReceber = bgTotalReceber;
        this.txtTotalFinanceiroReceber = txtTotalFinanceiroReceber;
        this.txtTotalItemFinanceiroReceber = txtTotalItemFinanceiroReceber;
        this.txtValorFormaPagamento = txtValorFormaPagamento;
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
        View view = inflater.inflate(R.layout.item_financeiro, parent, false);

        //
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //
        final FormasPagamentoReceberTemp temp = elementos.get(position);
        classAuxiliar = new ClassAuxiliar();

        //
        TextView txtFormaPagamento = holder.txtFormaPagamento;
        txtFormaPagamento.setText(temp.getId_forma_pagamento().replace(" _ ", ""));

        //
        TextView total = holder.txtFinanceiro;
        total.setText(classAuxiliar.maskMoney(new BigDecimal(temp.getValor())));

        holder.btnExcluirFinanceiro.setOnClickListener(v -> excluirItem(temp.getId(), temp.getId_cliente(), position));
    }

    private void deleteItem(int position) {
        elementos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, elementos.size());
        //holder.itemView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //LinearLayout LlList;
        TextView txtFormaPagamento, txtFinanceiro;
        ImageButton btnExcluirFinanceiro;

        public ViewHolder(View itemView) {
            super(itemView);

            //
            //LlList = (LinearLayout) itemView.findViewById(R.id.LlList);
            txtFormaPagamento = itemView.findViewById(R.id.txtFormaPagamento);
            txtFinanceiro = itemView.findViewById(R.id.txtFinanceiro);
            btnExcluirFinanceiro = itemView.findViewById(R.id.btnExcluirFinanceiro);
        }
    }

    public void excluirItem(String codigo, String id_cliente, int position) {
        FormasPagamentoReceberTemp temp = new FormasPagamentoReceberTemp(codigo, null, null, null);
        DatabaseHelper bd = new DatabaseHelper(context);
        bd.deleteItemFinanceiroReceberTemp(temp);


        elementos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, elementos.size());

        if (elementos.size() != 0) {
            String valor = bd.SomaValTotFinReceber(id_cliente);
            txtTotalItemFinanceiroReceber.setText(classAuxiliar.maskMoney(new BigDecimal(valor)));
            //textTotalItens.setText(String.valueOf(elementos.size()));
        } else {
            txtTotalItemFinanceiroReceber.setText(classAuxiliar.maskMoney(new BigDecimal("0.0")));
            //textTotalItens.setText("0");
        }

        //
        String valorFinanceiroReceber = String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiroReceber.getText().toString()));
        String valorFinanceiroReceberAdd = String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiroReceber.getText().toString()));

        //SUBTRAIR O VALOR PELA QUANTIDADE
        String[] subtracao = {valorFinanceiroReceber, valorFinanceiroReceberAdd};
        String total = String.valueOf(classAuxiliar.subitrair(subtracao));

        txtValorFormaPagamento.setText(total);
        //
        if (comparar()) {

            bgTotalReceber.setBackgroundColor(ContextCompat.getColor(context, R.color.erro));
            txtValorFormaPagamento.setText("0,00");
        } else {
            bgTotalReceber.setBackgroundColor(ContextCompat.getColor(context, R.color.transparente));
        }
    }

    //COMPARAR O VALOR DO FINANCEIRO COM O VALOR ADICIONADO
    private boolean comparar() {
        //
        BigDecimal valorFinanceiroReceber = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiroReceber.getText().toString())));
        BigDecimal valorFinanceiroReceberAdd = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiroReceber.getText().toString())));

        if (valorFinanceiroReceberAdd.compareTo(valorFinanceiroReceber) > 0) {
            //
            return !valorFinanceiroReceber.toString().equals(valorFinanceiroReceberAdd.toString());
        } else {
            return false;
        }
    }
}
