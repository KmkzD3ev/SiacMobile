package br.com.zenitech.siacmobile;

import android.os.Bundle;

import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.ClientesAdapter;
import br.com.zenitech.siacmobile.domains.Clientes;

public class VendasConsultarClientes extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private DatabaseHelper bd;
    ArrayList<Clientes> listaClientes;
    ClientesAdapter adapter;
    //
    private EditText edtConsultarCliente;
    private RecyclerView rvClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendas_consultar_clientes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vendas");
        getSupportActionBar().setSubtitle("Selecionar Cliente");

        bd = new DatabaseHelper(this);

        rvClientes = findViewById(R.id.rvClientes);
        rvClientes.setLayoutManager(new LinearLayoutManager(VendasConsultarClientes.this));
        listaClientes = bd.getAllClientes();
        adapter = new ClientesAdapter(this, listaClientes);
        rvClientes.setAdapter(adapter);


        //
        edtConsultarCliente = findViewById(R.id.edtConsultarCliente);
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
        ArrayList<Clientes> newlist = new ArrayList<>();
        for (Clientes clientes : listaClientes) {
            String codigo;
            //
            if (clientes.getApelido_cliente() != null) {
                codigo = clientes.getCodigo().toLowerCase() + " - " + clientes.getNome().toLowerCase() +
                        " - " + clientes.getApelido_cliente().toLowerCase();
            } else {
                codigo = clientes.getCodigo().toLowerCase() + " - " + clientes.getNome().toLowerCase();
            }
            if (codigo.contains(newText)) {
                newlist.add(clientes);
            }

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
