package br.com.zenitech.siacmobile;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

import br.com.zenitech.siacmobile.domains.Sincronizador;
import br.com.zenitech.siacmobile.interfaces.ISincronizar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;
    private DatabaseHelper bd;
    TextView txtSerial;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = this;
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        ed = prefs.edit();
        txtSerial = findViewById(R.id.txtSerial);

        if (prefs.getBoolean("reset", false)) {
            Intent i = new Intent(this, ResetApp.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return;
        }

        if (!prefs.getString("serial_app", "").equalsIgnoreCase("")) {
            txtSerial.setVisibility(View.VISIBLE);
            txtSerial.setText(String.format("SERIAL\n%s", prefs.getString("serial_app", "")));
        }
        /*DataBaseOpenHelper dataBaseOpenHelper = new DataBaseOpenHelper(context);
        SQLiteDatabase conexao = dataBaseOpenHelper.getWritableDatabase();
        //*/
        bd = new DatabaseHelper(this);

        final ISincronizar iSincronizar = ISincronizar.retrofit.create(ISincronizar.class);

        final Call<Sincronizador> call = iSincronizar.forcarResetApp(
                "forcar_reset_app_siac",
                prefs.getString("serial_app", "")
        );

        call.enqueue(new Callback<Sincronizador>() {
            @Override
            public void onResponse(@NonNull Call<Sincronizador> call, @NonNull Response<Sincronizador> response) {
                final Sincronizador sincronizacao = response.body();
                if (Objects.requireNonNull(sincronizacao).getErro().equalsIgnoreCase("ok")) {
                    resetarApp();
                } else {
                    /*txtMsgReset.setText("Não foi possível resetar o App, verifique as informações e tente novamente.");
                    erro();*/
                    avancar();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Sincronizador> call, @NonNull Throwable t) {
                Log.i("ResetApp", Objects.requireNonNull(t.getMessage()));
                avancar();
            }
        });
    }

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);

                Toast.makeText(getBaseContext(), "O App foi resetado com sucesso!", Toast.LENGTH_LONG).show();
            }

            //prefs.edit().putBoolean("reset", false).apply();
            Intent i = new Intent(getBaseContext(), SplashScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetarApp() {
        clearAppData();
    }

    private void avancar() {
        // ESPERA 2.3 SEGUNDOS PARA  SAIR DO SPLASH
        new Handler().postDelayed(() -> {

            //
            if (Objects.requireNonNull(prefs.getString("primeiro_acesso", "")).equalsIgnoreCase("")) {

                //CRIA UM DIRETÓRIO PARA BAIXAR O BANCO ONLINE
                if (getDirFromSDCard() == null) {
                    Toast.makeText(SplashScreen.this, "Não foi possivél criar o Diretório!", Toast.LENGTH_LONG).show();
                }

                //APLICA O PRIMEIRO ACESSO
                ed.putString("primeiro_acesso", "true").apply();

                //
                //Toast.makeText(SplashScreen.this, "Sincronizar!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(SplashScreen.this, SincronizarBancoDados.class));
            } else {

                if (prefs.getBoolean("sincronizado", false)) {

                    //VERIFICA SE O BANCO EXISTE
                    if (bd.checkDataBase()) {
                        //SE O BANCO ESXISTIR VAI PARA O LOGIN
                        //Toast.makeText(SplashScreen.this, "Login!", Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(SplashScreen.this, Login.class));
                        Intent i = new Intent(SplashScreen.this, Principal2.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        //SE O BANCO NÃO EXISTIR VAI PARA SINCRONIZAÇÃO
                        //Toast.makeText(SplashScreen.this, "Sincronizar!", Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(SplashScreen.this, Sincronizar.class));
                        startActivity(new Intent(SplashScreen.this, SincronizarBancoDados.class));
                    }
                } else {
                    startActivity(new Intent(SplashScreen.this, SincronizarBancoDados.class));
                }
            }

            finish();
        }, 4000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private File getDirFromSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File sdcard = Environment.getExternalStorageDirectory()
                    .getAbsoluteFile();
            File dir = new File(sdcard, "Siac_Mobile" + File.separator + "BD");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        } else {
            return null;
        }
    }
}
