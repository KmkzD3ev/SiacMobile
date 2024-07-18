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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.RelatorioVendasAdapter;
import br.com.zenitech.siacmobile.domains.VendasDomain;

public class RelatorioVendas extends AppCompatActivity {
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;

    private AlertDialog alerta;
    private DatabaseHelper bd;
    ArrayList<VendasDomain> vendasDomains;
    RelatorioVendasAdapter adapter;
    //
    private RecyclerView rvRelatorioVendas;

    ClassAuxiliar classAuxiliar;

    private Context context;
    private LinearLayout erroRelatorio;
    private Button venderProdutos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_vendas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //
        context = getBaseContext();

        //
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        ed = prefs.edit();
        bd = new DatabaseHelper(this);

        //
        classAuxiliar = new ClassAuxiliar();

        rvRelatorioVendas = findViewById(R.id.rvRelatorioVendas);
        rvRelatorioVendas.setLayoutManager(new LinearLayoutManager(context));
        vendasDomains = bd.getRelatorioVendas();
        adapter = new RelatorioVendasAdapter(this, vendasDomains);
        rvRelatorioVendas.setAdapter(adapter);


        //
        if (vendasDomains.size() == 0) {
            erroRelatorio = findViewById(R.id.erroRelatorio);
            erroRelatorio.setVisibility(View.VISIBLE);

            venderProdutos = findViewById(R.id.venderProdutos);
            venderProdutos.setOnClickListener(v -> {
                startActivity(new Intent(context, VendasConsultarClientes.class));
                finish();
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
