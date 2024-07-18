package br.com.zenitech.siacmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import br.com.zenitech.siacmobile.domains.Clientes;
import br.com.zenitech.siacmobile.domains.ContasBancarias;
import br.com.zenitech.siacmobile.domains.FinanceiroReceberDomain;
import br.com.zenitech.siacmobile.domains.UnidadesDomain;
import br.com.zenitech.siacmobile.domains.VendasPedidosDomain;
import stone.application.enums.Action;
import stone.application.interfaces.StoneActionCallback;
import stone.application.interfaces.StoneCallbackInterface;

import static br.com.zenitech.siacmobile.DataPorExtenso.dataPorExtenso;
import static br.com.zenitech.siacmobile.NumeroPorExtenso.valorPorExtenso;

public class ImpressoraPOS extends AppCompatActivity implements StoneActionCallback {

    private static final String LOG_TAG = "Impressora";

    //
    private DatabaseHelper bd;
    private ClassAuxiliar cAux;

    //DADOS PARA IMPRESSÃO
    String id_cliente, cliente, vencimento, numero, tel_contato, valor, tipoImpressao, cpfcnpj, endereco, nota_fiscal, strFormPags, nContaBanco;

    TextView total;
    public TextView imprimindo;

    public static String[] linhaProduto;

    ArrayList<VendasPedidosDomain> elementosPedidos;
    VendasPedidosDomain pedidos;

    ArrayList<FinanceiroReceberDomain> elementosRecebidos;
    FinanceiroReceberDomain recebidos;

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
        context = this;

        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);

        bd = new DatabaseHelper(this);
        //
        cAux = new ClassAuxiliar();

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
            Log.e("IMPRESSORA", "Imprimir relatorio");
            printRelatorioNFCE58mm();
        } else if (tipoImpressao.equals("relatorioBaixa")) {
            printRelatorioBaixas58mm();
        } else if (tipoImpressao.equals("Promissoria")) {
            printPromissoria();
        } else if (tipoImpressao.equals("Boleto")) {
            printBoleto();
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
        String txtDataVenda = "Data/Hora Emissão: " + cAux.exibirDataAtual() + " - " + cAux.horaAtual();
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
        String txtLinAss = "--------------------------------";
        String txtAss = "Ass. Emitente";

        //
        String txtNum = "N: " + numero;
        String txtCli = "CLIENTE: " + id_cliente + " - " + cliente;
        String txtVal = "VALOR: R$ " + valor;

        //
        String txtLinAss1 = "--------------------------------";
        String txtAss1 = unidade.getRazao_social();

        // IMPRESSÃO PROMISSÓRIA CLIENTE ********

        // PARTE 1
        pppPromissoria.addLine("***  NOTA PROMISSORIA  ***");
        pppPromissoria.addLine("");
        pppPromissoria.addLine("***  VIA CLIENTE ***");
        pppPromissoria.addLine("");

        // PARTE 2
        pppPromissoria.addLine(txtDataVenda);
        pppPromissoria.addLine(txtTel);
        pppPromissoria.addLine(txtNumVen);
        pppPromissoria.addLine(txtValor);
        pppPromissoria.addLine("");

        // PARTE 3
        /*String txtCorpo = "Ao(s) " + getDataPorExtenso(vencimento) +
                "pagarei por esta unica via de NOTA PROMISSORIA a " + unidade.getRazao_social() +
                " ou a sua ordem, " +
                "a quantidade de: " + getNumPorExtenso(Double.parseDouble(String.valueOf(cAux.converterValores(cAux.soNumeros(valor))))) + " em moeda corrente deste pais.";
*/
        pppPromissoria.addLine("Ao(s) " + getDataPorExtenso(vencimento));
        pppPromissoria.addLine("pagarei por esta unica via de NOTA PROMISSORIA a ");
        pppPromissoria.addLine(unidade.getRazao_social());
        pppPromissoria.addLine("ou a sua ordem, a quantidade de: ");
        pppPromissoria.addLine(getNumPorExtenso(Double.parseDouble(String.valueOf(cAux.converterValores(cAux.soNumeros(valor))))));
        pppPromissoria.addLine("em moeda corrente deste pais.");
        pppPromissoria.addLine("");

        // PARTE 4
        pppPromissoria.addLine(txtPagavel);
        pppPromissoria.addLine("");

        // PARTE 5
        pppPromissoria.addLine("Emitente:");
        pppPromissoria.addLine(cliente);
        pppPromissoria.addLine("CNPJ/CPF: " + cpfcnpj);
        pppPromissoria.addLine("Endereco:");
        pppPromissoria.addLine(endereco);
        pppPromissoria.addLine("");

        // PARTE 6
        pppPromissoria.addLine(txtLinAss);
        pppPromissoria.addLine(txtAss);
        pppPromissoria.addLine("");

        // PARTE 7
        pppPromissoria.addLine(txtLinAss);

        // PARTE 8
        pppPromissoria.addLine(txtNum);
        pppPromissoria.addLine("CLIENTE: " + id_cliente);
        pppPromissoria.addLine(cliente);
        pppPromissoria.addLine(txtVal);

        // PARTE 9
        pppPromissoria.addLine(txtLinAss1);
        pppPromissoria.addLine(txtAss1);

        pppPromissoria.addLine("");
        pppPromissoria.addLine("");
        pppPromissoria.addLine(txtLinAss);
        pppPromissoria.addLine("");
        pppPromissoria.addLine("");

        // VIA ESTABELECIMENTO ********VIA ESTABELECIMENTO


        // PARTE 1
        pppPromissoria.addLine("***  NOTA PROMISSORIA  ***");
        pppPromissoria.addLine("");
        pppPromissoria.addLine("***  VIA ESTABELECIMENTO ***");
        pppPromissoria.addLine("");

        // PARTE 2
        pppPromissoria.addLine(txtDataVenda);
        pppPromissoria.addLine(txtTel);
        pppPromissoria.addLine(txtNumVen);
        pppPromissoria.addLine(txtValor);
        pppPromissoria.addLine("");

        // PARTE 3
        /*String txtCorpo = "Ao(s) " + getDataPorExtenso(vencimento) +
                "pagarei por esta unica via de NOTA PROMISSORIA a " + unidade.getRazao_social() +
                " ou a sua ordem, " +
                "a quantidade de: " + getNumPorExtenso(Double.parseDouble(String.valueOf(cAux.converterValores(cAux.soNumeros(valor))))) + " em moeda corrente deste pais.";
*/
        pppPromissoria.addLine("Ao(s) " + getDataPorExtenso(vencimento));
        pppPromissoria.addLine("pagarei por esta unica via de NOTA PROMISSORIA a ");
        pppPromissoria.addLine(unidade.getRazao_social());
        pppPromissoria.addLine("ou a sua ordem, a quantidade de: ");
        pppPromissoria.addLine(getNumPorExtenso(Double.parseDouble(String.valueOf(cAux.converterValores(cAux.soNumeros(valor))))));
        pppPromissoria.addLine("em moeda corrente deste pais.");
        pppPromissoria.addLine("");

        // PARTE 4
        pppPromissoria.addLine(txtPagavel);
        pppPromissoria.addLine("");

        // PARTE 5
        pppPromissoria.addLine("Emitente:");
        pppPromissoria.addLine(cliente);
        pppPromissoria.addLine("CNPJ/CPF: " + cpfcnpj);
        pppPromissoria.addLine("Endereco:");
        pppPromissoria.addLine(endereco);
        pppPromissoria.addLine("");

        // PARTE 6
        pppPromissoria.addLine(txtLinAss);
        pppPromissoria.addLine(txtAss);
        pppPromissoria.addLine("");

        // PARTE 7
        pppPromissoria.addLine(txtLinAss);

        // PARTE 8
        pppPromissoria.addLine(txtNum);
        pppPromissoria.addLine("CLIENTE: " + id_cliente);
        pppPromissoria.addLine(cliente);
        pppPromissoria.addLine(txtVal);

        // PARTE 9
        pppPromissoria.addLine(txtLinAss1);
        pppPromissoria.addLine(txtAss1);
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

        strFormPags = bd.getFormPagRelatorioVendasPedidos();
        /*String serie = bd.getSeriePOS();*/
        //elementosUnidade = bd.getUnidade();
        //unidade = elementosUnidade.get(0);

        unidade = bd.getUnidade();

        String quantItens = "0";
        String valTotalPed = "0";

        int posicaoNota;

        //IMPRIMIR CABEÇALHO
        ppp.addLine("***  RELATORIO PEDIDOS  ***");
        ppp.addLine("");

        ppp.addLine("Unidade: " + unidade.getDescricao_unidade());
        ppp.addLine("Serial: " + prefs.getString("serial", ""));
        ppp.addLine("Data Movimento: " + cAux.exibirData(prefs.getString("data_movimento_atual", "")));

        ppp.addLine("");
        ppp.addLine("*** ITENS ***");
        ppp.addLine("--------------------------------");

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
                ppp.addLine("PRODUTO: " + pedidos.getProduto_venda());
                ppp.addLine("QTDE.:  | VL.UNIT:  | VL.TOTAL: ");
                ppp.addLine(pedidos.getQuantidade_venda() + "     | " + cAux.maskMoney(new BigDecimal(pedidos.getPreco_unitario())) + "  | " + cAux.maskMoney(new BigDecimal(pedidos.getValor_total())));

                ppp.addLine("FORMA(S) PAGAMENTO: ");
                ppp.addLine(pedidos.getFormas_pagamento());

                ppp.addLine("CLIENTE: " + pedidos.getCodigo_cliente());
                ppp.addLine("--------------------------------");

                try {
                    String[] sum = {String.valueOf(n), "1"};
                    imprimindo.setText(String.valueOf(cAux.somar(sum)));
                } catch (Exception ignored) {

                }
                //totalProdutos += Integer.parseInt(itensPedidos.getQuantidade());
            }
        }

        ppp.addLine("*** TOTAIS ***");
        ppp.addLine("");

        Double s = Double.parseDouble(quantItens);

        ppp.addLine("TOTAL DE VENDAS: " + elementosPedidos.size());
        ppp.addLine("TOTAL DE ITENS: " + s.intValue());
        ppp.addLine("FORMAS PAGAMENTO: ");
        ppp.addLine(strFormPags);
        ppp.addLine("VALOR TOTAL: R$ " + cAux.maskMoney(new BigDecimal(valTotalPed)));

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

    private void printRelatorioBaixas58mm() {
        PosPrintProvider ppp = new PosPrintProvider(this);

        elementosRecebidos = bd.getRelatorioContasReceber();
        /*String serie = bd.getSeriePOS();*/
        //elementosUnidade = bd.getUnidade();
        //unidade = elementosUnidade.get(0);

        unidade = bd.getUnidade();

        String quantItens = "0";
        String valTotalPed = "0";

        int posicaoNota;

        //IMPRIMIR CABEÇALHO
        ppp.addLine("***  RELATORIO BAIXAS  ***");
        ppp.addLine("");

        ppp.addLine("Unidade: " + unidade.getDescricao_unidade());
        ppp.addLine("Serial: " + prefs.getString("serial", ""));
        ppp.addLine("Data Movimento: " + cAux.exibirData(prefs.getString("data_movimento_atual", "")));

        ppp.addLine("");
        ppp.addLine("*** ITENS ***");
        ppp.addLine("--------------------------------");

        // TOTAL DE PRODUTOS
        int totalProdutos = 0;
        int totalProdutosNFE = 0;

        //DADOS DAS NOTAS NFC-e
        if (elementosRecebidos.size() > 0) {
            for (int n = 0; n < elementosRecebidos.size(); n++) {

                //DADOS DOS PEDIDO
                recebidos = elementosRecebidos.get(n);

                // SOMA A QUANTIDADE DE ITENS
                //String[] somarItens = {quantItens, recebidos.getQuantidade_venda()};
                //quantItens = String.valueOf(cAux.somar(somarItens));

                // SOMA O VALOR TOTAL DOS PEDIDOS
                String[] somarValTot = {valTotalPed, recebidos.getPago()};
                valTotalPed = String.valueOf(cAux.somar(somarValTot));

                //IMPRIMIR TEXTO
                ppp.addLine(recebidos.getFpagamento_financeiro().replace(" _ ", "") + "  " + cAux.maskMoney(new BigDecimal(recebidos.getPago())));
                //ppp.addLine("QTDE.:  | VL.UNIT:  | VL.TOTAL: "));
                //ppp.addLine(recebidos.getQuantidade_venda() + "       | " + cAux.maskMoney(new BigDecimal(recebidos.getPreco_unitario())) + "    | " + cAux.maskMoney(new BigDecimal(recebidos.getValor_total()))));
                //ppp.addLine("CLIENTE: " + pedidos.getCodigo_cliente()));
                ppp.addLine("--------------------------------");

                try {
                    String[] sum = {String.valueOf(n), "1"};
                    imprimindo.setText(String.valueOf(cAux.somar(sum)));
                } catch (Exception ignored) {

                }
                //totalProdutos += Integer.parseInt(itensPedidos.getQuantidade());
            }
        }

        ppp.addLine("*** TOTAIS ***");
        ppp.addLine("");

        Double s = Double.parseDouble(quantItens);

        ppp.addLine("FORMAS DE PAGAMENTO: " + elementosRecebidos.size());
        //ppp.addLine("TOTAL DE ITENS: " + s.intValue()));
        ppp.addLine("VALOR TOTAL: R$ " + cAux.maskMoney(new BigDecimal(valTotalPed)));

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

    private void printBoleto() {

        PosPrintProvider pppPromissoria = new PosPrintProvider(this);


        LinearLayout impressora1, impressora2;
        Bitmap bitmap1, bitmap2;
        Bitmap bp;
        ImageView imgCodBarraBoleto;

        // PEGA OS DADOS DA CONTA BANCARIA
        ContasBancarias conta = bd.ContaBancaria(nContaBanco);

        // PEGA OS DADOS DO CLIENTE
        Clientes cliente = bd.cliente(id_cliente);

        //String valFinanceiro;
        String[] a = {String.valueOf(cAux.converterValores(valor)), conta.getTaxa_boleto()};
        valor = cAux.maskMoney(cAux.somar(a));

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
            BitMatrix bitMatrix = multiFormatWriter.encode(numCodBarra, BarcodeFormat.ITF, 0, 100);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bp = barcodeEncoder.createBitmap(bitMatrix);
            SaveImage(bp);

            //ImageView imgCodBarraBoleto = findViewById(R.id.imgB);
            imgCodBarraBoleto.setImageBitmap(bp);
            //imgCodBarraBoleto.setScaleType(ImageView.ScaleType.FIT_START);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        //
        impressora1 = findViewById(R.id.printBoleto);
        bitmap1 = new PrintViewHelper().createBitmapFromView90(impressora1, 576, 200); // height: 146
        //
        impressora2 = findViewById(R.id.printBoletoCanhoto);
        bitmap2 = new PrintViewHelper().createBitmapFromView90(impressora2, 437, 200); // height: 146

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

        pppPromissoria.addBitmap(bitmap1);
        pppPromissoria.addBitmap(bitmap2);
        //Bitmap bitmap = printViewHelper.RotateBitmap(bitmap1, 90);
        //pppPromissoria.addBitmap(bitmap);
        //pppPromissoria.addLine("");
        pppPromissoria.execute();
    }

    private void printBoleto_OLD() {

        PosPrintProvider pppPromissoria = new PosPrintProvider(this);

        String numCodBarraBB = "10101010101010101010101010101010101";// cAux.numCodBarraBB("R$ 240,59", "01/09/2022", "52000001", bd);
        String numlinhaDigitavel = "10101010101010101010101010101010101";//cAux.numlinhaDigitavel(numCodBarraBB);

        // PARTE 1
        /*pppPromissoria.addLine("***  BOLETO  ***"));
        pppPromissoria.addLine("");
        pppPromissoria.addLine("***  TESTE ***"));*/
        //pppPromissoria.addLine("");

        // IDS TEXT CANHOTO BOLETO
        TextView txtCodBancoMoedaCanhoto = findViewById(R.id.txtCodBancoMoedaCanhoto);
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

        // ********************************************************
        /*MultiFormatWriter writer = new MultiFormatWriter();
        String finaldata = Uri.encode(numCodBarraBB, "utf-8");
        BitMatrix bm = null;
        try {
            bm = writer.encode(finaldata, BarcodeFormat.ITF, 250, 250);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        Bitmap ImageBitmap = Bitmap.createBitmap(400, 40, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < 400; i++) {//width
            for (int j = 0; j < 40; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }
        //qrcode.setImageBitmap(ImageBitmap);
        SaveImage(ImageBitmap);
        ImageView imgCodBarraBoleto = findViewById(R.id.imgCodBarraBoleto);
        imgCodBarraBoleto.setImageBitmap(ImageBitmap);
        //imgCodBarraBoleto.setScaleType(ImageView.ScaleType.CENTER_CROP);*/

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bp = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(numCodBarraBB, BarcodeFormat.CODE_128, 378, 37);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bp = barcodeEncoder.createBitmap(bitMatrix);
            SaveImage(bp);

            ImageView imgCodBarraBoleto = findViewById(R.id.imgCodBarraBoleto);
            //ImageView imgCodBarraBoleto = findViewById(R.id.imgB);
            imgCodBarraBoleto.setImageBitmap(bp);
            //imgCodBarraBoleto.setScaleType(ImageView.ScaleType.FIT_START);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        // Retorna o caminho da imagem do qrcode
        File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File dir = new File(sdcard, "Emissor_Web/");

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        FileInputStream inputStream;
        BufferedInputStream bufferedInputStream;

        try {
            inputStream = new FileInputStream(dir.getPath() + "/qrcode.png");
            bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options);
            ImageView imgCodBarraBoleto = findViewById(R.id.imgCodBarraBoleto);
            imgCodBarraBoleto.setImageBitmap(bitmap);
            imgCodBarraBoleto.setScaleType(ImageView.ScaleType.FIT_START);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // *********************************************

        LinearLayout impressora1 = findViewById(R.id.printBoletoCanhoto); // 520 x 260
        //Bitmap bitmap1 = printViewHelper.createBitmapFromView90(impressora1, 452, 220);
        Bitmap bitmap1 = printViewHelper.createBitmap(impressora1);

        LinearLayout impressora2 = findViewById(R.id.printBoleto); // 520 x 260
        //Bitmap bitmap2 = printViewHelper.createBitmapFromView90(impressora2, 590, 220);
        Bitmap bitmap2 = printViewHelper.createBitmap(impressora2);

        pppPromissoria.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onSuccess() {
                //liberarImpressora();
            }

            @Override
            public void onError() {
                liberarImpressora();
                Toast.makeText(context, "Erro ao imprimir: " + pppPromissoria.getListOfErrors(), Toast.LENGTH_SHORT).show();
            }
        });

        pppPromissoria.addBitmap(bitmap1);
        //pppPromissoria.addBitmap(bitmap2);
        Bitmap bitmap = printViewHelper.RotateBitmap(bitmap1, 90);
        pppPromissoria.addBitmap(bitmap);
        pppPromissoria.addLine("");
        pppPromissoria.execute();
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
