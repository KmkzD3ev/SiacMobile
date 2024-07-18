package br.com.zenitech.siacmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import br.com.zenitech.siacmobile.domains.ValesDomain;

public class Vales extends AppCompatActivity {

    ValesDomain valesDomain;
    private DatabaseHelper bd;
    EditText txtCodVale;
    TextView txtValeUti, codigo_vale, unidade_vale, codigo_cliente_vale, numero_vale, valor_vale, situacao_vale, produto_vale;
    LinearLayout ll_form_vales;
    Button btn_uti_vale;
    ClassAuxiliar cAux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vales);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        bd = new DatabaseHelper(this);
        cAux = new ClassAuxiliar();
        txtCodVale = findViewById(R.id.txtCodVale);

        ll_form_vales = findViewById(R.id.ll_form_vales);
        codigo_vale = findViewById(R.id.codigo_vale);
        unidade_vale = findViewById(R.id.unidade_vale);
        codigo_cliente_vale = findViewById(R.id.codigo_cliente_vale);
        numero_vale = findViewById(R.id.numero_vale);
        valor_vale = findViewById(R.id.valor_vale);
        produto_vale = findViewById(R.id.produto_vale);
        txtValeUti = findViewById(R.id.txtValeUti);

        findViewById(R.id.btn_cons_vale).setOnClickListener(v -> ConsultarVale(txtCodVale.getText().toString()));
        btn_uti_vale = findViewById(R.id.btn_uti_vale);
        btn_uti_vale.setOnClickListener(v -> UsarVale(txtCodVale.getText().toString(), codigo_cliente_vale.getText().toString()));

        //
        Intent intent = getIntent();

        if (intent != null) {
            Bundle params = intent.getExtras();

            if (params != null) {
                codigo_cliente_vale.setText(params.getString("codigo"));
                //
                String nomeCliente = cAux.maiuscula1(Objects.requireNonNull(params.getString("nome")).toLowerCase());
                getSupportActionBar().setSubtitle(nomeCliente);
            }
        }
    }

    private void UsarVale(String nVale, String codCli) {
        if (bd.UsarVale(nVale, codCli) == 1) {
            Toast.makeText(this, "Vale utilizado!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void ConsultarVale(String codVale) {
        //ESCONDER O TECLADO
        // TODO Auto-generated method stub
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        valesDomain = bd.ConsVale(codVale);

        if (valesDomain != null) {
            if (valesDomain.getSituacao_vale().equalsIgnoreCase("UTILIZADO")) {
                txtValeUti.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Vale utilizado!", Toast.LENGTH_SHORT).show();
                return;
            }

            ll_form_vales.setVisibility(View.GONE);
            Log.e("VALE", "VALE ENCONTRADO! " + codVale);

            codigo_vale.setText(valesDomain.getCodigo_vale());
            unidade_vale.setText(valesDomain.getUnidade_vale());
            if (codigo_cliente_vale.getText().toString().equalsIgnoreCase("")) {
                codigo_cliente_vale.setText(valesDomain.getCodigo_cliente_vale());
            }
            numero_vale.setText(valesDomain.getNumero_vale());
            valor_vale.setText(cAux.maskMoney(cAux.converterValores(valesDomain.getValor_vale())));
            produto_vale.setText(valesDomain.getProduto_vale());
        } else {
            Toast.makeText(this, "Vale Não Encontrado!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}