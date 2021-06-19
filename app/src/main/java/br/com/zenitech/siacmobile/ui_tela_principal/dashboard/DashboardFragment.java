package br.com.zenitech.siacmobile.ui_tela_principal.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.RelatorioContasReceber;
import br.com.zenitech.siacmobile.RelatorioVendas;
import br.com.zenitech.siacmobile.RelatorioVendasPedido;

public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.fragment_relatorios_content, container, false);
        setHasOptionsMenu(true);

        //INICIAR RELATÓRIO DE VENDAS
        view.findViewById(R.id.cv_relatorio_venda).setOnClickListener(view12 -> {
            //
            //startActivity(new Intent(getContext(), RelatorioVendas.class));
            startActivity(new Intent(getContext(), RelatorioVendasPedido.class));
        });

        //INICIAR RELATÓRIO DE VENDAS
        view.findViewById(R.id.cv_rcr).setOnClickListener(view1 -> {
            //
            startActivity(new Intent(getContext(), RelatorioContasReceber.class));
        });

        return view;
    }
}