package br.com.zenitech.siacmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

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

public class ContasReceberCliente extends AppCompatActivity {
    //
    SharedPreferences prefs;
    DatabaseHelper bd;

    ArrayList<FinanceiroReceberClientes> listaContasReceberCliente;
    ContasReceberClientesAdapter adapter;
    //
    RecyclerView rvClientes;
    Button btn_fpg_contas_receber;

    private String codigo_cliente = "";
    private String nome_cliente = "";
    int id = 1;

    // static
    public TextView tvTotalPagarContasReceberCliente;
    public static String tvCodsDocs;
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

        IdsCR = new ArrayList<>();

        //
        id = prefs.getInt("id_venda", 1);
        tvCodsDocs = "";

        bd = new DatabaseHelper(this);

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
        adapter = new ContasReceberClientesAdapter(
                this,
                listaContasReceberCliente,
                classAuxiliar,
                tvTotalPagarContasReceberCliente);
        rvClientes.setAdapter(adapter);

        btn_fpg_contas_receber.setOnClickListener(view -> {
            int val = Integer.parseInt(classAuxiliar.soNumeros(tvTotalPagarContasReceberCliente.getText().toString()));
            if (val == 0) {
                Snackbar.make(view, "Selecione uma conta para pagar.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                //
                Intent i = new Intent(ContasReceberCliente.this, ContasReceberBaixarConta.class);
                i.putExtra("codigo_cliente", codigo_cliente);
                i.putExtra("nome_cliente", nome_cliente);
                i.putExtra("valorVenda", tvTotalPagarContasReceberCliente.getText().toString());
                i.putExtra("CodsDocs", tvCodsDocs);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            cancelarVenda();
        }

        return super.onOptionsItemSelected(item);
    }

    private void cancelarVenda() {
        //int i = bd.deleteFinanceiroRecebidos(Integer.parseInt(codigo_cliente));
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelarVenda();
        super.onBackPressed();
    }

}
