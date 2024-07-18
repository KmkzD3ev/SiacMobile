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
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.RelatorioVendasPedidosAdapter;
import br.com.zenitech.siacmobile.domains.VendasPedidosDomain;
import stone.application.StoneStart;
import stone.utils.Stone;

public class RelatorioVendasPedido extends AppCompatActivity {

    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;

    private AlertDialog alerta;
    private DatabaseHelper bd;
    ArrayList<VendasPedidosDomain> vendasDomains;
    RelatorioVendasPedidosAdapter adapter;
    //
    private RecyclerView rvRelatorioVendas;

    ClassAuxiliar classAuxiliar;

    private Context context;
    private LinearLayout erroRelatorio;
    private Button venderProdutos;
    Configuracoes configuracoes;
    VendasPedidosDomain pedidos;
    TextView txtProdutoRelatorioVendas, txtQuantidadeRelatorioVendas, txtTotalRelatorioVendas, txtFormsPagRelat;
    String strFormPags = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_vendas_pedido);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //
        context = this;

        //
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        ed = prefs.edit();
        bd = new DatabaseHelper(this);

        //
        classAuxiliar = new ClassAuxiliar();

        rvRelatorioVendas = findViewById(R.id.rvRelatorioVendas);
        rvRelatorioVendas.setLayoutManager(new LinearLayoutManager(context));

        //
        vendasDomains = bd.getRelatorioVendasPedidos();
        adapter = new RelatorioVendasPedidosAdapter(this, vendasDomains);
        rvRelatorioVendas.setAdapter(adapter);

        //
        txtFormsPagRelat = findViewById(R.id.txtFormsPagRelat);
        strFormPags = bd.getFormPagRelatorioVendasPedidos();
        txtFormsPagRelat.setText(strFormPags);

        txtProdutoRelatorioVendas = findViewById(R.id.txtProdutoRelatorioVendas);
        txtQuantidadeRelatorioVendas = findViewById(R.id.txtQuantidadeRelatorioVendas);
        txtTotalRelatorioVendas = findViewById(R.id.txtTotalRelatorioVendas);

        //
        if (vendasDomains.size() == 0) {
            erroRelatorio = findViewById(R.id.erroRelatorio);
            erroRelatorio.setVisibility(View.VISIBLE);

            venderProdutos = findViewById(R.id.venderProdutos);
            venderProdutos.setOnClickListener(v -> {
                startActivity(new Intent(context, VendasConsultarClientes.class));
                finish();
            });
        } else {
            String quantItens = "0";
            String valTotalPed = "0";


            for (int n = 0; n < vendasDomains.size(); n++) {

                //DADOS DOS PEDIDO
                pedidos = vendasDomains.get(n);

                // SOMA A QUANTIDADE DE ITENS
                String[] somarItens = {quantItens, pedidos.getQuantidade_venda()};
                quantItens = String.valueOf(classAuxiliar.somar(somarItens));

                // SOMA O VALOR TOTAL DOS PEDIDOS
                String[] somarValTot = {valTotalPed, pedidos.getValor_total()};
                valTotalPed = String.valueOf(classAuxiliar.somar(somarValTot));
            }

            txtProdutoRelatorioVendas.setText(String.valueOf(vendasDomains.size()));
            Double s = Double.parseDouble(quantItens);
            txtQuantidadeRelatorioVendas.setText(String.valueOf(s.intValue()));
            txtTotalRelatorioVendas.setText(String.valueOf(classAuxiliar.maskMoney(new BigDecimal(valTotalPed))));

        }
        configuracoes = new Configuracoes();
        findViewById(R.id.btnPrintRelPed).setOnClickListener(v -> {
            Intent i;

            if (configuracoes.GetDevice()) {
                i = new Intent(context, ImpressoraPOS.class);
            } else {
                i = new Intent(context, Impressora.class);
            }

            //
            i.putExtra("imprimir", "relatorio");

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
        if (id == android.R.id.home) {
            super.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}