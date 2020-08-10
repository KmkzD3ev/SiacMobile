package br.com.zenitech.siacmobile.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;

import static br.com.zenitech.siacmobile.FinanceiroDaVenda.bgTotal;
import static br.com.zenitech.siacmobile.FinanceiroDaVenda.txtTotalFinanceiro;
import static br.com.zenitech.siacmobile.FinanceiroDaVenda.txtTotalItemFinanceiro;
import static br.com.zenitech.siacmobile.FinanceiroDaVenda.txtValorFormaPagamento;

public class FinanceiroVendasAdapter extends RecyclerView.Adapter<FinanceiroVendasAdapter.ViewHolder> {

    private ClassAuxiliar classAuxiliar;
    private Context context;
    private ArrayList<FinanceiroVendasDomain> elementos;

    public FinanceiroVendasAdapter(Context context, ArrayList<FinanceiroVendasDomain> elementos) {
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
                excluirItem(
                        financeiroVendasDomain.getCodigo_financeiro(),
                        financeiroVendasDomain.getId_financeiro_app(),
                        financeiroVendasDomain.getValor_financeiro(),
                        position
                );
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

    public void excluirItem(String codigo, String codigo_financeiro_app, String totalVenda, int position) {
        FinanceiroVendasDomain financeiroVendasDomain = new FinanceiroVendasDomain(codigo, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        DatabaseHelper bd;
        bd = new DatabaseHelper(context);
        bd.deleteItemFinanceiro(financeiroVendasDomain);


        elementos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, elementos.size());

        //
        //txtTotalFinanceiro

        if (elementos.size() != 0) {
            String valor = bd.getValorTotalFinanceiro(codigo_financeiro_app);
            txtTotalItemFinanceiro.setText(classAuxiliar.maskMoney(new BigDecimal(valor)));
            //textTotalItens.setText(String.valueOf(elementos.size()));
        } else {
            txtTotalItemFinanceiro.setText(classAuxiliar.maskMoney(new BigDecimal("0.0")));
            //textTotalItens.setText("0");
        }

        //
        String valorFinanceiro = String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiro.getText().toString()));
        String valorFinanceiroAdd = String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiro.getText().toString()));

        //SUBTRAIR O VALOR DO FINANCEIRO PELO VALOR TOTAL DE ITENS
        String[] subtrair = {valorFinanceiro, valorFinanceiroAdd};
        String total = String.valueOf(classAuxiliar.subitrair(subtrair));

        txtValorFormaPagamento.setText(total);

        //
        if (comparar()) {

            bgTotal.setBackgroundColor(ContextCompat.getColor(context, R.color.erro));
            txtValorFormaPagamento.setText("0,00");
        } else {
            bgTotal.setBackgroundColor(ContextCompat.getColor(context, R.color.transparente));
        }
    }

    //COMPARAR O VALOR DO FINANCEIRO COM O VALOR ADICIONADO
    private boolean comparar() {

        //
        BigDecimal valorFinanceiro = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiro.getText().toString())));
        BigDecimal valorFinanceiroAdd = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiro.getText().toString())));

        if (valorFinanceiroAdd.compareTo(valorFinanceiro) == 1) {
            //
            if (valorFinanceiro.toString().equals(valorFinanceiroAdd.toString())) {

                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }
}
