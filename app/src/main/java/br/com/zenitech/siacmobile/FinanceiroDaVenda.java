package br.com.zenitech.siacmobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
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

import br.com.zenitech.siacmobile.adapters.FinanceiroVendasAdapter;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;

public class FinanceiroDaVenda extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;

    public static String totalFinanceiro;
    public static TextView txtTotalFinanceiro;
    public static TextView txtTotalItemFinanceiro;
    private ArrayList<String> listaFormasPagamentoCliente;
    private DatabaseHelper bd;
    private Spinner spFormasPagamentoCliente;
    private String codigo_cliente = "";
    private EditText txtDocumentoFormaPagamento;
    public static EditText txtVencimentoFormaPagamento, txtValorFormaPagamento;
    public static LinearLayout bgTotal;

    //LISTAR VENDAS
    private ArrayList<FinanceiroVendasDomain> listaFinanceiroCliente;
    private FinanceiroVendasAdapter adapter;
    private RecyclerView rvFinanceiro;

    private Button btnAddF, btnPagamento;

    int id = 1;
    int id_venda_app = 1;
    private String total_venda = "0.0";
    private ClassAuxiliar classAuxiliar;

    TextInputLayout tilDocumento, tilVencimento;
    private AlertDialog alerta;

    //DADOS PARA PASSAR AO EMISSOR WEB
    private String produto_emissor;
    private String quantidade_emissor;
    private String valor_unit_emissor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financeiro_da_venda);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //
        classAuxiliar = new ClassAuxiliar();

        //
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        ed = prefs.edit();

        //
        id = prefs.getInt("id_financeiro_venda", 1);

        //
        bd = new DatabaseHelper(this);

        //
        bgTotal = findViewById(R.id.bgTotal);

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
        txtTotalFinanceiro = findViewById(R.id.txtTotalFinanceiro);
        //
        txtValorFormaPagamento = findViewById(R.id.txtValorFormaPagamento);
        txtValorFormaPagamento.addTextChangedListener(new FinanceiroDaVenda.MoneyTextWatcher(txtValorFormaPagamento));

        txtDocumentoFormaPagamento = findViewById(R.id.txtDocumentoFormaPagamento);

        //
        txtVencimentoFormaPagamento = findViewById(R.id.txtVencimentoFormaPagamento);
        txtVencimentoFormaPagamento.setText(classAuxiliar.exibirDataAtual());
        txtVencimentoFormaPagamento.setOnFocusChangeListener((v, hasFocus) -> showDatePickerDialog(v));

        txtTotalItemFinanceiro = findViewById(R.id.txtTotalItemFinanceiro);

        //
        btnAddF = findViewById(R.id.btnAddF);
        btnAddF.setOnClickListener(v -> {
            //SE O USUÁRIO NÃO ADICIONAR NENHUM VALOR
            if (txtValorFormaPagamento.getText().toString().equals("") || txtValorFormaPagamento.getText().toString().equals("R$0,00")) {
                //
                Toast.makeText(FinanceiroDaVenda.this, "Adicione uma valor para esta forma de pagamento.", Toast.LENGTH_LONG).show();
            } else {

                //
                String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

                //SE A FORMA DE PAGAMENTO FOR IGUAL A PRAZO VERIFICA O NÚMERO DO DOCUMENTO E O TIPO DE BAIXA
                if (fPag[1].equals("A PRAZO")) {

                    //SE O NÚMERO DO DOCUMENTO ESTIVER VÁSIO MOSTRA A MENSAGEM
                    if (txtDocumentoFormaPagamento.getText().toString().equals("")) {
                        //
                        Toast.makeText(FinanceiroDaVenda.this, "Número do documento é obrigatório.", Toast.LENGTH_LONG).show();
                    }
                    //SE A BAIXA FOR MANUAL VERIFICA O CAMPO VENCIMENTO
                    else if (fPag[3].equals("1")) {

                        //SE O CAMPO VENCIMENTO FOR IGUAL A 00/00/0000 PEDE QUE INFORME A DATA DO VENCIMENTO
                        if (txtVencimentoFormaPagamento.getText().toString().equals("") || txtVencimentoFormaPagamento.getText().toString().equals("00/00/0000")) {

                            //
                            Toast.makeText(FinanceiroDaVenda.this, "Data do vencimento é obrigatório.", Toast.LENGTH_LONG).show();
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
        });
        //
        btnPagamento = findViewById(R.id.btnPagamento);
        btnPagamento.setOnClickListener(v -> {
            if (txtTotalItemFinanceiro.getText().equals("0,00")) {
                //
                Toast.makeText(FinanceiroDaVenda.this, "Adicione pelo menos uma forma de pagamento ao financeiro.", Toast.LENGTH_LONG).show();
            } else if (!txtTotalItemFinanceiro.getText().equals(txtTotalFinanceiro.getText())) {
                //
                Toast.makeText(FinanceiroDaVenda.this, "O valor do financeiro está diferente da venda.", Toast.LENGTH_LONG).show();
            } else {

                bd.updateFinalizarVenda(String.valueOf(prefs.getInt("id_venda_app", 1)));

                Toast.makeText(FinanceiroDaVenda.this, "Venda Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FinanceiroDaVenda.this, Principal2.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                super.finish();
                //mostrarMsg();
                //
                /*Toast.makeText(FinanceiroDaVenda.this, "Venda Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FinanceiroDaVenda.this, Principal.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                sair();*/

                /*//
                try {

                    bd.updateFinalizarVenda(prefs.getString("id_venda_app", ""));

                    //
                    Toast.makeText(FinanceiroDaVenda.this, "Venda Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(FinanceiroDaVenda.this, Principal.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    sair();

                } catch (Exception e) {
                    //
                    Toast.makeText(FinanceiroDaVenda.this, "Não foi possível finalizar a venda, verifique todos os dados.", Toast.LENGTH_LONG).show();
                }*/
            }
        });


        //
        Intent intent = getIntent();

        if (intent != null) {
            Bundle params = intent.getExtras();

            if (params != null) {

                //
                getSupportActionBar().setTitle("Financeiro");
                //getSupportActionBar().setSubtitle("R$ " + params.getString("valorVenda"));// + "  " + prefs.getInt("id_venda_app", 1)

                //
                //nome_cliente
                codigo_cliente = params.getString("codigo_cliente");
                //txtNomeClienteFinanceiro.setText(params.getString("nome_cliente"));
                txtTotalFinanceiro.setText(params.getString("valorVenda"));

                txtValorFormaPagamento.setText(params.getString("valorVenda"));

                //DADOS EMISSOR WEB
                produto_emissor = params.getString("produto");
                quantidade_emissor = params.getString("quantidade");
                valor_unit_emissor = params.getString("valor_unit");

                //
                String nomeCliente = params.getString("nome_cliente");
                getSupportActionBar().setSubtitle(classAuxiliar.maiuscula1(nomeCliente.toLowerCase()));
            }
        }

        //
        listaFormasPagamentoCliente = bd.getFormasPagamentoCliente(codigo_cliente);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaFormasPagamentoCliente);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormasPagamentoCliente = (Spinner) findViewById(R.id.spFormasPagamentoCliente);
        spFormasPagamentoCliente.setAdapter(adapter);

        spFormasPagamentoCliente.setOnItemSelectedListener(FinanceiroDaVenda.this);

        atualizarValFin();
    }

    private void atualizarValFin() {
        try {
            //
            listaFinanceiroCliente = bd.getFinanceiroCliente(prefs.getInt("id_venda_app", 1));
            adapter = new FinanceiroVendasAdapter(this, listaFinanceiroCliente);
            rvFinanceiro.setAdapter(adapter);

            //
            String tif = classAuxiliar.maskMoney(new BigDecimal(bd.getValorTotalFinanceiro(String.valueOf(prefs.getInt("id_venda_app", 1)))));
            txtTotalItemFinanceiro.setText(tif);

            //
            //!txtTotalItemFinanceiro.getText().equals(txtTotalFinanceiro.getText()

            //
            String valorFinanceiro = String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiro.getText().toString()));
            String valorFinanceiroAdd = String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiro.getText().toString()));

            //SUBTRAIR O VALOR PELA QUANTIDADE
            String[] subtracao = {valorFinanceiro, valorFinanceiroAdd};
            String total = String.valueOf(classAuxiliar.subitrair(subtracao));

            txtValorFormaPagamento.setText(total);
        } catch (Exception e) {
            Log.i("Financeiro", e.getMessage());
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DialogFragment();
        newFragment.show(getSupportFragmentManager(), "dataPicker");
    }

    public void mostrarMsg() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo_emissor_web);
        //define o titulo
        builder.setTitle("Emissor Web");
        //define a mensagem
        String msg = "Deseja emitir a NFC-e?";
        builder.setMessage(msg);
        //define um botão como positivo
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(InformacoesVagas.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();

                /*/
                Toast.makeText(FinanceiroDaVenda.this, "Venda Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FinanceiroDaVenda.this, Principal.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                */

                //sair(); //
                //listaVendas = bd.getVendasCliente(prefs.getInt("id_venda_app", 1));
                //ArrayList<Produtos> listaProdutos;
                //listaProdutos = bd.getAllProdutos();
                //i.putExtra("produtos", listaProdutos);

                PackageManager packageManager = getPackageManager();
                String packageName = "br.com.zenitech.emissorweb";
                Intent i = packageManager.getLaunchIntentForPackage(packageName);
                //DADOS EMISSOR WEB
                i.putExtra("siac", "1");
                i.putExtra("produto", produto_emissor);
                i.putExtra("quantidade", quantidade_emissor);
                i.putExtra("valor_unit", valor_unit_emissor);
                i.putExtra("forma_pagamento", "DINHEIRO");

                startActivity(i);
                finish();
            }
        });

        //define um botão como negativo.
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                sair();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();
    }

    private void addFinanceiro() {

        //
        id = id + 1;
        ed.putInt("id_financeiro_venda", id).apply();

        //
        String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

        String sql = "";
        sql += String.valueOf(id) + "\n";//CODIGO_FINANCEIRO
        sql += prefs.getString("unidade", "UNIDADE TESTE") + "\n";//UNIDADE_FINANCEIRO
        sql += classAuxiliar.inserirDataAtual() + "\n";//DATA_FINANCEIRO
        sql += codigo_cliente + "\n";//CODIGO_CLIENTE_FINANCEIRO
        sql += fPag[0] + "\n";//sql += spFormasPagamentoCliente.getSelectedItem().toString() + "\n";//FPAGAMENTO_FINANCEIRO
        sql += txtDocumentoFormaPagamento.getText().toString() + "\n";//DOCUMENTO_FINANCEIRO
        sql += String.valueOf(classAuxiliar.inserirData(txtVencimentoFormaPagamento.getText().toString())) + "\n";//VENCIMENTO_FINANCEIRO
        sql += String.valueOf(classAuxiliar.converterValores(txtValorFormaPagamento.getText().toString())) + "\n";//VALOR_FINANCEIRO
        sql += "0" + "\n";//STATUS_AUTORIZACAO
        sql += "0" + "\n";//PAGO
        sql += "0" + "\n";//VASILHAME_REF
        sql += "0" + "\n";//USUARIO_ATUAL_FINANCEIRO
        sql += classAuxiliar.inserirDataAtual() + "\n";//DATA_INCLUSAO
        sql += "" + "\n";//NOSSO_NUMERO_FINANCEIRO
        sql += "" + prefs.getInt("id_vendedor", 1) + "\n";//ID_VENDEDOR_FINANCEIRO
        sql += "" + prefs.getInt("id_venda_app", 1) + "\n";

        //SETAR O SQL NO LOG PARA CONSULTA
        Log.e("SQL", sql);

        //INSERIR FINANCEIRO
        bd.addFinanceiro(new FinanceiroVendasDomain(
                String.valueOf(id),//CODIGO_FINANCEIRO
                prefs.getString("unidade", "UNIDADE TESTE"),//UNIDADE_FINANCEIRO
                classAuxiliar.inserirDataAtual(),//DATA_FINANCEIRO
                codigo_cliente,//CODIGO_CLIENTE_FINANCEIRO
                fPag[0],//spFormasPagamentoCliente.getSelectedItem().toString(),//FPAGAMENTO_FINANCEIRO
                txtDocumentoFormaPagamento.getText().toString(),//DOCUMENTO_FINANCEIRO
                String.valueOf(classAuxiliar.inserirData(txtVencimentoFormaPagamento.getText().toString())),//VENCIMENTO_FINANCEIRO
                String.valueOf(classAuxiliar.converterValores(txtValorFormaPagamento.getText().toString())),//VALOR_FINANCEIRO
                "0",//STATUS_AUTORIZACAO
                "0",//PAGO
                "0",//VASILHAME_REF
                "0",//USUARIO_ATUAL_FINANCEIRO
                "" + classAuxiliar.inserirDataAtual(),//DATA_INCLUSAO
                "",//NOSSO_NUMERO_FINANCEIRO
                "" + prefs.getInt("id_vendedor", 1),//ID_VENDEDOR_FINANCEIRO
                "" + prefs.getInt("id_venda_app", 1)
        ));

        //
        listaFinanceiroCliente = bd.getFinanceiroCliente(prefs.getInt("id_venda_app", 1));
        adapter = new FinanceiroVendasAdapter(this, listaFinanceiroCliente);
        rvFinanceiro.setAdapter(adapter);

        //
        String tif = classAuxiliar.maskMoney(new BigDecimal(bd.getValorTotalFinanceiro(String.valueOf(prefs.getInt("id_venda_app", 1)))));
        txtTotalItemFinanceiro.setText(tif);

        //
        //!txtTotalItemFinanceiro.getText().equals(txtTotalFinanceiro.getText()

        //
        String valorFinanceiro = String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiro.getText().toString()));
        String valorFinanceiroAdd = String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiro.getText().toString()));

        //SUBTRAIR O VALOR PELA QUANTIDADE
        String[] subtracao = {valorFinanceiro, valorFinanceiroAdd};
        String total = String.valueOf(classAuxiliar.subitrair(subtracao));

        txtValorFormaPagamento.setText(total);

        //
        if (comparar()) {

            bgTotal.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.erro));
            txtValorFormaPagamento.setText("0,00");
        } else {
            bgTotal.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.transparente));
        }

        //
        txtDocumentoFormaPagamento.setText("");
        tilDocumento.setVisibility(View.GONE);
        spFormasPagamentoCliente.setSelection(0);

        //ESCONDER O TECLADO
        // TODO Auto-generated method stub
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");
        if (fPag[1].equals("A PRAZO")) {

            runOnUiThread(() -> {
                tilDocumento.setVisibility(View.VISIBLE);
                tilVencimento.setVisibility(View.VISIBLE);
            });

            if (fPag[3].equals("1")) {

                runOnUiThread(() -> tilVencimento.setVisibility(View.VISIBLE));
            }
        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tilDocumento.setVisibility(View.GONE);
                    tilVencimento.setVisibility(View.GONE);
                    txtVencimentoFormaPagamento.setText(classAuxiliar.exibirDataAtual());
                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
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
            String cleanString = s.toString().replaceAll("[^0-9]", "");
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
        BigDecimal valorFinanceiro = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiro.getText().toString())));
        BigDecimal valorFinanceiroAdd = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiro.getText().toString())));

        if (valorFinanceiroAdd.compareTo(valorFinanceiro) == 1) {
            //
            if (valorFinanceiro.toString().equals(valorFinanceiroAdd.toString())) {

                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                sair();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        sair();
    }

    private void sair() {
        /*//
        Toast.makeText(FinanceiroDaVenda.this, "Venda Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(FinanceiroDaVenda.this, Principal2.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        super.finish();*/
        cancelarVenda();
    }

    private void cancelarVenda() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        builder.setMessage("Você Deseja Realmente Cancelar Esta Venda?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", (arg0, arg1) -> {
            //Toast.makeText(InformacoesVagas.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
            int i = bd.deleteVenda(prefs.getInt("id_venda_app", 0));
            if (i != 0) {
                //
                Toast.makeText(FinanceiroDaVenda.this, "Venda Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FinanceiroDaVenda.this, Principal2.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                super.finish();
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

}
