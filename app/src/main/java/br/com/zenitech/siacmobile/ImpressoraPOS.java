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
/*import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;*/

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
import br.com.zenitech.siacmobile.domains.VendasPedidosDomain;
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

    ArrayList<VendasPedidosDomain> elementosPedidos;
    VendasPedidosDomain pedidos;

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

                // COMPROVANTE CANCELAMENTO CARTÃO
                //dataHoraCan = params.getString("dataHoraCan");
                //codAutCan = params.getString("codAutCan");

            } else {
                Toast.makeText(context, "Envie algo para imprimir!", Toast.LENGTH_LONG).show();
            }
        }

        printViewHelper = new PrintViewHelper();
//        ppp = new PosPrintProvider(context);

        if (tipoImpressao.equals("relatorio")) {
            printRelatorioNFCE58mm();
        } else if (tipoImpressao.equals("Promissoria")) {
            printPromissoria();
        }


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

        PosPrintProvider pppPromissoria = new PosPrintProvider(this);

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
        String txtLinAss = "-----------------------------------------------";
        String txtAss = "Ass. Emitente";

        //
        String txtNum = "N: " + numero;
        String txtCli = "CLIENTE: " + id_cliente + " - " + cliente;
        String txtVal = "VALOR: R$ " + valor;

        //
        String txtLinAss1 = "-----------------------------------------------";
        String txtAss1 = unidade.getRazao_social();

        // IMPRESSÃO PROMISSÓRIA CLIENTE ********

        // PARTE 1
        pppPromissoria.addLine(new CentralizedBigText("***  NOTA PROMISSORIA  ***"));
        pppPromissoria.addLine("");
        pppPromissoria.addLine(new CentralizedBigText("***  VIA CLIENTE ***"));
        pppPromissoria.addLine("");

        // PARTE 2
        pppPromissoria.addLine(new CentralizedBigText(txtTel));
        pppPromissoria.addLine(new CentralizedBigText(txtNumVen));
        pppPromissoria.addLine(new CentralizedBigText(txtValor));
        pppPromissoria.addLine("");

        // PARTE 3
        /*String txtCorpo = "Ao(s) " + getDataPorExtenso(vencimento) +
                "pagarei por esta unica via de NOTA PROMISSORIA a " + unidade.getRazao_social() +
                " ou a sua ordem, " +
                "a quantidade de: " + getNumPorExtenso(Double.parseDouble(String.valueOf(cAux.converterValores(cAux.soNumeros(valor))))) + " em moeda corrente deste pais.";
*/
        pppPromissoria.addLine(new CentralizedBigText("Ao(s) " + getDataPorExtenso(vencimento)));
        pppPromissoria.addLine(new CentralizedBigText("pagarei por esta unica via de NOTA PROMISSORIA a "));
        pppPromissoria.addLine(new CentralizedBigText(unidade.getRazao_social()));
        pppPromissoria.addLine(new CentralizedBigText("ou a sua ordem, a quantidade de: "));
        pppPromissoria.addLine(new CentralizedBigText(getNumPorExtenso(Double.parseDouble(String.valueOf(cAux.converterValores(cAux.soNumeros(valor)))))));
        pppPromissoria.addLine(new CentralizedBigText("em moeda corrente deste pais."));
        pppPromissoria.addLine("");

        // PARTE 4
        pppPromissoria.addLine(new CentralizedBigText(txtPagavel));
        pppPromissoria.addLine("");

        // PARTE 5
        pppPromissoria.addLine(new CentralizedBigText("Emitente:"));
        pppPromissoria.addLine(new CentralizedBigText(cliente));
        pppPromissoria.addLine(new CentralizedBigText("CNPJ/CPF: " + cpfcnpj));
        pppPromissoria.addLine(new CentralizedBigText("Endereco:"));
        pppPromissoria.addLine(new CentralizedBigText(endereco));
        pppPromissoria.addLine("");

        // PARTE 6
        pppPromissoria.addLine(new CentralizedBigText(txtLinAss));
        pppPromissoria.addLine(new CentralizedBigText(txtAss));
        pppPromissoria.addLine("");

        // PARTE 7
        pppPromissoria.addLine(new CentralizedBigText(txtLinAss));

        // PARTE 8
        pppPromissoria.addLine(new CentralizedBigText(txtNum));
        pppPromissoria.addLine(new CentralizedBigText("CLIENTE: " + id_cliente));
        pppPromissoria.addLine(new CentralizedBigText(cliente));
        pppPromissoria.addLine(new CentralizedBigText(txtVal));

        // PARTE 9
        pppPromissoria.addLine(new CentralizedBigText(txtLinAss1));
        pppPromissoria.addLine(new CentralizedBigText(txtAss1));

        pppPromissoria.addLine("");
        pppPromissoria.addLine("");
        pppPromissoria.addLine(new CentralizedBigText(txtLinAss));
        pppPromissoria.addLine("");
        pppPromissoria.addLine("");

        // VIA ESTABELECIMENTO ********VIA ESTABELECIMENTO


        // PARTE 1
        pppPromissoria.addLine(new CentralizedBigText("***  NOTA PROMISSORIA  ***"));
        pppPromissoria.addLine("");
        pppPromissoria.addLine(new CentralizedBigText("***  VIA ESTABELECIMENTO ***"));
        pppPromissoria.addLine("");

        // PARTE 2
        pppPromissoria.addLine(new CentralizedBigText(txtTel));
        pppPromissoria.addLine(new CentralizedBigText(txtNumVen));
        pppPromissoria.addLine(new CentralizedBigText(txtValor));
        pppPromissoria.addLine("");

        // PARTE 3
        /*String txtCorpo = "Ao(s) " + getDataPorExtenso(vencimento) +
                "pagarei por esta unica via de NOTA PROMISSORIA a " + unidade.getRazao_social() +
                " ou a sua ordem, " +
                "a quantidade de: " + getNumPorExtenso(Double.parseDouble(String.valueOf(cAux.converterValores(cAux.soNumeros(valor))))) + " em moeda corrente deste pais.";
*/
        pppPromissoria.addLine(new CentralizedBigText("Ao(s) " + getDataPorExtenso(vencimento)));
        pppPromissoria.addLine(new CentralizedBigText("pagarei por esta unica via de NOTA PROMISSORIA a "));
        pppPromissoria.addLine(new CentralizedBigText(unidade.getRazao_social()));
        pppPromissoria.addLine(new CentralizedBigText("ou a sua ordem, a quantidade de: "));
        pppPromissoria.addLine(new CentralizedBigText(getNumPorExtenso(Double.parseDouble(String.valueOf(cAux.converterValores(cAux.soNumeros(valor)))))));
        pppPromissoria.addLine(new CentralizedBigText("em moeda corrente deste pais."));
        pppPromissoria.addLine("");

        // PARTE 4
        pppPromissoria.addLine(new CentralizedBigText(txtPagavel));
        pppPromissoria.addLine("");

        // PARTE 5
        pppPromissoria.addLine(new CentralizedBigText("Emitente:"));
        pppPromissoria.addLine(new CentralizedBigText(cliente));
        pppPromissoria.addLine(new CentralizedBigText("CNPJ/CPF: " + cpfcnpj));
        pppPromissoria.addLine(new CentralizedBigText("Endereco:"));
        pppPromissoria.addLine(new CentralizedBigText(endereco));
        pppPromissoria.addLine("");

        // PARTE 6
        pppPromissoria.addLine(new CentralizedBigText(txtLinAss));
        pppPromissoria.addLine(new CentralizedBigText(txtAss));
        pppPromissoria.addLine("");

        // PARTE 7
        pppPromissoria.addLine(new CentralizedBigText(txtLinAss));

        // PARTE 8
        pppPromissoria.addLine(new CentralizedBigText(txtNum));
        pppPromissoria.addLine(new CentralizedBigText("CLIENTE: " + id_cliente));
        pppPromissoria.addLine(new CentralizedBigText(cliente));
        pppPromissoria.addLine(new CentralizedBigText(txtVal));

        // PARTE 9
        pppPromissoria.addLine(new CentralizedBigText(txtLinAss1));
        pppPromissoria.addLine(new CentralizedBigText(txtAss1));
        pppPromissoria.addLine("");

        pppPromissoria.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                liberarImpressora();
            }

            @Override
            public void onError() {
                liberarImpressora();
                Toast.makeText(context, "Erro ao imprimir: " + pppPromissoria.getListOfErrors(), Toast.LENGTH_SHORT).show();
            }
        });

        pppPromissoria.execute();
    }

    // ** RELATÓRIO 58mm

    private void printRelatorioNFCE58mm() {
        PosPrintProvider ppp = new PosPrintProvider(this);

        elementosPedidos = bd.getRelatorioVendasPedidos();
        /*String serie = bd.getSeriePOS();*/
        //elementosUnidade = bd.getUnidade();
        //unidade = elementosUnidade.get(0);

        unidade = bd.getUnidade();

        String quantItens = "0";
        String valTotalPed = "0";

        int posicaoNota;

        //IMPRIMIR CABEÇALHO
        ppp.addLine(new CentralizedBigText("***  RELATORIO PEDIDOS  ***"));
        ppp.addLine("");

        ppp.addLine(new CentralizedBigText("Unidade: " + unidade.getDescricao_unidade()));
        ppp.addLine(new CentralizedBigText("Serial: " + prefs.getString("serial", "")));

        ppp.addLine("");
        ppp.addLine(new CentralizedBigText("*** ITENS ***"));
        ppp.addLine(new CentralizedBigText("-----------------------------------------------"));

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
                ppp.addLine(new CentralizedBigText("PRODUTO: " + pedidos.getProduto_venda()));
                ppp.addLine(new CentralizedBigText("QTDE.:  | VL.UNIT:  | VL.TOTAL: "));
                ppp.addLine(new CentralizedBigText(pedidos.getQuantidade_venda() + "       | " + cAux.maskMoney(new BigDecimal(pedidos.getPreco_unitario())) + "    | " + cAux.maskMoney(new BigDecimal(pedidos.getValor_total()))));
                ppp.addLine(new CentralizedBigText("CLIENTE: " + pedidos.getCodigo_cliente()));
                ppp.addLine(new CentralizedBigText("-----------------------------------------------"));

                try {
                    String[] sum = {String.valueOf(n), "1"};
                    imprimindo.setText(String.valueOf(cAux.somar(sum)));
                } catch (Exception ignored) {

                }
                //totalProdutos += Integer.parseInt(itensPedidos.getQuantidade());
            }
        }

        ppp.addLine(new CentralizedBigText("*** TOTAIS ***"));
        ppp.addLine("");

        Double s = Double.parseDouble(quantItens);

        ppp.addLine(new CentralizedBigText("TOTAL DE VENDAS: " + elementosPedidos.size()));
        ppp.addLine(new CentralizedBigText("TOTAL DE ITENS: "+s.intValue()));
        ppp.addLine(new CentralizedBigText("VALOR TOTAL: R$ "+cAux.maskMoney(new BigDecimal(valTotalPed))));

        ppp.addLine("");
        ppp.addLine("");

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

        //ppp.addLine(textBuffer.toString());
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
        Intent i = new Intent(ImpressoraPOS.this, Principal2.class);
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