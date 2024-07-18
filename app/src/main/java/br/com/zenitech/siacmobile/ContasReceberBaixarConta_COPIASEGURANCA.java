package br.com.zenitech.siacmobile;

import static br.com.zenitech.siacmobile.ContasReceberCliente.IdsCR;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.FinContasReceberCliAdapter;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;
import br.com.zenitech.siacmobile.domains.FormasPagamentoReceberTemp;

public class ContasReceberBaixarConta_COPIASEGURANCA extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;
    private ClassAuxiliar cAux;

    public static String totalFinanceiro;
    public static TextView txtTotalFinanceiroReceber;
    public static TextView txtTotalItemFinanceiroReceber;
    public static EditText txtVencimentoFormaPagamentoReceber, txtValorFormaPagamento;
    public static LinearLayout bgTotalReceber;

    //
    ArrayList<String> listaFormasPagamentoCliente;
    private DatabaseHelper bd;
    private Spinner spFormasPagamentoCliente;
    private String codigo_cliente = "";
    private EditText txtDocumentoFormaPagamento;
    private TextView textCodDoc;

    //LISTAR VENDAS
    private ArrayList<FormasPagamentoReceberTemp> listaFinanceiroCliente;
    private FinContasReceberCliAdapter adapter;
    //private FinanceiroContasReceberAdapter adapter;
    private RecyclerView rvFinanceiro;

    //
    int id = 1;

    //
    TextInputLayout tilDocumento, tilVencimento;
    Button btnAddFormaPagamento, btnPagamento;

    //
    //ArrayList<FinanceiroReceberClientes> listaContasReceberCliente;

    private String ValorABaixar = "0";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas_receber_baixar_conta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //----------------------------------------V-----------------------------------------------

        cAux = new ClassAuxiliar();
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        ed = prefs.edit();

        //
        id = prefs.getInt("id_financeiro_venda", 1);

        bd = new DatabaseHelper(this);
        try {
            bd.deleteFinanceiroReceberTemp();
        } catch (Exception e) {
            showLOG("ContasReceber", e.getMessage());
        }

        //
        bgTotalReceber = findViewById(R.id.bgTotalReceber);

        //
        rvFinanceiro = findViewById(R.id.rvFinanceiro);
        rvFinanceiro.setLayoutManager(new LinearLayoutManager(this));

        //
        tilDocumento = findViewById(R.id.tilDocumento);
        tilVencimento = findViewById(R.id.tilVencimento);

        textCodDoc = findViewById(R.id.textCodDoc);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        //Total Venda: R$
        //txtNomeClienteFinanceiro = (TextView) findViewById(R.id.txtNomeClienteFinanceiro);
        txtTotalFinanceiroReceber = findViewById(R.id.txtTotalFinanceiroReceber);
        //
        txtValorFormaPagamento = findViewById(R.id.txtValorFormaPagamento);
        txtValorFormaPagamento.addTextChangedListener(new MoneyTextWatcher(txtValorFormaPagamento));

        txtDocumentoFormaPagamento = findViewById(R.id.txtDocumentoFormaPagamento);

        //
        txtVencimentoFormaPagamentoReceber = findViewById(R.id.txtVencimentoFormaPagamento);
        txtVencimentoFormaPagamentoReceber.setText(cAux.exibirDataAtual());
        txtVencimentoFormaPagamentoReceber.addTextChangedListener(cAux.maskData("##/##/####", txtVencimentoFormaPagamentoReceber));

        //
        txtTotalItemFinanceiroReceber = findViewById(R.id.txtTotalItemFinanceiroReceber);

        //
        btnAddFormaPagamento = findViewById(R.id.btnAddF);
        btnAddFormaPagamento.setOnClickListener(v -> _verificarValores());
        //
        btnPagamento = findViewById(R.id.btnPagamento);
        btnPagamento.setOnClickListener(v -> _finalizarBaixaContaReceber());

        //Log.i("ContasReceber - IDS", String.valueOf(IdsCR.size()));


        //
        Intent intent = getIntent();

        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                //
                Objects.requireNonNull(getSupportActionBar()).setTitle("Baixa Financeiro");

                //
                codigo_cliente = params.getString("codigo_cliente");
                txtTotalFinanceiroReceber.setText(params.getString("valorVenda"));
                txtValorFormaPagamento.setText(params.getString("valorVenda"));
                textCodDoc.setText(params.getString("CodsDocs"));

                //
                ValorABaixar = params.getString("valorVenda");

                String nomeCliente = params.getString("nome_cliente");
                getSupportActionBar().setSubtitle(cAux.maiuscula1(Objects.requireNonNull(nomeCliente).toLowerCase()));
            }
        }

        //
        /*listaFinanceiroCliente = bd.getFinanceiroClienteRecebidos(Integer.parseInt(codigo_cliente));
        adapter = new FinanceiroContasReceberAdapter(this, listaFinanceiroCliente);
        rvFinanceiro.setAdapter(adapter);*/

        //
        listaFormasPagamentoCliente = bd.getFormasPagamentoClienteBaixa(codigo_cliente);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaFormasPagamentoCliente);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormasPagamentoCliente = findViewById(R.id.spFormasPagamentoCliente);
        spFormasPagamentoCliente.setAdapter(adapter);

        spFormasPagamentoCliente.setOnItemSelectedListener(ContasReceberBaixarConta_COPIASEGURANCA.this);
    }

    private void showMSG(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void showLOG(String tag, String msg) {
        Log.i(tag, msg);
    }

    //Começar a partir daqui!
    private void _finalizarBaixaContaReceber() {
        int totalItemFin = Integer.parseInt(cAux.soNumeros(txtTotalItemFinanceiroReceber.getText().toString()));
        int totalFin = Integer.parseInt(cAux.soNumeros(txtTotalFinanceiroReceber.getText().toString()));

        //Log.i("ContasReceber", "" + totalItemFin);
        //Log.i("ContasReceber", "" + totalFin);

        if (totalItemFin == 0) {
            showMSG("Adicione pelo menos uma forma de pagamento ao financeiro.");
        } else if (totalItemFin > totalFin) {
            showMSG("O valor ultrapassa o total.");
        } else {
            try {
                //
                ArrayList<FormasPagamentoReceberTemp> temp = bd.getFinanceiroClienteRecebidos(Integer.parseInt(codigo_cliente));
                //
                int valTemp;
                // LOOP DOS VALORES COM FORMAS DE PAGAMENTOS DIFERENTES
                for (int i = 0; i < temp.size(); i++) {

                    valTemp = Integer.parseInt(cAux.soNumeros(temp.get(i).getValor()));
                    showLOG("ContasReceber", "temp 1= FORMA PAG = " + temp.get(i).getId_forma_pagamento() + " | " + valTemp);

                    if (valTemp > 0) {
                        // FAZ UM LOOP COM OS IDS DAS BAIXAS SELECIONADAS
                        for (int a = 0; a < IdsCR.size(); a++) {
                            //showLOG("ContasReceber", "temp 2= " + a);
                            // PEGA O VALOR TOTAL DO FINANCEIRO REFERENTE AO ID INFORMADO
                            int valFinRecCli = Integer.parseInt(cAux.soNumeros(bd.getValorFinReceberCli(IdsCR.get(a))));
                            // PEGA O TOTAL RECEBIDO DO  FINANCEIRO REFERENTE AO ID INFORMADO
                            int totRecebido = Integer.parseInt(bd.getTotalRecebido(IdsCR.get(a)));

                            // SUBTRAI O VALOR DO FINANCEIRO COM O VALOR RECEBIDO
                            String[] v = {String.valueOf(valFinRecCli), String.valueOf(totRecebido)};
                            // VALOR RESTANTE É IGUAL AO VALOR DO FINANCEIRO MENOS O TOTAL JÁ ADICIONADO A TABELA RECEBIDOS
                            int valorRestante = Integer.parseInt(String.valueOf(cAux.subitrair(v)));
                            //
                            showLOG("ContasReceber", "valTemp = " + valTemp + " | ID " + IdsCR.get(a));
                            showLOG("ContasReceber", "valorRestante = " + valorRestante);

                            // SE O VALOR RESTANTE FOR MAIOR QUE ZERO
                            if (valorRestante > 0) {
                                //
                                int valSalvar;
                                // SE O VALOR TEMPORARIO DA FORMA DE PAGAMENTO FOR MAIOR OU IGUAL AO VALOR RESTANTE
                                if (valTemp >= valorRestante) {
                                    valSalvar = valorRestante;
                                    //
                                    String[] sub = {String.valueOf(valTemp), String.valueOf(valorRestante)};
                                    valTemp = Integer.parseInt(String.valueOf(cAux.subitrair(sub)));
                                } else {
                                    valSalvar = valTemp;
                                    //
                                    valTemp = 0;
                                }

                                // SE O VALOR PARA SALVAR FOR MAIOR QUE ZERO ELE SALVA
                                if (valSalvar > 0) {
                                    showLOG("ContasReceber", "addFinanceiroRecebidos");
                                    bd.addFinanceiroRecebidos(new FinanceiroVendasDomain(
                                            "" + IdsCR.get(a),
                                            "" + prefs.getString("unidade", "UNIDADE TESTE"),
                                            "" + cAux.inserirDataAtual(),
                                            "" + codigo_cliente,
                                            "" + temp.get(i).getId_forma_pagamento(),
                                            "" + txtDocumentoFormaPagamento.getText().toString(),
                                            "" + cAux.inserirData(cAux.formatarData(cAux.soNumeros(txtVencimentoFormaPagamentoReceber.getText().toString()))),
                                            "" + valFinRecCli,//cAux.converterValores(txtValorFormaPagamento.getText().toString()),
                                            "0",
                                            "" + valSalvar,
                                            "0",
                                            "0",
                                            "" + cAux.inserirDataAtual(),
                                            "",
                                            "" + prefs.getInt("id_vendedor", 1),
                                            "" + codigo_cliente,
                                            "",
                                            ""
                                    ));
                                }
                            }
                        }
                    }
                    showLOG("ContasReceber", "valTemp FINAL = " + valTemp);
                }
            } catch (Exception e) {
                showLOG("ContasReceber", "erro = " + e.getMessage());
            }


            //
            Toast.makeText(getBaseContext(), "Operação Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), Principal2.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            sair();
        }
    }

    private void _verificarValores() {
        int ValorFormaPagamento = Integer.parseInt(cAux.soNumeros(txtValorFormaPagamento.getText().toString()));
        //Log.i("ContasReceber", txtValorFormaPagamento.getText().toString());
        //Log.i("ContasReceber", String.valueOf(ValorFormaPagamento));

        //SE O USUÁRIO NÃO ADICIONAR NENHUM VALOR
        if (ValorFormaPagamento == 0) {
            Toast.makeText(getBaseContext(), "Adicione uma valor para esta forma de pagamento.", Toast.LENGTH_LONG).show();
        } else {
            String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

            //SE A FORMA DE PAGAMENTO FOR IGUAL A PRAZO VERIFICA O NÚMERO DO DOCUMENTO E O TIPO DE BAIXA
            if (fPag[1].equals("A PRAZO")) {

                //SE O NÚMERO DO DOCUMENTO ESTIVER VÁSIO MOSTRA A MENSAGEM
                if (txtDocumentoFormaPagamento.getText().toString().equals("") && fPag[2].equals("1")) {
                    //
                    Toast.makeText(getBaseContext(), "Número do documento é obrigatório.", Toast.LENGTH_LONG).show();
                }
                //SE A BAIXA FOR MANUAL VERIFICA O CAMPO VENCIMENTO
                else if (fPag[3].equals("1")) {

                    //SE O CAMPO VENCIMENTO FOR IGUAL A 00/00/0000 PEDE QUE INFORME A DATA DO VENCIMENTO
                    if (txtVencimentoFormaPagamentoReceber.getText().toString().equals("") || txtVencimentoFormaPagamentoReceber.getText().toString().equals("00/00/0000")) {

                        //
                        Toast.makeText(getBaseContext(), "Data do vencimento é obrigatório.", Toast.LENGTH_LONG).show();
                    }
                    //ADICIONA VALOR AO FINANCEIRO
                    else {
                        addFinanceiro();
                    }
                }
                //ADICIONA VALOR AO FINANCEIRO
                else {
                    addFinanceiro();
                }
            }
            //ADICIONA VALOR AO FINANCEIRO
            else {
                addFinanceiro();
            }
        }
    }

    // FAZENDO
    private void addFinanceiro() {
        try {
            String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

            // VERIFICA SE JÁ EXISTE UM REGISTRO PARA A FORMA DE PAGAMENTO ESCOLHIDA
            String[] codigoverFormaPagamentoRecebidos = bd.verForPagRecTemp(fPag[0], String.valueOf(codigo_cliente));
            Log.i("ContasReceber CFPR 0", codigoverFormaPagamentoRecebidos[0]);
            Log.i("ContasReceber CFPR 1", codigoverFormaPagamentoRecebidos[1]);

            //
            if (!codigoverFormaPagamentoRecebidos[0].equalsIgnoreCase("0")) {
                //
                String[] somaValUpd = {
                        codigoverFormaPagamentoRecebidos[1],
                        String.valueOf(cAux.converterValores(txtValorFormaPagamento.getText().toString()))
                };
                String valSoma = String.valueOf(cAux.somar(somaValUpd));
                int upFR = bd.updateFinRecTemp(codigoverFormaPagamentoRecebidos[0], valSoma);
                Log.i("ContasReceber", "upFR = " + upFR);
            } else {
                //
                bd.addValorFinReceber(
                        "" + codigo_cliente,
                        "" + fPag[0],
                        "" + cAux.converterValores(txtValorFormaPagamento.getText().toString())
                );
            }
        } catch (Exception e) {
            Log.i("ContasReceber ERRO ", Objects.requireNonNull(e.getMessage()));
        }

        listarFinanceiro();
    }

    // FAZENDO
    private void listarFinanceiro() {
        try {
            //
            listaFinanceiroCliente = bd.getFinanceiroClienteRecebidos(Integer.parseInt(codigo_cliente));
            adapter = new FinContasReceberCliAdapter(
                    this,
                    listaFinanceiroCliente,
                    bgTotalReceber,
                    txtTotalFinanceiroReceber,
                    txtTotalItemFinanceiroReceber,
                    txtValorFormaPagamento
            );
            rvFinanceiro.setAdapter(adapter);

            //
            String tif = cAux.maskMoney(new BigDecimal(bd.SomaValTotFinReceber(String.valueOf(codigo_cliente))));
            txtTotalItemFinanceiroReceber.setText(tif);

            //
            String valorFinanceiroReceber = String.valueOf(cAux.converterValores(txtTotalFinanceiroReceber.getText().toString()));
            String valorFinanceiroReceberAdd = String.valueOf(cAux.converterValores(txtTotalItemFinanceiroReceber.getText().toString()));

            //SUBTRAIR O VALOR PELA QUANTIDADE
            String[] subtracao = {valorFinanceiroReceber, valorFinanceiroReceberAdd};
            String total = String.valueOf(cAux.subitrair(subtracao));

            txtValorFormaPagamento.setText(total);

            //
            if (comparar()) {

                bgTotalReceber.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.erro));
                txtValorFormaPagamento.setText(R.string.zero_reais);
            } else {
                bgTotalReceber.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.transparente));
            }

            //
            txtDocumentoFormaPagamento.setText("");
            tilDocumento.setVisibility(View.GONE);
            spFormasPagamentoCliente.setSelection(0);
        } catch (Exception e) {
            Log.i("ContasReceber ERRO ", Objects.requireNonNull(e.getMessage()));
        }
    }


    // -----++++++++------
    private void addFinanceiro_OLD() {
        try {
            //
            //listaContasReceberCliente = bd.getListFormContasReceberCliente(codigo_cliente);
            Log.d("ContasReceber", " ValorABaixar = " + ValorABaixar);

            // FAZ UM LOOP COM OS IDS DAS BAIXAS SELECIONADAS
            for (int i = 0; i < IdsCR.size(); i++) {
                //
                Log.i("ContasReceber ID", IdsCR.get(i));
                boolean a = false;

                //
                try {
                    // VERIFICA SE JÁ FOI BAIXADO
                    FinanceiroVendasDomain financeiroBaixaDomains = bd.getBaixaRecebida(IdsCR.get(i));
                    Log.i("ContasReceber FBD", financeiroBaixaDomains.getValor_financeiro());
                    a = false;
                } catch (Exception e) {
                    Log.i("ContasReceber ERRO ", Objects.requireNonNull(e.getMessage()));
                }


                //
                String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

                if (!a) {
                    // VERIFICA SE JÁ EXISTE UM REGISTRO PARA A FORMA DE PAGAMENTO ESCOLHIDA
                    String[] codigoverFormaPagamentoRecebidos = bd.verFormaPagamentoRecebidos(fPag[0], String.valueOf(codigo_cliente));
                    Log.i("ContasReceber CFPR 0", codigoverFormaPagamentoRecebidos[0]);
                    Log.i("ContasReceber CFPR 1", codigoverFormaPagamentoRecebidos[1]);

                    //
                    if (!codigoverFormaPagamentoRecebidos[0].equalsIgnoreCase("0")) {
                        //
                        //bd.updateFinanceiroRecebidos(codigoverFormaPagamentoRecebidos[1], String.valueOf(cAux.converterValores(txtValorFormaPagamento.getText().toString())));
                        String[] somaValUpd = {
                                codigoverFormaPagamentoRecebidos[1],
                                String.valueOf(cAux.converterValores(txtValorFormaPagamento.getText().toString()))
                        };
                        String valSoma = String.valueOf(cAux.somar(somaValUpd));
                        int upFR = bd.updateFinanceiroRecebidos(codigoverFormaPagamentoRecebidos[0], valSoma);
                        Log.i("ContasReceber", "upFR = " + upFR);
                    }
                    //
                    else {
                        //
                        //SFGSDFGDSFG
                        int valFinRecCli = Integer.parseInt(bd.getValorFinReceberCli(IdsCR.get(i)));
                        int totRecebido = Integer.parseInt(bd.getTotalRecebido(IdsCR.get(i)));
                        Log.i("ContasReceber", "valFinRecCli = " + valFinRecCli);
                        Log.i("ContasReceber", "totRecebido = " + totRecebido);

                        if (totRecebido < valFinRecCli) {
                            int valSalvar;
                            Log.i("ContasReceber", "valSalvar = " + totRecebido);
                            int valInformado = Integer.parseInt(cAux.soNumeros(txtValorFormaPagamento.getText().toString()));
                            Log.i("ContasReceber", "valInformado = " + valInformado);
                            if (valInformado >= valFinRecCli) {
                                valSalvar = valFinRecCli;

                                String[] v = {String.valueOf(valInformado), String.valueOf(valInformado)};
                                ValorABaixar = String.valueOf(cAux.subitrair(v));
                            } else {
                                valSalvar = valInformado;
                            }
                            bd.addFinanceiroRecebidos(new FinanceiroVendasDomain(
                                    "" + IdsCR.get(i),
                                    "" + prefs.getString("unidade", "UNIDADE TESTE"),
                                    "" + cAux.inserirDataAtual(),
                                    "" + codigo_cliente,
                                    "" + fPag[0],
                                    "" + txtDocumentoFormaPagamento.getText().toString(),
                                    "" + cAux.inserirData(cAux.formatarData(cAux.soNumeros(txtVencimentoFormaPagamentoReceber.getText().toString()))),
                                    "" + valSalvar,//cAux.converterValores(txtValorFormaPagamento.getText().toString()),
                                    "0",
                                    "0",
                                    "0",
                                    "0",
                                    "" + cAux.inserirDataAtual(),
                                    "",
                                    "" + prefs.getInt("id_vendedor", 1),
                                    "" + codigo_cliente,
                                    "",
                                    ""
                            ));
                        }
                    }
                }
            }

            /*// XXX
            listaFinanceiroCliente = bd.getFinanceiroClienteRecebidos(Integer.parseInt(codigo_cliente));
            adapter = new FinanceiroContasReceberAdapter(this, listaFinanceiroCliente);
            rvFinanceiro.setAdapter(adapter);
            // XXX
            String tif = cAux.maskMoney(new BigDecimal(bd.getValorTotalFinanceiroReceber(String.valueOf(codigo_cliente))));
            txtTotalItemFinanceiroReceber.setText(tif);*/

            //
            String valorFinanceiroReceber = String.valueOf(cAux.converterValores(txtTotalFinanceiroReceber.getText().toString()));
            String valorFinanceiroReceberAdd = String.valueOf(cAux.converterValores(txtTotalItemFinanceiroReceber.getText().toString()));

            //SUBTRAIR O VALOR PELA QUANTIDADE
            String[] subtracao = {valorFinanceiroReceber, valorFinanceiroReceberAdd};
            String total = String.valueOf(cAux.subitrair(subtracao));

            txtValorFormaPagamento.setText(total);

            //
            if (comparar()) {

                bgTotalReceber.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.erro));
                txtValorFormaPagamento.setText(R.string.zero_reais);
            } else {
                bgTotalReceber.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.transparente));
            }

            //
            txtDocumentoFormaPagamento.setText("");
            tilDocumento.setVisibility(View.GONE);
            spFormasPagamentoCliente.setSelection(0);
        } catch (Exception ignored) {

        }
/*

        //
        id = id + 1;
        ed.putInt("id_financeiro_venda", id).apply();

        //
        String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");
        Log.i("ContasReceber", txtVencimentoFormaPagamentoReceber.getText().toString());
        //INSERIR FINANCEIRO
        bd.addFinanceiroRecebidos(new FinanceiroVendasDomain(
                String.valueOf(id),//CODIGO_FINANCEIRO
                prefs.getString("unidade", "UNIDADE TESTE"),//UNIDADE_FINANCEIRO
                cAux.inserirDataAtual(),//DATA_FINANCEIRO
                codigo_cliente,//CODIGO_CLIENTE_FINANCEIRO
                fPag[0],//spFormasPagamentoCliente.getSelectedItem().toString(),//FPAGAMENTO_FINANCEIRO
                txtDocumentoFormaPagamento.getText().toString(),//DOCUMENTO_FINANCEIRO
                String.valueOf(cAux.inserirData(cAux.formatarData(cAux.soNumeros(txtVencimentoFormaPagamentoReceber.getText().toString())))),//VENCIMENTO_FINANCEIRO
                String.valueOf(cAux.converterValores(txtValorFormaPagamento.getText().toString())),//VALOR_FINANCEIRO
                "0",//STATUS_AUTORIZACAO
                "0",//PAGO
                "0",//VASILHAME_REF
                "0",//USUARIO_ATUAL_FINANCEIRO
                "" + cAux.inserirDataAtual(),//DATA_INCLUSAO
                "",//NOSSO_NUMERO_FINANCEIRO
                "" + prefs.getInt("id_vendedor", 1),//ID_VENDEDOR_FINANCEIRO
                "" + prefs.getInt("id_baixa_app", 1)
        ));

        //
        listaFinanceiroCliente = bd.getFinanceiroClienteRecebidos(prefs.getInt("id_baixa_app", 1));
        adapter = new FinanceiroContasReceberAdapter(this, listaFinanceiroCliente);
        rvFinanceiro.setAdapter(adapter);

        //
        String tif = cAux.maskMoney(new BigDecimal(bd.getValorTotalFinanceiroReceber(String.valueOf(prefs.getInt("id_baixa_app", 1)))));
        txtTotalItemFinanceiroReceber.setText(tif);

        //
        String valorFinanceiroReceber = String.valueOf(cAux.converterValores(txtTotalFinanceiroReceber.getText().toString()));
        String valorFinanceiroReceberAdd = String.valueOf(cAux.converterValores(txtTotalItemFinanceiroReceber.getText().toString()));

        //SUBTRAIR O VALOR PELA QUANTIDADE
        String[] subtracao = {valorFinanceiroReceber, valorFinanceiroReceberAdd};
        String total = String.valueOf(cAux.subitrair(subtracao));

        txtValorFormaPagamento.setText(total);

        //
        if (comparar()) {

            bgTotalReceber.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.erro));
            txtValorFormaPagamento.setText(R.string.zero_reais);
        } else {
            bgTotalReceber.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.transparente));
        }

        //
        txtDocumentoFormaPagamento.setText("");
        tilDocumento.setVisibility(View.GONE);
        spFormasPagamentoCliente.setSelection(0);
*/

        //ESCONDER O TECLADO
        // TODO Auto-generated method stub
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");
        if (fPag[1].equals("A PRAZO")) {

            runOnUiThread(() -> {
                //tilDocumento.setVisibility(View.VISIBLE);
                tilVencimento.setVisibility(View.VISIBLE);
            });

            if (fPag[3].equals("1")) {

                runOnUiThread(() -> tilDocumento.setVisibility(View.VISIBLE));
            }
        } else {
            runOnUiThread(() -> {
                tilDocumento.setVisibility(View.GONE);
                tilVencimento.setVisibility(View.GONE);
                txtVencimentoFormaPagamentoReceber.setText(cAux.exibirDataAtual());

                //Log.i("ContasReceber", cAux.exibirDataAtual());
                //Log.i("ContasReceber", txtVencimentoFormaPagamentoReceber.getText().toString());
            });
        }

        //Toast.makeText(this, fPag[1], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            EditText editText = editTextWeakReference.get();
            if (editText == null) return;
            String s = editable.toString();
            editText.removeTextChangedListener(this);
            String cleanString = s.replaceAll("[^0-9]", "");
            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            String formatted = NumberFormat.getCurrencyInstance().format(parsed);
            editText.setText(formatted);
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);
        }
    }

    //COMPARAR O VALOR DO FINANCEIRO COM O VALOR ADICIONADO
    private boolean comparar() {

        //
        BigDecimal valorFinanceiroReceber = new BigDecimal(String.valueOf(cAux.converterValores(txtTotalFinanceiroReceber.getText().toString())));
        BigDecimal valorFinanceiroReceberAdd = new BigDecimal(String.valueOf(cAux.converterValores(txtTotalItemFinanceiroReceber.getText().toString())));

        if (valorFinanceiroReceberAdd.compareTo(valorFinanceiroReceber) > 0) {
            //
            return !valorFinanceiroReceber.toString().equals(valorFinanceiroReceberAdd.toString());
        } else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sair() {
        super.finish();
    }



    /*public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DialogFragment();
        newFragment.show(getSupportFragmentManager(), "dataPicker");
    }*/

}
