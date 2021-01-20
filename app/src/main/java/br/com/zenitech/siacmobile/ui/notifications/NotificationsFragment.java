package br.com.zenitech.siacmobile.ui.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.EnviarDadosServidor;
import br.com.zenitech.siacmobile.FinanceiroDaVenda;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.Sincronizar;
import br.com.zenitech.siacmobile.SplashScreen;
import br.com.zenitech.siacmobile.domains.EnviarDados;
import br.com.zenitech.siacmobile.ftps.MyFTPClientFunctions;
import br.com.zenitech.siacmobile.interfaces.IEnviarDados;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {
    private Context context = null;
    private CardView cv_btn_resetar_app;
    private LinearLayout cv_enviar_dados;
    private DatabaseHelper bd;
    private AlertDialog alerta;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(
                R.layout.fragment_notifications, container, false);
        setHasOptionsMenu(true);
        context = this.getContext();
        bd = new DatabaseHelper(context);

        cv_enviar_dados = view.findViewById(R.id.cv_enviar_dados);
        cv_enviar_dados.setOnClickListener(v -> enviarDados());
        //
        cv_btn_resetar_app = view.findViewById(R.id.cv_btn_resetar_app);
        cv_btn_resetar_app.setOnClickListener(v -> mostrarMsg());

        //
        if (bd.getAllVendas().size() > 0 || bd.getAllRecebidos().size() > 0) {
            cv_enviar_dados.setVisibility(View.VISIBLE);
            cv_btn_resetar_app.setVisibility(View.GONE);
        } else {
            cv_enviar_dados.setVisibility(View.GONE);
            cv_btn_resetar_app.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void enviarDados() {
        startActivity(new Intent(context, EnviarDadosServidor.class));
    }

    private void resetarApp() {
        Toast.makeText(getContext(), "App resetado com sucesso!",
                Toast.LENGTH_LONG).show();

        //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
        getContext().deleteDatabase("siacmobileDB");
        Intent i = new Intent(getContext(), SplashScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void mostrarMsg() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Siac Mobile");
        //define a mensagem
        String msg = "Deseja realmente resetar o app ao estado inicial?";
        builder.setMessage(msg);
        //define um botão como positivo
        builder.setPositiveButton("SIM", (arg0, arg1) -> resetarApp());

        //define um botão como negativo.
        builder.setNegativeButton("NÃO", (arg0, arg1) -> {

        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();
    }
}