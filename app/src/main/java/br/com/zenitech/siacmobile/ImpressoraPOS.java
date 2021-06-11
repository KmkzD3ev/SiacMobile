package br.com.zenitech.siacmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.datecs.api.BuildInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.stone.posandroid.providers.PosPrintProvider;
import br.com.zenitech.siacmobile.controller.PrintViewHelper;
/*import br.com.zenitech.siacmobile.domains.AutorizacoesPinpad;
import br.com.zenitech.siacmobile.domains.ItensPedidos;
import br.com.zenitech.siacmobile.domains.Pedidos;
import br.com.zenitech.siacmobile.domains.PedidosNFE;*/
import br.com.zenitech.siacmobile.domains.UnidadesDomain;
import stone.application.enums.Action;
import stone.application.interfaces.StoneActionCallback;
import stone.application.interfaces.StoneCallbackInterface;

import static br.com.zenitech.siacmobile.ClassAuxiliar.getSha1Hex;
import static br.com.zenitech.siacmobile.DataPorExtenso.dataPorExtenso;
import static br.com.zenitech.siacmobile.NumeroPorExtenso.valorPorExtenso;

public class ImpressoraPOS extends AppCompatActivity implements StoneActionCallback {

    private static final String LOG_TAG = "Impressora";

    //
    private DatabaseHelper bd;
    private ClassAuxiliar cAux;

    //DADOS PARA IMPRESSÃO
    String id_cliente, cliente, vencimento, numero, tel_contato, valor, tipoImpressao, cpfcnpj, endereco;

    TextView total;
    public TextView imprimindo;

    public static String[] linhaProduto;

    UnidadesDomain unidade;

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
    PrintViewHelper printViewHelper;
    PosPrintProvider ppp;

    //
    boolean impressao1 = false, impressao2 = false, impressao3 = false, impressao4 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressora);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);

        bd = new DatabaseHelper(this);
        //
        cAux = new ClassAuxiliar();
        context = this;

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

                // COMPROVANTE CANCELAMENTO CARTÃO
                //dataHoraCan = params.getString("dataHoraCan");
                //codAutCan = params.getString("codAutCan");

            } else {
                Toast.makeText(context, "Envie algo para imprimir!", Toast.LENGTH_LONG).show();
            }
        }

        printViewHelper = new PrintViewHelper();
        ppp = new PosPrintProvider(context);

        printPromissoria();

        /*try {
            if (tipoImpressao.equals("promissoria")) {
                printPromissoria();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    public String getNumPorExtenso(double valor) {
        return valorPorExtenso(valor);
    }

    public String getDataPorExtenso(String data) {
        return dataPorExtenso(data);
    }

    private void printPromissoria() {

        PosPrintProvider ppp = new PosPrintProvider(context);

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

        ppp.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                liberarImpressora();
            }

            @Override
            public void onError() {
                liberarImpressora();
                Toast.makeText(context, "Erro ao imprimir: " + ppp.getListOfErrors(), Toast.LENGTH_SHORT).show();
            }
        });

        ppp.addLine(textBuffer.toString());
        ppp.execute();
    }

    private void toast(final String text) {
        Log.d(LOG_TAG, text);

        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }

    /***************************** - IMPRESSÃO - *********************************/

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

    //
    private void liberarImpressora() {
        impressao1 = true;
        impressao2 = true;
        impressao3 = true;
        impressao4 = true;
        finalizarImpressao();
    }

    //
    private void finalizarImpressao() {
        //
        if (!impressao1 || !impressao2 || !impressao3 || !impressao4) return;

        //
        Intent i = new Intent(ImpressoraPOS.this, Principal.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("nomeImpressoraBlt", enderecoBlt);
        i.putExtra("enderecoBlt", enderecoBlt);
        startActivity(i);
        finish();
    }

    @Override
    public void onStatusChanged(Action action) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError() {

    }
}
