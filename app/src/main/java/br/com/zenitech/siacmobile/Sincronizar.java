package br.com.zenitech.siacmobile;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Objects;

import br.com.zenitech.siacmobile.domains.Conta;
import br.com.zenitech.siacmobile.interfaces.ILogin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sincronizar extends AppCompatActivity {

    //
    final String TAG = "Sincronizar";
    private SharedPreferences prefs;
    private ClassAuxiliar classAuxiliar;
    private DatabaseHelper db;
    private Context context;

    //
    private DownloadManager mgr = null;
    private long lastDownload = -1L;
    private ProgressDialog pd;
    public static final int REQUEST_PERMISSIONS_CODE = 128;
    private AlertDialog alerta;
    private EditText etLogin, etSenha;

    // **
    boolean erro = false;
    String msgErro = "", msgErroTec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Siac Mobile");
        getSupportActionBar().setSubtitle("Sincronizar");

        context = this;

        //
        prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
        classAuxiliar = new ClassAuxiliar();

        //VERIFICA SE O USUÁRIO DEU PERMISSÃO PARA ACESSAR O SDCARD
        if (ActivityCompat.checkSelfPermission(Sincronizar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (ActivityCompat.shouldShowRequestPermissionRationale(Sincronizar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                callDialog("Conceder Permissão Para Acessar Dados Externos.", new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});


            } else {
                ActivityCompat.requestPermissions(Sincronizar.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
            }
        }

        /*//
        File dbfile = new File("data/data/br.com.zenitech.siacmobile/databases", "siacmobileDB");

        //File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
        //File dir = new File(sdcard, "Siac_Mobile/BD/siacmobileDB.db");

        //SE O BANCO NÃO EXISITR
        if (!dbfile.exists()) {
            //Toast.makeText(getApplicationContext(), "O banco não existe", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(getApplicationContext(), "Database Exist", Toast.LENGTH_LONG).show();

            startActivity(new Intent(Sincronizar.this, Principal.class));
            finish();
        }*/

        mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));

        etLogin = findViewById(R.id.login);
        //etLogin.addTextChangedListener(MaskUtil.insert(etLogin, MaskUtil.MaskType.LOGINMask));
        etSenha = findViewById(R.id.senha);

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
            pd = ProgressDialog.show(Sincronizar.this, "Sincronizando...", "Verificando dados de login",
                    true, false);


            //
            final ILogin login = ILogin.retrofit.create(ILogin.class);

            //
            final Call<Conta> call = login.login(
                    etLogin.getText().toString(),
                    etSenha.getText().toString(),
                    "login",
                    ""
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
                                gerarBancoOnline(v, sincronizacao.getUnidade_vendedor());


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

        findViewById(R.id.btn_sincronizar).setOnClickListener(view -> {

            //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
            pd = ProgressDialog.show(Sincronizar.this, "", "Sincronizando...",
                    true, false);

            //
            startDownload(view);

            //
            importarBD();
            //downloadFile();
        });

        findViewById(R.id.btnDown).setOnClickListener(view -> {

            startActivity(new Intent(Sincronizar.this, Download.class));

            //
            importarBD();
        });

        //
        _limparDadosSincronizacao(true);
    }

    private void gerarBancoOnline(final View v, final String unidade) {
        /*Log.i(TAG, unidade);

        //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
        pd = ProgressDialog.show(Sincronizar.this, "Sincronizando...", "Criando banco para " + classAuxiliar.maiuscula1(unidade.toLowerCase()),
                true, false);

        //
        final ILogin login = ILogin.retrofit.create(ILogin.class);

        //
        final Call<Conta> call = login.getBancoOnline("gerarbanco", unidade);

        call.enqueue(new Callback<Conta>() {
            @Override
            public void onResponse(Call<Conta> call, Response<Conta> response) {

                //
                final Conta sincronizacao = response.body();
                if (sincronizacao != null) {

                    //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();

                        //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
                        pd = ProgressDialog.show(Sincronizar.this, "Sincronizando...", "Fazendo download dos dados",
                                true, false);
                        //INICIA O DOWNLOAD DO BANCO ONLINE
                        chamadaParaDownload(v);
                    }
                }
            }

            @Override
            public void onFailure(Call<Conta> call, Throwable t) {

                //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();

                    //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
                    pd = ProgressDialog.show(Sincronizar.this, "Sincronizando...", "Fazendo download dos dados",
                            true, false);
                    //INICIA O DOWNLOAD DO BANCO ONLINE
                    chamadaParaDownload(v);
                }
            }
        });*/

        chamadaParaDownload(v);
    }

    private void chamadaParaDownload(final View v) {
        // ESPERA 2.3 SEGUNDOS PARA  SAIR DO SPLASH
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //INICIA O DOWNLOAD DO BANCO ONLINE
                startDownload(v);
            }
        }, 10000);
    }

    private void callDialog(String message, final String[] permissions) {

        //
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logosiac);
        //define o titulo
        builder.setTitle("Permissão");
        //define a mensagem
        builder.setMessage("Conceder Permissão Para Acessar a Memória Externa.");

        //define um botão como positivo
        builder.setPositiveButton("Conceder", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

                ActivityCompat.requestPermissions(Sincronizar.this, permissions, REQUEST_PERMISSIONS_CODE);
                //Toast.makeText(InformacoesVagas.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });

        /*//define um botão como negativo.
        builder.setNegativeButton("Negativo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(InformacoesVagas.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });*/

        //cria o AlertDialog
        alerta = builder.create();

        //Exibe
        alerta.show();

        /*mMaterialDialog = new MaterialDialog(this)
                .setTitle("Permission")
                .setMessage(message)
                .setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ActivityCompat.requestPermissions(Sincronizar.this, permissions, REQUEST_PERMISSIONS_CODE);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onComplete);
        unregisterReceiver(onNotificationClick);
    }

    public void startDownload(View v) {

        Uri uri = Uri.parse("https://emissorweb.com.br/sistemas/apps/siac_mobile_melhor_gas/siacmobileDB123.db");
        Environment
                .getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                )
                .mkdirs();

        lastDownload = mgr.enqueue(new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("siacmobileDB")
                .setDescription("BD SIAC MOBILE.")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        "siacmobileDB.db"));

        //v.setEnabled(false);

        //
        // -------- importarBD();
    }

    public void queryStatus(View v) {
        Cursor c = mgr.query(new DownloadManager.Query().setFilterById(lastDownload));

        /*if (c == null) {
            Toast.makeText(this, "Download not found!", Toast.LENGTH_LONG).show();
        } else {
            c.moveToFirst();

            Log.d(getClass().getName(), "COLUMN_ID: " +
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
            Log.d(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: " +
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            Log.d(getClass().getName(), "COLUMN_LAST_MODIFIED_TIMESTAMP: " +
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
            Log.d(getClass().getName(), "COLUMN_LOCAL_URI: " +
                    c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            Log.d(getClass().getName(), "COLUMN_STATUS: " +
                    c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            Log.d(getClass().getName(), "COLUMN_REASON: " +
                    c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));

            Toast.makeText(this, statusMessage(c), Toast.LENGTH_LONG).show();
        }*/
    }

    public void viewLog(View v) {
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }

    private String statusMessage(Cursor c) {
        String msg = "???";

        /*switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg = "Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg = "Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg = "Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg = "Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg = "Download complete!";
                break;

            default:
                msg = "Download is nowhere in sight";
                break;
        }
*/
        return (msg);
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            //Toast.makeText(ctxt, "Download Completado!!!", Toast.LENGTH_LONG).show();

            //finish();
            // findViewById(R.id.start).setEnabled(true);

            importarBD();
        }
    };

    BroadcastReceiver onNotificationClick = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Ummmm...hi!", Toast.LENGTH_LONG).show();
        }
    };

    private void baixarBD() {
        //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
        pd = ProgressDialog.show(Sincronizar.this, "", "Sincronizando...",
                true, false);

        //INICIA O DOWNLOAD DO BANCO NO SERVIDOR
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse("https://emissorweb.com.br/sistemas/apps/siac_mobile_melhor_gas/");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Long reference = downloadManager.enqueue(request);
        //downloadFile();

        //DOWNLOAD INICIADO
        Toast.makeText(Sincronizar.this, "Carregando banco...", Toast.LENGTH_LONG).show();

        importarBD();
    }

    int totVer = 0;

    private void importarBD() {
        // ESPERA 1 SEGUNDOS PARA
        new Handler().postDelayed(() -> {

            //PEGA O CAMINHO DA PASTA DOWNLOAD DO APARELHO PARA VERIFICAR SE O BANCO EXISTE
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //CRIA O ARQUIVO DO BANCO - POR ALGUM MOTIVO O BANCO É SALVO EM .TXT
            File arquivo = new File(path + "/siacmobileDB.db"); //.txt pasta);

            //SE O BANCO EXISTIR FAZ A IMPORTAÇÃO PARA O APP
            if (!arquivo.exists()) {
                if (totVer <= 50) {
                    totVer++;
                    //CHAMA A IMPORTAÇÃO NOVAMENTE
                    importarBD();
                } else {
                    erro = true;
                    msgErro = "Importação do banco de dados falhou! Tente novamente.";
                }

            } else {

               /* //CRIA UMA INSTANCIA DO BANCO
                db = new DatabaseHelper(getBaseContext());
                try {
                    db.createDataBase();
                } catch (Exception ioe) {
                    erro = true;
                    msgErro = "Não foi possível criar o banco de dados!";
                    msgErroTec = ioe.getMessage();
                    throw new Error("Não foi possível criar o banco de dados!");
                }
                try {
                    db.openDataBase();
                } catch (SQLException sqle) {
                    Log.d(TAG, Objects.requireNonNull(sqle.getMessage()));
                    erro = true;
                    msgErro = "Não foi possível ler o banco de dados.";
                    msgErroTec = sqle.getMessage();
                    throw sqle;
                }*/

                Toast.makeText(Sincronizar.this, "Dados sincronizados com sucesso!", Toast.LENGTH_SHORT).show();

                //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }


                if (!erro) {
                    try {
                        //APAGA O BANCO DA PASTA DOWNLOADS
                        //arquivo.delete();

                        //ABRI A TELA PRINCIPAL
                        Intent i = new Intent(Sincronizar.this, Principal.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                        finish();
                    } catch (Exception e) {
                        msgErro = "Importação do banco de dados falhou! Tente novamente.";
                        msgErroTec = e.getMessage();
                        _limparDadosSincronizacao(true);
                        _resetarSincronismo(5000, true);
                        Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                } else {
                    msgErro = "Importação do banco de dados falhou! Tente novamente.";
                    _limparDadosSincronizacao(true);
                    _resetarSincronismo(5000, true);
                }
            }


        }, 5000);
    }

    // LIMPA OS DADOS DA SINCRONIZAÇÃO
    void _limparDadosSincronizacao(boolean apagarBanco) {
        //PEGA O CAMINHO DA PASTA DOWNLOAD DO APARELHO PARA VERIFICAR SE O BANCO EXISTE
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File arquivo = new File(path + "/siacmobileDB.db");
        //APAGA O BANCO DA PASTA DOWNLOADS
        if (arquivo.isFile()) arquivo.delete();

        // Retorna o caminho da imagem do qrcode
        File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File dir = new File(sdcard, "Siac_Mobile/BD/siacmobileDB.db");
        dir.delete();

        // APAGAR BANCO DE DADOS IMPORTADO
        if (apagarBanco) {
            //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
            context.deleteDatabase("siacmobileDB");
        }
    }

    // MOSTRAR OS CAMPOS PARA SINCRONIZAR NOVAMENTE
    void _resetarSincronismo(long time, boolean erro) {
        /*if (erro) {
            txtTotMemoria.setText(msgErro);
            ll_erro.setVisibility(View.VISIBLE);
            ll_sincronizando.setVisibility(View.GONE);
            ll_sincronizar.setVisibility(View.GONE);
        }

        new Handler().postDelayed(() -> {
            txtTotMemoria.setText("");
            ll_erro.setVisibility(View.GONE);
            ll_sincronizando.setVisibility(View.GONE);
            ll_sincronizar.setVisibility(View.VISIBLE);

            if (erro) {
                fabWhatsapp.setVisibility(View.VISIBLE);

                if (!prefs.getBoolean("introBtnWhats", false)) {
                    introducao();
                }
            }
        }, time);*/
    }


    /*public void downloadFile() {
        FTPClient con = null;

        try {
            con = new FTPClient();
            con.connect("ftp.veinovo.com.br");

            if (con.login("veinovo", "0035yasm")) {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                String data = "/Siac_Mobile/BD/siacmobileDB.db";


                //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                //CRIA O ARQUIVO DO BANCO - POR ALGUM MOTIVO O BANCO É SALVO EM .TXT
                //File arquivo = new File(path + "/siacmobileDB.txt"); //pasta);

                OutputStream out = new FileOutputStream(new File(data));
                boolean result = con.retrieveFile("siacmobileDB.db", out);
                out.close();
                if (result) Log.v("download result", "succeeded");
                con.logout();
                con.disconnect();
            }
        } catch (Exception e) {
            Log.v("download result", "failed");
            e.printStackTrace();
        }

        importarBD();
    }*/
}
