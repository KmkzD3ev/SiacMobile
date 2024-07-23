package br.com.zenitech.siacmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.domains.PosApp;
import br.com.zenitech.siacmobile.domains.UnidadesDomain;

public class Principal2 extends AppCompatActivity {

    private DatabaseHelper bd;
    ClassAuxiliar aux;
    private SharedPreferences prefs;
    AlertDialog alerta;
    TextView textView, txtTransmitida, txtContigencia, txtStatusTransmissao, txtVersao, txtEmpresa, txtCodUnidade, txtDataUltimoSinc;
    ArrayList<PosApp> elementosPos;
    PosApp posApp;
    ArrayList<UnidadesDomain> elementosUnidades;
    UnidadesDomain unidades;

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
                String enderecoBltParam = params.getString("enderecoBlt");
                if (enderecoBltParam != null && !enderecoBltParam.isEmpty()) {
                    if (!Objects.requireNonNull(prefs.getString("enderecoBlt", "")).equalsIgnoreCase(enderecoBltParam)) {
                        prefs.edit().putBoolean("naoPerguntarImpressora", false).apply();
                    }

                    if (!Objects.requireNonNull(enderecoBltParam).isEmpty() &&
                            !prefs.getBoolean("naoPerguntarImpressora", false)) {

                        if (!Objects.requireNonNull(prefs.getString("enderecoBlt", "")).equalsIgnoreCase(enderecoBltParam)) {
                            callDialog(enderecoBltParam);
                        }
                    }
                }
            }
        }

        bd = new DatabaseHelper(this);
        aux = new ClassAuxiliar();

        // Inicialize todos os componentes da interface do usuário
        txtEmpresa = findViewById(R.id.txtEmpresa);
        txtCodUnidade = findViewById(R.id.txtCodUnidade);
        textView = findViewById(R.id.text_home);
        txtVersao = findViewById(R.id.txtVersao);
        txtDataUltimoSinc = findViewById(R.id.txtDataUltimoSinc);

        elementosUnidades = bd.getUnidades();
        if (!elementosUnidades.isEmpty()) {
            unidades = elementosUnidades.get(0);
            txtEmpresa.setText(unidades.getRazao_social());
        }

        posApp = bd.getPos();
        if (posApp != null) {
            txtCodUnidade.setText(posApp.getUnidade());
            textView.setText(String.format("%s | %s", posApp.getSerial(), posApp.getSerie()));
        }

        txtVersao.setText(BuildConfig.VERSION_NAME);
        txtDataUltimoSinc.setText(prefs.getString("data_sincronizado", ""));
    }

    private void callDialog(String impressora) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo_emissor_web);
        builder.setTitle("Nova Impressora");
        builder.setMessage("Deseja salvar como impressora padrão do app?");

        builder.setPositiveButton("Sim", (arg0, arg1) -> {
            prefs.edit().putString("enderecoBlt", impressora).apply();
        });

        builder.setNegativeButton("Não", (arg0, arg1) -> {
            prefs.edit().putBoolean("naoPerguntarImpressora", true).apply();
        });

        builder.setNeutralButton("Depois", (arg0, arg1) -> {
            // Nada a fazer
        });

        alerta = builder.create();
        alerta.show();
    }
}
