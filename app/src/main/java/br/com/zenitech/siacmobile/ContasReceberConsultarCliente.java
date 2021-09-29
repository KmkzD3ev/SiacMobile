package br.com.zenitech.siacmobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.ClientesContasReceberAdapter;
import br.com.zenitech.siacmobile.domains.ClientesContasReceber;

public class ContasReceberConsultarCliente extends AppCompatActivity implements SearchView.OnQueryTextListener {

    DatabaseHelper bd;
    ArrayList<ClientesContasReceber> listaClientes;
    ClientesContasReceberAdapter adapter;

    //
    RecyclerView rvClientes;
    LinearLayout llNCReceber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas_receber_consultar_cliente);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Financeiro a Receber");

        bd = new DatabaseHelper(this);

        llNCReceber = findViewById(R.id.llNCReceber);
        rvClientes = findViewById(R.id.rvClientes);
        rvClientes.setLayoutManager(new LinearLayoutManager(ContasReceberConsultarCliente.this));
        listaClientes = bd.getAllClientesContasReceber();
        if (listaClientes.size() > 0) {
            rvClientes.setVisibility(View.VISIBLE);
            llNCReceber.setVisibility(View.GONE);

            adapter = new ClientesContasReceberAdapter(this, listaClientes);
            rvClientes.setAdapter(adapter);
        } else {
            rvClientes.setVisibility(View.GONE);
            llNCReceber.setVisibility(View.VISIBLE);
        }

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        findViewById(R.id.fab).setOnClickListener(view -> startActivity(new Intent(ContasReceberConsultarCliente.this,
                ContasReceberCliente.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_consultar_cliente_vendas, menu);

        //
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<ClientesContasReceber> newlist = new ArrayList<>();
        for (ClientesContasReceber clientes : listaClientes) {
            //
            String codigo;
            //
            if (clientes.apelido_cliente != null) {
                codigo = clientes.codigo_cliente.toLowerCase() + " - " + clientes.nome_cliente.toLowerCase() +
                        " - " + clientes.apelido_cliente.toLowerCase();
            } else {
                codigo = clientes.codigo_cliente.toLowerCase() + " - " + clientes.nome_cliente.toLowerCase();
            }
            if (codigo.contains(newText)) {
                newlist.add(clientes);
            }
            /*String codigo = clientes.getCodigo_cliente().toLowerCase() + " - " + clientes.getNome_cliente().toLowerCase() +
                    " - " + clientes.getApelido_cliente().toLowerCase();
            if (codigo.contains(newText)) {
                newlist.add(clientes);
            }*/
            //
            /*String nome = clientes.getNome().toLowerCase();
            if (nome.contains(newText)) {
                newlist.add(clientes);
            }*/
        }

        adapter.setFilter(newlist);
        return true;
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
        super.finish();
    }

}
