package br.com.zenitech.siacmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
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

import br.com.zenitech.siacmobile.adapters.FinanceiroContasReceberAdapter;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;

public class ContasReceberBaixarConta extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;

    public static String totalFinanceiro;
    public static TextView txtTotalFinanceiroReceber;
    public static TextView txtTotalItemFinanceiroReceber;
    private ArrayList<String> listaFormasPagamentoCliente;
    private DatabaseHelper bd;
    private Spinner spFormasPagamentoCliente;
    private String codigo_cliente = "";
    private EditText txtDocumentoFormaPagamento;
    public static EditText txtVencimentoFormaPagamentoReceber, txtValorFormaPagamento;
    public static LinearLayout bgTotalReceber;

    //LISTAR VENDAS
    private ArrayList<FinanceiroVendasDomain> listaFinanceiroCliente;
    private FinanceiroContasReceberAdapter adapter;
    private RecyclerView rvFinanceiro;

    private Button btnAddF, btnPagamento;

    int id = 1;
    private ClassAuxiliar classAuxiliar;

    TextInputLayout tilDocumento, tilVencimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas_receber_baixar_conta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        classAuxiliar = new ClassAuxiliar();

        //
        prefs = getSharedPreferences("preferencias", this.MODE_PRIVATE);
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
        txtValorFormaPagamento = (EditText) findViewById(R.id.txtValorFormaPagamento);
        txtValorFormaPagamento.addTextChangedListener(new ContasReceberBaixarConta.MoneyTextWatcher(txtValorFormaPagamento));

        txtDocumentoFormaPagamento = (EditText) findViewById(R.id.txtDocumentoFormaPagamento);

        //
        txtVencimentoFormaPagamentoReceber = (EditText) findViewById(R.id.txtVencimentoFormaPagamento);
        txtVencimentoFormaPagamentoReceber.setText(classAuxiliar.exibirDataAtual());
        txtVencimentoFormaPagamentoReceber.addTextChangedListener(classAuxiliar.maskData("##/##/####", txtVencimentoFormaPagamentoReceber));
        /*txtVencimentoFormaPagamentoReceber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showDatePickerDialog(v);
            }
        });*/

        txtTotalItemFinanceiroReceber = findViewById(R.id.txtTotalItemFinanceiroReceber);

        //
        btnAddF = findViewById(R.id.btnAddF);
        btnAddF.setOnClickListener(v -> {

            //SE O USUÁRIO NÃO ADICIONAR NENHUM VALOR
            if (txtValorFormaPagamento.getText().toString().equals("") || txtValorFormaPagamento.getText().toString().equals("R$0,00")) {
                //
                Toast.makeText(getBaseContext(), "Adicione uma valor para esta forma de pagamento.", Toast.LENGTH_LONG).show();
            } else {

                //
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
        });
        //
        btnPagamento = findViewById(R.id.btnPagamento);
        btnPagamento.setOnClickListener(v -> {

            if (txtTotalItemFinanceiroReceber.getText().equals("0,00")) {
                //
                Toast.makeText(getBaseContext(), "Adicione pelo menos uma forma de pagamento ao financeiro.", Toast.LENGTH_LONG).show();
            } else if (!txtTotalItemFinanceiroReceber.getText().equals(txtTotalFinanceiroReceber.getText())) {
                //
                Toast.makeText(getBaseContext(), "O valor do financeiro está diferente da conta a receber.", Toast.LENGTH_LONG).show();
            } else {
                //
                Toast.makeText(getBaseContext(), "Operação Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), Principal2.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                sair();
            }
        });


        //
        Intent intent = getIntent();

        if (intent != null) {
            Bundle params = intent.getExtras();

            if (params != null) {

                //
                getSupportActionBar().setTitle("Baixa Financeiro");

                //
                codigo_cliente = params.getString("codigo_cliente");
                txtTotalFinanceiroReceber.setText(params.getString("valorVenda"));
                txtValorFormaPagamento.setText(params.getString("valorVenda"));

                String nomeCliente = params.getString("nome_cliente");
                getSupportActionBar().setSubtitle(classAuxiliar.maiuscula1(nomeCliente.toLowerCase()));
            }
        }

        //
        listaFinanceiroCliente = bd.getFinanceiroClienteRecebidos(prefs.getInt("id_baixa_app", 1));
        adapter = new FinanceiroContasReceberAdapter(this, listaFinanceiroCliente);
        rvFinanceiro.setAdapter(adapter);

        //
        listaFormasPagamentoCliente = bd.getFormasPagamentoCliente(codigo_cliente);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaFormasPagamentoCliente);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormasPagamentoCliente = (Spinner) findViewById(R.id.spFormasPagamentoCliente);
        spFormasPagamentoCliente.setAdapter(adapter);

        spFormasPagamentoCliente.setOnItemSelectedListener(ContasReceberBaixarConta.this);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DialogFragment();
        newFragment.show(getSupportFragmentManager(), "dataPicker");
    }

    private void addFinanceiro() {

        //
        id = id + 1;
        ed.putInt("id_financeiro_venda", id).apply();

        //
        String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

        //INSERIR FINANCEIRO
        bd.addFinanceiroRecebidos(new FinanceiroVendasDomain(
                String.valueOf(id),//CODIGO_FINANCEIRO
                prefs.getString("unidade", "UNIDADE TESTE"),//UNIDADE_FINANCEIRO
                classAuxiliar.inserirDataAtual(),//DATA_FINANCEIRO
                codigo_cliente,//CODIGO_CLIENTE_FINANCEIRO
                fPag[0],//spFormasPagamentoCliente.getSelectedItem().toString(),//FPAGAMENTO_FINANCEIRO
                txtDocumentoFormaPagamento.getText().toString(),//DOCUMENTO_FINANCEIRO
                String.valueOf(classAuxiliar.inserirData(txtVencimentoFormaPagamentoReceber.getText().toString())),//VENCIMENTO_FINANCEIRO
                String.valueOf(classAuxiliar.converterValores(txtValorFormaPagamento.getText().toString())),//VALOR_FINANCEIRO
                "0",//STATUS_AUTORIZACAO
                "0",//PAGO
                "0",//VASILHAME_REF
                "0",//USUARIO_ATUAL_FINANCEIRO
                classAuxiliar.inserirDataAtual(),//DATA_INCLUSAO
                "",//NOSSO_NUMERO_FINANCEIRO
                "" + prefs.getInt("id_vendedor", 1),//ID_VENDEDOR_FINANCEIRO
                "" + prefs.getInt("id_baixa_app", 1)
        ));

        //
        listaFinanceiroCliente = bd.getFinanceiroClienteRecebidos(prefs.getInt("id_baixa_app", 1));
        adapter = new FinanceiroContasReceberAdapter(this, listaFinanceiroCliente);
        rvFinanceiro.setAdapter(adapter);

        //
        String tif = classAuxiliar.maskMoney(new BigDecimal(bd.getValorTotalFinanceiroReceber(String.valueOf(prefs.getInt("id_baixa_app", 1)))));
        txtTotalItemFinanceiroReceber.setText(tif);

        //
        String valorFinanceiroReceber = String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiroReceber.getText().toString()));
        String valorFinanceiroReceberAdd = String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiroReceber.getText().toString()));

        //SUBTRAIR O VALOR PELA QUANTIDADE
        String[] subtracao = {valorFinanceiroReceber, valorFinanceiroReceberAdd};
        String total = String.valueOf(classAuxiliar.subitrair(subtracao));

        txtValorFormaPagamento.setText(total);

        //
        if (comparar()) {

            bgTotalReceber.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.erro));
            txtValorFormaPagamento.setText("0,00");
        } else {
            bgTotalReceber.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.transparente));
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

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //tilDocumento.setVisibility(View.VISIBLE);
                    tilVencimento.setVisibility(View.VISIBLE);
                }
            });

            if (fPag[3].equals("1")) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tilDocumento.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tilDocumento.setVisibility(View.GONE);
                    tilVencimento.setVisibility(View.GONE);
                    txtVencimentoFormaPagamentoReceber.setText(classAuxiliar.exibirDataAtual());
                }
            });
        }

        //Toast.makeText(this, fPag[1], Toast.LENGTH_LONG).show();
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
        BigDecimal valorFinanceiroReceber = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiroReceber.getText().toString())));
        BigDecimal valorFinanceiroReceberAdd = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiroReceber.getText().toString())));

        if (valorFinanceiroReceberAdd.compareTo(valorFinanceiroReceber) == 1) {
            //
            if (valorFinanceiroReceber.toString().equals(valorFinanceiroReceberAdd.toString())) {

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
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sair() {
        super.finish();
    }

}
