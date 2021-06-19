package br.com.zenitech.siacmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

public class Principal2 extends AppCompatActivity {

    private SharedPreferences prefs;
    AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal2);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        // INFORMA QUE A VENDA NÃO ESTÁ SENDO EDITADA PARA NÃO APAGAR QUANDO VOLTAR
        prefs.edit().putBoolean("EditarVenda", false).apply();

        // SALVAR IMPRESSORA
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                //posicao = params.getString("enderecoBlt");
                if (!Objects.requireNonNull(prefs.getString("enderecoBlt", "")).equalsIgnoreCase(params.getString("enderecoBlt"))) {
                    prefs.edit().putBoolean("naoPerguntarImpressora", false).apply();
                }
                //prefs.edit().putBoolean("naoPerguntarImpressora", false).apply();
                //prefs.edit().putString("enderecoBlt", "").apply();

                if (!Objects.requireNonNull(params.getString("enderecoBlt")).equalsIgnoreCase("") &&
                        !prefs.getBoolean("naoPerguntarImpressora", false)) {

                    if (!Objects.requireNonNull(prefs.getString("enderecoBlt", "")).equalsIgnoreCase(params.getString("enderecoBlt"))) {
                        callDialog(params.getString("enderecoBlt"));
                    }
                }

            }
        }
    }

    private void callDialog(String impressora) {

        //
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo_emissor_web);
        //define o titulo
        builder.setTitle("Nova Impressora");
        //define a mensagem
        builder.setMessage("Deseja salvar como impressora padrão do app?");

        //define um botão como positivo
        builder.setPositiveButton("Sim", (arg0, arg1) -> {
            prefs.edit().putString("enderecoBlt", impressora).apply();
        });

        //define um botão como negativo.
        builder.setNegativeButton("Não", (arg0, arg1) -> {
            //Toast.makeText(InformacoesVagas.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            prefs.edit().putBoolean("naoPerguntarImpressora", true).apply();
        });

        //define um botão como negativo.
        builder.setNeutralButton("Depois", (arg0, arg1) -> {
            //Toast.makeText(InformacoesVagas.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            //prefs.edit().putBoolean("naoPerguntarImpressora", true).apply();
        });

        //cria o AlertDialog
        alerta = builder.create();

        //Exibe
        alerta.show();
    }

}