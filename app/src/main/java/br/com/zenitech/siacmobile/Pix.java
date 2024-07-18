package br.com.zenitech.siacmobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.stone.posandroid.providers.PosPrintProvider;
import br.com.zenitech.siacmobile.domains.PixDomain;
import br.com.zenitech.siacmobile.domains.UnidadesDomain;
import br.com.zenitech.siacmobile.interfaces.IPix;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import stone.application.interfaces.StoneCallbackInterface;

public class Pix extends AppCompatActivity {

    private DatabaseHelper bd;
    ImageView imageView2;
    TextView textView, textView2, tvTime;
    String apiKey, cliCob, pedido, valor;
    String idLisForPag, idPagamento, idForPagPix;
    AlertDialog alerta;
    Bitmap btm;
    SharedPreferences prefs;
    LinearLayoutCompat llcPagarPix, llcPixPago, llcPixNaoPago, llcConsultarPix;
    boolean consulta = false;
    int totPrint = 0;

    /*ArrayList<PosApp> elementosPos;
    PosApp posApp;*/

    ArrayList<UnidadesDomain> elementosUnidades;
    UnidadesDomain unidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pix);
        bd = new DatabaseHelper(this);
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);

        /*elementosPos = bd.getPos();
        posApp = elementosPos.get(0);*/

        elementosUnidades = bd.getUnidades();
        unidades = elementosUnidades.get(0);

        //
        llcPagarPix = findViewById(R.id.llcPagarPix);
        llcPixPago = findViewById(R.id.llcPixPago);
        llcPixNaoPago = findViewById(R.id.llcPixNaoPago);
        llcConsultarPix = findViewById(R.id.llcConsultarPix);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        tvTime = findViewById(R.id.tvTime);

        imageView2 = findViewById(R.id.imageView2);
        //String cb64 = "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";

        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                idLisForPag = params.getString("idLisForPag");
                textView2.setText(params.getString("valor"));
                apiKey = params.getString("apiKey");
                cliCob = params.getString("cliCob");
                valor = String.valueOf(new ClassAuxiliar().converterValores(params.getString("valor")));
                pedido = params.getString("pedido");//"Serial: " + prefs.getString("serial", "") + ", Pedido: " +
                idForPagPix = params.getString("idForPagPix");
                idPagamento = params.getString("idPagamento");
            }
        }


        getQrCodePayment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VerificarActivityAtiva.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VerificarActivityAtiva.activityPaused();
    }

    private void getQrCodePayment() {

        //Log.e("PIX", "cobranca | " + " | " + apiKey + " | " + cliCob + " | " + pedido + " | " + valor);
        final IPix iPix = IPix.retrofit.create(IPix.class);
        final Call<PixDomain> call = iPix.getImgQrCode(
                "token",
                "shipay",
                "shipay",
                "shipay",
                pedido,
                valor,
                "05",
                unidades.getRazao_social()
        );
        call.enqueue(new Callback<PixDomain>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                try {
                    final PixDomain infoPix = (PixDomain) response.body();
                    if (!infoPix.getEncodedImage().equalsIgnoreCase("")) {
                        Log.e("PIX", infoPix.getId());
                        btm = convertBase64ToBitmap(infoPix.getEncodedImage());
                        imageView2.setImageBitmap(btm);

                        idPagamento = infoPix.getId();

                        ImprimirComprovante();
                    }
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
            }
        });
    }

    void ImprimirComprovante() {
        // --------------------     COMPROVANTE PIX         --------------------------------------------

        PosPrintProvider ppp = new PosPrintProvider(this);
        ppp.setConnectionCallback(new StoneCallbackInterface() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Toast.makeText(getBaseContext(), "Erro ao imprimir: " + ppp.getListOfErrors(), Toast.LENGTH_SHORT).show();
            }
        });

        ClassAuxiliar aux = new ClassAuxiliar();
        ppp.addLine("        Comprovante Pix\n");
        ppp.addLine("        Via do Lojista");
        ppp.addLine("Pedido: " + pedido);
        ppp.addLine("Identificador: \n" + idPagamento);
        ppp.addLine("Valor: " + aux.maskMoney(new BigDecimal(valor)));
        ppp.addLine("Data/Hora: " + aux.exibirDataAtual() + " - " + aux.horaAtual());

        ppp.execute();

        _finalizarPagamento();
    }

    private Bitmap convertBase64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    private void _finalizarPagamento() {
        //
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "ok");

        //
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        mostrarMsg();
    }

    void sair() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void mostrarMsg() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.logo_emissor_web);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        String msg = "Você deseja realmente cancelar este pagamento?";
        builder.setMessage(msg);
        //define um botão como positivo
        builder.setPositiveButton("SIM", (arg0, arg1) -> {
            sair();
        });
        //define um botão como negativo.
        builder.setNegativeButton("NÃO", (arg0, arg1) -> {

            // **
            //msg(true);
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();
    }
}