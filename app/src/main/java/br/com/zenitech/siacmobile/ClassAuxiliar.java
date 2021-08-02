package br.com.zenitech.siacmobile;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
        Log.e("TOTAL", "DIVIDIR" + String.valueOf(valor));
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
            parsed = new BigDecimal(this.soNumeros(value)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);

            Log.e("TOTAL", "FORAMATAR NUMERO: " + String.valueOf(parsed));
        } catch (Exception e) {
            Log.e("sua_tag", e.getMessage(), e);
        }
        return parsed;
    }

    //SÓ NÚMEROS
    public String soNumeros(String txt) {
        String numero = txt;

        numero = numero.replaceAll("[^0-9]*", "");

        return numero;
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

    public String zerosAEsquerda(String numero, String banco) {
        // RETIRA TUDO QUE NÃO FOR NÚMERO
        numero = this.soNumeros(numero);
        Log.e("Numero", numero);

        // VERIFICA QUAL É O BANCO
        if (banco.equalsIgnoreCase("BB")) {
            numero = String.format("%08d", Integer.parseInt(numero));
        } else {
            numero = String.format("%08d", Integer.parseInt(numero));
        }
        Log.e("Numero", numero);
        return numero;
    }

    public String DiffDias(String data1, String data2) throws ParseException {
        // Dando um exemplo: quantos dias se passam desde 07/09/1822 até 05/06/2006?
        DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
        df.setLenient(false);
        Date d1 = df.parse (data1); //"07/09/1822"
        System.out.println (d1);
        Date d2 = df.parse (data2); //"05/06/2006"
        System.out.println (d2);
        long dt = (Objects.requireNonNull(d2).getTime() - Objects.requireNonNull(d1).getTime()) + 3600000; // 1 hora para compensar horário de verão
        System.out.println (dt / 86400000L); // passaram-se 67111 dias

        return String.valueOf(dt / 86400000L);
    }

    public String numCodBarraBB(String valor, DatabaseHelper bd) {

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
        ContasBancarias conta = bd.updateFinalizarVenda();

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

        String dias = "";
        try {
            dias = this.DiffDias("07/10/1997", "17/11/2010");//"04/07/2000"
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //dias = String.valueOf(this.somar(new String[]{dias, "1"}));

        // Parte 1
        numCodBarra.append("001");  // Código do Banco na Câmara de Compensação = "001"
        // Parte 2
        numCodBarra.append("9");    // Código da Moeda = "9"
        // Parte 3
        numCodBarra.append("|");     // DV do Código de Barras (Anexo VI)
        // Parte 4
        numCodBarra.append(dias);     // Fator de Vencimento (Anexo IV)
        numCodBarra.append("|");
        // Parte 5
        numCodBarra.append(this.zerosAEsquerda(valor, "BB"));  // Valor
        // Parte 6
        numCodBarra.append("");     // Número do Convênio de Seis Posições
        // Parte 7
        numCodBarra.append("");     // Nosso Número Livre do cliente.
        // Parte 8
        numCodBarra.append("");     // "21" Tipo de Modalidade de Cobrança.


        return numCodBarra.toString();
    }
}
