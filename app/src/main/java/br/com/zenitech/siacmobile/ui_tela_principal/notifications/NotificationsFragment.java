package br.com.zenitech.siacmobile.ui_tela_principal.notifications;

import static android.content.Context.MODE_PRIVATE;
import static br.com.zenitech.siacmobile.Configuracoes.getApplicationName;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.EnviarDadosServidor;
import br.com.zenitech.siacmobile.ImpressoraPOS;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.SincronizarBancoDados;
import br.com.zenitech.siacmobile.SplashScreen;
import br.com.zenitech.siacmobile.domains.Sincronizador;
import br.com.zenitech.siacmobile.interfaces.ISincronizar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import stone.application.StoneStart;
import stone.utils.Stone;

public class NotificationsFragment extends Fragment {
    private Context context = null;
    private CardView cv_btn_resetar_app, cv_btn_finalizar_app;
    private LinearLayout cv_enviar_dados;
    private DatabaseHelper bd;
    private AlertDialog alerta;
    private ProgressDialog pd;
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
        //
        cv_btn_finalizar_app = view.findViewById(R.id.cv_btn_finalizar_app);
        cv_btn_finalizar_app.setOnClickListener(v -> mostrarMsgFinalizarPOS());

        view.findViewById(R.id.cv_btn_print).setOnClickListener(v -> {
            Intent i = new Intent(getContext(), ImpressoraPOS.class);
            i.putExtra("imprimir", "Boleto");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        try {
            //
            if (bd.getAllVendas().size() > 0 || bd.getAllRecebidos().size() > 0 || bd.getAllVales().size() > 0) {
                cv_enviar_dados.setVisibility(View.VISIBLE);
                //cv_btn_resetar_app.setVisibility(View.GONE);
                cv_btn_finalizar_app.setVisibility(View.GONE);
            } else {
                cv_enviar_dados.setVisibility(View.GONE);
                //cv_btn_resetar_app.setVisibility(View.VISIBLE);
                cv_btn_finalizar_app.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            cv_enviar_dados.setVisibility(View.GONE);
            Log.e("Enviar Dados", Objects.requireNonNull(e.getMessage()));
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

    private void mostrarMsgFinalizarPOS() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Siac Mobile");
        //define a mensagem
        String msg = "Deseja realmente finalizar o POS?";
        builder.setMessage(msg);
        //define um botão como positivo
        builder.setPositiveButton("SIM", (arg0, arg1) -> FinalizarPOS());

        //define um botão como negativo.
        builder.setNegativeButton("NÃO", (arg0, arg1) -> {

        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();
    }


    void FinalizarPOS() {

        //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
        pd = ProgressDialog.show(context, "Finalizando POS...", "Aguarde...",
                true, false);

        final ISincronizar iSincronizar = ISincronizar.retrofit.create(ISincronizar.class);

        final Call<Sincronizador> call = iSincronizar.ativarDesativarPOS("desativar", prefs.getString("serial_app", ""));

        call.enqueue(new Callback<Sincronizador>() {
            @Override
            public void onResponse(@NonNull Call<Sincronizador> call, @NonNull Response<Sincronizador> response) {

                //
                final Sincronizador sincronizacao = response.body();
                if (sincronizacao != null) {
                    if (sincronizacao.getErro().equalsIgnoreCase("0")) {
                        prefs.edit().putBoolean("sincronizado", false).apply();
                        prefs.edit().putString("unidade_vendedor", "").apply();
                        prefs.edit().putString("unidade_usuario", "").apply();
                        prefs.edit().putString("codigo_usuario", "").apply();
                        prefs.edit().putString("login_usuario", "").apply();
                        prefs.edit().putString("senha_usuario", "").apply();
                        prefs.edit().putString("usuario_atual", "").apply();
                        prefs.edit().putString("nome_vendedor", "").apply();
                        prefs.edit().putString("data_movimento", "").apply();
                        prefs.edit().putString("biometria", "").apply();

                        //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }

                        Toast.makeText(context, "POS Finalizado!", Toast.LENGTH_SHORT).show();


                        //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
                        context.deleteDatabase("siacmobileDB");
                        Intent i = new Intent(context, SincronizarBancoDados.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        //requireActivity().onBackPressed();
                    }
                } else {
                    Toast.makeText(context, "Não foi possível Finalizar o POS!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Sincronizador> call, @NonNull Throwable t) {
                //msgErro = "Não conseguimos ativar o app! Tente novamente em alguns instantes.";
                Toast.makeText(context, "Falha - " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}