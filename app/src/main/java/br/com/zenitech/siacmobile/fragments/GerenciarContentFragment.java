package br.com.zenitech.siacmobile.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.Sincronizar;
import br.com.zenitech.siacmobile.ftps.MyFTPClientFunctions;


public class GerenciarContentFragment extends Fragment implements View.OnClickListener {
    public GerenciarContentFragment() {
    }

    /*public static EmpregosContentFragment newInstance() {
        EmpregosContentFragment fragment = new EmpregosContentFragment();
        return fragment;
    }*/

    //
    private SharedPreferences prefs;
    ClassAuxiliar classAuxiliar;
    private DatabaseHelper bd;

    private static final String TAG = "GerenciarCF";
    private static final String TEMP_FILENAME = "siacmobileDB.db";
    private Context context = null;

    private MyFTPClientFunctions ftpclient = null;

    private Button btnLoginFtp, btnUploadFile;
    private ProgressDialog pd;

    private String[] fileList;

    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            if (pd != null && pd.isShowing()) {
                try {
                    pd.dismiss();
                } catch (Exception e) {

                }

            }

            //SE A MENSAGEM FOR 0 FAZ A EXPORTAÇÃO DO BANCO
            if (msg.what == 0) {

                //EXPORTA O BANCO DE DADOS
                exportDB();

                //CARREGA AS PASTA DO SERVIDOR
                //getFTPFileList();
            } else if (msg.what == 1) {
                showCustomDialog(fileList);
            } else if (msg.what == 2) {
                Toast.makeText(getContext(), "Banco enviado com sucesso!",
                        Toast.LENGTH_LONG).show();

                //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
                getContext().deleteDatabase("siacmobileDB");
                Intent i = new Intent(getContext(), Sincronizar.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            } else if (msg.what == 3) {
                Toast.makeText(getContext(), "Desconectado com sucesso!",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Não é possível executar esta ação!",
                        Toast.LENGTH_LONG).show();
            }

        }

    };
    private LinearLayout cv_enviar_dados;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(
                R.layout.fragment_gerenciar_content, container, false);
        setHasOptionsMenu(true);

        //
        prefs = getContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        classAuxiliar = new ClassAuxiliar();
        context = this.getContext();
        bd = new DatabaseHelper(context);

        // kleilson
        if (bd.getAllVendas().size() > 0) {

        }


        // Create a temporary file. You can use this to upload
        //createDummyFile();

        ftpclient = new MyFTPClientFunctions();

        /*cv_enviar_dados = (LinearLayout) view.findViewById(R.id.cv_enviar_dados);
        cv_enviar_dados.setOnClickListener(this);*/

        //CONSULTAR CLIENTE CONTAS RECEBER
        view.findViewById(R.id.cv_excluir_db).setOnClickListener(view1 -> {

            //PEGA O CAMINHO DA PASTA DOWNLOAD DO APARELHO PARA VERIFICAR SE O BANCO EXISTE
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //CRIA O ARQUIVO DO BANCO
            File arquivo = new File(path + "/siacmobileDB.db");
            //APAGA O BANCO DA PASTA DOWNLOADS
            arquivo.delete();

            //APAGA O BANCO DE DADOS E VAI PARA TELA INICIAL DE SINCRONIZAÇÃO
            getContext().deleteDatabase("siacmobileDB");

            //SALVA OS DADOS DO USUÁRIO
            prefs.edit().putString("unidade_usuario", "").apply();
            prefs.edit().putString("codigo_usuario", "").apply();
            prefs.edit().putString("login_usuario", "").apply();
            prefs.edit().putString("senha_usuario", "").apply();
            prefs.edit().putString("usuario_atual", "").apply();
            prefs.edit().putString("nome_vendedor", "").apply();
            prefs.edit().putString("data_movimento", "").apply();

            Intent i = new Intent(getContext(), Sincronizar.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        //view.findViewById(R.id.cv_enviar_dados).setOnClickListener(v -> enviarDados());

        return view;
    }

    /*private void enviarDados() {
        String[] dados = bd.EnviarDados();

        Log.i("Dados: ", dados[0]);
        Log.i("Dados: ", dados[1]);
        Log.i("Dados: ", dados[2]);
        Log.i("Dados: ", dados[3]);
        Log.i("Dados: ", dados[4]);
        Log.i("Dados: ", dados[5]);
        Log.i("Dados: ", dados[6]);
        Log.i("Dados: ", dados[7]);
        Log.i("Dados: ", dados[8]);
        Log.i("Dados: ", dados[9]);
        Log.i("Dados: ", dados[10]);

        //MOSTRA A MENSAGEM DE SINCRONIZAÇÃO
        pd = ProgressDialog.show(context, "Enviando dados...", "Aguarde...",
                true, false);

        //
        final IEnviarDados iEnviarDados = IEnviarDados.retrofit.create(IEnviarDados.class);

        //
        *//*
        VENDAS.toString(),
                CLIENTES.toString(),
                PRODUTOS.toString(),
                QUANTIDADES.toString(),
                DATAS.toString(),
                VALORES.toString(),
                FINANCEIROS.toString(),
                FINVEN.toString(),
                VENCIMENTOS.toString(),
                VALORESFIN.toString(),
                FPAGAMENTOS.toString(),
                DOCUMENTOS.toString()
        * *//*
        final Call<ArrayList<EnviarDados>> call = iEnviarDados.enviarDados(
                "850",
                "419000026",
                "" + dados[0],
                "" + dados[1],
                "" + dados[2],
                "" + dados[3],
                "" + dados[4],
                "" + dados[5],
                "" + dados[6],
                "" + dados[7],
                "" + dados[8],
                "" + dados[9],
                "" + dados[10],
                "" + dados[11]
        );

        call.enqueue(new Callback<ArrayList<EnviarDados>>() {
            @Override
            public void onResponse(Call<ArrayList<EnviarDados>> call, Response<ArrayList<EnviarDados>> response) {

                //
                final ArrayList<EnviarDados> sincronizacao = response.body();
                if (sincronizacao != null) {

                    //CANCELA A MENSAGEM DE SINCRONIZAÇÃO
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
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
    }*/


    //exporting database
    private void exportDB() {
        // TODO Auto-generated method stub

        /*String currentDBPath = "//data//" + "br.com.zenitech.siacmobile"
                + "//databases//" + "siacmobileDB";
        String backupDBPath = "/Siac_Mobile/BD/siacmobileDB.db";*/

        String currentDBPath = "/data/" + "br.com.zenitech.siacmobile"
                + "/databases/" + "siacmobileDB";
        String backupDBPath = "/Siac_Mobile/BD/siacmobileDB.db";

        try {
            //File sd = Environment.getExternalStorageDirectory();
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                //
                Toast.makeText(getContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();


                try {
                    //ENVIA O BANCO PARA O SERVIDOR ONLINE
                    uploadDB();

                } catch (Exception e) {

                }
            }

        } catch (Exception e) {
            Log.i("BD", currentDBPath.toString());
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
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

    private void getFTPFileList() {
        pd = ProgressDialog.show(getContext(), "", "Getting Files...",
                true, false);

        new Thread(new Runnable() {

            @Override
            public void run() {
                fileList = ftpclient.ftpPrintFilesList("/");
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void showCustomDialog(String[] fileList) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom);
        dialog.setTitle("/ Directory File List");

        TextView tvHeading = (TextView) dialog.findViewById(R.id.tvListHeading);
        tvHeading.setText(":: File List ::");

        if (fileList != null && fileList.length > 0) {
            ListView listView = (ListView) dialog
                    .findViewById(R.id.lstItemList);
            ArrayAdapter<String> fileListAdapter = new ArrayAdapter<String>(
                    context, android.R.layout.simple_list_item_1, fileList);
            listView.setAdapter(fileListAdapter);
        } else {
            tvHeading.setText(":: No Files ::");
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.btnOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            //
            case R.id.btnLoginFtp:
                if (isOnline(context)) {
                    connectToFTPAddress();
                } else {
                    Toast.makeText(context,
                            "Verifique a sua Conexão à Internet!",
                            Toast.LENGTH_LONG).show();
                }
                break;
            //
            case R.id.cv_enviar_dados:
                //CRIA UM DIRETÓRIO PARA BAIXAR O BANCO ONLINE
                if (getDirFromSDCard() == null) {
                    Toast.makeText(getContext(), "Não foi possivél criar o Diretório!", Toast.LENGTH_LONG).show();
                }

                if (isOnline(context)) {
                    connectToFTPAddress();
                } else {
                    Toast.makeText(context,
                            "Verifique a sua Conexão à Internet!",
                            Toast.LENGTH_LONG).show();
                }
                break;
            /*case R.id.btnUploadFile:
                pd = ProgressDialog.show(Principal.this, "", "Uploading...",
                        true, false);
                new Thread(new Runnable() {
                    public void run() {
                        boolean status = false;

                        //String nomeArquivo = "/Siac_Mobile/BD/extenalDB";
                        String nomeArquivo = "/Siac_Mobile/BD/siacmobileDB.db";
                        String pasta = Environment.getExternalStorageDirectory() + "/" + nomeArquivo;
                        //File file = new File(pasta);

                        //*//**
             MFTPClient:
             objeto de conexão de cliente FTP (consulte Exemplo de conexão FTP)

             SrcFilePath:
             caminho do arquivo de origem no sdcard

             desFileName: nome do arquivo a ser Armazenado no servidor FTP

             desDirectory:
             caminho do diretório onde o arquivo Ser carregado para
             *//*

                        //public boolean ftpUpload(
                        // String srcFilePath,
                        // String desFileName,
                        // String desDirectory,
                        // Context context
                        // ) {

                        //
                        ftpclient.ftpChangeDirectory("/public_html/apps/zenitech/siacmobile/");
                        status = ftpclient.ftpUpload(
                                pasta,
                                "siacmobileDB.db",
                                "/",
                                cntx
                        );

                        /*//*status = ftpclient.ftpUpload(
                                Environment.getExternalStorageDirectory()+ "/TAGFtp/" + TEMP_FILENAME,
                                TEMP_FILENAME,
                                "/",
                                cntx);*//*//*
                        if (status == true) {
                            Log.d(TAG, "Upload success");
                            handler.sendEmptyMessage(2);
                        } else {
                            Log.d(TAG, "Upload failed");
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
                break;*/
            /*case R.id.btnDownloadFile:
                pd = ProgressDialog.show(Sincronizar.this, "", "Download...",
                        true, false);
                new Thread(new Runnable() {
                    public void run() {
                        boolean status = false;

                        String nomeArquivo = "/Siac_Mobile/BD";
                        String pasta = Environment.getExternalStorageDirectory() + "/" + nomeArquivo;

                        /*//**
             MFTPClient:
             objeto de conexão do cliente FTP (veja exemplo de conexão FTP)

             SrcFilePath:
             caminho para o arquivo de origem no servidor FTP

             desFilePath:
             caminho para O arquivo de destino a ser salvo em sdcard
             *//*

                        ftpclient.ftpChangeDirectory("/public_html/apps/zenitech/siacmobile/");

                        //
                        status = ftpclient.ftpDownload("siacmobileDB.db", pasta);
                        if (status == true) {
                            Log.d(TAG, "Download success");
                            handler.sendEmptyMessage(2);
                        } else {
                            Log.d(TAG, "Download failed");
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
                break;*/
            /*case R.id.btnDisconnectFtp:
                pd = ProgressDialog.show(Principal.this, "", "Disconnecting...",
                        true, false);

                new Thread(new Runnable() {
                    public void run() {
                        ftpclient.ftpDisconnect();
                        handler.sendEmptyMessage(3);
                    }
                }).start();

                break;
            case R.id.btnExit:
                this.finish();
                break;*/
        }

    }

    private void uploadDB() {

        //bd.FecharConexao();

        pd = ProgressDialog.show(context, "", "Enviando...",
                true, false);
        new Thread(() -> {
            boolean status = false;
            String DB_PATH;

            // ***********
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                //this.DB_PATH = context.getDatabasePath(DB_NAME).getPath() + File.separator;
                DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";

            } else {
                //String DB_PATH = Environment.getDataDirectory() + "/data/my.trial.app/databases/";
                //myPath = DB_PATH + dbName;
                DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
            }

            //this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
            Log.e(TAG, "Gerenciar Banco de Dados - " + DB_PATH);
            String pasta = DB_PATH + "siacmobileDB";

            // ***********

            //String nomeArquivo = "/Siac_Mobile/BD/extenalDB";
            //String nomeArquivo = "/Siac_Mobile/BD/siacmobileDB.db";
            //String pasta = Environment.getExternalStorageDirectory() + "/" + nomeArquivo;
            //File file = new File(pasta);

            /**
             MFTPClient:
             objeto de conexão de cliente FTP (consulte Exemplo de conexão FTP)

             SrcFilePath:
             caminho do arquivo de origem no sdcard

             desFileName: nome do arquivo a ser Armazenado no servidor FTP

             desDirectory:
             caminho do diretório onde o arquivo Ser carregado para
             */

            //public boolean ftpUpload(
            // String srcFilePath,
            // String desFileName,
            // String desDirectory,
            // Context context
            // ) {

            //kleilson
            //ftpclient.ftpChangeDirectory("/public_html/apps/zenitech/siacmobile/");
            //ftpclient.ftpChangeDirectory("/html/sistemas/apps/siac_mobile");
            //ftpclient.ftpChangeDirectory("/");
            ftpclient.ftpChangeDirectory("/bds_recebidos/");

            //String unidade = prefs.getString("unidade_usuario", "app").replace(" ", "_");
            //String id_unidade = bd.getIdUnidade(prefs.getString("unidade_usuario", "app"));
            String id_unidade = "3";
            status = ftpclient.ftpUpload(
                    pasta,
                    id_unidade + "_" + classAuxiliar.inserirDataAtual().replace("-", "") + classAuxiliar.horaAtual().replace(":", "") + ".db",//siacmobileDB
                    "/",
                    context
            );

            if (status == true) {

                //SALVA OS DADOS DO USUÁRIO
                prefs.edit().putString("unidade_usuario", "").apply();
                prefs.edit().putString("codigo_usuario", "").apply();
                prefs.edit().putString("login_usuario", "").apply();
                prefs.edit().putString("senha_usuario", "").apply();
                prefs.edit().putString("usuario_atual", "").apply();
                prefs.edit().putString("nome_vendedor", "").apply();
                prefs.edit().putString("data_movimento", "").apply();


                Log.d(TAG, "Upload success");
                handler.sendEmptyMessage(2);
            } else {
                Log.d(TAG, "Upload failed");
                handler.sendEmptyMessage(-1);
            }
        }).start();
    }

    private void connectToFTPAddress() {

        /*final String host = edtHostName.getText().toString().trim();
        final String username = edtUserName.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();*/

        /*final String host = "ftp.veinovo.com.br";
        final String username = "veinovo";
        final String password = "0035yasm";

        final String host = "177.153.22.33:8080";
        final String username = "cloudftp";
        final String password = "N0v342Cl02d!";


        final String host = "177.153.22.33";
        final String username = "ftpsiacbd";
        final String password = "mobilesiac2017";

        */

        final String host = "zenitech.com.br";
        final String username = "kleilson@zenitech.com.br";
        final String password = "zeni102030";

        if (host.length() < 1) {
            Toast.makeText(context, "Please Enter Host Address!",
                    Toast.LENGTH_LONG).show();
        } else if (username.length() < 1) {
            Toast.makeText(context, "Please Enter User Name!",
                    Toast.LENGTH_LONG).show();
        } else if (password.length() < 1) {
            Toast.makeText(context, "Please Enter Password!",
                    Toast.LENGTH_LONG).show();
        } else {

            //Toast.makeText(Principal.this, "Conectando...",
            //        Toast.LENGTH_LONG).show();

            pd = ProgressDialog.show(context, "", "Conectando...",
                    true, false);

            new Thread(new Runnable() {
                public void run() {
                    boolean status = false;
                    status = ftpclient.ftpConnect(host, username, password, 21);
                    if (status == true) {
                        Log.d(TAG, "Sucesso da conexão");
                        handler.sendEmptyMessage(0);
                    } else {
                        Log.d(TAG, "Falha na conexão");
                        handler.sendEmptyMessage(-1);
                    }
                }
            }).start();
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }

        return false;
    }

    /*public void mostrarMsg() {

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo_emissor_web);
        //define o titulo
        builder.setTitle("Emissor Web");
        //define a mensagem
        String msg = "Deseja emitir a NFC-e?";
        builder.setMessage(msg);
        //define um botão como positivo
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(InformacoesVagas.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();

                *//*//*
                Toast.makeText(FinanceiroDaVenda.this, "Venda Finalizada Com Sucesso.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FinanceiroDaVenda.this, Principal.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                *//*

                //sair(); //
                //listaVendas = bd.getVendasCliente(prefs.getInt("id_venda_app", 1));
                //ArrayList<Produtos> listaProdutos;
                //listaProdutos = bd.getAllProdutos();
                //i.putExtra("produtos", listaProdutos);

                PackageManager packageManager = getPackageManager();
                String packageName = "br.com.zenitech.emissorweb";
                Intent i = packageManager.getLaunchIntentForPackage(packageName);
                //DADOS EMISSOR WEB
                i.putExtra("siac", "1");
                i.putExtra("produto", produto_emissor);
                i.putExtra("quantidade", quantidade_emissor);
                i.putExtra("valor_unit", valor_unit_emissor);
                i.putExtra("forma_pagamento", "DINHEIRO");

                startActivity(i);
                finish();
            }
        });

        //define um botão como negativo.
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                sair();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe alerta
        alerta.show();
    }*/
}
