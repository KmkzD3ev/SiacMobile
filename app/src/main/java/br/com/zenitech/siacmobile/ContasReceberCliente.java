package br.com.zenitech.siacmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.ContasReceberClientesAdapter;
import br.com.zenitech.siacmobile.domains.FinanceiroReceberClientes;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;

public class ContasReceberCliente extends AppCompatActivity {
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;

    private AlertDialog alerta;
    private DatabaseHelper bd;
    ArrayList<FinanceiroReceberClientes> listaContasReceberCliente;
    ContasReceberClientesAdapter adapter;
    //
    RecyclerView rvClientes;
    Button btn_fpg_contas_receber;

    private String codigo_cliente = "";
    private String nome_cliente = "";
    int id = 1;

    //
    //public static int id_baixa_app = 1;
    public static TextView tvTotalPagarContasReceberCliente;
    public static ArrayList<String> IdsCR;

    ClassAuxiliar classAuxiliar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas_receber_cliente);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        ed = prefs.edit();

        IdsCR = new ArrayList<>();

        //
        id = prefs.getInt("id_venda", 1);


        bd = new DatabaseHelper(this);
        ArrayList<FinanceiroVendasDomain> financeiroVendasDomains = bd.getRelatorioContasReceber();
        /*if (financeiroVendasDomains.size() > 0) {
            id_baixa_app = financeiroVendasDomains.size() + 1;
            ed.putInt("id_baixa_app", id_baixa_app).apply();
        } else {
            id_baixa_app = prefs.getInt("id_baixa_app", 1);
            ed.putInt("id_baixa_app", id_baixa_app).apply();
        }*/

        //
        classAuxiliar = new ClassAuxiliar();

        //
        Intent intent = getIntent();

        if (intent != null) {
            Bundle params = intent.getExtras();

            if (params != null) {
                //
                getSupportActionBar().setTitle("Financeiro a Receber");

                //nome_cliente
                codigo_cliente = params.getString("codigo");
                nome_cliente = params.getString("nome");
                getSupportActionBar().setSubtitle(classAuxiliar.maiuscula1(nome_cliente.toLowerCase()));
            }
        }

        tvTotalPagarContasReceberCliente = findViewById(R.id.tvTotalPagarContasReceberCliente);
        btn_fpg_contas_receber = findViewById(R.id.btn_fpg_contas_receber);

        rvClientes = findViewById(R.id.rvContasReceberClientes);
        rvClientes.setLayoutManager(new LinearLayoutManager(ContasReceberCliente.this));
        listaContasReceberCliente = bd.getContasReceberCliente(codigo_cliente);
        adapter = new ContasReceberClientesAdapter(this, listaContasReceberCliente, classAuxiliar);
        rvClientes.setAdapter(adapter);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        btn_fpg_contas_receber.setOnClickListener(view -> {
            int val = Integer.parseInt(classAuxiliar.soNumeros(tvTotalPagarContasReceberCliente.getText().toString()));
            //if (tvTotalPagarContasReceberCliente.getText().toString().equals("0,00")) {
            if (val == 0) {
                Snackbar.make(view, "Selecione uma conta para pagar.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                //
                Intent i = new Intent(ContasReceberCliente.this, ContasReceberBaixarConta.class);
                i.putExtra("codigo_cliente", codigo_cliente);
                i.putExtra("nome_cliente", nome_cliente);
                i.putExtra("valorVenda", tvTotalPagarContasReceberCliente.getText().toString());
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                cancelarVenda();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cancelarVenda() {
        int i = bd.deleteFinanceiroRecebidos(Integer.parseInt(codigo_cliente));
        finish();

        /*//Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        builder.setMessage("Você Deseja Realmente Cancelar Esta Conta a Receber?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(InformacoesVagas.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                int i = bd.deleteFinanceiroRecebidos(prefs.getInt("id_baixa_app", 0));
                finish();
                if (i != 0) {
                }*//* else if (textTotalItens.getText().toString().equals("0")) {
                    finish();
                }*//*
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(InformacoesVagas.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();*/
    }

    @Override
    public void onBackPressed() {
        cancelarVenda();
        //super.onBackPressed();
    }

}
