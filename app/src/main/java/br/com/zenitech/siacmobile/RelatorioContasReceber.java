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

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import br.com.zenitech.siacmobile.adapters.RelatorioContasReceberAdapter;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;

public class RelatorioContasReceber extends AppCompatActivity {
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;

    private AlertDialog alerta;
    private DatabaseHelper bd;
    ArrayList<FinanceiroVendasDomain> financeiroVendasDomains;
    RelatorioContasReceberAdapter adapter;
    //
    private RecyclerView rvRelatorioContasReceber;

    ClassAuxiliar classAuxiliar;

    private Context context;

    private LinearLayout erroRelatorio;
    private Button venderProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_contas_receber);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        context = getBaseContext();

        //
        prefs = getSharedPreferences("preferencias", this.MODE_PRIVATE);
        ed = prefs.edit();

        bd = new DatabaseHelper(this);

        //
        classAuxiliar = new ClassAuxiliar();

        rvRelatorioContasReceber = findViewById(R.id.rvRelatorioContasReceber);
        rvRelatorioContasReceber.setLayoutManager(new LinearLayoutManager(context));
        financeiroVendasDomains = bd.getRelatorioContasReceber();
        adapter = new RelatorioContasReceberAdapter(this, financeiroVendasDomains);
        rvRelatorioContasReceber.setAdapter(adapter);
        //
        if (financeiroVendasDomains.size() == 0) {
            erroRelatorio = findViewById(R.id.erroRelatorio);
            erroRelatorio.setVisibility(View.VISIBLE);

            venderProdutos = findViewById(R.id.venderProdutos);
            venderProdutos.setOnClickListener(v -> {
                startActivity(new Intent(context, ContasReceberConsultarCliente.class));
                finish();
            });
        }
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
