package br.com.zenitech.siacmobile.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.domains.FinanceiroReceberClientes;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;

import static br.com.zenitech.siacmobile.ContasReceberCliente.IdsCR;
//import static br.com.zenitech.siacmobile.ContasReceberCliente.id_baixa_app;
import static br.com.zenitech.siacmobile.ContasReceberCliente.tvCodsDocs;
//import static br.com.zenitech.siacmobile.ContasReceberCliente.tvTotalPagarContasReceberCliente;

public class ContasReceberClientesAdapter extends RecyclerView.Adapter<ContasReceberClientesAdapter.ViewHolder> {


    private final Context context;
    private final ArrayList<FinanceiroReceberClientes> elementos;
    private final ClassAuxiliar classAuxiliar;
    SQLiteDatabase conexao;
    private DatabaseHelper bd;
    private final TextView tvTotalPagarContasReceberCliente;

    public ContasReceberClientesAdapter(
            Context context,
            ArrayList<FinanceiroReceberClientes> elementos,
            ClassAuxiliar classAuxiliar,
            TextView tvTotalPagarContasReceberCliente
    ) {
        this.context = context;
        this.elementos = elementos;
        this.classAuxiliar = classAuxiliar;
        this.tvTotalPagarContasReceberCliente = tvTotalPagarContasReceberCliente;
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //
        final FinanceiroReceberClientes financeiroVendasDomain = elementos.get(position);

        //valFinanceiroReceber = financeiroVendasDomain.getValor_financeiro();

        TextView fpgItemContaReceberCliente = holder.fpgItemContaReceberCliente;
        fpgItemContaReceberCliente.setText(financeiroVendasDomain.getFpagamento_financeiro());

        TextView fpgItemContaReceberVencimento = holder.fpgItemContaReceberVencimento;
        fpgItemContaReceberVencimento.setText(String.format("Vencimento: %s", classAuxiliar.exibirData(financeiroVendasDomain.getVencimento_financeiro())));

        TextView fpgItemContaReceberDocumento = holder.fpgItemContaReceberDocumento;
        fpgItemContaReceberDocumento.setText(String.format("Doc: %s", financeiroVendasDomain.getDocumento_financeiro()));

        TextView vfpgItemContaReceberCliente = holder.vfpgItemContaReceberCliente;
        //
        String valorPago = bd.getTotalRecebidoList(financeiroVendasDomain.getCodigo_financeiro());
        Log.i("ContasReceber", " TOTAL PAGO " + valorPago);
        if (!valorPago.equalsIgnoreCase("0")) {
            String[] subtrValorPago = {
                    String.valueOf(financeiroVendasDomain.getValor_financeiro()),
                    valorPago
            };

            Log.i("ContasReceber", " VALOR FINANCEIRO " + classAuxiliar.subitrair(subtrValorPago));
            vfpgItemContaReceberCliente.setText(String.format("R$ %s", classAuxiliar.maskMoney(classAuxiliar.subitrair(subtrValorPago))));
        } else {
            vfpgItemContaReceberCliente.setText(String.format("R$ %s", classAuxiliar.maskMoney(new BigDecimal(financeiroVendasDomain.getValor_financeiro()))));
        }

        Log.i("ContasReceber - IDS ", String.valueOf(IdsCR.size()));

        //
        CheckBox cbItemContaReceberCliente = holder.cbItemContaReceberCliente;
        cbItemContaReceberCliente.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Log.i("ContasReceber - IDS ", financeiroVendasDomain.getCodigo_financeiro());
                IdsCR.add(financeiroVendasDomain.getCodigo_financeiro());
                Log.i("ContasReceber - IDS ", String.valueOf(IdsCR.size()));
                //
                String[] sv = {
                        String.valueOf(classAuxiliar.converterValores(tvTotalPagarContasReceberCliente.getText().toString())),
                        String.valueOf(financeiroVendasDomain.getValor_financeiro())
                };

                if (!valorPago.equalsIgnoreCase("0")) {
                    String[] subtrValorPago = {
                            String.valueOf(financeiroVendasDomain.getValor_financeiro()),
                            valorPago
                    };

                    String[] sv2 = {
                            String.valueOf(classAuxiliar.converterValores(tvTotalPagarContasReceberCliente.getText().toString())),
                            String.valueOf(classAuxiliar.subitrair(subtrValorPago))
                    };

                    tvTotalPagarContasReceberCliente.setText(classAuxiliar.maskMoney(classAuxiliar.somar(sv2)));
                } else {
                    tvTotalPagarContasReceberCliente.setText(classAuxiliar.maskMoney(classAuxiliar.somar(sv)));
                }

                //
                bd.updateFinanceiroReceber(financeiroVendasDomain.getCodigo_financeiro(), "0", Integer.parseInt(financeiroVendasDomain.getCodigo_cliente()));
                tvCodsDocs += "Cód. Fin.: " + financeiroVendasDomain.getCodigo_financeiro() + " | Doc." +
                        " Fin.: " + financeiroVendasDomain.getDocumento_financeiro() + "\n";
            } else {
                //
                String[] sv = {
                        String.valueOf(classAuxiliar.converterValores(tvTotalPagarContasReceberCliente.getText().toString())),
                        String.valueOf(financeiroVendasDomain.getValor_financeiro())
                };

                for (int i = 0; i < IdsCR.size(); i++) {
                    if (IdsCR.get(i).equalsIgnoreCase(financeiroVendasDomain.getCodigo_financeiro())) {
                        try {
                            Log.i("ContasReceber - IDS ", IdsCR.get(i));
                            IdsCR.remove(i);
                        } catch (Exception ignored) {

                        }
                    }
                }

                Log.i("ContasReceber - IDS ", String.valueOf(IdsCR.size()));

                if (!valorPago.equalsIgnoreCase("0")) {
                    String[] subtrValorPago = {
                            String.valueOf(financeiroVendasDomain.getValor_financeiro()),
                            valorPago
                    };

                    String[] sv2 = {
                            String.valueOf(classAuxiliar.converterValores(tvTotalPagarContasReceberCliente.getText().toString())),
                            String.valueOf(classAuxiliar.subitrair(subtrValorPago))
                    };

                    tvTotalPagarContasReceberCliente.setText(classAuxiliar.maskMoney(classAuxiliar.subitrair(sv2)));
                } else {
                    tvTotalPagarContasReceberCliente.setText(classAuxiliar.maskMoney(classAuxiliar.subitrair(sv)));
                }

                //
                //tvTotalPagarContasReceberCliente.setText(classAuxiliar.maskMoney(classAuxiliar.subitrair(sv)));
                bd.updateFinanceiroReceber(financeiroVendasDomain.getCodigo_financeiro(), "1", 0);
            }
        });

        /*if(valFinanceiroReceber.equalsIgnoreCase("0")){
            elementos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, elementos.size());
        }*/
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbItemContaReceberCliente;
        TextView fpgItemContaReceberCliente, vfpgItemContaReceberCliente, fpgItemContaReceberVencimento,
                fpgItemContaReceberDocumento;

        public ViewHolder(View itemView) {
            super(itemView);
            cbItemContaReceberCliente = itemView.findViewById(R.id.cbItemContaReceberCliente);
            fpgItemContaReceberCliente = itemView.findViewById(R.id.fpgItemContaReceberCliente);
            vfpgItemContaReceberCliente = itemView.findViewById(R.id.vfpgItemContaReceberCliente);
            fpgItemContaReceberVencimento = itemView.findViewById(R.id.fpgItemContaReceberVencimento);
            fpgItemContaReceberDocumento = itemView.findViewById(R.id.fpgItemContaReceberDocumento);
        }
    }
}
