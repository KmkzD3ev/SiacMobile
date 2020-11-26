package br.com.zenitech.siacmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.domains.EnviarDados;
import br.com.zenitech.siacmobile.domains.VendasDomain;
import br.com.zenitech.siacmobile.interfaces.IEnviarDados;
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
    String[] dados, dadosFin;

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
        dados = bd.EnviarDados();
        Log.i(TAG + " Venda", dados[0]);
        Log.i(TAG + " Venda", dados[1]);
        Log.i(TAG + " Venda", dados[2]);
        Log.i(TAG + " Venda", dados[3]);
        Log.i(TAG + " Venda", dados[4]);
        Log.i(TAG + " Venda", dados[5]);
        //
        dadosFin = bd.EnviarDadosFinanceiro();
        Log.i(TAG + " Financeiro", dadosFin[0]);
        Log.i(TAG + " Financeiro", dadosFin[1]);
        Log.i(TAG + " Financeiro", dadosFin[2]);
        Log.i(TAG + " Financeiro", dadosFin[3]);
        Log.i(TAG + " Financeiro", dadosFin[4]);

        findViewById(R.id.cv_enviar_dados).setOnClickListener(v -> enviarDados());
    }

    void enviarDados() {
        findViewById(R.id.cv_btn_enviar_dados).setVisibility(View.GONE);

        //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
        pd = ProgressDialog.show(context, "Enviando dados...", "Aguarde...",
                true, false);

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
                "" + dadosFin[5]
        );

        call.enqueue(new Callback<ArrayList<EnviarDados>>() {
            @Override
            public void onResponse(Call<ArrayList<EnviarDados>> call, Response<ArrayList<EnviarDados>> response) {

                //
                final ArrayList<EnviarDados> sincronizacao = response.body();
                if (sincronizacao != null) {

                    Log.i(TAG, sincronizacao.get(0).getnVenda());
                    Log.i(TAG, sincronizacao.get(0).getnVendaSiac());

                    for (EnviarDados enviarDados : sincronizacao) {
                        bd.updateVendaApp(enviarDados.getnVenda(), enviarDados.getnVendaSiac());
                    }

                    //
                    ArrayList<VendasDomain> v = bd.vendasNaoSinc();
                    Log.i(TAG, String.valueOf(v.size()));
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

                    Toast.makeText(EnviarDadosServidor.this, "Dados enviado com sucesso!", Toast.LENGTH_SHORT).show();


                    //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
                    context.deleteDatabase("siacmobileDB");
                    Intent i = new Intent(context, SincronizarBancoDados.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}