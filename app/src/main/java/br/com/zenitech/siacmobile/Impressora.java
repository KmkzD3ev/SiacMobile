package br.com.zenitech.siacmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

/*import br.com.zenitech.siacmobile.domains.AutorizacoesPinpad;
import br.com.zenitech.siacmobile.domains.ItensPedidos;
import br.com.zenitech.siacmobile.domains.Pedidos;
import br.com.zenitech.siacmobile.domains.PedidosNFE;*/
import br.com.zenitech.siacmobile.domains.UnidadesDomain;
import br.com.zenitech.siacmobile.network.PrinterServer;
import br.com.zenitech.siacmobile.util.HexUtil;

import static br.com.zenitech.siacmobile.DataPorExtenso.dataPorExtenso;
import static br.com.zenitech.siacmobile.FinanceiroDaVenda.codigo_cliente;
import static br.com.zenitech.siacmobile.FinanceiroDaVenda.nomeCliente;
import static br.com.zenitech.siacmobile.FinanceiroDaVenda.txtDocumentoFormaPagamento;
import static br.com.zenitech.siacmobile.FinanceiroDaVenda.txtVencimentoFormaPagamento;
import static br.com.zenitech.siacmobile.NumeroPorExtenso.*;

public class Impressora extends AppCompatActivity {

    private static final String LOG_TAG = "Impressora";
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
    String id_cliente, cliente, vencimento, numero, tel_contato, valor, tipoImpressao, cpfcnpj, endereco;

    TextView total;
    public TextView imprimindo;

    public static String[] linhaProduto;

    //ArrayList<Unidades> elementosUnidade;
    UnidadesDomain unidade;

    //ArrayList<PosApp> elementosPos;
    //PosApp posApp;

    // NFC-e
    /*ArrayList<Pedidos> elementosPedidos;
    Pedidos pedidos;

    ArrayList<ItensPedidos> elementosItens;
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
    File myDir = new File(root + "/Emissor_Web");

    //
    String dataHoraCan, codAutCan;

    //
    private boolean impComPagViaCliente = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressora);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);

        if (prefs.getString("tamPapelImpressora", "").equalsIgnoreCase("58mm")) {
            tamFont = "{s}";
        }

        liberaImpressao = false;

        bd = new DatabaseHelper(this);
        //
        cAux = new ClassAuxiliar();
        context = this;

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

            } else {
                Toast.makeText(context, "Envie algo para imprimir!", Toast.LENGTH_LONG).show();
            }
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            ativarBluetooth();
        });
        ativarBluetooth();

        if (!prefs.getString("enderecoBlt", "").equalsIgnoreCase("")) {
            establishBluetoothConnection(prefs.getString("enderecoBlt", ""));
        } else {
            waitForConnection();
        }

        //
        tempo(1000);
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
                /*if (tipoImpressao.equals("relatorio")) {
                    //Log.i(LOG_TAG, "Relatório");

                    //Imprimir relatório de notas fiscais eletronica

                    if (prefs.getString("tamPapelImpressora", "").equalsIgnoreCase("58mm")) {
                        printRelatorioNFCE58mm();
                    } else {
                        printRelatorioNFCE();
                    }
                    //printPage();
                } else if (tipoImpressao.equals("nfe")) {

                    //Imprimir nota fiscal eletronica

                    if (prefs.getString("tamPapelImpressora", "").equalsIgnoreCase("58mm")) {
                        printNFE58mm(linhaProduto);
                    } else {
                        printNFE(linhaProduto);
                    }

                    //printImage();
                    //printBarcode();

                } else if (tipoImpressao.equals("reimpressao_comprovante")) {

                    //Imprimir comprovante do pagamento cartão
                    tempoImprCompViaEsta(1000, true);

                } else if (tipoImpressao.equals("comprovante_cancelamento")) {

                    //Imprimir comprovante do pagamento cartão
                    imprimirComprovanteCancelCartaoCliente();
                    tempoImprCompCancelEsta(5000);
                } else {

                    //Imprimir nota fiscal eletronica
                    if (prefs.getString("tamPapelImpressora", "").equalsIgnoreCase("58mm")) {
                        printNFCE58mm(linhaProduto);
                        *//*if (form_pagamento.equalsIgnoreCase("CARTAO DE CREDITO") || form_pagamento.equalsIgnoreCase("CARTAO DE DEBITO")) {
                            tempoImprCompViaCli(linhaProduto);
                        }*//*
                        tempoImprCompViaCli();

                    } else {
                        printNFCE(linhaProduto);
                        *//*if (form_pagamento.equalsIgnoreCase("CARTAO DE CREDITO") || form_pagamento.equalsIgnoreCase("CARTAO DE DEBITO")) {
                            tempoImprCompViaCli(linhaProduto);
                        }*//*

                        tempoImprCompViaCli();
                    }

                    //printImage();
                    //printBarcode();

                }
                */

                if (tipoImpressao.equals("Promissoria")) {
                    //Log.i(LOG_TAG, "Relatório");

                    //Imprimir relatório de notas fiscais eletronica

                    if (prefs.getString("tamPapelImpressora", "").equalsIgnoreCase("58mm")) {
                        //printPromissoria58mm();
                    } else {
                        printPromissoria();
                    }
                    //printPage();
                }

                liberaImpressao = false;
            } else {
                //
                tempo(2000);
            }
        }, tempo);
    }

    private void ativarBluetooth() {
        new AtivarDesativarBluetooth().enableBT();
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
                    establishBluetoothConnection(address);
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
        Log.d(LOG_TAG, "Initialize printer...");

        // Here you can enable various debug information
        //ProtocolAdapter.setDebug(true);
        Printer.setDebug(true);
        EMSR.setDebug(true);

        // Check if printer is into protocol mode. Ones the object is created it can not be released
        // without closing base streams.
        mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (mProtocolAdapter.isProtocolEnabled()) {
            Log.d(LOG_TAG, "Protocol mode is enabled");

            // Into protocol mode we can callbacks to receive printer notifications
            mProtocolAdapter.setPrinterListener(new ProtocolAdapter.PrinterListener() {
                @Override
                public void onThermalHeadStateChanged(boolean overheated) {
                    if (overheated) {
                        Log.d(LOG_TAG, "Thermal head is overheated");
                        status("OVERHEATED");
                    } else {
                        status(null);
                    }
                }

                @Override
                public void onPaperStateChanged(boolean hasPaper) {
                    if (hasPaper) {
                        Log.d(LOG_TAG, "Event: Paper out");
                        status("PAPER OUT");
                    } else {
                        status(null);
                    }
                }

                @Override
                public void onBatteryStateChanged(boolean lowBattery) {
                    if (lowBattery) {
                        Log.d(LOG_TAG, "Low battery");
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

        closeActiveConnection();

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
        final ProgressDialog dialog = new ProgressDialog(Impressora.this);
        dialog.setTitle(getString(R.string.title_please_wait));
        dialog.setMessage(getString(R.string.msg_connecting));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        closePrinterServer();

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        final Thread t = new Thread(() -> {
            Log.d(LOG_TAG, "BluetoothConnection - Conectando à " + address + "...");

            btAdapter.cancelDiscovery();

            try {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);

                InputStream in;
                OutputStream out;

                try {
                    BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();

                    mBtSocket = btSocket;
                    in = mBtSocket.getInputStream();
                    out = mBtSocket.getOutputStream();
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

    private synchronized void closeBluetoothConnection() {
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

        // Close network server
        PrinterServer ps = mPrinterServer;
        mPrinterServer = null;
        if (ps != null) {
            Log.d(LOG_TAG, "Close Network server");
            try {
                ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void closeActiveConnection() {
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
        new AtivarDesativarBluetooth().disableBT();
    }

    /***************************** - IMPRESSÃO - *********************************/

    // ** RELATÓRIO 58mm
    private void printPromissoria() {

        runTask((dialog, printer) -> {
            Log.d(LOG_TAG, "Print Relatório NFC-e");
            printer.reset();

            //
            String txtTel = "TEL. CONTATO: " + unidade.getTelefone();
            String txtNumVen = "N: " + numero + " / VENCIMENTO: " + vencimento;
            String txtValor = "VALOR: R$ " + valor;

            //
            String txtCorpo = "Ao(s) " + getDataPorExtenso(vencimento) +
                    "pagarei por esta unica via de NOTA PROMISSORIA a " + unidade.getRazao_social() +
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
            String txtVal = "VALOR: R$ " + valor;

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
            textBuffer.append(tamFont).append("      ***  VIA ESTABELECIMENTO ***").append("{br}");
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

            printer.reset();
            printer.printTaggedText(textBuffer.toString());
            printer.feedPaper(100);
            printer.flush();

            //
            desativarBluetooth();

            finish();

        }, R.string.msg_printing_relatorio);
    }

    // ** RELATÓRIO 80mm
    private void _printPromissoria() {

        runTask((dialog, printer) -> {
            Log.d(LOG_TAG, "Print Relatório NFC-e");
            printer.reset();

            //
            String txtTel = "TEL. CONTATO: " + unidade.getTelefone();
            String txtNumVen = "N: " + numero + " / VENCIMENTO: " + vencimento;
            String txtValor = "VALOR: R$ " + valor;

            //
            String txtCorpo = "Ao(s) " + getDataPorExtenso(vencimento) +
                    "pagarei por esta unica via de NOTA PROMISSORIA a " + unidade.getRazao_social() +
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
            String txtVal = "VALOR: R$ " + valor;

            //
            String txtLinAss1 = "-------------------------------";
            String txtAss1 = unidade.getRazao_social();

            // IMPRESSÃO PROMISSÓRIA CLIENTE ********

            StringBuilder textBuffer = new StringBuilder();

            // PARTE 1
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append("   ***  NOTA PROMISSORIA  ***").append("{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append("      ***  VIA CLIENTE ***").append("{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 2
            textBuffer.append("{reset}{center}").append(tamFont).append(txtTel).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtNumVen).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtValor).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 3
            textBuffer.append("{reset}{center}").append(tamFont).append(txtCorpo).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 4
            textBuffer.append("{reset}{center}").append(tamFont).append(txtPagavel).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 5
            textBuffer.append("{reset}{center}").append(tamFont).append(txtEmitente).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtCnpjCpf).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtEndereco).append("{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 6
            textBuffer.append("{reset}{center}").append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtAss).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 7
            textBuffer.append("{reset}{center}").append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 8
            textBuffer.append("{reset}{center}").append(tamFont).append(txtNum).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtCli).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtVal).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 9
            textBuffer.append("{reset}{center}").append(tamFont).append(txtLinAss1).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtAss1).append("{br}");

            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{br}");

            // VIA ESTABELECIMENTO ********

            // PARTE 1
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append("   ***  NOTA PROMISSORIA  ***").append("{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append("      ***  VIA ESTABELECIMENTO ***").append("{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 2
            textBuffer.append("{reset}{center}").append(tamFont).append(txtTel).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtNumVen).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtValor).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 3
            textBuffer.append("{reset}{center}").append(tamFont).append(txtCorpo).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 4
            textBuffer.append("{reset}{center}").append(tamFont).append(txtPagavel).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 5
            textBuffer.append("{reset}{center}").append(tamFont).append(txtEmitente).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtCnpjCpf).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtEndereco).append("{br}");
            textBuffer.append("{reset}{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 6
            textBuffer.append("{reset}{center}").append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtAss).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 7
            textBuffer.append("{reset}{center}").append(tamFont).append(txtLinAss).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 8
            textBuffer.append("{reset}{center}").append(tamFont).append(txtNum).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtCli).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtVal).append("{br}");
            textBuffer.append("{reset}{br}");

            // PARTE 9
            textBuffer.append("{reset}{center}").append(tamFont).append(txtLinAss1).append("{br}");
            textBuffer.append("{reset}{center}").append(tamFont).append(txtAss1).append("{br}");

            printer.reset();
            printer.printTaggedText(textBuffer.toString());
            printer.feedPaper(100);
            printer.flush();

            //
            desativarBluetooth();
            finish();

        }, R.string.msg_printing_relatorio);
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
        Intent i = new Intent(Impressora.this, Principal.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("nomeImpressoraBlt", enderecoBlt);
        i.putExtra("enderecoBlt", enderecoBlt);
        startActivity(i);
        finish();
    }
}
