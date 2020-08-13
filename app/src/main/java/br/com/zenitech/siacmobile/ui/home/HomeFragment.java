package br.com.zenitech.siacmobile.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.ContasReceberConsultarCliente;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.EnviarDadosServidor;
import br.com.zenitech.siacmobile.FinanceiroDaVenda;
import br.com.zenitech.siacmobile.Principal2;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.Vendas;
import br.com.zenitech.siacmobile.VendasConsultarClientes;

import static android.content.Context.MODE_PRIVATE;

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
    SharedPreferences prefs;
    ClassAuxiliar aux;
    private AlertDialog alerta;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(
                R.layout.fragment_principal_content, container, false);
        setHasOptionsMenu(true);

        //
        context = view.getContext();
        bd = new DatabaseHelper(context);
        prefs = context.getSharedPreferences("preferencias", MODE_PRIVATE);
        aux = new ClassAuxiliar();

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
        view.findViewById(R.id.cv_venda).setOnClickListener(view1 -> {
            if (Objects.requireNonNull(prefs.getString("data_movimento_atual", "")).equalsIgnoreCase(aux.inserirDataAtual())) {
                startActivity(new Intent(getContext(), VendasConsultarClientes.class));
            } else {
                alerta();
            }
        });

        //CONSULTAR CLIENTE CONTAS RECEBER
        view.findViewById(R.id.cv_contas_receber).setOnClickListener(view13 -> startActivity(new Intent(getContext(), ContasReceberConsultarCliente.class)));

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

    private void alerta() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        builder.setMessage("Não foi possível iniciar uma nova venda, sincronismo pendente!");
        //define um botão como positivo
        builder.setPositiveButton("Gerenciar", (arg0, arg1) -> startActivity(new Intent(context, EnviarDadosServidor.class)));
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