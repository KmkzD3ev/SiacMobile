package br.com.zenitech.siacmobile;

import static br.com.zenitech.siacmobile.ContasReceberCliente.IdsCR;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.material.textfield.TextInputLayout;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.FinContasReceberCliAdapter;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;
import br.com.zenitech.siacmobile.domains.FormasPagamentoReceberTemp;
import br.com.zenitech.siacmobile.repositories.FinanceiroReceberRepositorio;

public class ContasReceberBaixarContaCOPIAdeSEGURANCA3 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // CONTEXT
    private Context context;
    View view;
    // PREFERENCIAS
    SharedPreferences prefs;
    // CLASS AUXILIAR
    ClassAuxiliar cAux;
    // BANCO DE DADOS
    DatabaseHelper bd;
    // REPOSITORIO BANCO DE DADOS
    FinanceiroReceberRepositorio getFinRecRepo, setFinRecRepo;
    // PREENCHIDAS FORA DA CLASSE
    public LinearLayout bgTotalReceber;
    // LISTAS
    ArrayList<String> listaFormasPagamentoCliente;
    ArrayList<FormasPagamentoReceberTemp> listaFinanceiroCliente;
    // SPINNERS
    private Spinner spFormasPagamentoCliente;
    // EDITTEXTS
    public EditText txtVencimentoFormaPagamentoReceber, txtValorFormaPagamento;
    private EditText txtDocumentoFormaPagamento;
    // TEXTVIEWS
    public TextView txtTotalFinanceiroReceber;
    public TextView txtTotalItemFinanceiroReceber;
    TextView textCodDoc;
    // ADAPTERS
    FinContasReceberCliAdapter adapter;
    ArrayAdapter adapterForMPagCli;
    // RECYCLERVIEWS
    private RecyclerView rvFinanceiro;
    // INTEIROS
    int id = 1;
    // TEXTINPUTLAYOUTs
    TextInputLayout tilDocumento, tilVencimento;
    // BUTTONS
    Button btnAddFormaPagamento, btnPagamento;
    // STRINGS
    public static String totalFinanceiro;
    private String codigo_cliente = "";
    String ValorABaixar = "0";
    /* VARIÁVEIS DE COMPARAÇÃO DA FORMA DE PAGAMENTO
       TIPO, AUTO NUMERADA, BAIXA AUTOMATICA */
    String compFormaPagamento = "";
    String compTipoFormaPagmeto = "";
    String compAutoNumerada = "";       // NÃO PRECISA DIGITAR O NÚMERO DO DOCUMENTO -  STATUS: 1 PRA SIM 2 PRA NÃO
    String compBaixaAutomatica = "";    // O PAGAMENTO NÃO PRECISAR AUTORIZAÇAO -       STATUS: 1 PRA SIM 2 PRA NÃO


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas_receber_baixar_conta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        context = this;
        view = findViewById(android.R.id.content).getRootView();
        cAux = new ClassAuxiliar();
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);

        bd = new DatabaseHelper(this);

        // RECEBE O ID DO FINANCEIRO A RECEER
        id = prefs.getInt("id_financeiro_venda", 1);

        // GET AND SET * REPOSITORIO BANCO DE DADOS
        getFinRecRepo = new FinanceiroReceberRepositorio(bd.getReadableDatabase(), cAux);
        setFinRecRepo = new FinanceiroReceberRepositorio(bd.getWritableDatabase(), cAux);

        // APAGA O FINANCEIRO TEMPORARIO ANTERIOR
        try {
            setFinRecRepo.deleteFinanceiroReceberTemp();
        } catch (Exception e) {
            showLOG("ContasReceber", e.getMessage());
        }

        // INICIA AS REFENCIAS DOS CAMPOS
        bgTotalReceber = findViewById(R.id.bgTotalReceber);
        rvFinanceiro = findViewById(R.id.rvFinanceiro);
        tilDocumento = findViewById(R.id.tilDocumento);
        tilVencimento = findViewById(R.id.tilVencimento);
        textCodDoc = findViewById(R.id.textCodDoc);
        txtTotalFinanceiroReceber = findViewById(R.id.txtTotalFinanceiroReceber);
        txtValorFormaPagamento = findViewById(R.id.txtValorFormaPagamento);
        txtDocumentoFormaPagamento = findViewById(R.id.txtDocumentoFormaPagamento);
        txtTotalItemFinanceiroReceber = findViewById(R.id.txtTotalItemFinanceiroReceber);
        btnAddFormaPagamento = findViewById(R.id.btnAddF);
        btnPagamento = findViewById(R.id.btnPagamento);
        txtVencimentoFormaPagamentoReceber = findViewById(R.id.txtVencimentoFormaPagamento);
        spFormasPagamentoCliente = findViewById(R.id.spFormasPagamentoCliente);

        // RECEBE OS DADOS DO CONTAS A RECEBER
        Intent intent = getIntent();

        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {

                //
                codigo_cliente = params.getString("codigo_cliente");
                txtTotalFinanceiroReceber.setText(params.getString("valorVenda"));
                txtValorFormaPagamento.setText(params.getString("valorVenda"));
                textCodDoc.setText(params.getString("CodsDocs"));
                ValorABaixar = params.getString("valorVenda");

                //
                Objects.requireNonNull(getSupportActionBar()).setTitle("Baixa Financeiro");
                String nomeCliente = params.getString("nome_cliente");
                getSupportActionBar().setSubtitle(cAux.maiuscula1(Objects.requireNonNull(nomeCliente).toLowerCase()));
            }
        }

        init();
    }

    private void init() {

        //
        rvFinanceiro.setLayoutManager(new LinearLayoutManager(this));
        txtValorFormaPagamento.addTextChangedListener(new MoneyTextWatcher(txtValorFormaPagamento));
        txtVencimentoFormaPagamentoReceber.setText(cAux.exibirDataAtual());
        txtVencimentoFormaPagamentoReceber.addTextChangedListener(
                cAux.maskData("##/##/####", txtVencimentoFormaPagamentoReceber)
        );
        btnAddFormaPagamento.setOnClickListener(v -> _verificarValores());
        btnPagamento.setOnClickListener(v -> _finalizarBaixaContaReceber());

        //
        listaFormasPagamentoCliente = getFinRecRepo.getFormasPagamentoClienteBaixa(codigo_cliente);
        adapterForMPagCli = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaFormasPagamentoCliente);
        adapterForMPagCli.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormasPagamentoCliente.setAdapter(adapterForMPagCli);
        spFormasPagamentoCliente.setOnItemSelectedListener(ContasReceberBaixarContaCOPIAdeSEGURANCA3.this);
    }

    private void showMSG(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    private void showLOG(String tag, String msg) {
        Log.i(tag, msg);
    }

    private void _verificarValores() {
        int ValorFormaPagamento = Integer.parseInt(cAux.soNumeros(txtValorFormaPagamento.getText().toString()));

        // SE O USUÁRIO NÃO ADICIONAR NENHUM VALOR
        if (ValorFormaPagamento == 0) {
            cAux.ShowMsgToast(context, "Adicione uma valor para esta forma de pagamento.");
        } else {
            // String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

            // SE A FORMA DE PAGAMENTO FOR IGUAL A PRAZO VERIFICA O NÚMERO DO DOCUMENTO E O TIPO DE BAIXA
            if (compTipoFormaPagmeto.equals("A PRAZO")) {

                // SE O NÚMERO DO DOCUMENTO ESTIVER VÁSIO MOSTRA A MENSAGEM
                if (txtDocumentoFormaPagamento.getText().toString().equals("") && compAutoNumerada.equals("1")) {
                    //
                    cAux.ShowMsgToast(context, "Número do documento é obrigatório.");
                }
                // SE A BAIXA FOR MANUAL VERIFICA O CAMPO VENCIMENTO 1 PRA SIM 2 PRA NÃO
                else if (compBaixaAutomatica.equals("1")) {

                    // SE O CAMPO VENCIMENTO FOR IGUAL A 00/00/0000 PEDE QUE INFORME A DATA DO VENCIMENTO
                    if (txtVencimentoFormaPagamentoReceber.getText().toString().equals("")
                            || txtVencimentoFormaPagamentoReceber.getText().toString().equals("00/00/0000")
                    ) {
                        cAux.ShowMsgToast(context, "Data do vencimento é obrigatório.");
                    }
                    // ADICIONA VALOR AO FINANCEIRO
                    else {
                        addFinanceiro();
                    }
                }
                // ADICIONA VALOR AO FINANCEIRO
                else {
                    addFinanceiro();
                }
            }
            // ADICIONA VALOR AO FINANCEIRO
            else {
                addFinanceiro();
            }
        }
    }

    //
    private void addFinanceiro() {
        try {
            String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

            // VERIFICA SE JÁ EXISTE UM REGISTRO PARA A FORMA DE PAGAMENTO ESCOLHIDA
            String[] codigoverFormaPagamentoRecebidos = getFinRecRepo.verForPagRecTemp(fPag[0], codigo_cliente);
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
                int upFR = setFinRecRepo.updateFinRecTemp(codigoverFormaPagamentoRecebidos[0], valSoma);
                Log.i("ContasReceber", "upFR = " + upFR);
            } else {
                //
                setFinRecRepo.addValorFinReceber(
                        "" + codigo_cliente,
                        "" + fPag[0],
                        "" + cAux.converterValores(txtValorFormaPagamento.getText().toString())
                );
            }

            listarFinanceiro();
        } catch (Exception e) {
            cAux.ShowMsgLog("ContasReceber ERRO ", Objects.requireNonNull(e.getMessage()));
        }
    }

    // GARREGAR AS FORMAS DE PAGAMENTO INSERIDAS
    private void listarFinanceiro() {
        try {
            //
            listaFinanceiroCliente = getFinRecRepo.getFinanceiroClienteRecebidos(codigo_cliente);
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
            String tif = cAux.maskMoney(new BigDecimal(getFinRecRepo.SomaValTotFinReceber(String.valueOf(codigo_cliente))));
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

    // FINALIZAR A BAIXA DE CONTAS A RECEBER ESCOLHIDAS
    private void _finalizarBaixaContaReceber() {
        // VARIAVEIS DE COMPARACAO
        int totalItemFin = Integer.parseInt(cAux.soNumeros(txtTotalItemFinanceiroReceber.getText().toString()));
        int totalFin = Integer.parseInt(cAux.soNumeros(txtTotalFinanceiroReceber.getText().toString()));

        if (totalItemFin == 0) {
            showMSG("Adicione pelo menos uma forma de pagamento ao financeiro.");
        } else if (totalItemFin > totalFin) {
            showMSG("O valor ultrapassa o total.");
        } else {

            /*
             * REGRA DO FINANCEIRO A RECEBER *
             * PEGA O DOCUMENTO A RECEBER E COMPARA A PRIMEIRA FORMA DE PAGAMENTO
             * - SE O VALOR FOR IGUAL FINALIZAR A BAIXA DO DOCUMENTO
             * - SE O VALOR DO DOCUMENTO FOR MENOR OU MAIOR, SETA O VALOR REFERENTE AO DOCUMENTO E PASSA PARA O PROXIMO
             * -
             */

            try {

                // RECEBE OS VALORES INSERIDOS PARA O FINANCEIRO A RECEBER SELECIONADOS
                ArrayList<FormasPagamentoReceberTemp> _formasPgFinanceiro = getFinRecRepo.getFinanceiroClienteRecebidos(codigo_cliente);

                Log.e("ErrorCR", "QUATIDADE DE FORMAS DE PAGAMENTO INSERIDAS: " + _formasPgFinanceiro.size());

                // LOOP DOS VALORES COM FORMAS DE PAGAMENTOS DIFERENTES
                for (int i = 0; i < _formasPgFinanceiro.size(); i++) {

                    // * RECEBE O VALOR DE CADA FORMA DE PAGAMENTO
                    String valorFormPg = _formasPgFinanceiro.get(i).getValor();
                    Log.e("ErrorCR", "VALOR DO FINANCEIRO: " + valorFormPg);

                    // * CONVERTE PARA INTEIRO PARA COMPARAÇÃO
                    int valInt = Integer.parseInt(cAux.soNumeros(valorFormPg));
                    Log.e("ErrorCR", "VALOR DO FINANCEIRO (VAL INTEIRO PARA COMPARAR): " + valInt);

                    // * SE O VALOR DO FINANCEIRO CONVERTIDO EM INT FOR MAIOR QUE 0, AVANCA
                    if (valInt > 0) {

                        // FAZ UM LOOP COM OS IDS DAS BAIXAS SELECIONADAS
                        for (int a = 0; a < IdsCR.size(); a++) {

                            //
                            String idContaReceber = IdsCR.get(a);
                            Log.e("ErrorCR", "IDS DAS CONTAS SELECIONADAS: " + idContaReceber);

                            // PEGA O VALOR TOTAL DO FINANCEIRO REFERENTE AO ID INFORMADO
                            String TotValFinRecCli = getFinRecRepo.getValorFinReceberCli(idContaReceber);

                            /*
                             * ENQUANTO O VALOR DA FORMA DE PAGAMENTO FOR DIFERENTE DO VALOR DO FINANCEIRO (TotValFinRecCli)
                             * TEM QUE CONTINUAR CONSULTANDO AS FORMAS DE PAGAMENTO INSERIDAS.
                             */

                            // PEGA O TOTAL RECEBIDO DO  FINANCEIRO REFERENTE AO ID INFORMADO
                            String TtotRecebido = getFinRecRepo.getTotalRecebido(idContaReceber);

                            // SUBTRAI O VALOR DO FINANCEIRO COM O VALOR RECEBIDO
                            String[] v = {String.valueOf(TotValFinRecCli), String.valueOf(TtotRecebido)};
                            // VALOR RESTANTE É IGUAL AO VALOR DO FINANCEIRO MENOS O TOTAL JÁ ADICIONADO A TABELA RECEBIDOS
                            int valorRestante = Integer.parseInt(cAux.soNumeros(String.valueOf(cAux.subitrair(v))));
                            String TvalorRestante = String.valueOf(cAux.subitrair(v));
                            //
                            showLOG("ContasReceber", "valTemp = " + valInt + " | ID " + idContaReceber);
                            showLOG("ContasReceber", "valorRestante = " + valorRestante);

                            // SE O VALOR RESTANTE FOR MAIOR QUE ZERO
                            if (valorRestante > 0) {

                                //
                                int valSalvar;
                                String TvalorSalvar;

                                // SE O VALOR TEMPORARIO DA FORMA DE PAGAMENTO FOR MAIOR OU IGUAL AO VALOR RESTANTE
                                if (valInt >= valorRestante) {
                                    valSalvar = valorRestante;
                                    //
                                    String[] sub = {String.valueOf(valInt), String.valueOf(valorRestante)};
                                    valInt = Integer.parseInt(String.valueOf(cAux.subitrair(sub)));
                                    TvalorSalvar = TvalorRestante;
                                } else {
                                    valSalvar = valInt;
                                    TvalorSalvar = valorFormPg;
                                    //
                                    valInt = 0;
                                }

                                // SE O VALOR PARA SALVAR FOR MAIOR QUE ZERO ELE SALVA
                                if (valSalvar > 0) {

                                    String unidade = prefs.getString("unidade", "UNIDADE TESTE");
                                    String dataAtual = cAux.inserirDataAtual();
                                    String formaPagamento = _formasPgFinanceiro.get(i).getId_forma_pagamento();
                                    String numDocumento = txtDocumentoFormaPagamento.getText().toString();
                                    String venDocumento = cAux.inserirData(cAux.formatarData(cAux.soNumeros(txtVencimentoFormaPagamentoReceber.getText().toString())));
                                    String idVendedor = String.valueOf(prefs.getInt("id_vendedor", 1));

                                    //
                                    FinanceiroVendasDomain finParaAdd = new FinanceiroVendasDomain(
                                            "" + idContaReceber,
                                            "" + unidade,
                                            "" + dataAtual,
                                            "" + codigo_cliente,
                                            "" + formaPagamento,
                                            "" + numDocumento,
                                            "" + venDocumento,
                                            "" + TotValFinRecCli,//cAux.converterValores(txtValorFormaPagamento.getText().toString()),
                                            "0",
                                            "" + TvalorSalvar,
                                            "0",
                                            "0",
                                            "" + dataAtual,
                                            "",
                                            "" + idVendedor,
                                            "" + codigo_cliente,
                                            "",
                                            ""
                                    );
                                    showLOG("ErrorCR", "*************************************************************");
                                    showLOG("ErrorCR", "addFinanceiroRecebidos: " + finParaAdd);
                                    showLOG("ErrorCR", "*************************************************************");

                                    //
                                    setFinRecRepo.addFinanceiroRecebidos(finParaAdd);
                                }
                            }
                        }
                    }

                    //
                    finalizarFechar();
                }
            } catch (Exception e) {
                showLOG("ContasReceber", "erro = " + e.getMessage());
                Toast.makeText(getBaseContext(), "erro = " + e.getMessage(), Toast.LENGTH_LONG).show();
            }


        }
    }

    void finalizarFechar() {
        Toast.makeText(getBaseContext(), "Operação Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(), Principal2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        sair();
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        compFormaPagamento = spFormasPagamentoCliente.getSelectedItem().toString();
        compTipoFormaPagmeto = getFinRecRepo.GetTipoFormaPagamento(compFormaPagamento);
        compAutoNumerada = getFinRecRepo.GetFormaPagamentoAutoNumerada(compFormaPagamento);
        compBaixaAutomatica = getFinRecRepo.GetFormaPagamentoBaixaAutomatica(compFormaPagamento);

        Log.e("FORMPG", "FRMPG: " + compFormaPagamento +
                " - Tipo: " + compTipoFormaPagmeto +
                " - AutoNum: " + compAutoNumerada +
                " - BaixAut: " + compBaixaAutomatica);
        //String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

        //if (fPag[1].equals("A PRAZO")) {
        if (compTipoFormaPagmeto.equals("A PRAZO")) {

            runOnUiThread(() -> tilVencimento.setVisibility(View.VISIBLE));

            // BAIXA AUTOMATICA HABILITA O CAMPO
            if (compBaixaAutomatica.equals("1")) {

                runOnUiThread(() -> tilDocumento.setVisibility(View.VISIBLE));
            }
        } else {
            runOnUiThread(() -> {
                tilDocumento.setVisibility(View.GONE);
                tilVencimento.setVisibility(View.GONE);
                txtVencimentoFormaPagamentoReceber.setText(cAux.exibirDataAtual());
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
}
