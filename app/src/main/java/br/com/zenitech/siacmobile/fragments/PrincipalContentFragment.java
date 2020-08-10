package br.com.zenitech.siacmobile.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import br.com.zenitech.siacmobile.ContasReceberConsultarCliente;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.Vendas;
import br.com.zenitech.siacmobile.VendasConsultarClientes;


public class PrincipalContentFragment extends Fragment {


    private DatabaseHelper bd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(
                R.layout.fragment_principal_content, container, false);
        setHasOptionsMenu(true);

        //
        bd = new DatabaseHelper(getContext());

        try {
            String[] dados_venda = bd.getUltimaVendasCliente();
            if (dados_venda.length != 0) {
                view.findViewById(R.id.cv_editar_ultima_venda).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.cv_editar_ultima_venda).setVisibility(View.GONE);
            }
        } catch (Exception ignored) {

        }

        //
        view.findViewById(R.id.ll_editar_ultima_venda).setOnClickListener(v -> {
            try {
                //CONSULTAR DADOS DA VENDA
                String[] dados_venda = bd.getUltimaVendasCliente();

                Intent in = new Intent(getContext(), Vendas.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("id_venda", dados_venda[0]);
                in.putExtra("id_venda_app", dados_venda[1]);
                in.putExtra("codigo", dados_venda[2]);
                in.putExtra("nome", dados_venda[3]);
                requireContext().startActivity(in);
            } catch (Exception ignored) {

            }
        });

        //INICIAR VENDAS
        view.findViewById(R.id.cv_venda).setOnClickListener(view1 -> startActivity(new Intent(getContext(), VendasConsultarClientes.class)));

        //CONSULTAR CLIENTE CONTAS RECEBER
        view.findViewById(R.id.cv_contas_receber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ContasReceberConsultarCliente.class));
            }
        });

        //CONSULTAR CLIENTE CONTAS RECEBER
        view.findViewById(R.id.cv_emissor_notas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager packageManager = getActivity().getPackageManager();
                String packageName = "com.lvrenyang.sample1";
                Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                intent.putExtra("teste", "Olha, kkk");
                intent.putExtra("teste1", "O negocio");
                intent.putExtra("teste2", "vai dá certo");
                if (null != intent) {
                    startActivity(intent);
                }
            }
        });


        return view;
    }
}
