package br.com.zenitech.siacmobile;

import static br.com.zenitech.siacmobile.NumeroPorExtenso.valorPorExtenso;

public class DataPorExtenso {

    public static String dataPorExtenso(String data) {

        String[] dataF = data.split("/");

        String[] dia = {"zero", "um", "dois", "tres", "quatro", "cinco", "seis", "sete", "oito", "nove",
                "dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito",
                "dezenove", "vinte", "vinte e um", "vinte e dois", "vinte e três", "vinte e quatro",
                "vinte e cinco", "vinte e seis", "vinte e sete", "vinte e oito", "vinte e nove", "trinta", "trinta e um"};

        String[] mes = {"", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        //
        String diaF = dia[Integer.parseInt(dataF[0])];
        String mesF = mes[Integer.parseInt(dataF[1])];
        String anoF = valorPorExtenso(Double.parseDouble(dataF[2]));

        return diaF + " de " + mesF + " de " + anoF.replace("real", "").replace("reais", "");
    }
}
