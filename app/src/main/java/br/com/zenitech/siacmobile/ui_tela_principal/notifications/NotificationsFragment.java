package br.com.zenitech.siacmobile.ui_tela_principal.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.List;

import br.com.zenitech.siacmobile.Configuracoes;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.EnviarDadosServidor;
import br.com.zenitech.siacmobile.Impressora;
import br.com.zenitech.siacmobile.ImpressoraPOS;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.SplashScreen;
import stone.application.StoneStart;
import stone.user.UserModel;
import stone.utils.Stone;

import static android.content.Context.MODE_PRIVATE;
import static br.com.zenitech.siacmobile.Configuracoes.getApplicationName;

public class NotificationsFragment extends Fragment {
    private Context context = null;
    private CardView cv_btn_resetar_app;
    private LinearLayout cv_enviar_dados;
    private DatabaseHelper bd;
    private AlertDialog alerta;
    SharedPreferences prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(
                R.layout.fragment_notifications, container, false);
        setHasOptionsMenu(true);
        context = this.getContext();
        bd = new DatabaseHelper(context);

        prefs = context.getSharedPreferences("preferencias", MODE_PRIVATE);

        cv_enviar_dados = view.findViewById(R.id.cv_enviar_dados);
        cv_enviar_dados.setOnClickListener(v -> enviarDados());
        //
        cv_btn_resetar_app = view.findViewById(R.id.cv_btn_resetar_app);
        cv_btn_resetar_app.setOnClickListener(v -> mostrarMsg());

        view.findViewById(R.id.cv_btn_print).setOnClickListener(v -> {
            Intent i = new Intent(getContext(), ImpressoraPOS.class);
            i.putExtra("imprimir", "Teste");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        //
        if (bd.getAllVendas().size() > 0 || bd.getAllRecebidos().size() > 0) {
            cv_enviar_dados.setVisibility(View.VISIBLE);
            //cv_btn_resetar_app.setVisibility(View.GONE);
        } else {
            cv_enviar_dados.setVisibility(View.GONE);
            //cv_btn_resetar_app.setVisibility(View.VISIBLE);
        }

        //cv_enviar_dados.setVisibility(View.VISIBLE);
        cv_btn_resetar_app.setVisibility(View.VISIBLE);
        iniciarStone();
        return view;
    }

    private void enviarDados() {
        startActivity(new Intent(context, EnviarDadosServidor.class));
    }

    private void resetarApp() {
        /*Toast.makeText(getContext(), "App resetado com sucesso!",
                Toast.LENGTH_LONG).show();*/

        prefs.edit().putBoolean("reset", true).apply();

        //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
        //getContext().deleteDatabase("siacmobileDB");
        Intent i = new Intent(getContext(), SplashScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    // Iniciar o Stone
    void iniciarStone() {
        // O primeiro passo é inicializar o SDK.
        StoneStart.init(context);
        /*Em seguida, é necessário chamar o método setAppName da classe Stone,
        que recebe como parâmetro uma String referente ao nome da sua aplicação.*/
        Stone.setAppName(getApplicationName(context));
        //Ambiente de Sandbox "Teste"
        //Stone.setEnvironment(new Configuracoes().Ambiente());
        //Ambiente de Produção
        //Stone.setEnvironment((Environment.PRODUCTION));

        // Esse método deve ser executado para inicializar o SDK
        //List<UserModel> userList = StoneStart.init(context);

        // Quando é retornado null, o SDK ainda não foi ativado
        /*if (userList != null) {
            // O SDK já foi ativado.
            _pinpadAtivado();

        } else {
            // Inicia a ativação do SDK
            ativarStoneCode();
        }*/
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