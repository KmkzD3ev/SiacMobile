package br.com.zenitech.siacmobile.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import br.com.zenitech.siacmobile.ContasReceberConsultarCliente;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.Vendas;
import br.com.zenitech.siacmobile.VendasConsultarClientes;

public class HomeFragment extends Fragment {
/*

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }






*/

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
        view.findViewById(R.id.cv_emissor_notas).setOnClickListener(view12 -> {
            PackageManager packageManager = requireActivity().getPackageManager();
            String packageName = "com.lvrenyang.sample1";
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            Objects.requireNonNull(intent).putExtra("teste", "Olha, kkk");
            intent.putExtra("teste1", "O negocio");
            intent.putExtra("teste2", "vai dá certo");
            startActivity(intent);
        });


        return view;
    }
}