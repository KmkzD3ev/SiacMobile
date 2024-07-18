package br.com.zenitech.siacmobile;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import stone.environment.Environment;

import static stone.environment.Environment.PRODUCTION;
import static stone.environment.Environment.SANDBOX;

public class Configuracoes {
    // VERSÃO DO BANCO DE DADOS
    public static int VERSAO_BD = 13;

    // VERSÃO DO APP PARA GERAR NOVOS CAMPOS NO BANCO DE DADOS ONLINE
    public static String VERSAO_APP = "2054";

    // FALSE PARA DEFINIR PRODUÇÃO
    final boolean ambinteTeste = false;

    // INFORMA SE O APARELHO UTILIZADO É UM POS
    // SEMPRE RETORNAR FALSE CONFORME FOR GERADO O BUILD PARA PLAY STORE
    public boolean GetDevice() {
        //return true;
        return false;
    }

    // RETORNA SE O AMBIENTE É DE PRODUÇÃO OU DE TESTE
    public Environment Ambiente() {
        if (ambinteTeste)
            return SANDBOX;
        else
            return PRODUCTION;
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public String GetUrlServer() {
        return "http://191.243.197.5/";
        //return  "https://emissorweb.com.br";
    }

    public String GetUFCeara(){
        return "CE";
    }
}
