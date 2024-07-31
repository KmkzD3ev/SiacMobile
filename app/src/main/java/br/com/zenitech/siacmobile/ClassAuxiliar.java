package br.com.zenitech.siacmobile;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import br.com.zenitech.siacmobile.domains.ContasBancarias;
import br.com.zenitech.siacmobile.domains.PosApp;

public class ClassAuxiliar {

    //FORMATAR DATA - INSERIR E EXIBIR
    private final SimpleDateFormat inserirDataFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat exibirDataFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat exibirDataFormat_dataHora = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
    //FORMATAR HORA
    private final SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
    private final Date data = new Date();
    private final Calendar cal = Calendar.getInstance();

    //
    public String dataFutura(int dias) {

        cal.setTime(data);
        cal.add(Calendar.DAY_OF_MONTH, dias);
        Date dataFutura = cal.getTime();
        String dataReturn = exibirDataFormat.format(dataFutura);
        Log.i("DataFutura", exibirDataFormat.format(cal.getTime()));
        return dataReturn;// exibirDataFormat.format(cal.getTime());
    }

    //EXIBIR DATA ATUAL DO SISTEMA - pt-BR
    public String exibirDataAtual() {
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String dataAtual = exibirDataFormat.format(data_atual);

        return dataAtual;
    }

    //INSERIR DATA ATUAL DO SISTEMA
    public String inserirDataAtual() {
        cal.setTime(data);
        Date data_atual = cal.getTime();

        Log.i("Data", inserirDataFormat.format(data_atual));
        return inserirDataFormat.format(data_atual);
    }

    //FORMATAR DATA
    public String formatarData(String data) {
        String CurrentString = data;

        String dia = CurrentString.substring(0, 2);
        String mes = CurrentString.substring(2, 4);
        String ano = CurrentString.substring(4, 8);

        data = dia + "/" + mes + "/" + ano;
        Log.i("Fin", data);

        return data;
    }

    //EXIBIR DATA
    public String exibirData(String data) {
        String CurrentString = data;
        String[] separated = CurrentString.split("-");
        data = separated[2] + "/" + separated[1] + "/" + separated[0];

        return data;
    }

    //INSERIR DATA
    public String inserirData(String data) {
        String CurrentString = data;
        String[] separated = CurrentString.split("/");
        data = separated[2] + "-" + separated[1] + "-" + separated[0];

        return data;
    }

    //EXIBIR HORA ATUAL
    public String horaAtual() {
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String horaAtual = dateFormat_hora.format(data_atual);

        return horaAtual;
    }

    //SOMAR VALORES
    public BigDecimal somar(String[] args) {
        BigDecimal valor = new BigDecimal("0.0");

        //
        for (String v : args) {
            valor = new BigDecimal(String.valueOf(valor)).add(new BigDecimal(v));
            //
            Log.e("TOTAL", "SOMAR" + valor);
        }
        return valor;
    }

    //SUBTRAIR VALORES
    public BigDecimal subitrair(String[] args) {
        BigDecimal valor = new BigDecimal(args[0]).subtract(new BigDecimal(args[1]));

        //
        Log.e("TOTAL", "SUBTRAIR" + String.valueOf(valor));
        return valor;
    }

    //MULTIPLICAR VALORES
    public BigDecimal multiplicar(String[] args) {
        BigDecimal valor = new BigDecimal(args[0]).multiply(new BigDecimal(args[1]));

        //
        Log.e("TOTAL", "MULTIPLICAR" + String.valueOf(valor));
        return valor;
    }

    //DIVIDIR VALORES
    public BigDecimal dividir(String[] args) {
        BigDecimal valor = new BigDecimal(args[0]).divide(new BigDecimal(args[1]), 3, RoundingMode.UP);

        //
        Log.e("TOTAL", "DIVIDIR" + valor);
        return valor;
    }

    //COMPARAR VALORES
    public int comparar(String[] args) {
        int valor = new BigDecimal(args[0]).compareTo(new BigDecimal(args[1]));
        //
        Log.e("TOTAL", "COMPARAR" + String.valueOf(valor));
        return valor;
    }

    //CONVERTER VALORES PARA CALCULO E INSERÇÃO NO BANCO DE DADOS
    public BigDecimal converterValores(String value) {
        BigDecimal parsed = null;
        try {
            //String cleanString = value.replaceAll("[R,$,.]", "");
            parsed = new BigDecimal(this.soNumeros(value))
                    .setScale(2, BigDecimal.ROUND_FLOOR)
                    .divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);

            Log.e("TOTAL", "FORAMATAR NUMERO: " + parsed);
        } catch (Exception e) {
            Log.e("sua_tag", e.getMessage(), e);
        }
        return parsed;
    }

    // Método para formatar valores monetários com "R$" e duas casas decimais
    public String formatarValorMonetario(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat("R$ #,##0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("R$ ");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        return df.format(valor);
    }

    //SÓ NÚMEROS
    public String soNumeros(String txt) {
        String numero = txt;

        numero = numero.replaceAll("[^0-9]*", "");

        return numero;
    }

    //SO NUMEROS (INTEIRO)
    public double soNumerosInt(String txt) {
        //return Integer.parseInt(this.soNumeros(txt));
        //return Double.parseDouble(txt);
        return Float.parseFloat(txt);
    }

    //
    public String maskMoney(BigDecimal valor) {
        /*NumberFormat formato1 = NumberFormat.getCurrencyInstance();
        NumberFormat formato2 = NumberFormat.getCurrencyInstance(new Locale("en", "EN"));
        NumberFormat formato3 = NumberFormat.getIntegerInstance();
        NumberFormat formato4 = NumberFormat.getPercentInstance();
        NumberFormat formato5 = new DecimalFormat(".##");
        NumberFormat formato6 = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        //
        String valorFormat = valor;

        valorFormat = formato5.format(valor);*/

        //
        //texto.setText(formato1.format(valor));
        /*Log.i("Moeda atual", formato1.format(valor));
        Log.i("Moeda EUA", formato2.format(valor));
        Log.i("Número inteiro", formato3.format(valor));
        Log.i("Porcentagem", formato4.format(valor));
        Log.i("Decimal", formato5.format(valor));
*/
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        nf.setMinimumFractionDigits(2);
        //System.out.println(nf.format(12345.124).trim());

        String valForm = nf.format(valor).trim().replaceAll(" ", "");
        Log.i("Decimal", valForm);
        return valForm;
    }

    /*public static void main(String[] args) {
        System.out.println("Subtrai");
        System.out.println(new BigDecimal("2.00").subtract(new BigDecimal("1.1")));

        System.out.println("");
        System.out.println("Soma");
        System.out.println(new BigDecimal("2.00").add(new BigDecimal("1.2")));

        System.out.println("");
        System.out.println("Compara");
        System.out.println(new BigDecimal("2.00").compareTo(new BigDecimal("1.3")));

        System.out.println("");
        System.out.println("Divide");
        System.out.println(new BigDecimal("2.00").divide(new BigDecimal("2.00")));

        System.out.println("");
        System.out.println("Máximo");
        System.out.println(new BigDecimal("2.00").max(new BigDecimal("1.5")));

        System.out.println("");
        System.out.println("Mínimo");
        System.out.println(new BigDecimal("2.00").min(new BigDecimal("1.6")));

        System.out.println("");
        System.out.println("Potência");
        System.out.println(new BigDecimal("2.00").pow(2));

        System.out.println("");
        System.out.println("Multiplica");
        System.out.println(new BigDecimal("2.00").multiply(new BigDecimal("1.8")));

    }*/

    //DEIXAR A PRIMEIRA LETRA DA STRING EM MAIUSCULO
    public String maiuscula1(String palavra) {
        //betterIdea = Character.toUpperCase(userIdea.charAt(0)) + userIdea.substring(1);
        palavra = palavra.trim();
        palavra = Character.toUpperCase(palavra.charAt(0)) + palavra.substring(1);
        //return palavra.substring(0, 1).toUpperCase() + palavra.substring(1);
        return palavra;
    }


    ////////////////////////////
    public TextWatcher maskData(final String mask, final EditText et) {
        return new TextWatcher() {
            boolean isUpdating = true;
            String oldTxt = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*String str = unmask(s.toString());
                String maskCurrent = "";
                if (isUpdating) {
                    oldTxt = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > oldTxt.length()) {
                        maskCurrent += m;
                        continue;
                    }
                    try {
                        maskCurrent += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                et.setText(maskCurrent);
                et.setSelection(maskCurrent.length());*/
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                String str = unmask(s.toString());
                String maskCurrent = "";
                if (isUpdating) {
                    oldTxt = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > oldTxt.length()) {
                        maskCurrent += m;
                        continue;
                    }
                    try {
                        maskCurrent += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                Log.i("Mask 1000", maskCurrent);

                // VERIFICA SE A VARIAVEL SO CONTEM NUMERO
                boolean soNumeros = maskCurrent.matches("^\\d+$");

                // CASO SO TENHA NUMERO FORMATA A DATA PELO formatarData()
                if (soNumeros && maskCurrent.length() == 8) {
                    et.setText(formatarData(maskCurrent));
                }
                // CASO CONTRARIO USA O maskCurrent
                else {
                    et.setText(maskCurrent);
                }
                //
                et.setSelection(maskCurrent.length());
            }
        };
    }

    public String unmask(String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "")
                .replaceAll("[/]", "").replaceAll("[(]", "")
                .replaceAll("[)]", "");
    }

    static String getSha1Hex(String clearString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String zerosAEsquerda(String numero, int quant) {
        // RETIRA TUDO QUE NÃO FOR NÚMERO
        numero = this.soNumeros(numero);
        //Log.e("Numero", numero);

        numero = String.format("%0" + quant + "d", Integer.parseInt(numero));

        // VERIFICA QUAL É O BANCO
        /*if (banco.equalsIgnoreCase("BB")) {
            numero = String.format("%010d", Integer.parseInt(numero));
        } else if (banco.equalsIgnoreCase("Bradesco")) {
            numero = String.format("%010d", Integer.parseInt(numero));
        } else if (banco.equalsIgnoreCase("Bradesco7")) {
            numero = String.format("%07d", Integer.parseInt(numero));

            Log.e("zerosAEsquerda", numero);
        } else {
            numero = String.format("%08d", Integer.parseInt(numero));
        }*/
        //Log.e("Numero", numero);
        return numero;
    }

    public String DiffDias(String data1, String data2) throws ParseException {
        // Dando um exemplo: quantos dias se passam desde 07/09/1822 até 05/06/2006?
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setLenient(false);
        Date d1 = df.parse(data1); //"07/09/1822"
        System.out.println(d1);
        Date d2 = df.parse(data2); //"05/06/2006"
        System.out.println(d2);
        long dt = (Objects.requireNonNull(d2).getTime() - Objects.requireNonNull(d1).getTime()) + 3600000; // 1 hora para compensar horário de verão
        System.out.println(dt / 86400000L); // passaram-se 67111 dias

        return String.valueOf(dt / 86400000L);
    }

    // GERAR NÚMERO CÓDGIO BARRAS BOLETO
    public String numCodBarraBB(String valor, String vencimento, String numeroDoc, DatabaseHelper bd, ContasBancarias conta) {

        /*
            FORMATO DO CÓDIGO DE BARRAS PARA CONVÊNIOS DA CARTEIRA SEM
            REGISTRO – COM "NOSSO NÚMERO" LIVRE DE 17 POSIÇÕES.
            ------------------------------------------------------------------------------------
            Posição     Tamanho     Picture     Conteúdo
            01 a 03     03          9(3)        Código do Banco na Câmara de Compensação = '001'
            04 a 04     01          9(1)        Código da Moeda = '9'
            05 a 05     01          9(1)        DV do Código de Barras (Anexo VI)
            06 a 09     04          9(04)       Fator de Vencimento (Anexo IV)
            10 a 19     10          9(08)       V(2) Valor
            20 a 25     06          9(6)        Número do Convênio de Seis Posições
            26 a 42     17          9(17)       Nosso Número Livre do cliente.
            43 a 44     02          02          '21' Tipo de Modalidade de Cobrança.
        */

        //
        //ContasBancarias conta = bd.contasBancarias();
        //PosApp

        // GERAR CÓDIGO BARRA BOLETO BANCO DO BRASIL
        StringBuilder numCodBarra = new StringBuilder();

        /*long meses = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            //define datas
            LocalDateTime dataCadastro;
            dataCadastro = LocalDateTime.of(1997, 10, 7, 0, 0, 0);

            //LocalDateTime hoje = LocalDateTime.now();
            LocalDateTime hoje = LocalDateTime.of(2000, 7, 4, 0, 0, 0);
            meses = dataCadastro.until(hoje, ChronoUnit.DAYS);
        } else {
            //define datas
            Calendar dataCadastro = Calendar.getInstance();
            dataCadastro.set(1997, 10, 7);
            Calendar hoje = Calendar.getInstance();
            hoje.set(2000, 7, 4);

            //calcula diferença
            meses = (hoje.get(Calendar.YEAR) * 12 + hoje.get(Calendar.MONTH))
                    - (dataCadastro.get(Calendar.YEAR) * 12 + dataCadastro.get(Calendar.MONTH));
        }*/
        Log.e("BOLETO vencimento", vencimento);
        String dias = "";
        try {
            dias = this.DiffDias("07/10/1997", vencimento);//"04/07/2000" - "17/11/2010"
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //dias = String.valueOf(this.somar(new String[]{dias, "1"}));

        // Código do Banco na Câmara de Compensação = "001"
        String codBanco = "001";
        Log.e("BOLETO codBanco", codBanco);
        // Código da Moeda = "9"
        String codMoeda = "9";
        Log.e("BOLETO codMoeda", codMoeda);
        // Fator de Vencimento (Anexo IV)
        String fatorVencimento = dias;
        Log.e("BOLETO fatorVencimento", fatorVencimento);
        // Valor
        String valorBoleto = this.zerosAEsquerda(valor, 10);
        Log.e("BOLETO valorBoleto", valorBoleto);
        // Zeros de Seis Posições
        String zeros = "000000";
        Log.e("BOLETO zeros", zeros);
        // Nosso Número Livre do cliente.
        String nossonumero = this.nossoNumero(conta.getConvenio(), numeroDoc);
        Log.e("BOLETO nossonumero", nossonumero);
        // "21" Tipo de Modalidade de Cobrança.
        String carteira = conta.getCarteira();
        Log.e("BOLETO carteira", carteira);
        // DV do Código de Barras (Anexo VI)
        //String dvCodBarra = digitoVerificadorModulo11(codBanco + codMoeda + fatorVencimento + valorBoleto + zeros + nossonumero + carteira); //"0019373700000001000500940144816060680935031"
        String dvCodBarra = modulo11(codBanco + codMoeda + fatorVencimento + valorBoleto + zeros + nossonumero + carteira);
        Log.e("BOLETO dvCodBarra", dvCodBarra);

        // Parte 1
        numCodBarra.append(codBanco);
        // Parte 2
        numCodBarra.append(codMoeda);
        // Parte 3
        numCodBarra.append(dvCodBarra);
        // Parte 4
        numCodBarra.append(fatorVencimento);
        // Parte 5
        numCodBarra.append(valorBoleto);
        // Parte 6
        numCodBarra.append(zeros);
        // Parte 7
        numCodBarra.append(nossonumero);
        // Parte 8
        numCodBarra.append(carteira);

        Log.e("CODIGO BOLETO", numCodBarra.toString());
        return numCodBarra.toString();
    }

    public String numCodBarraBradesco(String valor, String vencimento, String numeroDoc, DatabaseHelper bd, ContasBancarias conta) {

        /*
            FORMATO DO CÓDIGO DE BARRAS PARA CONVÊNIOS DA CARTEIRA SEM
            REGISTRO – COM "NOSSO NÚMERO" LIVRE DE 17 POSIÇÕES.
            ------------------------------------------------------------------------------------
            Posição     Tamanho     Picture     Conteúdo
            01 a 03     03          9(3)        Código do Banco na Câmara de Compensação = '237'
            04 a 04     01          9(1)        Código da Moeda = '9'
            05 a 05     01          9(1)        DV do Código de Barras (Anexo VI)
            06 a 09     04          9(04)       Fator de Vencimento (Anexo IV)
            10 a 19     10          9(08)       V(2) Valor
            20 a 25     06          9(6)        Número do Convênio de Seis Posições
            26 a 42     17          9(17)       Nosso Número Livre do cliente.
            43 a 44     02          02          '21' Tipo de Modalidade de Cobrança.
        */

        //
        //ContasBancarias conta = bd.contasBancarias();
        //PosApp

        /*long meses = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            //define datas
            LocalDateTime dataCadastro;
            dataCadastro = LocalDateTime.of(1997, 10, 7, 0, 0, 0);

            //LocalDateTime hoje = LocalDateTime.now();
            LocalDateTime hoje = LocalDateTime.of(2000, 7, 4, 0, 0, 0);
            meses = dataCadastro.until(hoje, ChronoUnit.DAYS);
        } else {
            //define datas
            Calendar dataCadastro = Calendar.getInstance();
            dataCadastro.set(1997, 10, 7);
            Calendar hoje = Calendar.getInstance();
            hoje.set(2000, 7, 4);

            //calcula diferença
            meses = (hoje.get(Calendar.YEAR) * 12 + hoje.get(Calendar.MONTH))
                    - (dataCadastro.get(Calendar.YEAR) * 12 + dataCadastro.get(Calendar.MONTH));
        }*/
        Log.e("BOLETO vencimento", vencimento);
        String dias = "";
        try {
            dias = this.DiffDias("07/10/1997", vencimento);//"04/07/2000" - "17/11/2010"
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //dias = String.valueOf(this.somar(new String[]{dias, "1"}));

        // Código do Banco na Câmara de Compensação = "237"
        String codBanco = "237";
        Log.e("BOLETO codBanco", codBanco);
        // Código da Moeda = "9"
        String codMoeda = "9";
        Log.e("BOLETO codMoeda", codMoeda);
        // Fator de Vencimento (Anexo IV)
        String fatorVencimento = dias;
        Log.e("BOLETO fatorVencimento", fatorVencimento);
        // Valor
        String valorBoleto = this.zerosAEsquerda(valor, 10);
        Log.e("BOLETO valorBoleto", valorBoleto);
        // Nosso Número Livre do cliente.
        numeroDoc = this.zerosAEsquerda(numeroDoc, 11);
        String nossonumero = conta.getCarteira() + numeroDoc;
        Log.e("BOLETO nossonumero", nossonumero);
        // "21" Tipo de Modalidade de Cobrança.
        String carteira = conta.getCarteira();
        Log.e("BOLETO carteira", carteira);
        // DV do Código de Barras (Anexo VI)
        String nConta = this.zerosAEsquerda(conta.getConta(), 7);
        Log.e("MODULO DE 11", codBanco + codMoeda + fatorVencimento + valorBoleto + conta.getAgencia() + nossonumero + nConta + "0");
        String dvCodBarra = modulo11(codBanco + codMoeda + fatorVencimento + valorBoleto + conta.getAgencia() + nossonumero + nConta + "0");
        Log.e("BOLETO dvCodBarra", dvCodBarra);

        // Parte 1


        // GERAR CÓDIGO BARRA BOLETO BANCO DO BRASIL
        String numCodBarra = codBanco + codMoeda + dvCodBarra + fatorVencimento + valorBoleto + conta.getAgencia() + nossonumero + nConta + "0";
        Log.e("BOLETO COD BARRA", numCodBarra);
        return numCodBarra;
    }

    // GERAR NÚMERO DA LINHA DIGITÁVEL DO BOLETO
    public String numlinhaDigitavel(String numCodBarraBB) {
        // CAMPO 1
        String c1p1 = numCodBarraBB.substring(0, 3);
        String c1p2 = numCodBarraBB.substring(3, 4);
        String c1p3 = numCodBarraBB.substring(19, 24);
        //String c1 = this.digitoVerificadoModulo10("" + c1p1 + c1p2 + c1p3);
        String c1 = c1p1 + c1p2 + c1p3 + this.modulo10("" + c1p1 + c1p2 + c1p3);
        StringBuilder sc1 = new StringBuilder(c1);
        sc1.insert(c1.length() - 5, ".");
        // CAMPO 2
        String c2p1 = numCodBarraBB.substring(24, 34);
        //String c2 = this.digitoVerificadoModulo10(c2p1);
        String c2 = c2p1 + this.modulo10(c2p1);
        StringBuilder sc2 = new StringBuilder(c2);
        sc2.insert(c2.length() - 6, ".");
        // CAMPO 3
        String c3p1 = numCodBarraBB.substring(34, 44);
        //String c3 = this.digitoVerificadoModulo10(c3p1);
        String c3 = c3p1 + this.modulo10(c3p1);
        StringBuilder sc3 = new StringBuilder(c3);
        sc3.insert(c3.length() - 6, ".");
        // CAMPO 4
        String c4 = numCodBarraBB.substring(4, 5);
        // CAMPO 5
        String c5p1 = numCodBarraBB.substring(5, 9);
        String c5p2 = numCodBarraBB.substring(9, 19);
        String c5 = c5p1 + c5p2;
        String format = String.format("%s      %s      %s      %s      %s", sc1.toString(), sc2.toString(), sc3.toString(), c4, c5);
        Log.e("CODIGO BOLETO", format);
        return format;
    }

    // GERAR NÚMERO DA LINHA DIGITÁVEL DO BOLETO BRADESCO
    public String numlinhaDigitavelBradesco(String numCodBarraBB) {
        // CAMPO 1
        String c1p1 = numCodBarraBB.substring(0, 3);
        String c1p2 = numCodBarraBB.substring(3, 4);
        String c1p3 = numCodBarraBB.substring(19, 24);
        String c1 = c1p1 + c1p2 + c1p3 + this.modulo10("" + c1p1 + c1p2 + c1p3);
        StringBuilder sc1 = new StringBuilder(c1);
        sc1.insert(c1.length() - 5, ".");

        // CAMPO 2
        String c2p1 = numCodBarraBB.substring(24, 34);
        String c2 = c2p1 + this.modulo10(c2p1);
        StringBuilder sc2 = new StringBuilder(c2);
        sc2.insert(c2.length() - 6, ".");

        // CAMPO 3
        String c3p1 = numCodBarraBB.substring(34, 44);
        String c3 = c3p1 + this.modulo10(c3p1);
        StringBuilder sc3 = new StringBuilder(c3);
        sc3.insert(c3.length() - 6, ".");

        // CAMPO 4
        String c4 = numCodBarraBB.substring(4, 5);

        // CAMPO 5
        String c5p1 = numCodBarraBB.substring(5, 9);
        String c5p2 = numCodBarraBB.substring(9, 19);
        String c5 = c5p1 + c5p2;

        String format = String.format("%s      %s      %s      %s      %s", sc1, sc2, sc3, c4, c5);
        Log.e("CODIGO BOLETO", format);
        return format;
    }

    // NOSSO NÚMERO BOLETO - CONVÊNIO + SERIE NA CASA DE MILHÃO, COM ZEROS A ESQUERDA
    public String nossoNumero(String convenio, String serieBoleto) {
        //int n = (serieBoleto * 100000000) + 1;
        return String.format("%s%s", convenio, this.zerosAEsquerda(String.valueOf(serieBoleto), 10));
    }

    public String digitoVerificadorModulo11(String chave) {

        //
        int[] pesos = {4, 3, 2, 9, 8, 7, 6, 5};
        int somaPonderada = 0;
        for (int i = 0; i < chave.length(); i++) {
            somaPonderada += pesos[i % 8] * (Integer.parseInt(chave.substring(i, i + 1)));
        }

        //
        //MODULO 11 PARA GERAR O DIGITO VERIFICADOR
        int MODULO11 = 11;
        int DV = (MODULO11 - somaPonderada % MODULO11);
        Log.e("DIGITO 1", String.valueOf(DV));
        if (DV == 0 || DV == 10 || DV == 11) {
            DV = 1;
        }
        Log.e("DIGITO 2", String.valueOf(DV));
        return String.valueOf(DV);
    }

    //MODULO 11 PARA GERAR O DIGITO VERIFICADOR DA NOTA NFC-E
    //private final int MODULO10 = 10;

    public String digitoVerificadoModulo10(String chave) {
        int MODULO10 = 10;
        int[] pesos = {4, 3, 2, 9, 8, 7, 6, 5};
        int somaPonderada = 0;
        for (int i = 0; i < chave.length(); i++) {
            somaPonderada += pesos[i % 8] * (Integer.parseInt(chave.substring(i, i + 1)));
        }
        int DV = (MODULO10 - somaPonderada % MODULO10);
        if (DV >= 10) {
            DV = 0;
        }
        return chave + DV;
    }

    /**
     * @author :Allan Tenorio
     * @see :Calculo do Modulo 10 para geracao do digito verificador de boletos bancários.
     * @since :10/07/2012
     */

    //Módulo 10
    //Conforme o esquema abaixo, cada dígito do número, começando da direita para a esquerda
    //(menos significativo para o mais significativo) é multiplicado, na ordem, por 2, depois 1, depois 2, depois 1 e
    //assim sucessivamente.
    //Em vez de ser feito o somatório das multiplicações, será feito o somatório dos dígitos das multiplicações
    //(se uma multiplicação der 12, por exemplo, será somado 1 + 2 = 3).
    //O somatório será dividido por 10 e se o resto (módulo 10) for diferente de zero, o dígito será 10 menos este valor.
    //Número exemplo: 261533-4
    //  +---+---+---+---+---+---+   +---+
    //  | 2 | 6 | 1 | 5 | 3 | 3 | - | 4 |
    //  +---+---+---+---+---+---+   +---+
    //    |   |   |   |   |   |
    //   x1  x2  x1  x2  x1  x2
    //    |   |   |   |   |   |
    //   =2 =12  =1 =10  =3  =6
    //    +---+---+---+---+---+-> = (16 / 10) = 1, resto 6 => DV = (10 - 6) = 4
    public String modulo10(String num) {

        //variáveis de instancia
        int soma = 0;
        int resto = 0;
        int dv = 0;
        String[] numeros = new String[num.length() + 1];
        int multiplicador = 2;
        String aux;
        String aux2;
        String aux3;

        for (int i = num.length(); i > 0; i--) {
            //Multiplica da direita pra esquerda, alternando os algarismos 2 e 1
            if (multiplicador % 2 == 0) {
                // pega cada numero isoladamente
                numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * 2);
                multiplicador = 1;
            } else {
                numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * 1);
                multiplicador = 2;
            }
        }

        // Realiza a soma dos campos de acordo com a regra
        for (int i = (numeros.length - 1); i > 0; i--) {
            aux = String.valueOf(Integer.valueOf(numeros[i]));

            if (aux.length() > 1) {
                aux2 = aux.substring(0, aux.length() - 1);
                aux3 = aux.substring(aux.length() - 1, aux.length());
                numeros[i] = String.valueOf(Integer.valueOf(aux2) + Integer.valueOf(aux3));
            } else {
                numeros[i] = aux;
            }
        }

        //Realiza a soma de todos os elementos do array e calcula o digito verificador
        //na base 10 de acordo com a regra.
        for (int i = numeros.length; i > 0; i--) {
            if (numeros[i - 1] != null) {
                soma += Integer.valueOf(numeros[i - 1]);
            }
        }
        resto = soma % 10;
        dv = 10 - resto;
        if (dv == 10) dv = 0;
        //retorna o digito verificador
        Log.e("BOLETO", "DV M10 " + dv);
        return String.valueOf(dv);
    }

    /**
     * @author :Allan Tenorio
     * @see :Calculo do Modulo 11 para geracao do digito verificador de boletos bancários.
     * @since :11/07/2012
     */

    //Módulo 11
    //Conforme o esquema abaixo, para calcular o primeiro dígito verificador, cada dígito do número,
    //começando da direita para a esquerda (do dígito menos significativo para o dígito mais significativo)
    //é multiplicado, na ordem, por 2, depois 3, depois 4 e assim sucessivamente, até o primeiro dígito do número.
    //O somatório dessas multiplicações dividido por 11. O resto desta divisão (módulo 11) é subtraido da base (11),
    //o resultado é o dígito verificador. Para calcular o próximo dígito, considera-se o dígito anterior como parte
    //do número e efetua-se o mesmo processo. No exemplo, foi considerado o número 261533:
    //  +---+---+---+---+---+---+   +---+
    //  | 2 | 6 | 1 | 5 | 3 | 3 | - | 9 |
    //  +---+---+---+---+---+---+   +---+
    //    |   |   |   |   |   |
    //   x7  x6  x5  x4  x3  x2
    //    |   |   |   |   |   |
    //   =14 =36  =5 =20  =9  =6 soma = 90
    //    +---+---+---+---+---+-> = (90 / 11) = 8,1818 , resto 2 => DV = (11 - 2) = 9
    public String modulo11(String num) {

        //variáveis de instancia
        int soma = 0;
        int resto = 0;
        int dv = 0;
        String[] numeros = new String[num.length() + 1];
        int multiplicador = 2;

        for (int i = num.length(); i > 0; i--) {
            //Multiplica da direita pra esquerda, incrementando o multiplicador de 2 a 9
            //Caso o multiplicador seja maior que 9 o mesmo recomeça em 2
            if (multiplicador > 9) {
                // pega cada numero isoladamente
                multiplicador = 2;
                numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * multiplicador);
                multiplicador++;
            } else {
                numeros[i] = String.valueOf(Integer.valueOf(num.substring(i - 1, i)) * multiplicador);
                multiplicador++;
            }
        }

        //Realiza a soma de todos os elementos do array e calcula o digito verificador
        //na base 11 de acordo com a regra.
        for (int i = numeros.length; i > 0; i--) {
            if (numeros[i - 1] != null) {
                soma += Integer.valueOf(numeros[i - 1]);
            }
        }
        resto = soma % 11;
        dv = 11 - resto;

        if (dv == 0 || dv == 10 || dv == 11) {
            dv = 1;
        }

        //retorna o digito verificador
        return String.valueOf(dv);
    }

    public String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public String getIdBandeira(String s) {
        /*$bandeiras[1] = 'ELO';
        $bandeiras[2] = 'VISA';
        $bandeiras[3] = 'MASTER';
        $bandeiras[4] = 'AMEX';
        $bandeiras[5] = 'HIPER';
        $bandeiras[6] = 'DINERS';*/
        s = this.removerAcentos(s);
        String idFormaPagamento = "";
        if (Objects.requireNonNull(s).equalsIgnoreCase("ELO")) {
            idFormaPagamento = "1";

        } else if (s.equalsIgnoreCase("VISA")) {
            idFormaPagamento = "2";

        } else if (s.equalsIgnoreCase("MASTER")) {
            idFormaPagamento = "3";

        } else if (s.equalsIgnoreCase("AMEX")) {
            idFormaPagamento = "4";

        } else if (s.equalsIgnoreCase("HIPER")) {
            idFormaPagamento = "5";

        } else if (s.equalsIgnoreCase("DINERS")) {
            idFormaPagamento = "6";

        }

        return idFormaPagamento;
    }

    public String getBandeira(String s) {
        String bandeira = "";
        if (s.equalsIgnoreCase("1"))
            bandeira = "ELO";
        else if (s.equalsIgnoreCase("2"))
            bandeira = "VISA";
        else if (s.equalsIgnoreCase("3"))
            bandeira = "MASTER";
        else if (s.equalsIgnoreCase("4"))
            bandeira = "AMEX";
        else if (s.equalsIgnoreCase("5"))
            bandeira = "HIPER";
        else if (s.equalsIgnoreCase("6"))
            bandeira = "DINERS";

        return bandeira;
    }

    public void ShowMsgSnackbar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void ShowMsgToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public void ShowMsgLog(String tag, String msg) {
        Log.e(tag, msg);
    }
}
