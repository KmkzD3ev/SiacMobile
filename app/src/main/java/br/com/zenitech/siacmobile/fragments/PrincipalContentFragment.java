package br.com.zenitech.siacmobile.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import br.com.zenitech.siacmobile.ContasReceberConsultarCliente;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.Vendas;
import br.com.zenitech.siacmobile.VendasConsultarClientes;


public class PrincipalContentFragment extends Fragment {
    private DatabaseHelper bd;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(
                R.layout.fragment_principal_content, container, false);
        setHasOptionsMenu(true);

        //
        bd = new DatabaseHelper(getContext());
        prefs = getContext().getSharedPreferences("preferencias", MODE_PRIVATE);

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
                prefs.edit().putBoolean("EditarVenda", true).apply();
                //CONSULTAR DADOS DA VENDA
                String[] dados_venda = bd.getUltimaVendasCliente();

                Intent in = new Intent(getContext(), Vendas.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("id_venda", dados_venda[0]);
                in.putExtra("id_venda_app", dados_venda[1]);
                in.putExtra("codigo", dados_venda[2]);
                in.putExtra("nome", dados_venda[3]);
                //in.putExtra("editar", "sim");
                requireContext().startActivity(in);
            } catch (Exception ignored) {

            }
        });

        //INICIAR VENDAS
        view.findViewById(R.id.cv_venda).setOnClickListener(view1 -> startActivity(new Intent(getContext(), VendasConsultarClientes.class)));

        //CONSULTAR CLIENTE CONTAS RECEBER
        view.findViewById(R.id.cv_contas_receber).setOnClickListener(view13 -> startActivity(new Intent(getContext(), ContasReceberConsultarCliente.class)));

        //CONSULTAR CLIENTE CONTAS RECEBER
        view.findViewById(R.id.cv_emissor_notas).setOnClickListener(view12 -> {
            PackageManager packageManager = requireActivity().getPackageManager();
            String packageName = "br.com.zenitech.emissorweb";
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            intent.putExtra("teste", "Olha, kkk");
            intent.putExtra("teste1", "O negocio");
            intent.putExtra("teste2", "vai dá certo");
            startActivity(intent);/**/
        });


        return view;
    }
}
