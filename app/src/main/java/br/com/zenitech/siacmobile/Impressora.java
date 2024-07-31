package br.com.zenitech.siacmobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.datecs.api.BuildInfo;
import com.datecs.api.card.FinancialCard;
import com.datecs.api.emsr.EMSR;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.PrinterInformation;
import com.datecs.api.printer.ProtocolAdapter;
import com.datecs.api.rfid.ContactlessCard;
import com.datecs.api.rfid.FeliCaCard;
import com.datecs.api.rfid.ISO14443Card;
import com.datecs.api.rfid.ISO15693Card;
import com.datecs.api.rfid.RC663;
import com.datecs.api.rfid.STSRICard;
import com.datecs.api.universalreader.UniversalReader;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import br.com.zenitech.siacmobile.controller.PrintViewHelper;
import br.com.zenitech.siacmobile.domains.Clientes;
import br.com.zenitech.siacmobile.domains.ContasBancarias;
import br.com.zenitech.siacmobile.domains.FinanceiroReceberDomain;
import br.com.zenitech.siacmobile.domains.UnidadesDomain;
import br.com.zenitech.siacmobile.domains.VendasPedidosDomain;
import br.com.zenitech.siacmobile.network.PrinterServer;
import br.com.zenitech.siacmobile.util.HexUtil;

import static br.com.zenitech.siacmobile.DataPorExtenso.dataPorExtenso;
import static br.com.zenitech.siacmobile.NumeroPorExtenso.valorPorExtenso;
import static stone.utils.GlobalInformations.bluetoothAdapter;


public class Impressora extends AppCompatActivity {

    private static final String LOG_TAG = "Impressora";
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    public static boolean liberaImpressao;

    // Pedido para obter o dispositivo bluetooth
    private static final int REQUEST_GET_DEVICE = 0;

    // Pedido para obter o dispositivo bluetooth
    private static final int DEFAULT_NETWORK_PORT = 9100;

    // Interface, usado para invocar a operação da impressora assíncrona.
    private interface PrinterRunnable {
        void run(ProgressDialog dialog, Printer printer) throws IOException;
    }

    // Variáveis-membro
    private ProtocolAdapter mProtocolAdapter;
    private ProtocolAdapter.Channel mPrinterChannel;
    private ProtocolAdapter.Channel mUniversalChannel;
    private Printer mPrinter;
    private EMSR mEMSR;
    private PrinterServer mPrinterServer;
    private BluetoothSocket mBtSocket;
    private Socket mNetSocket;
    private RC663 mRC663;

    //
    private DatabaseHelper bd;
    private ClassAuxiliar cAux;

    //DADOS PARA IMPRESSÃO
    String id_cliente, cliente, vencimento, numero, tel_contato, valor, tipoImpressao, cpfcnpj, endereco, nota_fiscal, strFormPags, nContaBanco;

    TextView total;
    public TextView imprimindo;

    public static String[] linhaProduto;

    ArrayList<Unidades> elementosUnidade;
    UnidadesDomain unidade;

    //ArrayList<PosApp> elementosPos;
    //PosApp posApp;

    //
    ArrayList<VendasPedidosDomain> elementosPedidos;
    VendasPedidosDomain pedidos;

    ArrayList<FinanceiroReceberDomain> elementosRecebidos;
    FinanceiroReceberDomain recebidos;

    /*ArrayList<ItensPedidos> elementosItens;
    ItensPedidos itensPedidos;

    // NF-e
    ArrayList<PedidosNFE> elementosPedidosNFE;
    PedidosNFE pedidosNFE;*/

    Context context;
    ImageView qrcode;

    String enderecoBlt = "";
    String tamFont = "";
    SharedPreferences prefs;

    //
    String root = Environment.getExternalStorageDirectory().getAbsolutePath();
    File myDir = new File(root + "/Siac_Mobile");

    //
    String dataHoraCan, codAutCan;

    //
    private boolean impComPagViaCliente = false;

    LinearLayout impressora1, impressora2;
    Bitmap bitmap1, bitmap2;
    Bitmap bp = null;
    ImageView imgCodBarraBoleto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressora);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Solicitar permissões Bluetooth
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
            }, REQUEST_BLUETOOTH_PERMISSIONS);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_BLUETOOTH_PERMISSIONS);
        }

        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);

        if (prefs.getString("tamPapelImpressora", "").equalsIgnoreCase("58mm")) {
            tamFont = "{s}";
        }

        liberaImpressao = false;

        //
        cAux = new ClassAuxiliar();
        context = this;
        bd = new DatabaseHelper(this);
        unidade = bd.getUnidade();

        imprimindo = findViewById(R.id.imprimindo);
        total = findViewById(R.id.total);
        qrcode = findViewById(R.id.qrcode);

        // Show Android device information and API version.
        final TextView txtVersion = findViewById(R.id.txt_version);
        String txt = Build.MANUFACTURER + " " + Build.MODEL + ", Datecs API " + BuildInfo.VERSION;
        txtVersion.setText(txt);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {

                tipoImpressao = params.getString("imprimir");
                cliente = params.getString("razao_social");
                valor = params.getString("valor");
                id_cliente = params.getString("id_cliente");
                vencimento = params.getString("vencimento");
                numero = params.getString("numero");
                tel_contato = params.getString("tel_contato");
                cpfcnpj = params.getString("cpfcnpj");
                endereco = params.getString("endereco");
                nota_fiscal = params.getString("nota_fiscal");
                nContaBanco = params.getString("nContaBanco");

            } else {
                Toast.makeText(context, "Envie algo para imprimir!", Toast.LENGTH_LONG).show();
            }
        }

        ativarBluetooth();

        if (!prefs.getString("enderecoBlt", "").equalsIgnoreCase("")) {
            establishBluetoothConnection(prefs.getString("enderecoBlt", ""));
        } else {
            waitForConnection();
        }
        //// Remover a verificação inicial e tentar sempre estabelecer conexão
       // establishBluetoothConnection(prefs.getString("enderecoBlt", ""));


        // BOLETO
        if (tipoImpressao.equalsIgnoreCase("Boleto")) {


            //String valFinanceiro;
            String[] a = {String.valueOf(cAux.converterValores(valor)), bd.contasBancarias().getTaxa_boleto()};
            //valor = cAux.maskMoney(cAux.converterValores(String.valueOf(cAux.somar(a))));
            valor = cAux.maskMoney(cAux.somar(a));

            // PEGA OS DADOS DA CONTA BANCARIA
            ContasBancarias conta = bd.ContaBancaria(nContaBanco);

            // PEGA OS DADOS DO CLIENTE
            Clientes cliente = bd.cliente(id_cliente);

            //
            //String numCodBarraBB = cAux.numCodBarraBB(valor, cAux.exibirData(vencimento), numero, bd, conta);
            //String numlinhaDigitavel = cAux.numlinhaDigitavel(numCodBarraBB);


            // REFERENCIAS LOGO DO BANCO
            ImageView logoBoleto1 = findViewById(R.id.logoBanco1);
            ImageView logoBoleto2 = findViewById(R.id.logoBanco2);
            // IDS TEXT BOLETO
            TextView txtCodBancoMoedaBoleto = findViewById(R.id.txtCodBancoMoedaBoleto);
            // IDS TEXT BOLETO - CANHOTO
            TextView txtCodBancoMoedaCanhoto = findViewById(R.id.txtCodBancoMoedaCanhoto);

            String numCodBarra = "";
            //
            String numlinhaDigitavel = "";

            // BANCO DO BRASIL
            if (conta.getBanco_conta().equalsIgnoreCase("001")) {
                //
                numCodBarra = cAux.numCodBarraBB(valor, cAux.exibirData(vencimento), numero, bd, conta);
                //
                numlinhaDigitavel = cAux.numlinhaDigitavel(numCodBarra);

                // LOGO
                Drawable logobb = getResources().getDrawable(R.drawable.logo_bb);
                logoBoleto1.setImageDrawable(logobb);
                logoBoleto2.setImageDrawable(logobb);
                // SET BOLETO
                txtCodBancoMoedaBoleto.setText("001-9");
                // SET BOLETO - CANHOTO
                txtCodBancoMoedaCanhoto.setText("001-9");
            }
            // BRADESCO
            else if (conta.getBanco_conta().equalsIgnoreCase("237")) {

                //carteira/nosso nu/ digito ver nosso numero
                // $nnum = formata_numero($dadosboleto["carteira"],2,0).formata_numero($dadosboleto["nosso_numero"],11,0);
                numCodBarra = cAux.numCodBarraBradesco(valor, cAux.exibirData(vencimento), numero, bd, conta);
                //
                numlinhaDigitavel = cAux.numlinhaDigitavelBradesco(numCodBarra);

                // LOGO
                Drawable logobb = getResources().getDrawable(R.drawable.logobradesco);
                logoBoleto1.setImageDrawable(logobb);
                logoBoleto2.setImageDrawable(logobb);
                // SET BOLETO
                txtCodBancoMoedaBoleto.setText("237-9");
                // SET BOLETO - CANHOTO
                txtCodBancoMoedaCanhoto.setText("237-9");
            }

            //
            imgCodBarraBoleto = findViewById(R.id.imgCodBarraBoleto);

            // IDS TEXT BOLETO
            //TextView txtCodBancoMoedaBoleto = findViewById(R.id.txtCodBancoMoedaBoleto);
            TextView txtValorBoleto = findViewById(R.id.txtValorBoleto);
            TextView txtLinhaDigitavelBoleto = findViewById(R.id.txtLinhaDigitavelBoleto);
            TextView txtLinhaDigitavelBoletoBaixo = findViewById(R.id.txtLinhaDigitavelBoletoBaixo);
            TextView txtBeneficiarioBoleto = findViewById(R.id.txtBeneficiarioBoleto);
            TextView txtVencimentoBoleto = findViewById(R.id.txtVencimentoBoleto);
            TextView txtAgenciaBeneficiarioBoleto = findViewById(R.id.txtAgenciaBeneficiarioBoleto);
            TextView txtNossoNumeroBoleto = findViewById(R.id.txtNossoNumeroBoleto);
            TextView txtDataDocumentoBoleto = findViewById(R.id.txtDataDocumentoBoleto);
            TextView txtInstrucoesBoleto = findViewById(R.id.txtInstrucoesBoleto);
            TextView txtNumeroDocumentoBoleto = findViewById(R.id.txtNumeroDocumentoBoleto);
            TextView txtCarteiraBoleto = findViewById(R.id.txtCarteiraBoleto);
            TextView txtEspecieBoleto = findViewById(R.id.txtEspecieBoleto);
            TextView txtQuantidadeBoleto = findViewById(R.id.txtQuantidadeBoleto);
            TextView txtValorBoletoMeio = findViewById(R.id.txtValorBoletoMeio);
            TextView txtPagadorBoleto = findViewById(R.id.txtPagadorBoleto);
            TextView txtEnderecoPagadorBoleto = findViewById(R.id.txtEnderecoPagadorBoleto);
            TextView txtSacadorBoleto = findViewById(R.id.txtSacadorBoleto);
            TextView txtDataProcessamentoBoleto = findViewById(R.id.txtDataProcessamentoBoleto);
            TextView txtValorCobradoBoleto = findViewById(R.id.txtValorCobradoBoleto);
            // IDS TEXT BOLETO - CANHOTO
            //TextView txtCodBancoMoedaCanhoto = findViewById(R.id.txtCodBancoMoedaCanhoto);
            TextView txtLinhaDigitavelCanhoto = findViewById(R.id.txtLinhaDigitavelCanhoto);
            TextView txtValorCanhotoBoleto = findViewById(R.id.txtValorCanhotoBoleto);
            TextView txtBeneficiarioBoletoCanhoto = findViewById(R.id.txtBeneficiarioBoletoCanhoto);
            TextView txtCpfCeiCnpjBoletoCanhoto = findViewById(R.id.txtCpfCeiCnpjBoletoCanhoto);
            TextView txtContratoBoletoCanhoto = findViewById(R.id.txtContratoBoletoCanhoto);
            TextView txtAgenciaBeneficiarioBoletoCanhoto = findViewById(R.id.txtAgenciaBeneficiarioBoletoCanhoto);
            TextView txtNossoNumeroBoletoCanhoto = findViewById(R.id.txtNossoNumeroBoletoCanhoto);
            TextView txtEspecieBoletoCanhoto = findViewById(R.id.txtEspecieBoletoCanhoto);
            TextView txtQuantidadeBoletoCanhoto = findViewById(R.id.txtQuantidadeBoletoCanhoto);
            TextView txtNumeroDocumentoBoletoCanhoto = findViewById(R.id.txtNumeroDocumentoBoletoCanhoto);
            TextView txtVencimentoBoletoCanhoto = findViewById(R.id.txtVencimentoBoletoCanhoto);
            TextView txtValorCobradoBoletoCanhoto = findViewById(R.id.txtValorCobradoBoletoCanhoto);
            TextView txtPagadorBoletoCanhoto = findViewById(R.id.txtPagadorBoletoCanhoto);
            TextView txtEnderecoPagadorBoletoCanhoto = findViewById(R.id.txtEnderecoPagadorBoletoCanhoto);

            // SET BOLETO
            //txtCodBancoMoedaBoleto.setText("001-9");
            txtValorBoleto.setText(valor);
            txtLinhaDigitavelBoleto.setText(numlinhaDigitavel);
            txtLinhaDigitavelBoletoBaixo.setText(cAux.soNumeros(numCodBarra));//numlinhaDigitavel
            txtBeneficiarioBoleto.setText(conta.getCedente());
            txtVencimentoBoleto.setText(cAux.exibirData(vencimento));
            txtAgenciaBeneficiarioBoleto.setText(String.format("%s/%s-%s", conta.getAgencia(), conta.getConta(), conta.getDv_conta()));
            txtNossoNumeroBoleto.setText(cAux.nossoNumero(conta.getConvenio(), numero));
            txtDataDocumentoBoleto.setText(cAux.exibirDataAtual());
            txtInstrucoesBoleto.setText(String.format("%s%s", conta.getInstrucoes(), !nota_fiscal.equalsIgnoreCase("") ? "\nREFERENTE A NOTA FISCAL: " + nota_fiscal : ""));
            txtNumeroDocumentoBoleto.setText(numero);//cAux.nossoNumero(conta.getConvenio(), 2));
            txtCarteiraBoleto.setText(conta.getCarteira());
            txtEspecieBoleto.setText("9");
            txtQuantidadeBoleto.setText("1");
            txtValorBoletoMeio.setText(valor);
            txtPagadorBoleto.setText(String.format("%s - %s", cliente.getNome_cliente(), cliente.getCpfcnpj()));
            txtEnderecoPagadorBoleto.setText(cliente.getEndereco());
            txtSacadorBoleto.setText("");
            txtDataProcessamentoBoleto.setText(cAux.exibirDataAtual());
            txtValorCobradoBoleto.setText(valor);
            // SET BOLETO - CANHOTO
            //txtCodBancoMoedaCanhoto.setText("001-9");
            txtValorCanhotoBoleto.setText(valor);
            txtLinhaDigitavelCanhoto.setText(numlinhaDigitavel);
            txtBeneficiarioBoletoCanhoto.setText(conta.getCedente());
            txtCpfCeiCnpjBoletoCanhoto.setText(conta.getCpf_cnpj());
            txtContratoBoletoCanhoto.setText("");
            txtAgenciaBeneficiarioBoletoCanhoto.setText(String.format("%s/%s-%s", conta.getAgencia(), conta.getConta(), conta.getDv_conta()));
            txtNossoNumeroBoletoCanhoto.setText(cAux.nossoNumero(conta.getConvenio(), numero));
            txtEspecieBoletoCanhoto.setText("9");
            txtQuantidadeBoletoCanhoto.setText("1");
            txtNumeroDocumentoBoletoCanhoto.setText(numero);//cAux.nossoNumero(conta.getConvenio(), 2));
            txtVencimentoBoletoCanhoto.setText(cAux.exibirData(vencimento));
            txtValorCobradoBoletoCanhoto.setText(valor);
            txtPagadorBoletoCanhoto.setText(String.format("%s - %s", cliente.getNome_cliente(), cliente.getCpfcnpj()));
            txtEnderecoPagadorBoletoCanhoto.setText(cliente.getEndereco());

            //
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(numCodBarra, BarcodeFormat.ITF, 1000, 100);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bp = barcodeEncoder.createBitmap(bitMatrix);
                SaveImage(bp);

                //ImageView imgCodBarraBoleto = findViewById(R.id.imgB);
                imgCodBarraBoleto.setImageBitmap(bp);
                //imgCodBarraBoleto.setScaleType(ImageView.ScaleType.FIT_START);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            impressora1 = findViewById(R.id.printBoleto);
            bitmap1 = new PrintViewHelper().createBitmapFromView90(impressora1, 576, 146); // height: 146
            impressora2 = findViewById(R.id.printBoletoCanhoto);
            bitmap2 = new PrintViewHelper().createBitmapFromView90(impressora2, 437, 146); // height: 146
            //ByteArrayOutputStream out = new ByteArrayOutputStream();
            //bitmap1.compress(Bitmap.CompressFormat.JPEG,100,out);
            //SaveImage(bitmap1);
        }

        //
        tempo(1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    // Se alguma permissão não for concedida, mostrar mensagem e sair
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Permissões Bluetooth necessárias para imprimir o relatório.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                    return;
                }
            }
            // Permissões concedidas, iniciar a impressora
            ativarBluetooth();
        }
    }

    public String getNumPorExtenso(double valor) {
        return valorPorExtenso(valor);
    }

    public String getDataPorExtenso(String data) {
        return dataPorExtenso(data);
    }

    public void tempo(int tempo) {

        //
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //Log.i(LOG_TAG, "Relatório");

            //
            if (liberaImpressao) {

                if (tipoImpressao.equals("Promissoria")) {
                    //Log.i(LOG_TAG, "Relatório");

                    //Imprimir relatório de notas fiscais eletronica

                    if (prefs.getString("tamPapelImpressora", "").equalsIgnoreCase("58mm")) {
                        //printPromissoria58mm();
                    } else {
                        printPromissoria();
                    }
                    //printPage();
                } else if (tipoImpressao.equals("relatorio")) {
                    printRelatorioNFCE58mm();
                } else if (tipoImpressao.equals("relatorioBaixa")) {
                    printRelatorioBaixas58mm();
                } else if (tipoImpressao.equals("Boleto")) {
                    printBoleto();
                }

                liberaImpressao = false;
            } else {
                //
                tempo(4000);
            }
        }, tempo);
    }

    private void ativarBluetooth() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
            }, REQUEST_BLUETOOTH_PERMISSIONS);
            return;
        }
        new AtivarDesativarBluetooth().enableBT(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_DEVICE) {
            if (resultCode == DeviceListActivity.RESULT_OK) {
                String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // address = "192.168.11.136:9100";
                if (BluetoothAdapter.checkBluetoothAddress(address)) {
                    Log.d(LOG_TAG, "establishBluetoothConnection(" + address + ")");
                    establishBluetoothConnection(address); //// Modificado para tentar sempre estabelecer uma nova conexão
                } else {
                    Log.d(LOG_TAG, "establishNetworkConnection(" + address + ")");
                    establishNetworkConnection(address);
                }
            } else {
                finish();
            }
        }
    }

    private void toast(final String text) {
        Log.d(LOG_TAG, text);

        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }

    private void error(final String text) {
        Log.w(LOG_TAG, text);

        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show());
    }

    private void dialog(final int iconResId, final String title, final String msg) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Impressora.this);
            builder.setIcon(iconResId);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(android.R.string.ok,
                    (dialog, which) -> dialog.dismiss());

            AlertDialog dlg = builder.create();
            dlg.show();
        });
    }

    private void status(final String text) {
        runOnUiThread(() -> {
            if (text != null) {
                findViewById(R.id.panel_status).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.txt_status)).setText(text);
            } else {
                findViewById(R.id.panel_status).setVisibility(View.INVISIBLE);
            }
        });
    }

    private void runTask(final PrinterRunnable r, final int msgResId) {
        final ProgressDialog dialog = new ProgressDialog(Impressora.this);
        dialog.setTitle(getString(R.string.title_please_wait));
        dialog.setMessage(getString(msgResId));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Thread t = new Thread(() -> {
            try {
                r.run(dialog, mPrinter);
            } catch (IOException e) {
                e.printStackTrace();
                error("I/O error occurs: " + e.getMessage());

                Log.d(LOG_TAG, e.getMessage(), e);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, e.getMessage(), e);
                error("Critical error occurs: " + e.getMessage());
                //dialog.dismiss();
                finish();
            } finally {
                dialog.dismiss();
            }
        });
        t.start();
    }

    protected void initPrinter(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        Log.d(LOG_TAG, "Initialize printer... RASTREANDO TOASTS");

        // Here you can enable various debug information
        //ProtocolAdapter.setDebug(true);
        Printer.setDebug(true);
        EMSR.setDebug(true);

        // Check if printer is into protocol mode. Ones the object is created it can not be released
        // without closing base streams.
        mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (mProtocolAdapter.isProtocolEnabled()) {
            Log.d(LOG_TAG, "Modo de protocolo está habilitado ANTES DO TOAST");

            // Into protocol mode we can callbacks to receive printer notifications
            mProtocolAdapter.setPrinterListener(new ProtocolAdapter.PrinterListener() {
                @Override
                public void onThermalHeadStateChanged(boolean overheated) {
                    if (overheated) {
                        Log.d(LOG_TAG, "Estado do cabeçote térmico mudou: " + (overheated ? "Superaquecido" : "Normal"));
                        status("OVERHEATED");
                    } else {
                        status(null);
                    }
                }

                @Override
                public void onPaperStateChanged(boolean hasPaper) {
                    if (hasPaper) {
                        Log.d(LOG_TAG, "Estado do papel mudou: " + (hasPaper ? "Sem papel" : "Com papel"));
                        status("PAPER OUT");
                    } else {
                        status(null);
                    }
                }

                @Override
                public void onBatteryStateChanged(boolean lowBattery) {
                    if (lowBattery) {
                        Log.d(LOG_TAG, "Estado da bateria mudou: " + (lowBattery ? "Bateria fraca" : "Bateria ok"));
                        status("LOW BATTERY");
                    } else {
                        status(null);
                    }
                }
            });

            mProtocolAdapter.setBarcodeListener(() -> {
                Log.d(LOG_TAG, "On read barcode");
                runOnUiThread(() -> readBarcode(0));
            });

            mProtocolAdapter.setCardListener(encrypted -> {
                Log.d(LOG_TAG, "On read card(entrypted=" + encrypted + ")");

                if (encrypted) {
                    runOnUiThread(this::readCardEncrypted);
                } else {
                    runOnUiThread(this::readCard);
                }
            });

            // Get printer instance
            mPrinterChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
            mPrinter = new Printer(mPrinterChannel.getInputStream(), mPrinterChannel.getOutputStream());

            // Check if printer has encrypted magnetic head
            ProtocolAdapter.Channel emsrChannel = mProtocolAdapter
                    .getChannel(ProtocolAdapter.CHANNEL_EMSR);
            try {
                // Close channel silently if it is already opened.
                try {
                    emsrChannel.close();
                } catch (IOException ignored) {
                }

                // Try to open EMSR channel. If method failed, then probably EMSR is not supported
                // on this device.
                emsrChannel.open();

                mEMSR = new EMSR(emsrChannel.getInputStream(), emsrChannel.getOutputStream());
                EMSR.EMSRKeyInformation keyInfo = mEMSR.getKeyInformation(EMSR.KEY_AES_DATA_ENCRYPTION);
                if (!keyInfo.tampered && keyInfo.version == 0) {
                    Log.d(LOG_TAG, "Missing encryption key");
                    // If key version is zero we can load a new key in plain mode.
                    byte[] keyData = CryptographyHelper.createKeyExchangeBlock(0xFF,
                            EMSR.KEY_AES_DATA_ENCRYPTION, 1, CryptographyHelper.AES_DATA_KEY_BYTES,
                            null);
                    mEMSR.loadKey(keyData);
                }
                mEMSR.setEncryptionType(EMSR.ENCRYPTION_TYPE_AES256);
                mEMSR.enable();
                Log.d(LOG_TAG, "Encrypted magnetic stripe reader is available");
            } catch (IOException e) {
                if (mEMSR != null) {
                    mEMSR.close();
                    mEMSR = null;
                }
            }

            // Check if printer has encrypted magnetic head
            ProtocolAdapter.Channel rfidChannel = mProtocolAdapter
                    .getChannel(ProtocolAdapter.CHANNEL_RFID);

            try {
                // Close channel silently if it is already opened.
                try {
                    rfidChannel.close();
                } catch (IOException ignored) {
                }

                // Try to open RFID channel. If method failed, then probably RFID is not supported
                // on this device.
                rfidChannel.open();

                mRC663 = new RC663(rfidChannel.getInputStream(), rfidChannel.getOutputStream());
                mRC663.setCardListener(this::processContactlessCard);
                mRC663.enable();
                Log.d(LOG_TAG, "RC663 o leitor está disponível");
            } catch (IOException e) {
                if (mRC663 != null) {
                    mRC663.close();
                    mRC663 = null;
                }
            }

            // Check if printer has encrypted magnetic head
            mUniversalChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_UNIVERSAL_READER);
            new UniversalReader(mUniversalChannel.getInputStream(), mUniversalChannel.getOutputStream());

        } else {
            Log.d(LOG_TAG, "O modo de protocolo está desativado");

            // Protocol mode it not enables, so we should use the row streams.
            mPrinter = new Printer(mProtocolAdapter.getRawInputStream(),
                    mProtocolAdapter.getRawOutputStream());
        }

        mPrinter.setConnectionListener(() -> {
            Log.d(LOG_TAG, "A impressora está desconectada");
            toast("A impressora está desconectada");

            runOnUiThread(() -> {
                if (!isFinishing()) {
                    waitForConnection();
                }
            });
        });


    }

    private synchronized void waitForConnection() {
        //status(null);

        //closeActiveConnection();

        // Show dialog to select a Bluetooth device.
        startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_GET_DEVICE);

        // Start server to listen for network connection.
        try {
            mPrinterServer = new PrinterServer(socket -> {
                Log.d(LOG_TAG, "Aceitar conexão de "
                        + socket.getRemoteSocketAddress().toString());

                // Close Bluetooth selection dialog
                finishActivity(REQUEST_GET_DEVICE);

                mNetSocket = socket;
                try {
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();
                    initPrinter(in, out);
                } catch (IOException e) {
                    e.printStackTrace();
                    error("Falha na inicialização: " + e.getMessage());
                    waitForConnection();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void establishBluetoothConnection(final String address) {
        //// Checar se o endereço é vazio, se sim, chamar waitForConnection
       /* if (address.isEmpty()) {
            waitForConnection();
            return;
        }*/
        // Fechar qualquer conexão existente antes de tentar uma nova
        bluetoothAdapter.cancelDiscovery();



        closeBluetoothConnection();


        final ProgressDialog dialog = new ProgressDialog(Impressora.this);
        dialog.setTitle(getString(R.string.title_please_wait));
        dialog.setMessage(getString(R.string.msg_connecting));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        closeActiveConnection();

        closePrinterServer();


        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }


        final Thread t = new Thread(() -> {
            Log.d(LOG_TAG, "BluetoothConnection - Conectando à " + address + "...");

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            btAdapter.cancelDiscovery();

            try {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);

                InputStream in;
                OutputStream out;

                try {
                    /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }*/
                    BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();

                    mBtSocket = btSocket;
                    in = mBtSocket.getInputStream();
                    out = mBtSocket.getOutputStream();
                    Log.d(LOG_TAG, "Conexão Bluetooth estabelecida com sucesso");
                } catch (IOException e) {

                    error("Falhou ao conectar: " + e.getMessage());
                    waitForConnection();
                    return;
                }

                try {
                    initPrinter(in, out);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Falha na inicialização: " + e.getMessage(), e); // Log detalhado
                    error("Falha na inicialização: " + e.getMessage());
                    return;
                }

                if (in != null && out != null) {

                    liberaImpressao = true;
                    enderecoBlt = address;
                }

            } finally {
                dialog.dismiss();
            }
        });
        t.start();
    }

    private void establishNetworkConnection(final String address) {
        closePrinterServer();

        final ProgressDialog dialog = new ProgressDialog(Impressora.this);
        dialog.setTitle(getString(R.string.title_please_wait));
        dialog.setMessage(getString(R.string.msg_connecting));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        closePrinterServer();

        final Thread t = new Thread(() -> {
            Log.d(LOG_TAG, "NetworkConnection - Conectando à " + address + "...");
            try {
                Socket s;
                try {
                    String[] url = address.split(":");
                    int port = DEFAULT_NETWORK_PORT;

                    try {
                        if (url.length > 1) {
                            port = Integer.parseInt(url[1]);
                        }
                    } catch (NumberFormatException e) {
                        Log.i(LOG_TAG, Objects.requireNonNull(e.getMessage()), e);
                    }

                    s = new Socket(url[0], port);
                    s.setKeepAlive(true);
                    s.setTcpNoDelay(true);
                } catch (UnknownHostException e) {
                    error("Falhou ao conectar: " + e.getMessage());
                    waitForConnection();
                    return;
                } catch (IOException e) {
                    error("Falhou ao conectar: " + e.getMessage());
                    waitForConnection();
                    return;
                }

                InputStream in;
                OutputStream out;

                try {
                    mNetSocket = s;
                    in = mNetSocket.getInputStream();
                    out = mNetSocket.getOutputStream();
                } catch (IOException e) {
                    error("Falhou ao conectar: " + e.getMessage());
                    waitForConnection();
                    return;
                }

                try {
                    initPrinter(in, out);
                } catch (IOException e) {
                    error("Falha na inicialização: " + e.getMessage());
                    return;
                }


                if (s != null && in != null && out != null) {

                    liberaImpressao = true;
                }
            } finally {
                dialog.dismiss();
            }
        });
        t.start();
    }

    private synchronized void closePrinterConnection() {
        if (mRC663 != null) {
            try {
                mRC663.disable();
            } catch (IOException e) {
                Log.i(LOG_TAG, e.getMessage());
            }


            mRC663.close();
        }

        if (mEMSR != null) {
            mEMSR.close();
        }

        if (mPrinter != null) {
            mPrinter.close();
        }

        if (mProtocolAdapter != null) {
            mProtocolAdapter.close();
        }
    }



     synchronized void closeBluetoothConnection() {
        // Close Bluetooth connection
        BluetoothSocket s = mBtSocket;
        mBtSocket = null;
        if (s != null) {
            Log.d(LOG_TAG, "Close Bluetooth socket");
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void closeNetworkConnection() {
        // Close network connection
        Socket s = mNetSocket;
        mNetSocket = null;
        if (s != null) {
            Log.d(LOG_TAG, "Close Network socket");
            try {
                s.shutdownInput();
                s.shutdownOutput();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void closePrinterServer() {
        closeNetworkConnection();

        // Fechar servidor de rede
        PrinterServer ps = mPrinterServer;
        mPrinterServer = null;
        if (ps != null) {
            Log.d(LOG_TAG, "Fechar servidor de rede");
            try {
                ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void closeActiveConnection() {
        closePrinterConnection();
        closeBluetoothConnection();
        closeNetworkConnection();
        closePrinterServer();
    }

    private void readCard() {
        Log.d(LOG_TAG, "Read card");

        runTask((dialog, printer) -> {
            PrinterInformation pi = printer.getInformation();
            String[] tracks;
            FinancialCard card = null;
            Printer.setDebug(true);
            if (pi.getName().startsWith("CMP-10")) {
                // The printer CMP-10 can read only two tracks at once.
                tracks = printer.readCard(true, true, false, 15000);
            } else {
                tracks = printer.readCard(true, true, true, 15000);
            }

            if (tracks != null) {
                StringBuffer textBuffer = new StringBuffer();

                if (tracks[0] == null && tracks[1] == null && tracks[2] == null) {
                    textBuffer.append(getString(R.string.no_card_read));
                } else {
                    if (tracks[0] != null) {
                        card = new FinancialCard(tracks[0]);
                    } else if (tracks[1] != null) {
                        card = new FinancialCard(tracks[1]);
                    }

                    if (card != null) {
                        textBuffer.append(getString(R.string.card_no) + ": " + card.getNumber());
                        textBuffer.append("\n");
                        textBuffer.append(getString(R.string.holder) + ": " + card.getName());
                        textBuffer.append("\n");
                        textBuffer.append(getString(R.string.exp_date)
                                + ": "
                                + String.format("%02d/%02d", card.getExpiryMonth(),
                                card.getExpiryYear()));
                        textBuffer.append("\n");
                    }

                    if (tracks[0] != null) {
                        textBuffer.append("\n");
                        textBuffer.append(tracks[0]);

                    }
                    if (tracks[1] != null) {
                        textBuffer.append("\n");
                        textBuffer.append(tracks[1]);
                    }
                    if (tracks[2] != null) {
                        textBuffer.append("\n");
                        textBuffer.append(tracks[2]);
                    }
                }

                dialog(R.drawable.ic_card, getString(R.string.card_info), textBuffer.toString());
            }
        }, R.string.msg_reading_magstripe);
    }

    private void readCardEncrypted() {
        Log.d(LOG_TAG, "Read card encrypted");

        runTask((dialog, printer) -> {
            byte[] buffer = mEMSR.readCardData(EMSR.MODE_READ_TRACK1 | EMSR.MODE_READ_TRACK2
                    | EMSR.MODE_READ_TRACK3 | EMSR.MODE_READ_PREFIX);
            StringBuffer textBuffer = new StringBuffer();

            int encryptionType = (buffer[0] >>> 3);
            // Trim extract encrypted block.
            byte[] encryptedData = new byte[buffer.length - 1];
            System.arraycopy(buffer, 1, encryptedData, 0, encryptedData.length);

            if (encryptionType == EMSR.ENCRYPTION_TYPE_OLD_RSA
                    || encryptionType == EMSR.ENCRYPTION_TYPE_RSA) {
                try {
                    String[] result = CryptographyHelper.decryptTrackDataRSA(encryptedData);
                    textBuffer.append("Track2: " + result[0]);
                    textBuffer.append("\n");
                } catch (Exception e) {
                    error("Failed to decrypt RSA data: " + e.getMessage());
                    return;
                }
            } else if (encryptionType == EMSR.ENCRYPTION_TYPE_AES256) {
                try {
                    String[] result = CryptographyHelper.decryptAESBlock(encryptedData);

                    textBuffer.append("Random data: " + result[0]);
                    textBuffer.append("\n");
                    textBuffer.append("Serial number: " + result[1]);
                    textBuffer.append("\n");
                    if (result[2] != null) {
                        textBuffer.append("Track1: " + result[2]);
                        textBuffer.append("\n");
                    }
                    if (result[3] != null) {
                        textBuffer.append("Track2: " + result[3]);
                        textBuffer.append("\n");
                    }
                    if (result[4] != null) {
                        textBuffer.append("Track3: " + result[4]);
                        textBuffer.append("\n");
                    }
                } catch (Exception e) {
                    error("Failed to decrypt AES data: " + e.getMessage());
                    return;
                }
            } else if (encryptionType == EMSR.ENCRYPTION_TYPE_IDTECH) {
                try {
                    String[] result = CryptographyHelper.decryptIDTECHBlock(encryptedData);

                    textBuffer.append("Card type: " + result[0]);
                    textBuffer.append("\n");
                    if (result[1] != null) {
                        textBuffer.append("Track1: " + result[1]);
                        textBuffer.append("\n");
                    }
                    if (result[2] != null) {
                        textBuffer.append("Track2: " + result[2]);
                        textBuffer.append("\n");
                    }
                    if (result[3] != null) {
                        textBuffer.append("Track3: " + result[3]);
                        textBuffer.append("\n");
                    }
                } catch (Exception e) {
                    error("Failed to decrypt IDTECH data: " + e.getMessage());
                    return;
                }
            } else {
                textBuffer.append("Encrypted block: " + HexUtil.byteArrayToHexString(buffer));
                textBuffer.append("\n");
            }

            dialog(R.drawable.ic_card, getString(R.string.card_info), textBuffer.toString());
        }, R.string.msg_reading_magstripe);
    }

    private void readBarcode(final int timeout) {
        Log.d(LOG_TAG, "Read Barcode");

        runTask((dialog, printer) -> {
            String barcode = printer.readBarcode(timeout);

            if (barcode != null) {
                dialog(R.drawable.ic_read_barcode, getString(R.string.barcode), barcode);
            }
        }, R.string.msg_reading_barcode);
    }

    private void processContactlessCard(ContactlessCard contactlessCard) {
        final StringBuffer msgBuf = new StringBuffer();

        if (contactlessCard instanceof ISO14443Card) {
            ISO14443Card card = (ISO14443Card) contactlessCard;
            msgBuf.append("ISO14 card: " + HexUtil.byteArrayToHexString(card.uid) + "\n");
            msgBuf.append("ISO14 type: " + card.type + "\n");

            if (card.type == ContactlessCard.CARD_MIFARE_DESFIRE) {
                ProtocolAdapter.setDebug(true);
                mPrinterChannel.suspend();
                mUniversalChannel.suspend();
                try {
                    // KLEILSON
                    card.getATS();
                    Log.d(LOG_TAG, "Select application");
                    card.DESFire().selectApplication(0x78E127);
                    Log.d(LOG_TAG, "Application is selected");
                    msgBuf.append("DESFire Application: " + Integer.toHexString(0x78E127) + "\n");
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Select application", e);
                } finally {
                    ProtocolAdapter.setDebug(false);
                    mPrinterChannel.resume();
                    mUniversalChannel.resume();
                }
            }
            /*
             // 16 bytes reading and 16 bytes writing
             // Try to authenticate first with default key
            byte[] key= new byte[] {-1, -1, -1, -1, -1, -1};
            // It is best to store the keys you are going to use once in the device memory,
            // then use AuthByLoadedKey function to authenticate blocks rather than having the key in your program
            card.authenticate('A', 8, key);

            // Write data to the card
            byte[] input = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                    0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };
            card.write16(8, input);

            // Read data from card
            byte[] result = card.read16(8);
            */
        } else if (contactlessCard instanceof ISO15693Card) {
            ISO15693Card card = (ISO15693Card) contactlessCard;

            msgBuf.append("ISO15 card: " + HexUtil.byteArrayToHexString(card.uid) + "\n");
            msgBuf.append("Block size: " + card.blockSize + "\n");
            msgBuf.append("Max blocks: " + card.maxBlocks + "\n");

            /*
            if (card.blockSize > 0) {
                byte[] security = card.getBlocksSecurityStatus(0, 16);
                ...

                // Write data to the card
                byte[] input = new byte[] { 0x00, 0x01, 0x02, 0x03 };
                card.write(0, input);
                ...

                // Read data from card
                byte[] result = card.read(0, 1);
                ...
            }
            */
        } else if (contactlessCard instanceof FeliCaCard) {
            FeliCaCard card = (FeliCaCard) contactlessCard;

            msgBuf.append("FeliCa card: " + HexUtil.byteArrayToHexString(card.uid) + "\n");

            /*
            // Write data to the card
            byte[] input = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                    0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };
            card.write(0x0900, 0, input);
            ...

            // Read data from card
            byte[] result = card.read(0x0900, 0, 1);
            ...
            */
        } else if (contactlessCard instanceof STSRICard) {
            STSRICard card = (STSRICard) contactlessCard;

            msgBuf.append("STSRI card: " + HexUtil.byteArrayToHexString(card.uid) + "\n");
            msgBuf.append("Block size: " + card.blockSize + "\n");

            /*
            // Write data to the card
            byte[] input = new byte[] { 0x00, 0x01, 0x02, 0x03 };
            card.writeBlock(8, input);
            ...

            // Try reading two blocks
            byte[] result = card.readBlock(8);
            ...
            */
        } else {
            msgBuf.append("Cartão sem contato: " + HexUtil.byteArrayToHexString(contactlessCard.uid));
        }

        dialog(R.drawable.ic_tag, getString(R.string.tag_info), msgBuf.toString());

        // Wait silently to remove card
        try {
            contactlessCard.waitRemove();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // DESATIVAR BLUETOOTH
    private void desativarBluetooth() {
        new AtivarDesativarBluetooth().disableBT(this);
    }

    /***************************** - IMPRESSÃO - *********************************/

    // ** PROMISSORIA 58mm
    @SuppressLint({"MissingPermission", "LongLogTag"})
    private void printPromissoria() {



        runTask((dialog, printer) -> {
            Log.d(LOG_TAG, "Print Relatório NFC-e");
            printer.reset();

            //
            String txtTel = "TEL. CONTATO: " + unidade.getTelefone();
            String txtNumVen = "N: " + numero + " / VENCIMENTO: " + vencimento;

            // Formata o valor antes de imprimir usando o novo método
            BigDecimal valorFormatado = cAux.converterValores(cAux.soNumeros(valor));
            String valorMascarado = cAux.formatarValorMonetario(valorFormatado); // Mudança aqui
            String txtValor = "VALOR: " + valorMascarado; // Mudança aqui

            // Adicionar logs para verificar o valor formatado e mascarado
            Log.i("ValorFormatado", valorFormatado.toString());
            Log.i("ValorMascarado", valorMascarado);

            //
            String txtCorpo = "Ao(s) " + getDataPorExtenso(vencimento) +
                    " pagarei por esta unica via de NOTA PROMISSORIA a " + unidade.getRazao_social() +
                    " ou a sua ordem, " +
                    "a quantidade de: " + getNumPorExtenso(Double.parseDouble(String.valueOf(cAux.converterValores(cAux.soNumeros(valor))))) + " em moeda corrente deste pais.";

            //
            String txtPagavel = "Pagavel em " + unidade.getCidade() + "/" + unidade.getUf();

            // EMITENTE
            String txtEmitente = "Emitente: " + cliente;
            String txtCnpjCpf = "CNPJ/CPF: " + cpfcnpj;
            String txtEndereco = "Endereco: " + endereco;

            // ASSINATURA
            String txtLinAss = "-------------------------------";
            String txtAss = "        Ass. Emitente";

            //
            String txtNum = "N: " + numero;
            String txtCli = "CLIENTE: " + id_cliente + " - " + cliente;
            String txtVal = "VALOR: " + valorMascarado; // Mudança aqui

            //
            String txtLinAss1 = "-------------------------------";
            String txtAss1 = unidade.getRazao_social();

            // IMPRESSÃO PROMISSÓRIA CLIENTE ********

            StringBuilder textBuffer = new StringBuilder();

            // PARTE 1
            textBuffer.append("{br}");
            textBuffer.append(tamFont).append("   ***  NOTA PROMISSORIA  ***").append("{br}");
            textBuffer.append("{br}");
            textBuffer.append("{br}");
            textBuffer.append(tamFont).append("      ***  VIA CLIENTE ***").append("{br}");
            textBuffer.append("{br}");
            textBuffer.append("{br}");

            // PARTE 2
            textBuffer.append(tamFont).append(txtTel).append("{br}");
            textBuffer.append(tamFont).append(txtNumVen).append("{br}");
            textBuffer.append(tamFont).append(txtValor).append("{br}");
            textBuffer.append("{br}");

            // PARTE 3
            textBuffer.append(tamFont).append(txtCorpo).append("{br}");
            textBuffer.append("{br}");

            // PARTE 4
            textBuffer.append(tamFont).append(txtPagavel).append("{br}");
            textBuffer.append("{br}");

            // PARTE 5
            textBuffer.append(tamFont).append(txtEmitente).append("{br}");
            textBuffer.append(tamFont).append(txtCnpjCpf).append("{br}");
            textBuffer.append(tamFont).append(txtEndereco).append("{br}");
            textBuffer.append("{br}");
            textBuffer.append("{br}");

            // PARTE 6
            textBuffer.append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append(tamFont).append(txtAss).append("{br}");
            textBuffer.append("{br}");

            // PARTE 7
            textBuffer.append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append("{br}");

            // PARTE 8
            textBuffer.append(tamFont).append(txtNum).append("{br}");
            textBuffer.append(tamFont).append(txtCli).append("{br}");
            textBuffer.append(tamFont).append(txtVal).append("{br}");
            textBuffer.append("{br}");

            // PARTE 9
            textBuffer.append(tamFont).append(txtLinAss1).append("{br}");
            textBuffer.append(tamFont).append(txtAss1).append("{br}");

            textBuffer.append("{br}");
            textBuffer.append("{br}");
            textBuffer.append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append("{br}");
            textBuffer.append("{br}");

            // VIA ESTABELECIMENTO ********

            // PARTE 1
            textBuffer.append("{br}");
            textBuffer.append(tamFont).append("   ***  NOTA PROMISSORIA  ***").append("{br}");
            textBuffer.append("{br}");
            textBuffer.append("{br}");
            textBuffer.append(tamFont).append(" ***  VIA ESTABELECIMENTO ***").append("{br}");
            textBuffer.append("{br}");
            textBuffer.append("{br}");

            // PARTE 2
            textBuffer.append(tamFont).append(txtTel).append("{br}");
            textBuffer.append(tamFont).append(txtNumVen).append("{br}");
            textBuffer.append(tamFont).append(txtValor).append("{br}");
            textBuffer.append("{br}");

            // PARTE 3
            textBuffer.append(tamFont).append(txtCorpo).append("{br}");
            textBuffer.append("{br}");

            // PARTE 4
            textBuffer.append(tamFont).append(txtPagavel).append("{br}");
            textBuffer.append("{br}");

            // PARTE 5
            textBuffer.append(tamFont).append(txtEmitente).append("{br}");
            textBuffer.append(tamFont).append(txtCnpjCpf).append("{br}");
            textBuffer.append(tamFont).append(txtEndereco).append("{br}");
            textBuffer.append("{br}");
            textBuffer.append("{br}");

            // PARTE 6
            textBuffer.append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append(tamFont).append(txtAss).append("{br}");
            textBuffer.append("{br}");

            // PARTE 7
            textBuffer.append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append("{br}");

            // PARTE 8
            textBuffer.append(tamFont).append(txtNum).append("{br}");
            textBuffer.append(tamFont).append(txtCli).append("{br}");
            textBuffer.append(tamFont).append(txtVal).append("{br}");
            textBuffer.append("{br}");

            // PARTE 9
            textBuffer.append(tamFont).append(txtLinAss1).append("{br}");
            textBuffer.append(tamFont).append(txtAss1).append("{br}");


            Log.i("TextoCompletoParaImpressao1", textBuffer.toString());

            printer.reset();
            printer.printTaggedText(textBuffer.toString());
            printer.feedPaper(100);
            printer.flush();

            finalizarImpressao();

        }, R.string.msg_printing_relatorio);
    }

    // ** RELATÓRIO 58mm

    private void printRelatorioNFCE58mm() {
        runTask((dialog, printer) -> {
            Log.d(LOG_TAG, "Print Relatório NFC-e");
            //printer.reset();

            elementosPedidos = bd.getRelatorioVendasPedidos();

            strFormPags = bd.getFormPagRelatorioVendasPedidos();
            /*String serie = bd.getSeriePOS();*/
            //elementosUnidade = bd.getUnidade();
            //unidade = elementosUnidade.get(0);

            unidade = bd.getUnidade();

            String quantItens = "0";
            String valTotalPed = "0";

            StringBuilder textBuffer = new StringBuilder();

            int posicaoNota;

            //IMPRIMIR CABEÇALHO
            textBuffer.append(" {br}");
            textBuffer.append(tamFont).append("  ***  RELATORIO PEDIDOS  ***").append("{br}");
            textBuffer.append(tamFont).append("{br}");
            textBuffer.append(tamFont).append("Unidade: ").append(unidade.getDescricao_unidade()).append("{br}");
            textBuffer.append(tamFont).append("Serial: ").append(prefs.getString("serial", "")).append("{br}");
            textBuffer.append(tamFont).append("Data Movimento: ").append(cAux.exibirData(prefs.getString("data_movimento_atual", ""))).append("{br}");

            textBuffer.append(tamFont).append("{br}");
            textBuffer.append(tamFont).append("{br}").append("         *** ITENS ***").append("{br}");
            textBuffer.append(tamFont).append("-------------------------------{br}");

            // TOTAL DE PRODUTOS
            int totalProdutos = 0;
            int totalProdutosNFE = 0;

            //DADOS DAS NOTAS NFC-e
            if (elementosPedidos.size() > 0) {
                for (int n = 0; n < elementosPedidos.size(); n++) {

                    //DADOS DOS PEDIDO
                    pedidos = elementosPedidos.get(n);

                    // SOMA A QUANTIDADE DE ITENS
                    String[] somarItens = {quantItens, pedidos.getQuantidade_venda()};
                    quantItens = String.valueOf(cAux.somar(somarItens));

                    // SOMA O VALOR TOTAL DOS PEDIDOS
                    String[] somarValTot = {valTotalPed, pedidos.getValor_total()};
                    valTotalPed = String.valueOf(cAux.somar(somarValTot));

                    //IMPRIMIR TEXTO
                    //textBuffer.append(tamFont).append("PRODUTO | QTDE. | VL.UNIT | VL.TOTAL{br}");
                    textBuffer.append(tamFont).append("PRODUTO: ").append(pedidos.getProduto_venda()).append("{br}");
                    textBuffer.append(tamFont).append("QTDE.: ").append(" | VL.UNIT: ").append(" | VL.TOTAL: ").append("{br}");
                    textBuffer.append(tamFont).append(pedidos.getQuantidade_venda()).append("       | ").append(cAux.formatarValorMonetario(new BigDecimal(pedidos.getPreco_unitario()))).append("    | ").append(cAux.formatarValorMonetario(new BigDecimal(pedidos.getValor_total()))).append("{br}");

                    textBuffer.append(tamFont).append("FORMA(S) PAGAMENTO: ").append("{br}");
                    textBuffer.append(tamFont).append(pedidos.getFormas_pagamento()).append("{br}");

                    textBuffer.append(tamFont).append("CLIENTE: ").append(pedidos.getCodigo_cliente()).append("{br}");
                    textBuffer.append(tamFont).append("-------------------------------").append("{br}");

                    /*try {
                        String[] sum = {String.valueOf(n), "1"};
                        imprimindo.setText(String.valueOf(cAux.somar(sum)));
                    } catch (Exception ignored) {

                    }*/
                    //totalProdutos += Integer.parseInt(itensPedidos.getQuantidade());
                }
            }

            textBuffer.append(tamFont).append("{br}").append("         *** TOTAIS ***").append("{br}{br}");

            /*textBuffer.append(tamFont).append("Quant. P13: ").append(totalProdutos).append(" Total: R$ ").append(totalProdutos).append("{br}");
            textBuffer.append(tamFont).append("Quant. P20: ").append(totalProdutosNFE).append(" Total: R$ ").append("{br}");*/
            String[] somar = {String.valueOf(totalProdutos), String.valueOf(totalProdutosNFE)};
            //textBuffer.append(tamFont).append("-------------------------------").append("{br}");
            Double s = Double.parseDouble(quantItens);
            textBuffer.append(tamFont).append("TOTAL DE VENDAS: ").append(elementosPedidos.size()).append("{br}");
            textBuffer.append(tamFont).append("TOTAL DE ITENS: ").append(s.intValue()).append("{br}");
            textBuffer.append(tamFont).append("FORMAS PAGAMENTO: ").append("{br}").append(formatarFormasPagamento(strFormPags)).append("{br}");
            textBuffer.append(tamFont).append("VALOR TOTAL: ").append(cAux.formatarValorMonetario(new BigDecimal(valTotalPed))).append("{br}");

            textBuffer.append("{br}{br}");

            //printer.reset();
            printer.selectPageMode();
            printer.setPageXY(0, 0);
            printer.setAlign(0);
            printer.printTaggedText(textBuffer.toString());
            printer.feedPaper(100);
            printer.flush();

            //desativarBluetooth();

            /*Intent i = new Intent(Impressora.this, Principal2.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("nomeImpressoraBlt", enderecoBlt);
            i.putExtra("enderecoBlt", enderecoBlt);
            startActivity(i);*/
            finish();

        }, R.string.msg_printing_relatorio);
    }

//FORMATAÇAO PARA TODOS OS VALORES DE FORMAS DE PAGAMENTO RELATORIOS
    private String formatarFormasPagamento(String strFormPags) {
        StringBuilder formattedFormPags = new StringBuilder();
        String[] formasPagamento = strFormPags.split("\\r?\\n");
        for (String forma : formasPagamento) {
            if (forma.contains(":")) {
                String[] parts = forma.split(":");
                String descricao = parts[0].trim();
                try {
                    // Retira os caracteres não numéricos, exceto ponto e vírgula
                    String valorStr = parts[1].replaceAll("[^\\d,]", "").replace(",", ".");
                    BigDecimal valor = new BigDecimal(valorStr);
                    formattedFormPags.append(descricao).append(": ")
                            .append(cAux.formatarValorMonetario(valor)).append("{br}");
                } catch (NumberFormatException e) {
                    formattedFormPags.append(forma).append("{br}"); // Caso ocorra um erro na conversão
                }
            } else {
                formattedFormPags.append(forma).append("{br}");
            }
        }
        return formattedFormPags.toString();
    }


    private void printRelatorioBaixas58mm() {
        runTask((dialog, printer) -> {
            Log.d(LOG_TAG, "Print Relatório Baixa");
            elementosRecebidos = bd.getRelatorioContasReceber();
            unidade = bd.getUnidade();

            String quantItens = "0";
            String valTotalPed = "0";

            StringBuilder textBuffer = new StringBuilder();

            //IMPRIMIR CABEÇALHO
            textBuffer.append(" {br}");
            textBuffer.append(tamFont).append("  ***  RELATORIO BAIXAS  ***").append("{br}");
            textBuffer.append(tamFont).append("{br}");
            textBuffer.append(tamFont).append("Unidade: ").append(unidade.getDescricao_unidade()).append("{br}");
            textBuffer.append(tamFont).append("Serial: ").append(prefs.getString("serial", "")).append("{br}");
            textBuffer.append(tamFont).append("Data Movimento: ").append(cAux.exibirData(prefs.getString("data_movimento_atual", ""))).append("{br}");

            textBuffer.append(tamFont).append("{br}");
            textBuffer.append(tamFont).append("{br}").append("         *** ITENS ***").append("{br}");
            textBuffer.append(tamFont).append("-------------------------------{br}");

            //DADOS DAS NOTAS NFC-e
            if (elementosRecebidos.size() > 0) {
                for (int n = 0; n < elementosRecebidos.size(); n++) {

                    //DADOS DOS PEDIDO
                    recebidos = elementosRecebidos.get(n);

                    // SOMA O VALOR TOTAL DOS PEDIDOS
                    String[] somarValTot = {valTotalPed, recebidos.getPago()};
                    valTotalPed = String.valueOf(cAux.somar(somarValTot));

                    //IMPRIMIR TEXTO
                    //textBuffer.append(tamFont).append("PRODUTO | QTDE. | VL.UNIT | VL.TOTAL{br}");
                    textBuffer.append(tamFont).append(recebidos.getFpagamento_financeiro().replace(" _ ", "")).append("  ").append(cAux.maskMoney(new BigDecimal(recebidos.getPago()))).append("{br}");
                    textBuffer.append(tamFont).append("--------------------------------").append("{br}");
                }
            }

            textBuffer.append(tamFont).append("{br}").append("         *** TOTAIS ***").append("{br}{br}");
            textBuffer.append(tamFont).append("FORMAS DE PAGAMENTO: ").append(elementosRecebidos.size()).append("{br}");
            textBuffer.append(tamFont).append("VALOR TOTAL: R$ ").append(cAux.formatarValorMonetario(new BigDecimal(valTotalPed))).append("{br}");

            textBuffer.append("{br}{br}");

            //printer.reset();
            printer.selectPageMode();
            printer.setPageXY(0, 0);
            printer.setAlign(0);
            printer.printTaggedText(textBuffer.toString());
            printer.feedPaper(100);
            printer.flush();
            finish();

        }, R.string.msg_printing_relatorio);
    }

    private void printBoleto() {
        runTask((dialog, printer) -> {
            //Log.d(LOG_TAG, "Print Relatório NFC-e");
            //printer.reset();

            // PARTE 1
        /*pppPromissoria.addLine(new CentralizedBigText("***  BOLETO  ***"));
        pppPromissoria.addLine("");
        pppPromissoria.addLine(new CentralizedBigText("***  TESTE ***"));*/
            //pppPromissoria.addLine("");

            // IDS TEXT CANHOTO BOLETO
            /*TextView txtCodBancoMoedaCanhoto = findViewById(R.id.txtCodBancoMoedaCanhoto);
            TextView txtCodBancoMoedaBoleto = findViewById(R.id.txtCodBancoMoedaBoleto);
            //
            TextView txtValorCanhotoBoleto = findViewById(R.id.txtValorCanhotoBoleto);
            TextView txtValorBoleto = findViewById(R.id.txtValorBoleto);
            //
            TextView txtLinhaDigitavelCanhoto = findViewById(R.id.txtLinhaDigitavelCanhoto);
            TextView txtLinhaDigitavelBoleto = findViewById(R.id.txtLinhaDigitavelBoleto);

            //
            txtCodBancoMoedaCanhoto.setText("001-9");
            txtCodBancoMoedaBoleto.setText("001-9");
            //
            txtValorCanhotoBoleto.setText("R$ 240,59");
            txtValorBoleto.setText("R$ 240,59");
            //
            txtLinhaDigitavelCanhoto.setText(numlinhaDigitavel);
            txtLinhaDigitavelBoleto.setText(numlinhaDigitavel);
*/


            // **************
            // 520 x 260
            //Bitmap bitmap1 = printViewHelper.createBitmapFromView90(impressora1, 452, 220);

            // Retorna o caminho da imagem do qrcode
            /*File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
            File dir = new File(sdcard, "Siac_Mobile/");

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            FileInputStream inputStream;
            BufferedInputStream bufferedInputStream;

            inputStream = new FileInputStream(dir.getPath() + "/qrcode.png");
            bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options);*/

            /*final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final AssetManager assetManager = getApplicationContext().getAssets();
            final Bitmap bitmap = BitmapFactory.decodeStream(assetManager.open("1jpg"),
                    null, options);*/


            //Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            final int width1 = Objects.requireNonNull(bitmap1).getWidth();
            final int height1 = bitmap1.getHeight();
            final int[] argb1 = new int[width1 * height1];
            bitmap1.getPixels(argb1, 0, width1, 0, 0, width1, height1);
            bitmap1.recycle();
            // --------------
            final int width2 = Objects.requireNonNull(bitmap2).getWidth();
            final int height2 = bitmap2.getHeight();
            final int[] argb2 = new int[width2 * height2];
            bitmap2.getPixels(argb2, 0, width2, 0, 0, width2, height2);
            bitmap2.recycle();
            //---------------
            /*Bitmap bitmap_ = new PrintViewHelper().createBitmap(impressora1);//.RotateBitmap(bp, 90);
            final int width = Objects.requireNonNull(bitmap_).getWidth();
            final int height = bitmap_.getHeight();
            final int[] argb = new int[width * height];
            bitmap_.getPixels(argb, 0, width, 0, 0, width, height);
            bitmap_.recycle();*/

            //printer.reset();
            //printer.printImage(argb, width, height, Printer.ALIGN_CENTER, true);
            printer.printImage(argb2, width2, height2, Printer.ALIGN_CENTER, true);
            printer.printImage(argb1, width1, height1, Printer.ALIGN_CENTER, true);
            //printer.printTaggedText("{br}{br}");
            printer.feedPaper(120);
            printer.flush();

            finish();

        }, R.string.msg_printing_boleto);
        /*new Handler().postDelayed(() -> {


        }, 5000);*/

    }

    // ** SALVA A IMAGEM COM O QCODE OU COD. BARRA
    private void SaveImage(Bitmap finalBitmap) {

        myDir.mkdirs();

        String fname = "qrcode.png";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finalizarImpressao() {
        Intent i = new Intent(Impressora.this, Principal2.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("nomeImpressoraBlt", enderecoBlt);
        i.putExtra("enderecoBlt", enderecoBlt);
        startActivity(i);
        finish();
    }
}
