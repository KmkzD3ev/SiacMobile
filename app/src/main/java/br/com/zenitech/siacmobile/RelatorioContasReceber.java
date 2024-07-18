package br.com.zenitech.siacmobile;

import static br.com.zenitech.siacmobile.Configuracoes.getApplicationName;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.RelatorioContasReceberAdapter;
import br.com.zenitech.siacmobile.domains.FinanceiroReceberDomain;
import stone.application.StoneStart;
import stone.utils.Stone;

public class RelatorioContasReceber extends AppCompatActivity {
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;

    private AlertDialog alerta;
    private DatabaseHelper bd;
    ArrayList<FinanceiroReceberDomain> financeiroVendasDomains;
    RelatorioContasReceberAdapter adapter;
    //
    private RecyclerView rvRelatorioContasReceber;

    ClassAuxiliar classAuxiliar;

    private Context context;

    private LinearLayout erroRelatorio;
    private Button venderProdutos;
    Configuracoes configuracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_contas_receber);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        configuracoes = new Configuracoes();
        findViewById(R.id.btnPrintRelPed).setOnClickListener(v -> {
            Intent i;
            //i = new Intent(context, ImpressoraPOS.class);
            if (configuracoes.GetDevice()) {
                i = new Intent(context, ImpressoraPOS.class);
            } else {
                i = new Intent(context, Impressora.class);
            }

            //
            i.putExtra("imprimir", "relatorioBaixa");

            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        if (configuracoes.GetDevice()) {
            iniciarStone();
        }
    }

    // MODULO STONE **

    // Iniciar o Stone
    void iniciarStone() {
        // O primeiro passo é inicializar o SDK.
        StoneStart.init(context);
        /*Em seguida, é necessário chamar o método setAppName da classe Stone,
        que recebe como parâmetro uma String referente ao nome da sua aplicação.*/
        Stone.setAppName(getApplicationName(context));
        //Ambiente de Sandbox "Teste"
        /*Stone.setEnvironment(new Configuracoes().Ambiente());
        //Ambiente de Produção
        //Stone.setEnvironment((Environment.PRODUCTION));

        // Esse método deve ser executado para inicializar o SDK
        List<UserModel> userList = StoneStart.init(context);

        // Quando é retornado null, o SDK ainda não foi ativado
        if (userList != null) {
            // O SDK já foi ativado.
            _pinpadAtivado();

        } else {
            // Inicia a ativação do SDK
            ativarStoneCode();
        }*/
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
