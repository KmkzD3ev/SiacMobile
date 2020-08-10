package br.com.zenitech.siacmobile.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.domains.FinanceiroReceberClientes;

import static br.com.zenitech.siacmobile.ContasReceberCliente.id_baixa_app;
import static br.com.zenitech.siacmobile.ContasReceberCliente.tvTotalPagarContasReceberCliente;

public class ContasReceberClientesAdapter extends RecyclerView.Adapter<ContasReceberClientesAdapter.ViewHolder> {


    private Context context;
    private ArrayList<FinanceiroReceberClientes> elementos;
    private ClassAuxiliar classAuxiliar;
    private DatabaseHelper bd;

    public ContasReceberClientesAdapter(Context context, ArrayList<FinanceiroReceberClientes> elementos, ClassAuxiliar classAuxiliar) {
        this.context = context;
        this.elementos = elementos;
        this.classAuxiliar = classAuxiliar;
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

        bd = new DatabaseHelper(getContext());

        //
        View view = inflater.inflate(R.layout.item_contas_receber_cliente, parent, false);

        //
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //
        final FinanceiroReceberClientes financeiroVendasDomain = elementos.get(position);

        TextView fpgItemContaReceberCliente = holder.fpgItemContaReceberCliente;
        fpgItemContaReceberCliente.setText(financeiroVendasDomain.getFpagamento_financeiro());

        TextView fpgItemContaReceberVencimento = holder.fpgItemContaReceberVencimento;
        fpgItemContaReceberVencimento.setText("Venc. " + classAuxiliar.exibirData(financeiroVendasDomain.getVencimento_financeiro()));

        TextView vfpgItemContaReceberCliente = holder.vfpgItemContaReceberCliente;
        vfpgItemContaReceberCliente.setText("R$ " + classAuxiliar.maskMoney(new BigDecimal(financeiroVendasDomain.getValor_financeiro())));

        CheckBox cbItemContaReceberCliente = holder.cbItemContaReceberCliente;
        //cbItemContaReceberCliente.setChecked(false);
        cbItemContaReceberCliente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    //
                    String[] sv = {
                            String.valueOf(classAuxiliar.converterValores(tvTotalPagarContasReceberCliente.getText().toString())),
                            String.valueOf(financeiroVendasDomain.getValor_financeiro())
                    };
                    //
                    tvTotalPagarContasReceberCliente.setText(classAuxiliar.maskMoney(classAuxiliar.somar(sv)));

                    bd.updateFinanceiroReceber(financeiroVendasDomain.getCodigo_financeiro(), "0", id_baixa_app);

                    //ADICIONA O ID DO FINANCEIRO EM UM ARRAY PARA SALVAR A BAIXA NA TABELA CONTAS_RECEBER NA FINALIZAÇÃO
                    //ids.add(Integer.parseInt(financeiroVendasDomain.getCodigo_financeiro()), financeiroVendasDomain.getCodigo_financeiro());
                } else {
                    //
                    String[] sv = {
                            String.valueOf(classAuxiliar.converterValores(tvTotalPagarContasReceberCliente.getText().toString())),
                            String.valueOf(financeiroVendasDomain.getValor_financeiro())
                    };
                    //
                    tvTotalPagarContasReceberCliente.setText(classAuxiliar.maskMoney(classAuxiliar.subitrair(sv)));

                    bd.updateFinanceiroReceber(financeiroVendasDomain.getCodigo_financeiro(), "1", 0);

                    //REMOVE O ID DO FINANCEIRO A RECEBER DO ARRAY
                    //ids.remove(Integer.parseInt(financeiroVendasDomain.getCodigo_financeiro()));
                }
            }
        });

        /*//
        TextView codigo = holder.tvCodigo;
        codigo.setText(clientes.getCodigo());

        //
        TextView nome = holder.tvNome;
        nome.setText(clientes.getNome());

        holder.LlList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, ContasReceberCliente.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("codigo", clientes.getCodigo());
                in.putExtra("nome", clientes.getNome());
                context.startActivity(in);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        /*LinearLayout LlList;
        TextView tvCodigo;*/
        CheckBox cbItemContaReceberCliente;
        TextView fpgItemContaReceberCliente, vfpgItemContaReceberCliente, fpgItemContaReceberVencimento;

        public ViewHolder(View itemView) {
            super(itemView);

            cbItemContaReceberCliente = (CheckBox) itemView.findViewById(R.id.cbItemContaReceberCliente);
            fpgItemContaReceberCliente = (TextView) itemView.findViewById(R.id.fpgItemContaReceberCliente);
            vfpgItemContaReceberCliente = (TextView) itemView.findViewById(R.id.vfpgItemContaReceberCliente);
            fpgItemContaReceberVencimento = (TextView) itemView.findViewById(R.id.fpgItemContaReceberVencimento);
        }
    }
}
