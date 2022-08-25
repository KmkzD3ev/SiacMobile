package br.com.zenitech.siacmobile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

//import static br.com.zenitech.siacmobile.ContasReceberBaixarConta.txtVencimentoFormaPagamentoReceber;
//import static br.com.zenitech.siacmobile.FinanceiroDaVenda.txtVencimentoFormaPagamento;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    Intent intent;
    ProgressDialog dialog;
    String url;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(Long.parseLong("1480550404974"));
        //datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        //Create a new instance of DatePickerDialog and return it
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, final int year, final int month, final int day) {
        // Do something with the date chosen by the user
//
        String data = "" + (day < 10 ? "0" + day : day) + (month < 10 ? "0" + (month + 1) : (month + 1)) + year;
        String dataFormatada = "" + (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + (month + 1) : (month + 1)) + "/" + year;

        /*try {
            txtVencimentoFormaPagamento.setText(dataFormatada);

        } catch (Exception e) {

        }

        try {
            txtVencimentoFormaPagamentoReceber.setText(dataFormatada);

        } catch (Exception e) {

        }*/
    }
}