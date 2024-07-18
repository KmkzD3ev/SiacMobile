package br.com.zenitech.siacmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.domains.EnviarDados;
import br.com.zenitech.siacmobile.domains.Sincronizador;
import br.com.zenitech.siacmobile.interfaces.IEnviarDados;
import br.com.zenitech.siacmobile.interfaces.ISincronizar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnviarDadosServidor extends AppCompatActivity {

    //
    private SharedPreferences prefs;
    ClassAuxiliar classAuxiliar;
    private DatabaseHelper bd;
    private static final String TAG = "GerenciarCF";
    private Context context = null;
    private ProgressDialog pd;
    String[] dados, dadosFin, dadosContasReceber, dadosVales;
    private int quant = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_dados_servidor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //
        context = this;
        prefs = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        classAuxiliar = new ClassAuxiliar();
        bd = new DatabaseHelper(context);

        //
        dados = bd.EnviarDados(classAuxiliar.exibirData(prefs.getString("data_movimento_atual", "")));

        //
        dadosFin = bd.EnviarDadosFinanceiro();

        //
        dadosContasReceber = bd.EnviarDadosContasReceber();

        dadosVales = bd.EnviarDadosVales(prefs.getString("data_movimento_atual", ""));

        findViewById(R.id.cv_enviar_dados).setOnClickListener(v -> {
            //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
            pd = ProgressDialog.show(context, "Enviando dados...", "Aguarde...",
                    true, false);

            enviarDados();
            enviarDadosContasReceber();
            enviarDadosVales();
        });
    }

    void enviarDados() {
        findViewById(R.id.cv_btn_enviar_dados).setVisibility(View.GONE);
        //
        final IEnviarDados iEnviarDados = IEnviarDados.retrofit.create(IEnviarDados.class);

        final Call<ArrayList<EnviarDados>> call = iEnviarDados.enviarDados(
                "850",
                prefs.getString("serial", ""),
                "" + dados[0],
                "" + dados[1],
                "" + dados[2],
                "" + dados[3],
                "" + dados[4],
                "" + dados[5],
                "" + dadosFin[0],
                "" + dadosFin[1],
                "" + dadosFin[2],
                "" + dadosFin[3],
                "" + dadosFin[4],
                "" + dadosFin[5],
                "" + dadosFin[6],
                "" + dadosFin[7]
        );

        call.enqueue(new Callback<ArrayList<EnviarDados>>() {
            @Override
            public void onResponse(Call<ArrayList<EnviarDados>> call, Response<ArrayList<EnviarDados>> response) {
                final ArrayList<EnviarDados> sincronizacao = response.body();
                if (sincronizacao != null) {
                    quant++;
                    FinalizarPOS();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EnviarDados>> call, Throwable t) {

                //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }

    void enviarDadosContasReceber() {
        final IEnviarDados iEnviarDados = IEnviarDados.retrofit.create(IEnviarDados.class);
        final Call<ArrayList<EnviarDados>> call = iEnviarDados.enviarDadosContasReceber(
                "851",
                prefs.getString("serial", ""),
                "" + dadosContasReceber[0],
                "" + dadosContasReceber[1],
                "" + dadosContasReceber[2],
                "" + dadosContasReceber[3],
                "" + dadosContasReceber[4],
                "" + dadosContasReceber[5],
                "" + dadosContasReceber[6],
                "" + dadosContasReceber[7],
                "" + dadosContasReceber[8],
                "" + dadosContasReceber[9],
                "" + dadosContasReceber[10],
                "" + dadosContasReceber[11],
                "" + dadosContasReceber[12],
                "" + dadosContasReceber[13]
        );

        call.enqueue(new Callback<ArrayList<EnviarDados>>() {
            @Override
            public void onResponse(Call<ArrayList<EnviarDados>> call, Response<ArrayList<EnviarDados>> response) {
                final ArrayList<EnviarDados> sincronizacao = response.body();
                if (sincronizacao != null) {
                    quant++;
                    FinalizarPOS();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EnviarDados>> call, Throwable t) {

                //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }

    private void enviarDadosVales() {
        final IEnviarDados iEnviarDados = IEnviarDados.retrofit.create(IEnviarDados.class);
        final Call<ArrayList<EnviarDados>> call = iEnviarDados.enviarDadosVales(
                "753",
                prefs.getString("serial", ""),
                "" + dadosVales[0],
                "" + dadosVales[2],
                "" + dadosVales[3],
                "" + dadosVales[4]
        );

        call.enqueue(new Callback<ArrayList<EnviarDados>>() {
            @Override
            public void onResponse(Call<ArrayList<EnviarDados>> call, Response<ArrayList<EnviarDados>> response) {
                final ArrayList<EnviarDados> sincronizacao = response.body();
                if (sincronizacao != null) {
                    quant++;
                    FinalizarPOS();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EnviarDados>> call, Throwable t) {

                //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }

    void FinalizarPOS() {

        //if(quant < 3) return;

        //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
        pd = ProgressDialog.show(context, "Finalizando POS...", "Aguarde...",
                true, false);

        final ISincronizar iSincronizar = ISincronizar.retrofit.create(ISincronizar.class);
        final Call<Sincronizador> call = iSincronizar.ativarDesativarPOS(
                "desativar",
                prefs.getString("serial_app", "")
        );

        call.enqueue(new Callback<Sincronizador>() {
            @Override
            public void onResponse(@NonNull Call<Sincronizador> call, @NonNull Response<Sincronizador> response) {
                final Sincronizador sincronizacao = response.body();
                if (sincronizacao != null) {
                    if (sincronizacao.getErro().equalsIgnoreCase("0")) {

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

        //
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
        bd.FecharConexao();
        context.deleteDatabase("siacmobileDB");
        Intent i = new Intent(context, SincronizarBancoDados.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}