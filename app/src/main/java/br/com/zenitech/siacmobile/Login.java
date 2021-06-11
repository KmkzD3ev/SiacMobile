package br.com.zenitech.siacmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.Executor;

import br.com.zenitech.siacmobile.domains.Conta;
import br.com.zenitech.siacmobile.interfaces.ILogin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    //
    private String TAG = "Login";
    private SharedPreferences prefs;
    private TextView txtNomeUsuarioLogin;
    private EditText senhaLogin;
    private Context context;
    private ProgressDialog pd;

    // Autenticação por impressão digital
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    //
    private AlertDialog alerta;
    private EditText etLogin, etSenha;
    private ClassAuxiliar classAuxiliar;

    private LinearLayout llAutenticar, llEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        context = this;
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        classAuxiliar = new ClassAuxiliar();

        llAutenticar = findViewById(R.id.llAutenticar);
        llEntrar = findViewById(R.id.llEntrar);

        if (!Objects.requireNonNull(prefs.getString("login_usuario", "")).equalsIgnoreCase("")) {
            llAutenticar.setVisibility(View.GONE);
            llEntrar.setVisibility(View.VISIBLE);
        }

        txtNomeUsuarioLogin = findViewById(R.id.txtNomeUsuarioLogin);
        txtNomeUsuarioLogin.setText(prefs.getString("nome_vendedor", "Nome Usuário"));

        senhaLogin = findViewById(R.id.senhaLogin);

        //
        etLogin = findViewById(R.id.login);
        etSenha = findViewById(R.id.senha);


        //logar
        findViewById(R.id.btn_logar).setOnClickListener(view -> {
            //ESCODER O TECLADO
            // TODO Auto-generated method stub
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception e) {
                // TODO: handle exception
            }

            //
            if (Objects.requireNonNull(getMd5Hash(senhaLogin.getText().toString())).equals(prefs.getString("senha_usuario", ""))) {

                //startActivity(new Intent(Login.this, Principal.class));
                Intent i = new Intent(Login.this, Principal2.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            } else {
                Snackbar.make(view, "Senha inválida.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });

        findViewById(R.id.btnReset).setOnClickListener(view -> {
            prefs.edit().putBoolean("reset", true).apply();

            //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
            //getContext().deleteDatabase("siacmobileDB");
            Intent i = new Intent(context, SplashScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });


        findViewById(R.id.btn_entrar).setOnClickListener(v -> {

            //ESCODER O TECLADO
            // TODO Auto-generated method stub
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception e) {
                // TODO: handle exception
            }

            //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
            pd = ProgressDialog.show(context, "Sincronizando...", "Verificando dados de login",
                    true, false);


            //
            final ILogin login = ILogin.retrofit.create(ILogin.class);

            //
            final Call<Conta> call = login.login(
                    etLogin.getText().toString(),
                    etSenha.getText().toString(),
                    "login",
                    prefs.getString("serial", "")
            );

            call.enqueue(new Callback<Conta>() {
                @Override
                public void onResponse(Call<Conta> call, Response<Conta> response) {

                    //
                    final Conta sincronizacao = response.body();
                    if (sincronizacao != null) {

                        //
                        runOnUiThread(() -> {

                            //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }

                            if (sincronizacao.getErro().equals("")) {

                                //SALVA OS DADOS DO USUÁRIO
                                prefs.edit().putString("unidade_vendedor", sincronizacao.getUnidade_vendedor()).apply();
                                prefs.edit().putString("unidade_usuario", sincronizacao.getUnidade_vendedor()).apply();
                                prefs.edit().putString("codigo_usuario", sincronizacao.getCodigo_vendedor()).apply();
                                prefs.edit().putString("login_usuario", sincronizacao.getUsuario_vendedor()).apply();
                                prefs.edit().putString("senha_usuario", sincronizacao.getSenha_vendedor()).apply();
                                prefs.edit().putString("usuario_atual", sincronizacao.getUsuario_atual()).apply();
                                prefs.edit().putString("nome_vendedor", sincronizacao.getNome_vendedor()).apply();
                                prefs.edit().putString("data_movimento", classAuxiliar.inserirDataAtual()).apply();

                                //GERAR BANCO ONLINE
                                verificarDispBiometria();

                            } else {
                                Snackbar.make(v, "Usuário não encontrado.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Conta> call, Throwable t) {

                    Snackbar.make(v, "Não conseguimos acesso ao servidor, verifique sua conexão.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
        });

        if (Objects.requireNonNull(prefs.getString("biometria", "")).equalsIgnoreCase("Sim")) {
            autenticarPorBiometria();
        }
    }

    void verificarDispBiometria() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d(TAG, "App pode autenticar usando biometria.");
                alerta();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e(TAG, "Não há recursos biométricos disponíveis neste dispositivo.");
                loginRealizado();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e(TAG, "Os recursos biométricos não estão disponíveis no momento.");
                loginRealizado();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e(TAG, "O usuário não associou credenciais biométricas à sua conta.");
                loginRealizado();
                break;
        }
    }

    void autenticarPorBiometria() {
        // ******
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Login.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Erro de autenticação: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                /*Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();*/
                Intent i = new Intent(Login.this, Principal2.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Falha na autenticação",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Siac Mobile")
                .setSubtitle("Faça login usando sua credencial biométrica")
                .setNegativeButtonText("Entrar com senha")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void alerta() {

        //
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_baseline_fingerprint_24);
        //define o titulo
        builder.setTitle("Siac Mobile");
        String str = "Gostaria de usar sua biometria no próximo login?!";
        //define a mensagem
        builder.setMessage(str);

        //define um botão como positivo
        builder.setPositiveButton("Sim", (arg0, arg1) -> {
            prefs.edit().putString("biometria", "sim").apply();
            loginRealizado();
        });

        //define um botão como negativo.
        builder.setNeutralButton("Não", (arg0, arg1) -> {
            prefs.edit().putString("biometria", "nao").apply();
            loginRealizado();
        });

        //cria o AlertDialog
        alerta = builder.create();

        //Exibe
        alerta.show();
    }

    void loginRealizado() {
        Intent i = new Intent(context, Principal2.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            StringBuilder md5 = new StringBuilder(number.toString(16));
            while (md5.length() < 32)
                md5.insert(0, "0");
            return md5.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5", Objects.requireNonNull(e.getLocalizedMessage()));
            return null;
        }
    }

}
