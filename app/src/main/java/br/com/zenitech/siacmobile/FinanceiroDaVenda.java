package br.com.zenitech.siacmobile;

import static br.com.zenitech.siacmobile.Configuracoes.getApplicationName;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.maps.android.SphericalUtil;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.com.zenitech.siacmobile.adapters.FinanceiroVendasAdapter;
import br.com.zenitech.siacmobile.domains.Conta;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;
import br.com.zenitech.siacmobile.domains.PosApp;
import br.com.zenitech.siacmobile.interfaces.ILogin;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import stone.application.StoneStart;
import stone.user.UserModel;
import stone.utils.Stone;

public class FinanceiroDaVenda extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    protected static final int REQUEST_CHECK_SETTINGS = 1;
    //
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;
    private CreditoPrefs creditoPrefs;

    public static String totalFinanceiro;
    private LinearLayoutCompat bandPrazo;
    public static TextView txtTotalFinanceiro;
    public static TextView txtTotalItemFinanceiro;
    private ArrayList<String> listaFormasPagamentoCliente, listaBandeiras, listaParcelas;
    private DatabaseHelper bd;
    private Spinner spFormasPagamentoCliente, spBandeira, spParcela;
    public static String codigo_cliente = "";
    public static String nomeCliente = "";
    public static String cpfcnpjCliente = "";
    public static String enderecoCliente = "";
    public static EditText txtDocumentoFormaPagamento;
    public static EditText txtVencimentoFormaPagamento, txtValorFormaPagamento;
    public static LinearLayout bgTotal;
    public EditText txtNotaFiscal;
    LinearLayout formFinan;

    private ArrayList<String> formasPagamentoPrazo; // Array com a s formas de pagamento A_PRAZO
    private ArrayList<String> valoresCompra;  // Armazena o valor da compra quando formas de pagamento A_PRAZO e Identificada
    private BigDecimal valorTotalAPrazo = BigDecimal.ZERO; // Variável para armazenar o valor total das formas de pagamento a prazo



    //LISTAR VENDAS
    private ArrayList<FinanceiroVendasDomain> listaFinanceiroCliente;
    private FinanceiroVendasAdapter adapter;
    private RecyclerView rvFinanceiro;

    private Button btnAddF, btnPagamento;

    int id = 1;
    int id_venda_app = 1;
    private String total_venda = "0.0";
    private ClassAuxiliar classAuxiliar;

    TextInputLayout tilDocumento, tilVencimento, tilNotaFiscal;
    private AlertDialog alerta;

    //DADOS PARA PASSAR AO EMISSOR WEB
    private String produto_emissor;
    private String quantidade_emissor;
    private String valor_unit_emissor;
    private Context context;

    // DADOS DE CORDENADAS *********
    double coord_latitude = 0.0;
    double coord_longitude = 0.0;
    String precisao = "";
    GPStracker coord;
    LatLng posicaoInicial;
    LatLng posicaiFinal;
    double distance;

    // DADOS CLIENTE
    double coordCliLat = 0.0, coordCliLon = 0.0;
    // DADOS DE CORDENADAS *********

    private SpotsDialog dialog;

    private VerificarOnline verificarOnline;

    private String vencimentoTemp;

    //UnidadesDomain unidades;
    Configuracoes configuracoes;

    PosApp posApp;
    String nDoc = "";

    //
    ArrayAdapter adapterSpBandeira, adapterSpParcela;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financeiro_da_venda);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //
        formasPagamentoPrazo = new ArrayList<>();
        valoresCompra = new ArrayList<>();
        creditoPrefs = new CreditoPrefs(this);


        classAuxiliar = new ClassAuxiliar();
        context = this;
        coord = new GPStracker(context);
        // VERIFICA SE O GPS ESTÁ ATIVO
        if (coord.isGPSEnabled()) {
            coord.getLocation();
            //gps.getLatLon();
        }
        verificarOnline = new VerificarOnline();
        dialog = (SpotsDialog) new SpotsDialog.Builder()
                .setContext(context)
                .setTheme(R.style.Custom)
                .setCancelable(false)
                .build();

        //
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        ed = prefs.edit();

        //
        id = prefs.getInt("id_financeiro_venda", 1);

        //
        bd = new DatabaseHelper(this);
        posApp = bd.getPos();

        //
        bgTotal = findViewById(R.id.bgTotal);

        //
        bandPrazo = findViewById(R.id.bandPrazo);
        rvFinanceiro = findViewById(R.id.rvFinanceiro);
        rvFinanceiro.setLayoutManager(new LinearLayoutManager(this));

        //
        tilDocumento = findViewById(R.id.tilDocumento);
        tilVencimento = findViewById(R.id.tilVencimento);
        tilNotaFiscal = findViewById(R.id.tilNotaFiscal);

        //Total Venda: R$
        //txtNomeClienteFinanceiro = (TextView) findViewById(R.id.txtNomeClienteFinanceiro);
        txtTotalFinanceiro = findViewById(R.id.txtTotalFinanceiro);
        //
        txtValorFormaPagamento = findViewById(R.id.txtValorFormaPagamento);
        txtValorFormaPagamento.addTextChangedListener(new FinanceiroDaVenda.MoneyTextWatcher(txtValorFormaPagamento));

        txtDocumentoFormaPagamento = findViewById(R.id.txtDocumentoFormaPagamento);
        txtNotaFiscal = findViewById(R.id.txtNotaFiscal);

        //
        txtVencimentoFormaPagamento = findViewById(R.id.txtVencimentoFormaPagamento);
        txtVencimentoFormaPagamento.setText(classAuxiliar.exibirDataAtual());
        txtVencimentoFormaPagamento.addTextChangedListener(classAuxiliar.maskData("##/##/####", txtVencimentoFormaPagamento));

        txtTotalItemFinanceiro = findViewById(R.id.txtTotalItemFinanceiro);


        //
        formFinan = findViewById(R.id.formFinan);
        btnAddF = findViewById(R.id.btnAddF);
        btnAddF.setOnClickListener(v -> {

            // Captura o valor inserido no campo txtValorFormaPagamento
            String valorInserido = txtValorFormaPagamento.getText().toString().replaceAll("[^\\d.,]", "").replace(",", ".").trim();
            String[] fPag1 = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

            // Se a forma de pagamento for "A PRAZO", adicionar o valor ao array valoresCompra
            if (fPag1.length > 1 && "A PRAZO".equals(fPag1[1])) {
                valoresCompra.add(valorInserido);
                Log.d("ValorAdicionado", "Valor adicionado ao array: " + valorInserido);
            }
            Log.d("ValoresCompraArray", "Valores atuais no array: " + valoresCompra.toString());


            //SE O USUÁRIO NÃO ADICIONAR NENHUM VALOR
            String val = classAuxiliar.soNumeros(txtValorFormaPagamento.getText().toString());
            /*if (txtValorFormaPagamento.getText().toString().equals("") ||
                    txtValorFormaPagamento.getText().toString().equals("R$0,00") ||
                    txtValorFormaPagamento.getText().toString().equals("R$0,00")
            ) {*/
            if (val.equalsIgnoreCase("") || val.equalsIgnoreCase("000")) {
                //
                Toast.makeText(FinanceiroDaVenda.this, "Adicione uma valor para esta forma de pagamento.", Toast.LENGTH_LONG).show();
            } else {
                //
                String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

                //SE A FORMA DE PAGAMENTO FOR IGUAL A PRAZO VERIFICA O NÚMERO DO DOCUMENTO E O TIPO DE BAIXA
                if (fPag[1].equals("A PRAZO")) {

                    //SE O NÚMERO DO DOCUMENTO ESTIVER VÁSIO MOSTRA A MENSAGEM
                    if (txtDocumentoFormaPagamento.getText().toString().equals("") && fPag[2].equals("1")) {
                        //
                        Toast.makeText(FinanceiroDaVenda.this, "Para essa forma de pagamento o número do documento é obrigatório.", Toast.LENGTH_LONG).show();
                    }
                    //SE A BAIXA FOR MANUAL VERIFICA O CAMPO VENCIMENTO
                    else if (fPag[3].equals("1")) {

                        //SE O CAMPO VENCIMENTO FOR IGUAL A 00/00/0000 PEDE QUE INFORME A DATA DO VENCIMENTO
                        if (txtVencimentoFormaPagamento.getText().toString().equals("") || txtVencimentoFormaPagamento.getText().toString().equals("00/00/0000")) {
                            //
                            Toast.makeText(FinanceiroDaVenda.this, "Data do vencimento é obrigatório.", Toast.LENGTH_LONG).show();
                        }
                        //ADICIONA VALOR AO FINANCEIRO
                        else {
                            addFinanceiro();
                        }
                    }
                    //ADICIONA VALOR AO FINANCEIRO
                    else {
                        addFinanceiro();
                    }
                }
                //ADICIONA VALOR AO FINANCEIRO
                else {
                    addFinanceiro();
                }
            }
        });
        //
        btnPagamento = findViewById(R.id.btnPagamento);
        btnPagamento.setOnClickListener(v -> {
            Log.d("ValoresCompraAntesDeFinalizar", "Conteúdo do array valoresCompra: " + valoresCompra);
            //

            BigDecimal totalValoresCompra = somarValoresCompra();
            creditoPrefs.setValorAprazo(totalValoresCompra.toString());
            Log.d("TotalValoresCompra", "Soma  valores  compra a prazo enviad SHARE: " + totalValoresCompra.toString());


            _salvarFinanceiro();
        });

        //unidades = bd.getUnidade();


        //
        Intent intent = getIntent();

        if (intent != null) {
            Bundle params = intent.getExtras();

            if (params != null) {

                codigo_cliente = params.getString("codigo_cliente");
                Log.d("CREDITO PREFS", "onCreate:ENVIANDO PRO SHARE "+ codigo_cliente);


                if (!Objects.requireNonNull(params.getString("saldo")).equalsIgnoreCase("")) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Saldo: " + classAuxiliar.maskMoney(classAuxiliar.converterValores(params.getString("saldo"))));
                } else {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Financeiro");
                }
                //getSupportActionBar().setSubtitle("R$ " + params.getString("valorVenda"));// + "  " + prefs.getInt("id_venda_app", 1)

                //
                Log.i("Financeiro", Objects.requireNonNull(params.getString("latitude_cliente")));
                //nome_cliente
                codigo_cliente = params.getString("codigo_cliente");
                if (!Objects.requireNonNull(params.getString("latitude_cliente")).equalsIgnoreCase("") &&
                        !Objects.requireNonNull(params.getString("longitude_cliente")).equalsIgnoreCase("")
                ) {
                    coordCliLat = Double.parseDouble(Objects.requireNonNull(params.getString("latitude_cliente")));
                    coordCliLon = Double.parseDouble(Objects.requireNonNull(params.getString("longitude_cliente")));
                }

                //txtNomeClienteFinanceiro.setText(params.getString("nome_cliente"));
                txtTotalFinanceiro.setText(params.getString("valorVenda"));

                txtValorFormaPagamento.setText(params.getString("valorVenda"));

                //DADOS EMISSOR WEB
                produto_emissor = params.getString("produto");
                quantidade_emissor = params.getString("quantidade");
                valor_unit_emissor = params.getString("valor_unit");

                //
                nomeCliente = params.getString("nome_cliente");
                cpfcnpjCliente = params.getString("cpfcnpj");
                enderecoCliente = params.getString("endereco");

                getSupportActionBar().setSubtitle(classAuxiliar.maiuscula1(Objects.requireNonNull(nomeCliente).toLowerCase()));
            }
        }

        //
        spFormasPagamentoCliente = findViewById(R.id.spFormasPagamentoCliente);
        spBandeira = findViewById(R.id.spBandeira);
        spParcela = findViewById(R.id.spParcela);

        listaFormasPagamentoCliente = bd.getFormasPagamentoCliente(codigo_cliente);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaFormasPagamentoCliente);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormasPagamentoCliente.setAdapter(adapter);

        /*spFormasPagamentoCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");
                if (bd.getCartaoTrue(fPag[0]).equalsIgnoreCase("1")) {
                    bandPrazo.setVisibility(View.VISIBLE);

                    //
                    listaBandeiras = bd.getBandeiraFPg(fPag[0]);
                    adapterSpBandeira = new ArrayAdapter(context, android.R.layout.simple_spinner_item, listaBandeiras);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spBandeira.setAdapter(adapterSpBandeira);
                    //spBandeira.setOnItemSelectedListener(FinanceiroDaVenda.this);
                    spBandeira.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            //Toast.makeText(context, spBandeira.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                            //
                            listaParcelas = bd.getPrazoFPg(spBandeira.getSelectedItem().toString(), fPag[0]);
                            adapterSpParcela = new ArrayAdapter(context, android.R.layout.simple_spinner_item, listaParcelas);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spParcela.setAdapter(adapterSpParcela);
                            spParcela.setOnItemSelectedListener(FinanceiroDaVenda.this);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    listaBandeiras = null;
                    listaParcelas = null;
                    bandPrazo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        spFormasPagamentoCliente.setOnItemSelectedListener(FinanceiroDaVenda.this);
        atualizarValFin();
        logFormasPagamentoPrazo();

        // Verificar se o GPS foi aceito pelo entregador
        isGPSEnabled();
        configuracoes = new Configuracoes();
        if (configuracoes.GetDevice()) {
            iniciarStone();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VerificarActivityAtiva.activityResumed();
        // Verificar se o GPS foi aceito pelo operador
        isGPSEnabled();
        if (coord.isGPSEnabled()) {
            coord.getLocation();
            //gps.getLatLon();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        VerificarActivityAtiva.activityPaused();
    }

    // Iniciar o Stone
    void iniciarStone() {
        // O primeiro passo é inicializar o SDK.
        StoneStart.init(context);
        /*Em seguida, é necessário chamar o método setAppName da classe Stone,
        que recebe como parâmetro uma String referente ao nome da sua aplicação.*/
        Stone.setAppName(getApplicationName(context));
        //Ambiente de Sandbox "Teste"
        //Stone.setEnvironment(new Configuracoes().Ambiente());
        //Ambiente de Produção
        //Stone.setEnvironment((Environment.PRODUCTION));

        // Esse método deve ser executado para inicializar o SDK
        List<UserModel> userList = StoneStart.init(context);

        // Quando é retornado null, o SDK ainda não foi ativado
        /*if (userList != null) {
            // O SDK já foi ativado.
            _pinpadAtivado();

        } else {
            // Inicia a ativação do SDK
            ativarStoneCode();
        }*/
    }

    private void _verificarFPgVenda() {
        if (Objects.requireNonNull(prefs.getString("print_promissoria", "0")).equalsIgnoreCase("0")
                && Objects.requireNonNull(prefs.getString("print_boleto", "0")).equalsIgnoreCase("0")) {
            finish();
            return;
        }

        int v = 0;
        for (int a = 0; a < listaFinanceiroCliente.size(); a++) {
            //Log.e("FINANCEIRO", listaFinanceiroCliente.get(a).getFpagamento_financeiro());

            // IMPRESSÃO DA PROMISSORIA
            if (Objects.requireNonNull(prefs.getString("print_promissoria", "0")).equalsIgnoreCase("1")) {
                //if (listaFinanceiroCliente.get(a).getFpagamento_financeiro().replace(" _ ", "").equalsIgnoreCase("PROMISSORIA")) {
                if (listaFinanceiroCliente.get(a).getFpagamento_financeiro().replace(" _ ", "").contains("PROMISSORIA")) {
                    v++;
                    String val = classAuxiliar.maskMoney(new BigDecimal(listaFinanceiroCliente.get(a).getValor_financeiro()));
                    Intent i;
                    if (configuracoes.GetDevice()) {
                        i = new Intent(context, ImpressoraPOS.class);
                    } else {
                        i = new Intent(context, Impressora.class);
                    }

                    //
                    i.putExtra("razao_social", nomeCliente);
                    i.putExtra("tel_contato", "");
                    //i.putExtra("numero", txtDocumentoFormaPagamento.getText().toString());
                    i.putExtra("numero", listaFinanceiroCliente.get(a).getDocumento_financeiro());
                    i.putExtra("vencimento", txtVencimentoFormaPagamento.getText().toString());
                    i.putExtra("valor", val);
                    //i.putExtra("valor", financeiroVendasDomain.getValor_financeiro());
                    i.putExtra("id_cliente", codigo_cliente);
                    i.putExtra("cpfcnpj", cpfcnpjCliente);
                    i.putExtra("endereco", enderecoCliente);
                    i.putExtra("imprimir", "Promissoria");

                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    //Intent intent = new Intent(this, SomeActivity.class);
                    //launchSomeActivity.launch(i);
                }
            }


            /*if (listaFinanceiroCliente.get(a).getFpagamento_financeiro().replace(" _ ", "").equalsIgnoreCase("PROMISSORIA")) {
                v++;
                String val = classAuxiliar.maskMoney(new BigDecimal(listaFinanceiroCliente.get(a).getValor_financeiro()));
                Intent i;
                if (configuracoes.GetDevice()) {
                    i = new Intent(context, ImpressoraPOS.class);
                } else {
                    i = new Intent(context, Impressora.class);
                }

                //
                i.putExtra("razao_social", nomeCliente);
                i.putExtra("tel_contato", "");
                //i.putExtra("numero", txtDocumentoFormaPagamento.getText().toString());
                i.putExtra("numero", listaFinanceiroCliente.get(a).getDocumento_financeiro());
                i.putExtra("vencimento", txtVencimentoFormaPagamento.getText().toString());
                i.putExtra("valor", val);
                //i.putExtra("valor", financeiroVendasDomain.getValor_financeiro());
                i.putExtra("id_cliente", codigo_cliente);
                i.putExtra("cpfcnpj", cpfcnpjCliente);
                i.putExtra("endereco", enderecoCliente);
                i.putExtra("imprimir", "Promissoria");

                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                //Intent intent = new Intent(this, SomeActivity.class);
                //launchSomeActivity.launch(i);
            }
            */

            // IMPRESSÃO DO BOLETO
            if (Objects.requireNonNull(prefs.getString("print_boleto", "0")).equalsIgnoreCase("1")) {

                String contaBancFormPag = bd.getContaBancariaFormaPagamento(listaFinanceiroCliente.get(a).getFpagamento_financeiro().replace(" _ ", ""));
                // VERIFICA SE É PARA IMPRIMIR A PROMISSORIA, CASO SEJA 0, NÃO AVANÇA.

                if (!contaBancFormPag.equalsIgnoreCase("0")) {

                    String CodContaBanc = bd.getCodContaBancaria(contaBancFormPag);

                    // SE FOR IGUAL A 1 CÓD BB, IMPRIMI
                    if (CodContaBanc.equalsIgnoreCase("001") || CodContaBanc.equalsIgnoreCase("237")) {

                        Intent i;
                        String val = classAuxiliar.maskMoney(new BigDecimal(listaFinanceiroCliente.get(a).getValor_financeiro()));
                        if (configuracoes.GetDevice()) {
                            i = new Intent(context, ImpressoraPOS.class);
                        } else {
                            i = new Intent(context, Impressora.class);
                        }

                        i.putExtra("razao_social", nomeCliente);
                        i.putExtra("tel_contato", "");
                        i.putExtra("numero", listaFinanceiroCliente.get(a).getDocumento_financeiro());
                        i.putExtra("vencimento", listaFinanceiroCliente.get(a).getVencimento_financeiro());
                        i.putExtra("valor", val);
                        i.putExtra("id_cliente", codigo_cliente);
                        i.putExtra("cpfcnpj", cpfcnpjCliente);
                        i.putExtra("endereco", enderecoCliente);
                        i.putExtra("imprimir", "Boleto");
                        i.putExtra("nota_fiscal", listaFinanceiroCliente.get(a).getNota_fiscal());
                        i.putExtra("nContaBanco", contaBancFormPag);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        finish();
                    }
                }
            }
        }

        if (v == 0) finish();
    }
    // Create lanucher variable inside onAttach or onCreate or global
    /*ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // your operation....
                }
            });*/

    /*public void openYourActivity() {
        Intent intent = new Intent(this, SomeActivity.class);
        launchSomeActivity.launch(intent);
    }*/

    //
    private void _salvarFinanceiro() {
        if (txtTotalItemFinanceiro.getText().equals("0,00")) {
            //
            Toast.makeText(FinanceiroDaVenda.this, "Adicione pelo menos uma forma de pagamento ao financeiro.", Toast.LENGTH_LONG).show();
        } else if (!txtTotalItemFinanceiro.getText().equals(txtTotalFinanceiro.getText())) {
            //
            Toast.makeText(FinanceiroDaVenda.this, "O valor do financeiro está diferente da venda.", Toast.LENGTH_LONG).show();
        } else {

            // SE O VENDEDOR PODE VENDER SEM COMPARAR A POSIÇÃO DO CLIENTE, PASSA DIRETO PARA A INSERÇÃO DO FINANCEIRO
            if (prefs.getString("verificar_posicao_cliente", "1").equalsIgnoreCase("0")) {
                finalizarFinanceiroVenda();
            } else {
                if (coordCliLat != 0.0) {
                    // VERIFICA SE O GPS ESTÁ ATIVO
                    if (!coord.isGPSEnabled()) {
                        //Toast.makeText(FinanceiroDaVenda.this, "Ative o GPS para finalizar a venda!.", Toast.LENGTH_LONG).show();
                        /*Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);*/

                        createLocationRequest();

                    } else {
                        coord.getLocation();
                        verifCordenadas();
                    }

                } else {
                    finalizarFinanceiroVenda();
                }
            }
        }
    }

    private void atualizarValFin() {
        try {
            //
            listaFinanceiroCliente = bd.getFinanceiroCliente(prefs.getInt("id_venda_app", 1));
            adapter = new FinanceiroVendasAdapter(this, listaFinanceiroCliente, valoresCompra);
            rvFinanceiro.setAdapter(adapter);
            //
            String tif = classAuxiliar.maskMoney(new BigDecimal(bd.getValorTotalFinanceiro(String.valueOf(prefs.getInt("id_venda_app", 1)))));
            txtTotalItemFinanceiro.setText(tif);

            //
            //!txtTotalItemFinanceiro.getText().equals(txtTotalFinanceiro.getText()

            //
            String valorFinanceiro = String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiro.getText().toString()));
            String valorFinanceiroAdd = String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiro.getText().toString()));

            //SUBTRAIR O VALOR PELA QUANTIDADE
            String[] subtracao = {valorFinanceiro, valorFinanceiroAdd};
            String total = String.valueOf(classAuxiliar.subitrair(subtracao));

            txtValorFormaPagamento.setText(total);
        } catch (Exception e) {
            Log.i("Financeiro", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DialogFragment();
        newFragment.show(getSupportFragmentManager(), "dataPicker");
    }

    public void showDatePickerDialogAndroid10Plus(View v) {
        DialogFragment newFragment = new DialogFragment();
        newFragment.show(getSupportFragmentManager(), "dataPicker");
    }

    public void mostrarMsg() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo_emissor_web);
        //define o titulo
        builder.setTitle("Emissor Web");
        //define a mensagem
        String msg = "Deseja emitir a NFC-e?";
        builder.setMessage(msg);
        //define um botão como positivo
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(InformacoesVagas.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();

                /*/
                Toast.makeText(FinanceiroDaVenda.this, "Venda Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FinanceiroDaVenda.this, Principal.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                */

                //sair(); //
                //listaVendas = bd.getVendasCliente(prefs.getInt("id_venda_app", 1));
                //ArrayList<Produtos> listaProdutos;
                //listaProdutos = bd.getAllProdutos();
                //i.putExtra("produtos", listaProdutos);

                PackageManager packageManager = getPackageManager();
                String packageName = "br.com.zenitech.emissorweb";
                Intent i = packageManager.getLaunchIntentForPackage(packageName);
                //DADOS EMISSOR WEB
                i.putExtra("siac", "1");
                i.putExtra("produto", produto_emissor);
                i.putExtra("quantidade", quantidade_emissor);
                i.putExtra("valor_unit", valor_unit_emissor);
                i.putExtra("forma_pagamento", "DINHEIRO");

                startActivity(i);
                finish();
            }
        });

        //define um botão como negativo.
        builder.setNegativeButton("NÃO", (arg0, arg1) -> sair());
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();
    }

    private void addFinanceiro() {

        //
        id = id + 1;
        ed.putInt("id_financeiro_venda", id).apply();

        //
        String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");

        String sql = "";
        sql += id + "\n";//CODIGO_FINANCEIRO
        sql += prefs.getString("unidade", "UNIDADE TESTE") + "\n";//UNIDADE_FINANCEIRO
        sql += classAuxiliar.inserirDataAtual() + "\n";//DATA_FINANCEIRO
        sql += codigo_cliente + "\n";//CODIGO_CLIENTE_FINANCEIRO
        sql += fPag[0] + "\n";//sql += spFormasPagamentoCliente.getSelectedItem().toString() + "\n";//FPAGAMENTO_FINANCEIRO
        sql += txtDocumentoFormaPagamento.getText().toString() + "\n";//DOCUMENTO_FINANCEIRO
        sql += String.valueOf(classAuxiliar.inserirData(classAuxiliar.formatarData(classAuxiliar.soNumeros(txtVencimentoFormaPagamento.getText().toString())))) + "\n";//VENCIMENTO_FINANCEIRO
        sql += String.valueOf(classAuxiliar.converterValores(txtValorFormaPagamento.getText().toString())) + "\n";//VALOR_FINANCEIRO
        sql += "0" + "\n";//STATUS_AUTORIZACAO
        sql += "0" + "\n";//PAGO
        sql += "0" + "\n";//VASILHAME_REF
        sql += "0" + "\n";//USUARIO_ATUAL_FINANCEIRO
        sql += classAuxiliar.inserirDataAtual() + "\n";//DATA_INCLUSAO
        sql += "" + "\n";//NOSSO_NUMERO_FINANCEIRO
        sql += "" + prefs.getInt("id_vendedor", 1) + "\n";//ID_VENDEDOR_FINANCEIRO
        sql += "" + prefs.getInt("id_venda_app", 1) + "\n";

        //SETAR O SQL NO LOG PARA CONSULTA
        Log.e("SQL", sql);

        String sBandeira = "", sPrazo = "";
        if (bd.getCartaoTrue(fPag[0]).equalsIgnoreCase("1")) {
            try {
                /*PackageManager packageManager = getPackageManager();
                String packageName = "br.com.zenitech.emissorweb";
                //Intent i = new Intent(packageName);
                //i.setPackage(packageName);
                Intent i = packageManager.getLaunchIntentForPackage(packageName);
                //DADOS EMISSOR WEB
                i.putExtra("siac", "1");
                i.putExtra("cpfCnpj_cliente", "000.000.000-00");
                i.putExtra("formaPagamento", "CARTÃO DE DÉBITO");//CRÉDITO
                i.putExtra("produto", "P 13");
                i.putExtra("qnt", "1");
                i.putExtra("vlt", txtValorFormaPagamento.getText().toString());

                startActivityForResult(i, 2);*/

                sBandeira = classAuxiliar.getIdBandeira(spBandeira.getSelectedItem().toString());
                sPrazo = spParcela.getSelectedItem().toString();
            } catch (Exception ignored) {

            }
        }
        //INSERIR FINANCEIRO
        bd.addFinanceiro(new FinanceiroVendasDomain(
                String.valueOf(id),//CODIGO_FINANCEIRO
                prefs.getString("unidade", "UNIDADE TESTE"),//UNIDADE_FINANCEIRO
                classAuxiliar.inserirDataAtual(),//DATA_FINANCEIRO
                codigo_cliente,//CODIGO_CLIENTE_FINANCEIRO
                fPag[0],//spFormasPagamentoCliente.getSelectedItem().toString(),//FPAGAMENTO_FINANCEIRO
                txtDocumentoFormaPagamento.getText().toString(),//DOCUMENTO_FINANCEIRO
                String.valueOf(classAuxiliar.inserirData(classAuxiliar.formatarData(classAuxiliar.soNumeros(txtVencimentoFormaPagamento.getText().toString())))),//VENCIMENTO_FINANCEIRO
                String.valueOf(classAuxiliar.converterValores(txtValorFormaPagamento.getText().toString())),//VALOR_FINANCEIRO
                "0",//STATUS_AUTORIZACAO
                "0",//PAGO
                "0",//VASILHAME_REF
                "0",//USUARIO_ATUAL_FINANCEIRO
                "" + classAuxiliar.inserirDataAtual(),//DATA_INCLUSAO
                "",//NOSSO_NUMERO_FINANCEIRO
                "" + prefs.getInt("id_vendedor", 1),//ID_VENDEDOR_FINANCEIRO
                "" + prefs.getInt("id_venda_app", 1),
                txtNotaFiscal.getText().toString(),
                bd.getIdAliquota(bd.getIdFPG(fPag[0]), sBandeira, sPrazo)
        ));

        //if (fPag[0].equalsIgnoreCase("PROMISSORIA")) {
        if (fPag[0].contains("PROMISSORIA")) {
            bd.updatePosApp(txtDocumentoFormaPagamento.getText().toString());
            txtDocumentoFormaPagamento.setText("");
        } else if (!bd.getContaBancariaFormaPagamento(fPag[0]).equalsIgnoreCase("0")) {
            bd.updateUltimoBoleto(txtDocumentoFormaPagamento.getText().toString());
            txtDocumentoFormaPagamento.setText("");
        }

        //
        listaFinanceiroCliente = bd.getFinanceiroCliente(prefs.getInt("id_venda_app", 1));
        adapter = new FinanceiroVendasAdapter(this, listaFinanceiroCliente, valoresCompra);
        rvFinanceiro.setAdapter(adapter);
        //
        String tif = classAuxiliar.maskMoney(new BigDecimal(bd.getValorTotalFinanceiro(String.valueOf(prefs.getInt("id_venda_app", 1)))));
        txtTotalItemFinanceiro.setText(tif);

        //
        //!txtTotalItemFinanceiro.getText().equals(txtTotalFinanceiro.getText()

        //
        String valorFinanceiro = String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiro.getText().toString()));
        String valorFinanceiroAdd = String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiro.getText().toString()));

        //SUBTRAIR O VALOR PELA QUANTIDADE
        String[] subtracao = {valorFinanceiro, valorFinanceiroAdd};
        String total = String.valueOf(classAuxiliar.subitrair(subtracao));

        txtValorFormaPagamento.setText(total);

        //
        if (comparar()) {

            bgTotal.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.erro));
            txtValorFormaPagamento.setText("0,00");
        } else {
            bgTotal.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.transparente));
        }


        // SELECT PIX
        /*if (fPag[0].equals("PIX")) {
            Intent i = new Intent(context, Pix.class);
            startActivity(i);
        }*/


        //
        txtDocumentoFormaPagamento.setText("");
        tilDocumento.setVisibility(View.VISIBLE);
        spFormasPagamentoCliente.setSelection(0);


        //ESCONDER O TECLADO
        // TODO Auto-generated method stub
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void atualizarDataVencimento(String data) {
        txtVencimentoFormaPagamento.setText(data);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        txtDocumentoFormaPagamento.setText("");
        String[] fPag = spFormasPagamentoCliente.getSelectedItem().toString().split(" _ ");
        String selectedItem = spFormasPagamentoCliente.getSelectedItem().toString(); // Captura o item selecionado no Spinner
         creditoPrefs.setFormaPagamentoPrazo(selectedItem);
        // Logando o item selecionado para visualização no Logcat
        Log.d("ItemSelecionado", "Item selecionado ENVIADO AO PREFS: " + selectedItem);

        // Verificar se a forma de pagamento selecionada é "A PRAZO"
        String[] detalhesPagamento = selectedItem.split(" _ ");



        /*if (fPag.length > 1 && "A PRAZO".equals(fPag[1])) {
            Log.d("FormaPagamentoSelecionada", "Forma de pagamento selecionada a prazo: " + selectedItem);

            // Capturar o valor da compra inserido no campo txtValorFormaPagamento
            String valorCompraAtual = txtValorFormaPagamento.getText().toString(); // Assumindo que o valor da compra está no campo txtValorFormaPagamento

            // Limpar a lista e adicionar o valor atual para garantir que só tenha o valor atual
            valoresCompra.add(valorCompraAtual);
            Log.d("ValorCompra", "Valor da compra: " + valorCompraAtual);
            Log.d("ValoresCompra", "Valores da compra: " + valoresCompra);
        } else {
            Log.d("FormaPagamentoSelecionada", "Forma de pagamento selecionada não é a prazo: " + selectedItem);
            // Limpar a lista de valores caso a forma de pagamento não seja a prazo
            valoresCompra.clear();
        }*/

    // SELECT PIX
        if (fPag[0].equals("PIX")) {
            runOnUiThread(() -> {

            });
        }
        if (fPag[1].equals("A PRAZO")) {

            runOnUiThread(() -> {
                tilDocumento.setVisibility(View.VISIBLE);
                tilVencimento.setVisibility(View.VISIBLE);

                posApp = bd.getPos();
                //if (fPag[0].equalsIgnoreCase("PROMISSORIA")) {
                if (fPag[0].contains("PROMISSORIA")) {
                    int uprom = Integer.parseInt(posApp.getUltpromissoria());
                    //if (posApp.getUltpromissoria().equalsIgnoreCase("0")) {
                    if (uprom == 0) {
                        //nDoc = posApp.getSerie() + "00000001";
                        int n = (Integer.parseInt(posApp.getSerie()) * 1000000) + 1;
                        nDoc = String.valueOf(n);
                    } else {
                        /*String[] soma = {posApp.getUltpromissoria(), "1"};
                        String[] totSoma = String.valueOf(classAuxiliar.somar(soma)).split("[.]");
                        nDoc = classAuxiliar.soNumeros(totSoma[0]);*/
                        nDoc = String.valueOf(Integer.parseInt(posApp.getUltpromissoria()) + 1);
                    }

                    txtDocumentoFormaPagamento.setText(nDoc);
                    txtDocumentoFormaPagamento.setEnabled(false);
                } else if (!bd.getContaBancariaFormaPagamento(fPag[0]).equalsIgnoreCase("0") &&
                        !bd.getContaBancariaFormaPagamento(fPag[0]).equalsIgnoreCase("")) {
                    //else if (fPag[0].equalsIgnoreCase("BOLETO")) {
                    int ubol = Integer.parseInt(posApp.getUltboleto());
                    //if (posApp.getUltboleto().equalsIgnoreCase("0")) {
                    if (ubol == 0) {
                        //nDoc = posApp.getSerie() + "00000001";
                        int n = (Integer.parseInt(posApp.getSerie_boleto()) * 1000000) + 1;
                        nDoc = String.valueOf(n);
                    } else {
                        nDoc = String.valueOf(Integer.parseInt(posApp.getUltboleto()) + 1);
                        //String[] soma = {posApp.getUltpromissoria(), "1"};
                        //String[] totSoma = String.valueOf(classAuxiliar.somar(soma)).split("[.]");
                        //nDoc = classAuxiliar.soNumeros(totSoma[0]);
                    }

                    txtDocumentoFormaPagamento.setText(nDoc);
                    txtDocumentoFormaPagamento.setEnabled(false);
                    tilNotaFiscal.setVisibility(View.VISIBLE);
                } else {
                    txtDocumentoFormaPagamento.setEnabled(true);
                }

                //int n = (serieBoleto * 100000000) + 1;

                //atualizarDataVencimento(classAuxiliar.dataFutura(bd.DiasPrazoCliente(fPag[0], codigo_cliente)));
                txtVencimentoFormaPagamento.setText(classAuxiliar.dataFutura(bd.DiasPrazoCliente(fPag[0], codigo_cliente)));

            });

            if (fPag[3].equals("1")) {

                runOnUiThread(() -> tilVencimento.setVisibility(View.VISIBLE));
            }

        } else {
            runOnUiThread(() -> {
                tilDocumento.setVisibility(View.VISIBLE);
                tilVencimento.setVisibility(View.GONE);
                txtDocumentoFormaPagamento.setEnabled(true);
                //
                tilNotaFiscal.setVisibility(View.GONE);
                txtNotaFiscal.setText("");
                //
                txtVencimentoFormaPagamento.setText(classAuxiliar.exibirDataAtual());

                Log.i("Fin", classAuxiliar.formatarData(classAuxiliar.soNumeros(txtVencimentoFormaPagamento.getText().toString())));
            });
        }

        runOnUiThread(() -> {
            if (bd.getCartaoTrue(fPag[0]).equalsIgnoreCase("1")) {
                bandPrazo.setVisibility(View.VISIBLE);

                //
                listaBandeiras = bd.getBandeiraFPg(fPag[0]);
                adapterSpBandeira = new ArrayAdapter(context, android.R.layout.simple_spinner_item, listaBandeiras);
                adapterSpBandeira.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spBandeira.setAdapter(adapterSpBandeira);
                //spBandeira.setOnItemSelectedListener(FinanceiroDaVenda.this);
                spBandeira.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        //Toast.makeText(context, spBandeira.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        //
                        listaParcelas = bd.getPrazoFPg(spBandeira.getSelectedItem().toString(), fPag[0]);
                        adapterSpParcela = new ArrayAdapter(context, android.R.layout.simple_spinner_item, listaParcelas);
                        adapterSpParcela.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spParcela.setAdapter(adapterSpParcela);
                        //spParcela.setOnItemSelectedListener(FinanceiroDaVenda.this);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            } else {
                listaBandeiras = null;
                listaParcelas = null;
                bandPrazo.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            EditText editText = editTextWeakReference.get();
            if (editText == null) return;
            String s = editable.toString();
            editText.removeTextChangedListener(this);
            String cleanString = s.replaceAll("[^0-9]", "");
            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            String formatted = NumberFormat.getCurrencyInstance().format(parsed);
            editText.setText(formatted);
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);
        }
    }

    //COMPARAR O VALOR DO FINANCEIRO COM O VALOR ADICIONADO
    private boolean comparar() {

        //
        BigDecimal valorFinanceiro = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalFinanceiro.getText().toString())));
        BigDecimal valorFinanceiroAdd = new BigDecimal(String.valueOf(classAuxiliar.converterValores(txtTotalItemFinanceiro.getText().toString())));

        if (valorFinanceiroAdd.compareTo(valorFinanceiro) == 1) {
            //
            if (valorFinanceiro.toString().equals(valorFinanceiroAdd.toString())) {

                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

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
        /*//
        Toast.makeText(FinanceiroDaVenda.this, "Venda Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(FinanceiroDaVenda.this, Principal2.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        super.finish();*/
        cancelarVenda();
    }

    private void cancelarVenda() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        builder.setMessage("Você Deseja Realmente Cancelar Esta Venda?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", (arg0, arg1) -> {
            //Toast.makeText(InformacoesVagas.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
            int i = bd.deleteVenda(prefs.getInt("id_venda_app", 0));
            if (i != 0) {
                //
                Toast.makeText(FinanceiroDaVenda.this, "Esta Venda foi Cancelada!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FinanceiroDaVenda.this, Principal2.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                super.finish();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", (arg0, arg1) -> {
            //Toast.makeText(InformacoesVagas.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();
    }


    /////////// ***********************************************


    public boolean raio(double latIni, double lonIni) {
        boolean result = false;

        posicaoInicial = new LatLng(latIni, lonIni);
        try {
            posicaiFinal = new LatLng(coordCliLat, coordCliLon);
        } catch (Exception ignored) {
            posicaiFinal = new LatLng(0.0, 0.0);
        }
        distance = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);
        double distanciaComparar = distance;

        //Log.e("LOG", "A Distancia é = " + (distance));
        Locale mL = new Locale("pt", "BR");
        //String.format(mL, "%4.3f%s", distance, unit);
        String unit = "m";
        if (distance >= 1000) {
            distance /= 1000;
            unit = "km";
        }
        Log.e("Distancia", "A Distancia é = " + String.format(mL, "%4.2f%s", distance, unit));
        Log.e("Distancia", "A Distancia é = " + distanciaComparar);
        //if (distance <= 0.1) {
        if (distanciaComparar <= 150 && unit.equalsIgnoreCase("m")) {
            result = true;
        } else {
            // Retirar depois da atualização
            //result = true;

            //
            if (verificarOnline.isOnline(context)) {
                String posCli = coordCliLat + ", " + coordCliLon;
                String posVen = coord_latitude + ", " + coord_longitude;
                String raio = String.format(mL, "%4.2f%s", distance, unit);
                String prec = precisao + "m";
                //
                enviarLog(posCli, posVen, raio, prec);
            }
            msg("Você parece estar a uns " + String.format(mL, "%4.2f%s", distance, unit) + " de onde o cliente está. Chegue mais perto para finalizar a venda!");
        }

        return result;
    }

    /**
     * P1: PEGA AS CORDENADAS
     * P2: VERIFICAR SE TEM INTERNET
     * P3: TEM INTERNET! VERIFICA SE O CLIENTE JÁ POSSUI AS CORDENADAS DA SUA CASA
     * P3.1: CLIENTE NÃO TEM COREDENAS! ATUALIZA O CADASTRO COM AS CORDENAS INFORMADA ANTERIORMENTE
     * P4: CALCULAR O RAIO DA CASA DO CLIENTE COM A POSIÇÃO DO ENTREGADOR
     */

    int verificacaoLocalidade = 0;

    // PEGAR AS CORDENADAS DO ENTREGADOR
    private void verifCordenadas() {

        //msg(String.valueOf(coord_latitude_pedido));
        // VERIFICA SE A ACTIVITY ESTÁ VISÍVEL
        if (VerificarActivityAtiva.isActivityVisible()) {
            //barra de progresso pontos
            dialog.show();


            String[] c = coord.getLatLon().split(",");
            coord_latitude = Double.parseDouble(c[0]);
            coord_longitude = Double.parseDouble(c[1]);

            precisao = coord.getPrecisao();

            //Log.i("POS", c[0] + ", " + c[1]);
            // VERIFICA SE AS CORDENADAS DO ENTREGADOR FORAM RECONHECIDAS
            if (coord_latitude != 0.0) {

                //msg("Peguei a latitude: " + coord_latitude + ", " + coord_longitude);
                verifClienteCordenada();
            }

            new Handler().postDelayed(() -> {

                // SE O TOTAL DE VERIFICAÇÃO FOR MAIR QUE 2 PARA DE CONSULTAR
                verificacaoLocalidade++;
                if (verificacaoLocalidade > 2) {
                    verificacaoLocalidade = 0;
                    Toast.makeText(context, "Não foi possível verificar suas cordenadas!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                // VERIFICA SE AS CORDENADAS DO ENTREGADOR FORAM RECONHECIDAS
                if (coord_latitude == 0.0) {
                    verifCordenadas();
                }

            }, 3000);
        }
    }


// Função para somar os valores do array valoresCompra
    private BigDecimal somarValoresCompra() {
        BigDecimal somaTotal = BigDecimal.ZERO; // Inicializa a variável somaTotal com zero

        // Percorre todos os valores do array valoresCompra
        for (String valor : valoresCompra) {
            try {
                // Converte o valor de String para BigDecimal e adiciona à somaTotal
                BigDecimal valorBigDecimal = new BigDecimal(valor);
                somaTotal = somaTotal.add(valorBigDecimal);
            } catch (NumberFormatException e) {
                Log.e("SomarValoresCompra", "Erro ao converter valor para BigDecimal: " + valor, e);
            }
        }

        return somaTotal; // Retorna a soma dos valores
    }


    // VERIFICAR AS CORDENADAS DO CLIENTE
    private void verifClienteCordenada() {

        //msg("Cordenadas do cliente: " + coordCliLat + ", " + coordCliLon);

        if (coordCliLat != 0.0) {
            verifRaio();
        }
    }

    //
    private void verifRaio() {
        if (raio(coord_latitude, coord_longitude)) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // FINALIZA O PEDIDO
            finalizarFinanceiroVenda();
        } else {
            //msg("Você parece não está próximo ao cliente! Tente novamente.");

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void msg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    private void isGPSEnabled() {
        if (!coord.isGPSEnabled()) {
            Log.i("principal", "GPS Desativado!");
        } else {
            isGPSPermisson();
        }
    }

    private void isGPSPermisson() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            // int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
    }

    private void finalizarFinanceiroVenda() {
        bd.updateFinalizarVenda(String.valueOf(prefs.getInt("id_venda_app", 1)));

        msg("Venda Finalizada Com Sucesso.");

        /*Intent intent = new Intent(FinanceiroDaVenda.this, Principal2.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        super.finish();*/

        _verificarFPgVenda();
    }

    private void enviarLog(String posCli, String posVen, String raio, String precisao) {
        //
        final ILogin login = ILogin.retrofit.create(ILogin.class);

        //
        final Call<Conta> call = login.enviarLog(
                posCli,
                posVen,
                raio,
                precisao,
                "Marca: " + Build.BRAND + ", Modelo: " + Build.MODEL + ", SDK: " + Build.VERSION.SDK_INT,
                prefs.getString("serial", "")
        );

        call.enqueue(new Callback<Conta>() {
            @Override
            public void onResponse(Call<Conta> call, Response<Conta> response) {

                //
                final Conta sincronizacao = response.body();
                if (sincronizacao != null) {

                    //
                    runOnUiThread(() -> {
                    });
                }
            }

            @Override
            public void onFailure(Call<Conta> call, Throwable t) {
            }
        });
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(FinanceiroDaVenda.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    //captura e armazena da list somente as formas de pagamento A_PRAZO
    public void logFormasPagamentoPrazo() {
        formasPagamentoPrazo.clear(); // Limpa a lista antes de adicionar novos valores
        if (listaFormasPagamentoCliente != null && !listaFormasPagamentoCliente.isEmpty()) {
            for (String formaPagamento : listaFormasPagamentoCliente) {
                String[] detalhesPagamento = formaPagamento.split(" _ ");
                if (detalhesPagamento.length > 1 && "A PRAZO".equals(detalhesPagamento[1])) {
                    formasPagamentoPrazo.add(formaPagamento); // Adiciona à lista
                    Log.d("FormasPagamentoPrazo", "Forma de pagamento a prazo: " + formaPagamento);
                }
            }
        } else {
            Log.d("FormasPagamentoPrazo", "Nenhuma forma de pagamento encontrada.");
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // Todas as alterações necessárias foram feitas
                        coord.getLocation();
                        //verifCordenadas();
                        break;
                    case Activity.RESULT_CANCELED:
                        // O usuário cancelou o dialog, não fazendo as alterações requeridas
                        Toast.makeText(FinanceiroDaVenda.this, "Operação cancelada!", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(context, "Siac: " + data.getStringExtra("authorizationCode"), Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(context, Objects.requireNonNull(data).getStringExtra("authorizationCode"), Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // Todas as alterações necessárias foram feitas
                     ...
                        break;
                    case Activity.RESULT_CANCELED:
                        // O usuário cancelou o dialog, não fazendo as alterações requeridas
                     ...
                        break;
                    default:
                        break;
                }
                break;
        }
    }*/
}
