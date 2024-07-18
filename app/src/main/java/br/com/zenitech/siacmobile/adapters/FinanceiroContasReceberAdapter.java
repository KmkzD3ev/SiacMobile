package br.com.zenitech.siacmobile.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;


public class FinanceiroContasReceberAdapter extends RecyclerView.Adapter<FinanceiroContasReceberAdapter.ViewHolder> {

    private ClassAuxiliar classAuxiliar;
    private Context context;
    private ArrayList<FinanceiroVendasDomain> elementos;
    private LinearLayout bgTotalReceber;
    private final TextView txtTotalFinanceiroReceber;
    private final TextView txtTotalItemFinanceiroReceber;
    private final EditText txtValorFormaPagamento;

    public FinanceiroContasReceberAdapter(
            Context context,
            ArrayList<FinanceiroVendasDomain> elementos,
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
        final FinanceiroVendasDomain financeiroVendasDomain = elementos.get(position);
        classAuxiliar = new ClassAuxiliar();

        //
        TextView txtFormaPagamento = holder.txtFormaPagamento;
        txtFormaPagamento.setText(financeiroVendasDomain.getFpagamento_financeiro().replace(" _ ", ""));

        //
        TextView total = holder.txtFinanceiro;
        total.setText(classAuxiliar.maskMoney(new BigDecimal(financeiroVendasDomain.getValor_financeiro())));

        holder.btnExcluirFinanceiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirItem(financeiroVendasDomain.getCodigo_financeiro(), financeiroVendasDomain.getId_financeiro_app(), 0.0, position);
            }
        });
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
            txtFormaPagamento = (TextView) itemView.findViewById(R.id.txtFormaPagamento);
            txtFinanceiro = (TextView) itemView.findViewById(R.id.txtFinanceiro);
            btnExcluirFinanceiro = (ImageButton) itemView.findViewById(R.id.btnExcluirFinanceiro);
        }
    }

    public void excluirItem(String codigo, String codigo_financeiro_app, double totalVenda, int position) {
        FinanceiroVendasDomain financeiroVendasDomain = new FinanceiroVendasDomain(codigo, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        DatabaseHelper bd;
        bd = new DatabaseHelper(context);
        bd.deleteItemFinanceiroReceber(financeiroVendasDomain);


        elementos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, elementos.size());

        if (elementos.size() != 0) {
            String valor = bd.getValorTotalFinanceiroReceber(codigo_financeiro_app);
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

        if (valorFinanceiroReceberAdd.compareTo(valorFinanceiroReceber) == 1) {
            //
            if (valorFinanceiroReceber.toString().equals(valorFinanceiroReceberAdd.toString())) {

                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }
}
