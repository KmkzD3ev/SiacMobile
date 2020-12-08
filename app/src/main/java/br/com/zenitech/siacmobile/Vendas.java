package br.com.zenitech.siacmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.VendasAdapter;
import br.com.zenitech.siacmobile.domains.VendasDomain;

public class Vendas extends AppCompatActivity {
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;

    AlertDialog alerta;

    ArrayList<String> listaProdutos;
    //LISTAR VENDAS
    ArrayList<VendasDomain> listaVendas;
    VendasAdapter adapter;
    RecyclerView rvVendas;

    Spinner spProduto;
    EditText etQuantidade, etPreco;
    public static TextView textTotalItens, txtTotalVenda;
    Button btnAddProdutoLista;
    private DatabaseHelper bd;

    int totalVenda = 0;
    String id_cliente = "";
    String nome_cliente = "";
    String latitude_cliente = "";
    String longitude_cliente = "";
    int id = 1;
    int id_venda_app = 1;
    private String total_venda = "0.0";
    String saldo = "";
    private ClassAuxiliar classAuxiliar;

    //DADOS PARA PASSAR AO EMISSOR WEB
    private String produto_emissor;
    private String quantidade_emissor;
    private String valor_unit_emissor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //
        classAuxiliar = new ClassAuxiliar();

        //
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        ed = prefs.edit();


        //
        bd = new DatabaseHelper(this);


        //
        rvVendas = findViewById(R.id.rvVendas);
        rvVendas.setLayoutManager(new LinearLayoutManager(Vendas.this));

        //
        listaProdutos = bd.getProdutos();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaProdutos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProduto = findViewById(R.id.spProdutos);
        spProduto.setAdapter(adapter);
        //spProduto.requestFocus();


        //
        etQuantidade = findViewById(R.id.etQuantidade);
        etQuantidade.setText("");
        //etQuantidade.requestFocus();

        //
        etPreco = findViewById(R.id.etPreco);
        etPreco.addTextChangedListener(new MoneyTextWatcher(etPreco));

        //
        txtTotalVenda = findViewById(R.id.textTotalVenda);
        txtTotalVenda.setText(R.string.zeros);

        textTotalItens = findViewById(R.id.textTotalItens);
        textTotalItens.setText(R.string.zero);

        etPreco.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {

                if (etQuantidade.getText().toString().equals("") || etQuantidade.getText().toString().equals("0") || etPreco.getText().toString().equals("") || etPreco.getText().toString().equals("R$ 0,00")) {
                    Toast.makeText(Vendas.this, "Quantidade e Preço não podem ser vazios.", Toast.LENGTH_LONG).show();
                } else {
                    addVenda();
                }

                handled = true;
            }
            return handled;
        });

        //
        btnAddProdutoLista = findViewById(R.id.btnAddProdutoLista);
        btnAddProdutoLista.setOnClickListener(view -> {
            Log.i("Vendas ", etPreco.getText().toString());
            if (etQuantidade.getText().toString().equals("") || etQuantidade.getText().toString().equals("0") || etPreco.getText().toString().equals("") || etPreco.getText().toString().equals("R$ 0,00")) {
                Toast.makeText(Vendas.this, "Quantidade e Preço não podem ser vazios.", Toast.LENGTH_LONG).show();
            } else {
                addVenda();
            }
        });


        //
        Intent intent = getIntent();

        if (intent != null) {
            Bundle params = intent.getExtras();

            if (params != null) {

                //SE A VENDA FOR NOVA
                if (params.getString("id_venda").equals("")) {
                    //
                    id = prefs.getInt("id_venda", 1);

                    id_venda_app = (prefs.getInt("id_venda_app", 1) + 1);
                    ed.putInt("id_venda_app", id_venda_app).apply();

                    id_cliente = params.getString("codigo");
                    nome_cliente = params.getString("nome");
                    latitude_cliente = params.getString("latitude_cliente");
                    longitude_cliente = params.getString("longitude_cliente");
                    saldo = params.getString("saldo");
                }
                //SE FOR EDITAR A ÚLTIMA VENDA REALIZADA
                else {
                    //
                    id = Integer.parseInt(params.getString("id_venda"));
                    id_venda_app = Integer.parseInt(params.getString("id_venda_app"));
                    ed.putInt("id_venda_app", id_venda_app).apply();

                    id_cliente = params.getString("codigo");
                    nome_cliente = params.getString("nome");
                    latitude_cliente = params.getString("latitude_cliente");
                    longitude_cliente = params.getString("longitude_cliente");
                    saldo = params.getString("saldo");
                }

                //
                getSupportActionBar().setTitle("Data Mov. " + classAuxiliar.exibirDataAtual());

                //
                String nomeCliente = classAuxiliar.maiuscula1(nome_cliente.toLowerCase());
                getSupportActionBar().setSubtitle(nomeCliente);
            }
        }

        //
        findViewById(R.id.btnPagamento).setOnClickListener(view -> {

            if (textTotalItens.getText().toString().equals("0")) {
                Toast.makeText(Vendas.this, "Adicione Itens a Venda.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent1 = new Intent(Vendas.this, FinanceiroDaVenda.class);
                intent1.putExtra("codigo_cliente", id_cliente);
                intent1.putExtra("nome_cliente", nome_cliente);
                intent1.putExtra("latitude_cliente", latitude_cliente);
                intent1.putExtra("longitude_cliente", longitude_cliente);
                intent1.putExtra("valorVenda", txtTotalVenda.getText().toString());

                //DADOS EMISSOR WEB
                intent1.putExtra("produto", produto_emissor);
                intent1.putExtra("quantidade", quantidade_emissor);
                intent1.putExtra("valor_unit", valor_unit_emissor);
                intent1.putExtra("saldo", saldo);

                startActivity(intent1);

                finish();
            }
        });


        spProduto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //
                String preco = bd.getMargemCliente(spProduto.getSelectedItem().toString(), id_cliente);
                if (preco.equals("0,00")) {
                    etPreco.setEnabled(true);
                    etPreco.setText("0,00");
                } else {
                    etPreco.setText(preco);
                    etPreco.setEnabled(true);
                    //etPreco.setEnabled(false);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


        /*String[] somar = {"4.0", "3.0"};
        String[] subtrair = {"4.0", "3.0"};
        String[] multiplicar = {"4.0", "3.0"};
        String[] dividir = {"4.0", "3.0"};
        String[] comparar = {"4.0", "3.0"};

        classAuxiliar.somar(somar);
        classAuxiliar.subitrair(subtrair);
        classAuxiliar.multiplicar(multiplicar);
        classAuxiliar.dividir(dividir);
        classAuxiliar.comparar(comparar);*/
    }

    //ADICIONAR VENDAS
    private void addVenda() {
        if (listaVendas.size() == 0) {
            //
            id = id + 1;
            ed.putInt("id_venda", id).apply();

            //
            String valorUnit = String.valueOf(classAuxiliar.converterValores(etPreco.getText().toString()));

            //MULTIPLICA O VALOR PELA QUANTIDADE
            String[] multiplicar = {valorUnit, etQuantidade.getText().toString()};
            String total = String.valueOf(classAuxiliar.multiplicar(multiplicar));

            //INSERIR VENDA
            bd.addVenda(new VendasDomain(
                    "" + String.valueOf(id),//CODIGO_VENDA
                    "" + id_cliente,//CODIGO_CLIENTE_VENDA
                    "" + prefs.getString("unidade", ""),//UNIDADE_VENDA
                    "" + spProduto.getSelectedItem().toString(),//PRODUTO_VENDA
                    "" + prefs.getString("data_movimento", classAuxiliar.inserirDataAtual()),//DATA_MOVIMENTO
                    "" + etQuantidade.getText().toString(),//QUANTIDADE_VENDA
                    "" + valorUnit,//PRECO_UNITARIO
                    "" + total,//VALOR_TOTAL
                    "" + prefs.getString("nome_vendedor", "app"),//VENDEDOR_VENDA
                    "0",//STATUS_AUTORIZACAO_VENDA
                    "0",//ENTREGA_FUTURA_VENDA
                    "0",//ENTREGA_FUTURA_REALIZADA
                    "" + prefs.getString("usuario_atual", "app"),//USUARIO_ATUAL
                    "" + classAuxiliar.inserirDataAtual(),//DATA_CADASTRO
                    "" + String.valueOf(prefs.getInt("id_venda_app", 1)),
                    "0",
                    ""
            ));

            //SETA OS DADOS PARA ENVIAR AO EMISSOR
            produto_emissor = spProduto.getSelectedItem().toString();
            quantidade_emissor = etQuantidade.getText().toString();
            valor_unit_emissor = valorUnit;

            //
            listaVendas = bd.getVendasCliente(prefs.getInt("id_venda_app", 1));
            adapter = new VendasAdapter(this, listaVendas);
            rvVendas.setAdapter(adapter);

            textTotalItens.setText(String.valueOf(listaVendas.size()));

            String v = classAuxiliar.maskMoney(new BigDecimal(bd.getValorTotalVenda(String.valueOf(id_venda_app))));
            txtTotalVenda.setText(v);
            Log.e("TOTAL", v);
            Log.e("TOTAL", "VENDAS: " + bd.getValorTotalVenda(String.valueOf(id_venda_app)));

            etQuantidade.setText("");
            etPreco.setText(R.string.zeros);
            spProduto.requestFocus();

        } else {
            Toast.makeText(getBaseContext(), "No momento só é permitido um item por venda!", Toast.LENGTH_SHORT).show();
        }

        //ESCODER O TECLADO
        // TODO Auto-generated method stub
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        listarItensVendas();
    }

    public void listarItensVendas() {
        try {
            // **
            listaVendas = bd.getVendasCliente(prefs.getInt("id_venda_app", 1));
            adapter = new VendasAdapter(this, listaVendas);
            adapter.notifyDataSetChanged();
            rvVendas.setAdapter(adapter);

            // **
            textTotalItens.setText(String.valueOf(listaVendas.size()));
            String v = classAuxiliar.maskMoney(new BigDecimal(bd.getValorTotalVenda(String.valueOf(id_venda_app))));
            txtTotalVenda.setText(v);
            Log.e("TOTAL", v);
            Log.e("TOTAL", "VENDAS: " + bd.getValorTotalVenda(String.valueOf(id_venda_app)));

            Log.e("LOG", "TESTE");

        } catch (Exception e) {
            Log.i("Financeiro", e.getMessage());
        }
    }

    public class MoneyTextWatcher implements TextWatcher {
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

    @Override
    protected void onDestroy() {
        bd.close();
        super.onDestroy();
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
                Toast.makeText(Vendas.this, "Esta Venda foi Cancelada!", Toast.LENGTH_LONG).show();
                finish();
            } else if (textTotalItens.getText().toString().equals("0")) {
                finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vendas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                cancelarVenda();
                break;
            case R.id.action_cancelar_venda:
                cancelarVenda();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        cancelarVenda();
        //super.onBackPressed();
    }
}