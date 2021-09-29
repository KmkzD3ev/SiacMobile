package br.com.zenitech.siacmobile.ui_tela_principal.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.RelatorioContasReceber;
import br.com.zenitech.siacmobile.RelatorioVendas;
import br.com.zenitech.siacmobile.RelatorioVendasPedido;

import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {

    SharedPreferences prefs;
    Context context;
    private AlertDialog alerta;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.fragment_relatorios_content, container, false);
        setHasOptionsMenu(true);

        context = view.getContext();
        prefs = context.getSharedPreferences("preferencias", MODE_PRIVATE);

        //INICIAR RELATÓRIO DE VENDAS
        view.findViewById(R.id.cv_relatorio_venda).setOnClickListener(view12 -> {
            //
            //startActivity(new Intent(getContext(), RelatorioVendas.class));
            startActivity(new Intent(getContext(), RelatorioVendasPedido.class));
        });

        //INICIAR RELATÓRIO DE VENDAS
        view.findViewById(R.id.cv_rcr).setOnClickListener(view1 -> {
            //
            if (prefs.getString("mostrar_contas_receber", "0").equalsIgnoreCase("0")) {
                alertaContaReceber();
                return;
            }

            startActivity(new Intent(getContext(), RelatorioContasReceber.class));
        });

        return view;
    }

    private void alertaContaReceber() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        builder.setMessage("Esta opção não está disponível para esse serial.");
        //define um botão como positivo
        builder.setPositiveButton("Ok", (arg0, arg1) -> {
        });
        //define um botão como negativo.
        /*builder.setNegativeButton("Não", (arg0, arg1) -> {
            //Toast.makeText(InformacoesVagas.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
        });*/
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();
    }
}