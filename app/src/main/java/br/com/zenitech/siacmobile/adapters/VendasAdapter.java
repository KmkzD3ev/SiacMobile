package br.com.zenitech.siacmobile.adapters;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.domains.VendasDomain;

import static br.com.zenitech.siacmobile.Vendas.textTotalItens;
import static br.com.zenitech.siacmobile.Vendas.txtTotalVenda;

public class VendasAdapter extends RecyclerView.Adapter<VendasAdapter.ViewHolder> {

    private ClassAuxiliar classAuxiliar;
    private Context context;
    private ArrayList<VendasDomain> elementos;

    public VendasAdapter(Context context, ArrayList<VendasDomain> elementos) {
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
        View view = inflater.inflate(R.layout.item_vendas, parent, false);

        //
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

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
        TextView valor = holder.txtValor;
        valor.setText(classAuxiliar.maskMoney(new BigDecimal(vendasDomain.getPreco_unitario())));
        //
        TextView total = holder.txtTotal;
        total.setText(classAuxiliar.maskMoney(new BigDecimal(vendasDomain.getValor_total())));

        holder.btnExcluirVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirItem(vendasDomain.getCodigo_venda(), vendasDomain.getCodigo_venda_app(), 0.0, position);
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
        TextView txtProduto, txtQuantidade, txtValor, txtTotal;
        ImageButton btnExcluirVenda;

        public ViewHolder(View itemView) {
            super(itemView);

            //
            //LlList = (LinearLayout) itemView.findViewById(R.id.LlList);
            txtProduto = (TextView) itemView.findViewById(R.id.txtProduto);
            txtQuantidade = (TextView) itemView.findViewById(R.id.txtQuantidade);
            txtValor = (TextView) itemView.findViewById(R.id.txtValor);
            txtTotal = (TextView) itemView.findViewById(R.id.txtTotal);
            btnExcluirVenda = (ImageButton) itemView.findViewById(R.id.btnExcluirVenda);
        }
    }

    public void excluirItem(String codigo, String codigo_venda_app, double totalVenda, int position) {
        VendasDomain vendasDomain = new VendasDomain(codigo, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        DatabaseHelper bd = new DatabaseHelper(context);
        bd.deleteItemVenda(vendasDomain);


        elementos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, elementos.size());

        if (elementos.size() != 0) {
            String valor = bd.getValorTotalVenda(codigo_venda_app);
            txtTotalVenda.setText(classAuxiliar.maskMoney(new BigDecimal(valor)));
            textTotalItens.setText(String.valueOf(elementos.size()));
        } else {
            txtTotalVenda.setText(classAuxiliar.maskMoney(new BigDecimal("0.0")));
            textTotalItens.setText("0");
        }
    }
}
