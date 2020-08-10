package br.com.zenitech.siacmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.RelatorioContasReceber;
import br.com.zenitech.siacmobile.RelatorioVendas;


public class RelatoriosContentFragment extends Fragment {
    public RelatoriosContentFragment() {
    }

    /*
    public static EmpregosContentFragment newInstance() {
        EmpregosContentFragment fragment = new EmpregosContentFragment();
        return fragment;
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(
                R.layout.fragment_relatorios_content, container, false);
        setHasOptionsMenu(true);

        //INICIAR RELATÓRIO DE VENDAS
        view.findViewById(R.id.cv_relatorio_venda).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                startActivity(new Intent(getContext(), RelatorioVendas.class));
            }
        });

        //INICIAR RELATÓRIO DE VENDAS
        view.findViewById(R.id.cv_rcr).setOnClickListener(view1 -> {
            //
            startActivity(new Intent(getContext(), RelatorioContasReceber.class));
        });

        return view;
    }
}
