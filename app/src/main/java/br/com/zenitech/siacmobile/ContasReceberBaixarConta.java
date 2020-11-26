package br.com.zenitech.siacmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import br.com.zenitech.siacmobile.adapters.FinanceiroContasReceberAdapter;
import br.com.zenitech.siacmobile.domains.FinanceiroReceberClientes;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;

import static br.com.zenitech.siacmobile.ContasReceberCliente.IdsCR;

public class ContasReceberBaixarConta extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
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

    //LISTAR VENDAS
    private ArrayList<FinanceiroVendasDomain> listaFinanceiroCliente;
    private FinanceiroContasReceberAdapter adapter;
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

        //
        bd = new DatabaseHelper(this);

        //
        bgTotalReceber = findViewById(R.id.bgTotalReceber);

        //
        rvFinanceiro = findViewById(R.id.rvFinanceiro);
        rvFinanceiro.setLayoutManager(new LinearLayoutManager(this));

        //
        tilDocumento = findViewById(R.id.tilDocumento);
        tilVencimento = findViewById(R.id.tilVencimento);

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

        Log.i("ContasReceber - IDS ", String.valueOf(IdsCR.size()));


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

                //
                ValorABaixar = params.getString("valorVenda");

                String nomeCliente = params.getString("nome_cliente");
                getSupportActionBar().setSubtitle(cAux.maiuscula1(Objects.requireNonNull(nomeCliente).toLowerCase()));
            }
        }

        //
        listaFinanceiroCliente = bd.getFinanceiroClienteRecebidos(Integer.parseInt(codigo_cliente));
        adapter = new FinanceiroContasReceberAdapter(this, listaFinanceiroCliente);
        rvFinanceiro.setAdapter(adapter);

        //
        listaFormasPagamentoCliente = bd.getFormasPagamentoClienteBaixa(codigo_cliente);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaFormasPagamentoCliente);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormasPagamentoCliente = findViewById(R.id.spFormasPagamentoCliente);
        spFormasPagamentoCliente.setAdapter(adapter);

        spFormasPagamentoCliente.setOnItemSelectedListener(ContasReceberBaixarConta.this);
    }

    private void _finalizarBaixaContaReceber() {
        int totalItemFin = Integer.parseInt(cAux.soNumeros(txtTotalItemFinanceiroReceber.getText().toString()));
        int totalFin = Integer.parseInt(cAux.soNumeros(txtTotalFinanceiroReceber.getText().toString()));

        //Log.i("ContasReceber", "" + totalItemFin);
        //Log.i("ContasReceber", "" + totalFin);

        if (totalItemFin == 0) {
            //
            Toast.makeText(getBaseContext(), "Adicione pelo menos uma forma de pagamento ao financeiro.", Toast.LENGTH_LONG).show();
        } else if (totalItemFin > totalFin) {
            //
            Toast.makeText(getBaseContext(), "O valor ultrapassa o total.", Toast.LENGTH_LONG).show();
        } else {
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
                if (txtDocumentoFormaPagamento.getText().toString().equals("")) {
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

    private void addFinanceiro() {
        try {
            //
            //listaContasReceberCliente = bd.getListFormContasReceberCliente(codigo_cliente);
            Log.d("ContasReceber", " ValorABaixar: " + ValorABaixar);

            for (int i = 0; i < IdsCR.size(); i++) {
                Log.i("ContasReceber", IdsCR.get(i));
                boolean a = false;

                try {
                    FinanceiroVendasDomain financeiroBaixaDomains = bd.getBaixaRecebida(IdsCR.get(i));
                    Log.i("ContasReceber", financeiroBaixaDomains.getValor_financeiro());
                    a = false;
                } catch (Exception e) {
                    Log.i("ContasReceber ERRO ", Objects.requireNonNull(e.getMessage()));
                }


                //
                String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

                if (!a) {
                    // VERIFICA SE JÁ EXISTE UM REGISTRO PARA A FORMA DE PAGAMENTO ESCOLHIDA
                    String[] codigoverFormaPagamentoRecebidos = bd.verFormaPagamentoRecebidos(fPag[0], String.valueOf(codigo_cliente));
                    Log.i("ContasReceber", codigoverFormaPagamentoRecebidos[0]);
                    Log.i("ContasReceber", codigoverFormaPagamentoRecebidos[1]);

                    //
                    if (!codigoverFormaPagamentoRecebidos[0].equalsIgnoreCase("0")) {
                        //
                        //bd.updateFinanceiroRecebidos(codigoverFormaPagamentoRecebidos[1], String.valueOf(cAux.converterValores(txtValorFormaPagamento.getText().toString())));
                        String[] somaValUpd = {
                                codigoverFormaPagamentoRecebidos[1],
                                String.valueOf(cAux.converterValores(txtValorFormaPagamento.getText().toString()))
                        };
                        String valSoma = String.valueOf(cAux.somar(somaValUpd));
                        Log.i("ContasReceber", String.valueOf(bd.updateFinanceiroRecebidos(codigoverFormaPagamentoRecebidos[0], valSoma)));
                    }
                    //
                    else {

                        //
                        bd.addFinanceiroRecebidos(new FinanceiroVendasDomain(
                                "" + IdsCR.get(i),
                                "" + prefs.getString("unidade", "UNIDADE TESTE"),
                                "" + cAux.inserirDataAtual(),
                                "" + codigo_cliente,
                                "" + fPag[0],//spFormasPagamentoCliente.getSelectedItem().toString(),
                                "" + txtDocumentoFormaPagamento.getText().toString(),
                                "" + cAux.inserirData(cAux.formatarData(cAux.soNumeros(txtVencimentoFormaPagamentoReceber.getText().toString()))),
                                "" + cAux.converterValores(txtValorFormaPagamento.getText().toString()),
                                "0",
                                "0",
                                "0",
                                "0",
                                "" + cAux.inserirDataAtual(),
                                "",
                                "" + prefs.getInt("id_vendedor", 1),
                                "" + codigo_cliente
                        ));
                    }
                }

                a = false;
            }

            //
            listaFinanceiroCliente = bd.getFinanceiroClienteRecebidos(Integer.parseInt(codigo_cliente));
            adapter = new FinanceiroContasReceberAdapter(this, listaFinanceiroCliente);
            rvFinanceiro.setAdapter(adapter);
            //
            String tif = cAux.maskMoney(new BigDecimal(bd.getValorTotalFinanceiroReceber(String.valueOf(codigo_cliente))));
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
        }catch (Exception ignored){

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

                Log.i("ContasReceber", cAux.exibirDataAtual());
                Log.i("ContasReceber", txtVencimentoFormaPagamentoReceber.getText().toString());
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
