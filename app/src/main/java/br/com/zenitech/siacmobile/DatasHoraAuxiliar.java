package br.com.zenitech.siacmobile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kma_s on 10/2/17.
 */

public class DatasHoraAuxiliar {
    //
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormat_dataHora = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
    // OU
    SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
    Date data = new Date();
    Calendar cal = Calendar.getInstance();


    public String dataAtual() {
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String dataAtual = dateFormat.format(data_atual);

        return dataAtual;
    }

    public String horaAtual() {
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String horaAtual = dateFormat_hora.format(data_atual);

        return horaAtual;
    }
}
