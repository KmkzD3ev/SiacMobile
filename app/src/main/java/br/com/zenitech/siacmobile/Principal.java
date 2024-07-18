package br.com.zenitech.siacmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import java.io.File;

import br.com.zenitech.siacmobile.adapters.ViewPagerAdapter;
import br.com.zenitech.siacmobile.fragments.GerenciarContentFragment;
import br.com.zenitech.siacmobile.fragments.PrincipalContentFragment;
import br.com.zenitech.siacmobile.fragments.RelatoriosContentFragment;

public class Principal extends AppCompatActivity {

    //
    private SharedPreferences prefs;
    private ClassAuxiliar classAuxiliar;
    private DatabaseHelper bd;


    public ViewPager viewPager;
    public TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        classAuxiliar = new ClassAuxiliar();

        //
        bd = new DatabaseHelper(this);

        //
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle("  " + maiuscula1(prefs.getString("unidade_usuario", "app").toLowerCase()));
        getSupportActionBar().setSubtitle("  " + maiuscula1(prefs.getString("nome_vendedor", "app").toLowerCase()));

        //INICIA AS VENDAS
        findViewById(R.id.btn_vendas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Principal.this, VendasConsultarClientes.class));
            }
        });

        //CONSULTAR CLIENTE CONTAS RECEBER
        findViewById(R.id.btn_contas_receber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Principal.this, ContasReceberConsultarCliente.class));
            }
        });

        //CONSULTAR CLIENTE CONTAS RECEBER
        findViewById(R.id.btnExcluirBanco).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //PEGA O CAMINHO DA PASTA DOWNLOAD DO APARELHO PARA VERIFICAR SE O BANCO EXISTE
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                //CRIA O ARQUIVO DO BANCO
                File arquivo = new File(path + "/siacmobileDB.db");
                //APAGA O BANCO DA PASTA DOWNLOADS
                arquivo.delete();

                //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
                getApplicationContext().deleteDatabase("siacmobileDB");

                //SALVA OS DADOS DO USUÁRIO
                prefs.edit().putString("unidade_usuario", "").apply();
                prefs.edit().putString("codigo_usuario", "").apply();
                prefs.edit().putString("login_usuario", "").apply();
                prefs.edit().putString("senha_usuario", "").apply();
                prefs.edit().putString("usuario_atual", "").apply();
                prefs.edit().putString("nome_vendedor", "").apply();
                prefs.edit().putString("data_movimento", "").apply();

                Intent i = new Intent(Principal.this, Sincronizar.class);
                startActivity(i);
            }
        });

        // Setting ViewPager for each Tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        //
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new PrincipalContentFragment(), "PRINCIPAL");
        adapter.addFragment(new RelatoriosContentFragment(), "RELATÓRIOS");
        adapter.addFragment(new GerenciarContentFragment(), "GERENCIAR");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sair) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //DEIXAR A PRIMEIRA LETRA DA STRING EM MAIUSCULO
    public String maiuscula1(String palavra) {
        return palavra.substring(0, 1).toUpperCase() + palavra.substring(1);
    }

    private void consultarVendasNaoFinalizadas(){

    }
}
