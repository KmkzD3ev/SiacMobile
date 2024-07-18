package br.com.zenitech.siacmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.zenitech.siacmobile.adapters.RelatorioVendasClientesAdapter;
import br.com.zenitech.siacmobile.domains.RelatorioVendasClientesDomain;
import br.com.zenitech.siacmobile.domains.VendasDomain;

public class RelatorioVendasCliente extends AppCompatActivity {
    private String produto = "";
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;

    private AlertDialog alerta;
    private DatabaseHelper bd;
    ArrayList<VendasDomain> vendasDomains;
    ArrayList<RelatorioVendasClientesDomain> clientes;
    RelatorioVendasClientesAdapter adapter;
    //
    private RecyclerView rvRelatorioVendas;

    ClassAuxiliar classAuxiliar;

    private Context context;

    private TextView txtProdutoRelatorioVendas, txtQuantidadeRelatorioVendas, txtTotalRelatorioVendas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_vendas_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtProdutoRelatorioVendas = (TextView) findViewById(R.id.txtProdutoRelatorioVendas);
        txtQuantidadeRelatorioVendas = (TextView) findViewById(R.id.txtQuantidadeRelatorioVendas);
        txtTotalRelatorioVendas = (TextView) findViewById(R.id.txtTotalRelatorioVendas);


        //
        Intent intent = getIntent();

        if (intent != null) {
            Bundle params = intent.getExtras();

            if (params != null) {
                produto = params.getString("produto");

                txtProdutoRelatorioVendas.setText(produto);
                txtQuantidadeRelatorioVendas.setText(params.getString("quantidade"));
                txtTotalRelatorioVendas.setText("R$ " + params.getString("total"));
            }
        }

        //
        context = getBaseContext();

        //
        prefs = getSharedPreferences("preferencias", this.MODE_PRIVATE);
        ed = prefs.edit();
        bd = new DatabaseHelper(this);

        //
        classAuxiliar = new ClassAuxiliar();

        rvRelatorioVendas = (RecyclerView) findViewById(R.id.rvRelatorioVendas);
        rvRelatorioVendas.setLayoutManager(new LinearLayoutManager(context));
        //vendasDomains = bd.getRelatorioVendasClientes();
        clientes = bd.getRelatorioVendasClientes(produto);
        adapter = new RelatorioVendasClientesAdapter(this, clientes);
        rvRelatorioVendas.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
