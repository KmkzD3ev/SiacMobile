package br.com.zenitech.siacmobile.ui_tela_principal.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.BuildConfig;
import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.ContasReceberConsultarCliente;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.EnviarDadosServidor;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.SincronizarBancoDados;
import br.com.zenitech.siacmobile.ValeListClientes;
import br.com.zenitech.siacmobile.Vales;
import br.com.zenitech.siacmobile.Vendas;
import br.com.zenitech.siacmobile.VendasConsultarClientes;
import br.com.zenitech.siacmobile.domains.PosApp;
import br.com.zenitech.siacmobile.domains.UnidadesDomain;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private DatabaseHelper bd;
    SharedPreferences prefs;
    ClassAuxiliar aux;
    private AlertDialog alerta;
    Context context;

    TextView textView, txtTransmitida, txtContigencia, txtStatusTransmissao, txtVersao, txtEmpresa, txtCodUnidade, txtDataUltimoSinc;
    PosApp posApp;
    ArrayList<UnidadesDomain> elementosUnidades;
    UnidadesDomain unidades;
    CardView cardv_vales;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(
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
                view.findViewById(R.id.cv_excluir_ultima_venda).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.cv_editar_ultima_venda).setVisibility(View.GONE);
                view.findViewById(R.id.cv_excluir_ultima_venda).setVisibility(View.GONE);
            }
        } catch (Exception ignored) {

        }

        //
        view.findViewById(R.id.ll_editar_ultima_venda).setOnClickListener(v -> {
            if (Objects.requireNonNull(prefs.getString("data_movimento_atual", "")).equalsIgnoreCase(aux.inserirDataAtual())) {
                try {
                    //CONSULTAR DADOS DA VENDA
                    String[] dados_venda = bd.getUltimaVendasCliente();
                    //CONSULTAR DADOS DA VENDA
                    String[] dados_cli_venda = bd.getClienteUltimaVendas(dados_venda[2]);

                    Intent in = new Intent(getContext(), Vendas.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    in.putExtra("id_venda", dados_venda[0]);
                    in.putExtra("id_venda_app", dados_venda[1]);
                    in.putExtra("codigo", dados_venda[2]);
                    in.putExtra("nome", dados_venda[3]);
                    in.putExtra("latitude_cliente", prefs.getString("latitude_cliente", ""));
                    in.putExtra("longitude_cliente", prefs.getString("longitude_cliente", ""));
                    in.putExtra("saldo", dados_cli_venda[0]);
                    in.putExtra("cpfcnpj", dados_cli_venda[1]);
                    in.putExtra("endereco", dados_cli_venda[2]);
                    in.putExtra("editar", "sim");
                    requireContext().startActivity(in);
                } catch (Exception ignored) {

                }
            } else {
                alerta();
            }

        });

        //
        view.findViewById(R.id.ll_excluir_ultima_venda).setOnClickListener(v -> {
            /*try {
                //CONSULTAR DADOS DA VENDA
                String[] dados_venda = bd.getUltimaVendasCliente();

                Intent in = new Intent(getContext(), Vendas.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("id_venda", dados_venda[0]);
                in.putExtra("id_venda_app", dados_venda[1]);
                in.putExtra("codigo", dados_venda[2]);
                in.putExtra("nome", dados_venda[3]);
                in.putExtra("latitude_cliente", prefs.getString("latitude_cliente", ""));
                in.putExtra("longitude_cliente", prefs.getString("longitude_cliente", ""));
                in.putExtra("editar", "sim");
                requireContext().startActivity(in);
            } catch (Exception ignored) {

            }*/

            cancelarVenda();
        });

        //INICIAR VENDAS
        view.findViewById(R.id.cv_venda).setOnClickListener(view1 -> {

            //startActivity(new Intent(getContext(), VendasConsultarClientes.class));

            if (Objects.requireNonNull(prefs.getString("data_movimento_atual", "")).equalsIgnoreCase(aux.inserirDataAtual())) {
                startActivity(new Intent(getContext(), VendasConsultarClientes.class));
            } else {
                alerta();
            }
        });

        //CONSULTAR CLIENTE CONTAS RECEBER
        view.findViewById(R.id.cv_contas_receber).setOnClickListener(view13 -> {
            if (Objects.requireNonNull(prefs.getString("data_movimento_atual", "")).equalsIgnoreCase(aux.inserirDataAtual())) {

                if (prefs.getString("mostrar_contas_receber", "0").equalsIgnoreCase("0")) {
                    alertaContaReceber();
                    return;
                }
                startActivity(new Intent(getContext(), ContasReceberConsultarCliente.class));
            } else {
                alerta();
            }
        });

        //CONSULTAR CLIENTE CONTAS RECEBER
        view.findViewById(R.id.cv_emissor_notas).setOnClickListener(view12 -> {
            /*PackageManager packageManager = requireActivity().getPackageManager();
            String packageName = "br.com.zenitech.emissorweb";
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.putExtra("teste", "Olha, kkk");
                intent.putExtra("teste1", "O negocio");
                intent.putExtra("teste2", "vai dá certo");
                startActivity(intent);
            }*/

            Intent i = new Intent();
            i.setClassName("br.com.zenitech.siacmobile.Vale", "br.com.zenitech.zcallmobile.NovaEntrega");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("id_pedido", "111");
            i.putExtra("cliente", "111");
            i.putExtra("localidade", "111");
            context.startActivity(i);
        });

        //CONSULTAR VALE PRODUTO
        if (prefs.getString("baixar_vale", "0").equalsIgnoreCase("1")) {

            cardv_vales = view.findViewById(R.id.cardv_vales);
            cardv_vales.setVisibility(View.VISIBLE);
            view.findViewById(R.id.cv_vales).setOnClickListener(view1 -> {
                if (Objects.requireNonNull(prefs.getString("data_movimento_atual", "")).equalsIgnoreCase(aux.inserirDataAtual())) {

                    boolean escolher_cliente = false;

                    try {
                        if (posApp.getEscolher_cliente_vale().equalsIgnoreCase("1"))
                            escolher_cliente = true;

                    } catch (Exception ignored) {
                    }

                    if (escolher_cliente)
                        startActivity(new Intent(getContext(), ValeListClientes.class));
                    else
                        startActivity(new Intent(getContext(), Vales.class));

                    /*
                    Intent i = new Intent();
                    i.setClassName("br.com.zenitech.siacmobile", "br.com.zenitech.zcallmobile.NovaEntrega");
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("id_pedido", "111");
                    i.putExtra("cliente", "111");
                    i.putExtra("localidade", "111");
                    context.startActivity(i);*/
                } else {
                    alerta();
                }
            });
        }

        if (!Objects.requireNonNull(prefs.getString("data_movimento_atual", "")).equalsIgnoreCase(aux.inserirDataAtual())) {
            if (bd.getAllVendas().size() == 0 && bd.getAllRecebidos().size() == 0) {
                alertaBanco();
            }
        }

        elementosUnidades = bd.getUnidades();
        unidades = elementosUnidades.get(0);
        //elementosPos = bd.getPos();
        posApp = bd.getPos();

        //
        txtEmpresa = view.findViewById(R.id.txtEmpresa);
        txtCodUnidade = view.findViewById(R.id.txtCodUnidade);
        textView = view.findViewById(R.id.text_home);
        txtVersao = view.findViewById(R.id.txtVersao);
        txtDataUltimoSinc = view.findViewById(R.id.txtDataUltimoSinc);
        txtEmpresa.setText(unidades.getDescricao_unidade());
        txtCodUnidade.setText(posApp.getUnidade());
        //
        textView.setText(String.format("%s | %s", prefs.getString("serial_app", ""), posApp.getSerie()));
        //txtVersao.setText(String.format("Versão %s", BuildConfig.VERSION_NAME));
        txtVersao.setText(BuildConfig.VERSION_NAME);
        txtDataUltimoSinc.setText(prefs.getString("data_sincronizado", ""));
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

    private void alerta() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        builder.setMessage("Não foi possível editar ou iniciar uma nova venda, sincronismo pendente!");
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

    private void alertaBanco() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        builder.setMessage("Para continuar é preciso atualizar seu banco de dados");
        //define um botão como positivo
        builder.setPositiveButton("Atualizar Agora", (arg0, arg1) -> {
            //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
            context.deleteDatabase("siacmobileDB");
            Intent i = new Intent(context, SincronizarBancoDados.class);
            i.putExtra("atualizarbd", "sim");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
        //define um botão como negativo.
        /*builder.setNegativeButton("Não", (arg0, arg1) -> {
            //Toast.makeText(InformacoesVagas.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
        });*/
        builder.setCancelable(false);
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();
    }

    private void cancelarVenda() {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        builder.setMessage("Você Deseja Realmente Excluir Esta Venda?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", (arg0, arg1) -> {
            //Toast.makeText(InformacoesVagas.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
            int i = bd.deleteVenda(prefs.getInt("id_venda_app", 0));
            if (i != 0) {
                Toast.makeText(context, "Venda excluída!", Toast.LENGTH_LONG).show();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", (arg0, arg1) -> {
            //Toast.makeText(InformacoesVagas.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            String[] dados_venda = bd.getUltimaVendasCliente();
            if (dados_venda.length != 0) {
                view.findViewById(R.id.cv_editar_ultima_venda).setVisibility(View.VISIBLE);
                view.findViewById(R.id.cv_excluir_ultima_venda).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.cv_editar_ultima_venda).setVisibility(View.GONE);
                view.findViewById(R.id.cv_excluir_ultima_venda).setVisibility(View.GONE);
            }
        } catch (Exception ignored) {

        }
    }
}