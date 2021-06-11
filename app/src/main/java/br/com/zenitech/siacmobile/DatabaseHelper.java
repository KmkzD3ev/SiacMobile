package br.com.zenitech.siacmobile;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.zenitech.siacmobile.domains.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private String TAG = "DatabaseHelper";
    private String DB_PATH;
    private static String DB_NAME = "siacmobileDB";
    private SQLiteDatabase myDataBase;
    private SQLiteDatabase db;
    final Context context;
    private ClassAuxiliar aux = new ClassAuxiliar();


    //CONSTANTES CLIENTES
    private static final String TABELA_CLIENTES = "clientes";
    private static final String CODIGO_CLIENTE = "codigo_cliente";
    private static final String NOME_CLIENTE = "nome_cliente";
    private static final String LATITUDE_CLIENTE = "latitude_cliente";
    private static final String LONGITUDE_CLIENTE = "longitude_cliente";

    private static final String[] COLUNAS_CLIENTES = {CODIGO_CLIENTE, NOME_CLIENTE, LATITUDE_CLIENTE, LONGITUDE_CLIENTE};

    //CONSTANTES PRODUTOS
    private static final String TABELA_PRODUTOS = "produtos";
    private static final String CODIGO_PRODUTO = "codigo_produto";
    private static final String DESCRICAO_PRODUTO = "descricao_produto";

    private static final String[] COLUNAS_PRODUTOS = {CODIGO_PRODUTO, DESCRICAO_PRODUTO};


    /*public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 10);
        this.context = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH);
    }*/

    @SuppressLint("SdCardPath")
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 12);
        this.context = context;
        //this.DB_PATH = context.getFilesDir().getPath() + "/" + context.getPackageName() + "/" + "databases/";
        //this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            //this.DB_PATH = context.getDatabasePath(DB_NAME).getPath() + File.separator;
            this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";

        } else {
            //String DB_PATH = Environment.getDataDirectory() + "/data/my.trial.app/databases/";
            //myPath = DB_PATH + dbName;
            this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        }

        //this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e(TAG, " DatabaseHelper - " + DB_PATH);
    }


    void createDataBase() {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.i(TAG, "Error copying database: " + e.getMessage());
                throw new Error("Error copying database");
            }
        }
    }

    public boolean checkDataBase() {
        /*SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;*/

        File dbFile = context.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File arquivo = new File(path + "/siacmobileDB.db"); //.db pasta);
        FileInputStream myInput = new FileInputStream(arquivo);


        //InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();

            }
    }

    //
    public void addCliente(Clientes clientes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NOME_CLIENTE, clientes.getNome());
        db.insert(TABELA_CLIENTES, null, values);
        db.close();
    }

    //CONSULTAR CLIENTE
    public Clientes getCliente(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABELA_CLIENTES, // TABELA
                COLUNAS_CLIENTES, // COLUNAS
                " codigo = ?", // COLUNAS PARA COMPARAR
                new String[]{String.valueOf(codigo)}, // PARAMETROS
                null, // GROUP BY
                null, // HAVING
                null, // ORDER BY
                null // LIMIT
        );

        //
        if (cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
            Clientes clientes = cursorToCliente(cursor);
            return clientes;
        }
    }

    //
    private Clientes cursorToCliente(Cursor cursor) {
        Clientes clientes = new Clientes(null, null, null, null, null, null, null);
        //clientes.setCodigo(Integer.parseInt(cursor.getString(0)));
        clientes.setCodigo(cursor.getString(0));
        clientes.setNome(cursor.getString(1));
        clientes.setLatitude_cliente(cursor.getString(2));
        clientes.setLongitude_cliente(cursor.getString(3));
        clientes.setSaldo(cursor.getString(4));
        clientes.setCpfcnpj(cursor.getString(5));
        clientes.setEndereco(cursor.getString(6));
        return clientes;
    }

    //LISTAR TODOS OS CLIENTES
    public ArrayList<Clientes> getAllClientes() {
        ArrayList<Clientes> listaClientes = new ArrayList<>();
        //String query = "SELECT * FROM " + TABELA_CLIENTES + " ORDER BY " + NOME_CLIENTE;
        //String query = "SELECT * FROM " + TABELA_CLIENTES + " ORDER BY " + CODIGO_CLIENTE + ", " + NOME_CLIENTE;
        String query = "SELECT * FROM clientes ORDER BY codigo_cliente, nome_cliente";
        //Log.i("SQL_APP", query);

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Clientes clientes = cursorToCliente(cursor);
                //Log.i("SQL_APP", clientes.getCodigo());
                //Log.i("SQL_APP", clientes.getLatitude_cliente());
                //Log.i("SQL_APP", clientes.getLongitude_cliente());
                listaClientes.add(clientes);
            } while (cursor.moveToNext());
        }

        return listaClientes;
    }

    //ALTERAR CLIENTE
    public int updateCliete(Clientes clientes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOME_CLIENTE, clientes.getNome());

        int i = db.update(
                TABELA_CLIENTES,
                values,
                CODIGO_CLIENTE + " = ?",
                new String[]{String.valueOf(clientes.getCodigo())}
        );
        db.close();
        return i;
    }

    //
    public int deleteCliente(Clientes clientes) {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(
                TABELA_CLIENTES,
                CODIGO_CLIENTE + " = ?",
                new String[]{String.valueOf(clientes.getCodigo())}
        );
        db.close();
        return i;
    }

    ///////

    //
    private UnidadesDomain cursorToUnidades(Cursor cursor) {
        UnidadesDomain unidades = new UnidadesDomain(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        unidades.setId_unidade(cursor.getString(0));
        unidades.setDescricao_unidade(cursor.getString(1));
        unidades.setRazao_social(cursor.getString(2));
        unidades.setCnpj(cursor.getString(3));
        unidades.setEndereco(cursor.getString(4));
        unidades.setNumero(cursor.getString(5));
        unidades.setBairro(cursor.getString(6));
        unidades.setCep(cursor.getString(7));
        unidades.setTelefone(cursor.getString(8));
        unidades.setIe(cursor.getString(9));
        unidades.setCidade(cursor.getString(10));
        unidades.setUf(cursor.getString(11));
        unidades.setCodigo_ibge(cursor.getString(12));
        unidades.setUrl_consulta(cursor.getString(13));
        return unidades;
    }


    //SOMAR O VALOR DO FINANCEIRO
    public String getIdUnidade(String unidade) {

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        String selectQuery = "SELECT id_unidade FROM unidades WHERE descricao_unidade = '" + unidade + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        String id = "";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getString(0);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return id;
    }//SOMAR O VALOR DO FINANCEIRO

    public UnidadesDomain getUnidade() {

        UnidadesDomain unidades = new UnidadesDomain(null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        String query = "SELECT * FROM unidades LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        unidades = cursorToUnidades(cursor);
                    } while (cursor.moveToNext());
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.endTransaction();
            //db.close();
        }
        db.endTransaction();
        db.close();
        return unidades;
    }


    //########## PRODUTOS ############

    //
    private Produtos cursorToProdutos(Cursor cursor) {
        Produtos produtos = new Produtos(null, null);
        produtos.setCodigo_produto(cursor.getString(0));
        produtos.setDescricao_produto(cursor.getString(1));
        return produtos;
    }

    //LISTAR TODOS OS CLIENTES
    public ArrayList<Produtos> getAllProdutos() {
        ArrayList<Produtos> listaProdutos = new ArrayList<>();

        String query = "SELECT * FROM " + TABELA_PRODUTOS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Produtos prods = cursorToProdutos(cursor);
                listaProdutos.add(prods);

                /*
                String codigo_produto = cursor.getString(0);
                String descricao_produto = cursor.getString(1);
                Produtos prod = new Produtos(codigo_produto, descricao_produto);
                listaProdutos.add(prod);
                */
            } while (cursor.moveToNext());
        }

        return listaProdutos;
    }

    public ArrayList<String> getProdutos() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        String selectQuery = "Select * From " + TABELA_PRODUTOS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String codigo_produto = cursor.getString(cursor.getColumnIndex("codigo_produto"));
                    String descricao_produto = cursor.getString(cursor.getColumnIndex("descricao_produto"));
                    //list.add(codigo_produto + " " + descricao_produto);
                    list.add(descricao_produto);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return list;
    }

    //############### VENDAS ###############

    //CONSTANTES VENDAS
    private static final String TABELA_VENDAS = "vendas_app";
    private static final String CODIGO_VENDA = "codigo_venda";
    private static final String CODIGO_CLIENTE_VENDA = "codigo_cliente";
    private static final String UNIDADE_VENDA = "unidade_venda";
    private static final String PRODUTO_VENDA = "produto_venda";
    private static final String DATA_MOVIMENTO = "data_movimento";
    private static final String QUANTIDADE_VENDA = "quantidade_venda";
    private static final String PRECO_UNITARIO = "preco_unitario";
    private static final String VALOR_TOTAL = "valor_total";
    private static final String VENDEDOR_VENDA = "vendedor_venda";
    private static final String STATUS_AUTORIZACAO_VENDA = "status_autorizacao_venda";
    private static final String ENTREGA_FUTURA_VENDA = "entrega_futura_venda";
    private static final String ENTREGA_FUTURA_REALIZADA = "entrega_futura_realizada";
    private static final String USUARIO_ATUAL = "usuario_atual";
    private static final String DATA_CADASTRO = "data_cadastro";
    private static final String CODIGO_VENDA_APP = "codigo_venda_app";
    private static final String VENDA_FINALIZADA_APP = "venda_finalizada_app";
    private static final String CHAVE_IMPORTACAO_APP = "chave_importacao";

    private static final String[] COLUNAS_VENDAS = {
            CODIGO_VENDA,
            CODIGO_CLIENTE_VENDA,
            UNIDADE_VENDA,
            PRODUTO_VENDA,
            DATA_MOVIMENTO,
            QUANTIDADE_VENDA,
            PRECO_UNITARIO,
            VALOR_TOTAL,
            VENDEDOR_VENDA,
            STATUS_AUTORIZACAO_VENDA,
            ENTREGA_FUTURA_VENDA,
            ENTREGA_FUTURA_REALIZADA,
            USUARIO_ATUAL,
            DATA_CADASTRO,
            CODIGO_VENDA_APP,
            VENDA_FINALIZADA_APP,
            CHAVE_IMPORTACAO_APP
    };

    //
    private VendasDomain cursorToVendas(Cursor cursor) {
        VendasDomain vendas = new VendasDomain(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, null, null, null, null);
        vendas.setCodigo_venda(cursor.getString(0));
        vendas.setCodigo_cliente(cursor.getString(1));
        vendas.setUnidade_venda(cursor.getString(2));
        vendas.setProduto_venda(cursor.getString(3));
        vendas.setData_movimento(cursor.getString(4));
        vendas.setQuantidade_venda(cursor.getString(5));
        vendas.setPreco_unitario(cursor.getString(6));
        vendas.setValor_total(cursor.getString(7));
        vendas.setVendedor_venda(cursor.getString(8));
        vendas.setStatus_autorizacao_venda(cursor.getString(9));
        vendas.setEntrega_futura_venda(cursor.getString(10));
        vendas.setEntrega_futura_realizada(cursor.getString(11));
        vendas.setUsuario_atual(cursor.getString(12));
        vendas.setData_cadastro(cursor.getString(13));
        vendas.setCodigo_venda_app(cursor.getString(14));
        vendas.setVenda_finalizada_app(cursor.getString(15));
        return vendas;
    }

    //LISTAR TODOS OS CLIENTES
    public ArrayList<VendasDomain> getAllVendas() {
        ArrayList<VendasDomain> listaVendas = new ArrayList<>();

        String query = "SELECT * FROM " + TABELA_VENDAS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                VendasDomain vendas = cursorToVendas(cursor);
                listaVendas.add(vendas);
            } while (cursor.moveToNext());
        }

        return listaVendas;
    }

    //LISTAR TODOS OS CLIENTES
    public ArrayList<FinanceiroReceberClientes> getAllRecebidos() {
        ArrayList<FinanceiroReceberClientes> listaVendas = new ArrayList<>();

        String query = "SELECT * FROM recebidos";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                FinanceiroReceberClientes vendas = cursorToContasReceberCliente(cursor);
                listaVendas.add(vendas);
            } while (cursor.moveToNext());
        }

        return listaVendas;
    }

    //LISTAR TODOS OS ITENS DA VENDA
    public ArrayList<VendasDomain> getVendasCliente(int codigo_venda_app) {
        ArrayList<VendasDomain> listaVendas = new ArrayList<>();

        String query = "SELECT * FROM " + TABELA_VENDAS + " WHERE codigo_venda_app = '" + codigo_venda_app + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                VendasDomain vendas = cursorToVendas(cursor);
                listaVendas.add(vendas);
            } while (cursor.moveToNext());
        }

        //db.close();
        return listaVendas;
    }

    //LISTAR TODOS OS ITENS DA VENDA
    public String[] getUltimaVendasCliente() {

        String query = "SELECT " +
                "ven." + CODIGO_VENDA + ", " +
                "ven." + CODIGO_VENDA_APP + ", " +
                "cli." + CODIGO_CLIENTE + ", " +
                "cli." + NOME_CLIENTE +
                " FROM " + TABELA_VENDAS + " ven" +
                " INNER JOIN " + TABELA_CLIENTES + " cli ON cli." + CODIGO_CLIENTE + " = ven." + CODIGO_CLIENTE_VENDA +
                " WHERE ven." + VENDA_FINALIZADA_APP + " = 1" +
                " ORDER BY " + "ven." + CODIGO_VENDA_APP + " DESC" +
                " LIMIT 1";

        Log.i("SQL", "getUltimaVendasCliente - " + query);

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);
        cursor.moveToFirst();
        String[] id = {};
        try {
            id = new String[]{
                    cursor.getString(cursor.getColumnIndex(CODIGO_VENDA)),
                    cursor.getString(cursor.getColumnIndex(CODIGO_VENDA_APP)),
                    cursor.getString(cursor.getColumnIndex(CODIGO_CLIENTE)),
                    cursor.getString(cursor.getColumnIndex(NOME_CLIENTE))
            };
        } catch (Exception e) {

        }
        return id;
    }

    //
    public void addVenda(VendasDomain vendas) {
        //SQLiteDatabase db = this.getWritableDatabase();
        myDataBase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CODIGO_VENDA, vendas.getCodigo_venda());
        values.put(CODIGO_CLIENTE_VENDA, vendas.getCodigo_cliente());
        values.put(UNIDADE_VENDA, vendas.getUnidade_venda());
        values.put(PRODUTO_VENDA, vendas.getProduto_venda());
        values.put(DATA_MOVIMENTO, vendas.getData_movimento());
        values.put(QUANTIDADE_VENDA, vendas.getQuantidade_venda());
        values.put(PRECO_UNITARIO, vendas.getPreco_unitario());
        values.put(VALOR_TOTAL, vendas.getValor_total());
        values.put(VENDEDOR_VENDA, vendas.getVendedor_venda());
        values.put(STATUS_AUTORIZACAO_VENDA, vendas.getStatus_autorizacao_venda());
        values.put(ENTREGA_FUTURA_VENDA, vendas.getEntrega_futura_venda());
        values.put(ENTREGA_FUTURA_REALIZADA, vendas.getEntrega_futura_realizada());
        values.put(USUARIO_ATUAL, vendas.getUsuario_atual());
        values.put(DATA_CADASTRO, vendas.getData_cadastro());
        values.put(CODIGO_VENDA_APP, vendas.getCodigo_venda_app());
        values.put(VENDA_FINALIZADA_APP, vendas.getVenda_finalizada_app());
        values.put(CHAVE_IMPORTACAO_APP, vendas.getChave_importacao());
        myDataBase.insert(TABELA_VENDAS, null, values);
        myDataBase.close();
    }

    //LISTAR TODOS OS CLIENTES
    public String getValorTotalVenda(String codigo_venda_app) {

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        String selectQuery = "SELECT SUM(valor_total) FROM " + TABELA_VENDAS + " WHERE " + CODIGO_VENDA_APP + " = '" + codigo_venda_app + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        String total = "0.0";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                total = cursor.getString(0);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return total;
    }

    //ALTERAR CHAVE VENDA
    public int updateVendaApp(String nVenda, String nChave) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CHAVE_IMPORTACAO_APP, String.valueOf(nChave));

        int i = db.update(
                TABELA_VENDAS,
                values,
                CODIGO_VENDA + " = ?",
                new String[]{String.valueOf(nVenda)}
        );
        db.close();
        return i;
    }

    public ArrayList<VendasDomain> vendasNaoSinc() {
        ArrayList<VendasDomain> listaVendas = new ArrayList<>();

        String query = "SELECT " +
                "codigo_venda, " +
                "codigo_cliente, " +
                "unidade_venda, " +
                "produto_venda, " +
                "data_movimento, " +
                "SUM(quantidade_venda) quantidade_venda, " +
                "preco_unitario, " +
                "SUM(valor_total) valor_total, " +
                "vendedor_venda, " +
                "status_autorizacao_venda, " +
                "entrega_futura_venda, " +
                "entrega_futura_realizada, " +
                "usuario_atual, " +
                "data_cadastro, " +
                "codigo_venda_app, " +
                "venda_finalizada_app " +
                "chave_importacao " +
                "FROM " + TABELA_VENDAS + " WHERE venda_finalizada_app = '1' AND chave_importacao = '' GROUP BY " + PRODUTO_VENDA;

        Log.e("SQL = ", query);


        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                VendasDomain vendas = cursorToVendas(cursor);
                listaVendas.add(vendas);
            } while (cursor.moveToNext());
        }

        myDataBase.close();
        return listaVendas;
    }

    //CONSULTAR VENDA
    public VendasDomain getVenda(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABELA_VENDAS, // TABELA
                COLUNAS_VENDAS, // COLUNAS
                " codigo_venda = ?", // COLUNAS PARA COMPARAR
                new String[]{String.valueOf(codigo)}, // PARAMETROS
                null, // GROUP BY
                null, // HAVING
                null, // ORDER BY
                null // LIMIT
        );

        //
        if (cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
            VendasDomain vendas = cursorToVendas(cursor);
            return vendas;
        }
    }

    //
    public int deleteItemVenda(VendasDomain vendasDomain) {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(
                TABELA_VENDAS,
                CODIGO_VENDA + " = ?",
                new String[]{String.valueOf(vendasDomain.getCodigo_venda())}
        );
        db.close();
        return i;
    }

    //
    public int deleteVenda(int codigo_venda_app) {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(
                TABELA_VENDAS,
                CODIGO_VENDA_APP + " = ?",
                new String[]{String.valueOf(codigo_venda_app)}
        );
        db.close();
        return i;
    }


    //########## RELATÓRIOS DE VENDA ############
    //LISTAR TODOS OS CLIENTES
    /*
    ", " +
                "COUNT(" + QUANTIDADE_VENDA + ") AS quantidade_venda, " +
                "SUM(" + VALOR_TOTAL + ") AS valor_total " +



                private static final String TABELA_VENDAS = "vendas_app";
    private static final String CODIGO_VENDA = "codigo_venda";
    private static final String CODIGO_CLIENTE_VENDA = "codigo_cliente";
    private static final String UNIDADE_VENDA = "unidade_venda";
    private static final String PRODUTO_VENDA = "produto_venda";
    private static final String DATA_MOVIMENTO = "data_movimento";
    private static final String QUANTIDADE_VENDA = "quantidade_venda";
    private static final String PRECO_UNITARIO = "preco_unitario";
    private static final String VALOR_TOTAL = "valor_total";
    private static final String VENDEDOR_VENDA = "vendedor_venda";
    private static final String STATUS_AUTORIZACAO_VENDA = "status_autorizacao_venda";
    private static final String ENTREGA_FUTURA_VENDA = "entrega_futura_venda";
    private static final String ENTREGA_FUTURA_REALIZADA = "entrega_futura_realizada";
    private static final String USUARIO_ATUAL = "usuario_atual";
    private static final String DATA_CADASTRO = "data_cadastro";
    private static final String CODIGO_VENDA_APP = "codigo_venda_app";
     */
    public ArrayList<VendasDomain> getRelatorioVendas() {
        ArrayList<VendasDomain> listaVendas = new ArrayList<>();

        String query = "SELECT " +
                "codigo_venda, " +
                "codigo_cliente, " +
                "unidade_venda, " +
                "produto_venda, " +
                "data_movimento, " +
                "SUM(quantidade_venda) quantidade_venda, " +
                "preco_unitario, " +
                "SUM(valor_total) valor_total, " +
                "vendedor_venda, " +
                "status_autorizacao_venda, " +
                "entrega_futura_venda, " +
                "entrega_futura_realizada, " +
                "usuario_atual, " +
                "data_cadastro, " +
                "codigo_venda_app, " +
                "venda_finalizada_app " +
                "chave_importacao " +
                "FROM " + TABELA_VENDAS + " WHERE venda_finalizada_app = '1' GROUP BY " + PRODUTO_VENDA;

        Log.e("SQL = ", query);


        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                VendasDomain vendas = cursorToVendas(cursor);
                listaVendas.add(vendas);
            } while (cursor.moveToNext());
        }

        myDataBase.close();
        return listaVendas;
    }

    //
    public ArrayList<RelatorioVendasClientesDomain> getRelatorioVendasClientes(String produto) {
        RelatorioVendasClientesDomain dRelatorio = new RelatorioVendasClientesDomain(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        ArrayList<RelatorioVendasClientesDomain> listaVendasS = new ArrayList<>();

        String query = "SELECT " +
                "ven.codigo_venda codigo_venda, " +
                "ven.codigo_cliente codigo_cliente, " +
                "ven.unidade_venda unidade_venda, " +
                "ven.produto_venda produto_venda, " +
                "ven.data_movimento data_movimento, " +
                "SUM(ven.quantidade_venda) quantidade_venda, " +
                "ven.preco_unitario preco_unitario, " +
                "SUM(ven.valor_total) valor_total, " +
                "ven.vendedor_venda vendedor_venda, " +
                "ven.status_autorizacao_venda status_autorizacao_venda, " +
                "ven.entrega_futura_venda entrega_futura_venda, " +
                "ven.entrega_futura_realizada entrega_futura_realizada, " +
                "ven.usuario_atual usuario_atual, " +
                "ven.data_cadastro data_cadastro, " +
                "ven.codigo_venda_app codigo_venda_app, " +
                "(SELECT cli.nome_cliente FROM " + TABELA_CLIENTES + " AS cli WHERE cli.codigo_cliente = ven.codigo_cliente) nome " +
                " FROM " + TABELA_VENDAS + " AS ven " +
                " WHERE ven.produto_venda = '" + produto + "' AND ven.venda_finalizada_app = '1' " +
                " GROUP BY ven.codigo_cliente " +
                " ORDER BY nome";

        Log.e("SQL ", query);


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                dRelatorio = new RelatorioVendasClientesDomain(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

                dRelatorio.setCodigo_venda(cursor.getString(cursor.getColumnIndex("codigo_venda")));
                dRelatorio.setCodigo_cliente(cursor.getString(cursor.getColumnIndex("codigo_cliente")));
                dRelatorio.setUnidade_venda(cursor.getString(cursor.getColumnIndex("unidade_venda")));
                dRelatorio.setProduto_venda(cursor.getString(cursor.getColumnIndex("produto_venda")));
                dRelatorio.setData_movimento(cursor.getString(cursor.getColumnIndex("data_movimento")));
                dRelatorio.setQuantidade_venda(cursor.getString(cursor.getColumnIndex("quantidade_venda")));
                dRelatorio.setPreco_unitario(cursor.getString(cursor.getColumnIndex("preco_unitario")));
                dRelatorio.setValor_total(cursor.getString(cursor.getColumnIndex("valor_total")));
                dRelatorio.setVendedor_venda(cursor.getString(cursor.getColumnIndex("vendedor_venda")));
                dRelatorio.setStatus_autorizacao_venda(cursor.getString(cursor.getColumnIndex("status_autorizacao_venda")));
                dRelatorio.setEntrega_futura_venda(cursor.getString(cursor.getColumnIndex("entrega_futura_venda")));
                dRelatorio.setEntrega_futura_realizada(cursor.getString(cursor.getColumnIndex("entrega_futura_realizada")));
                dRelatorio.setUsuario_atual(cursor.getString(cursor.getColumnIndex("usuario_atual")));
                dRelatorio.setData_cadastro(cursor.getString(cursor.getColumnIndex("data_cadastro")));
                dRelatorio.setCodigo_venda_app(cursor.getString(cursor.getColumnIndex("codigo_venda_app")));
                dRelatorio.setNome(cursor.getString(cursor.getColumnIndex("nome")));

                listaVendasS.add(dRelatorio);
            } while (cursor.moveToNext());
        }

        /*if (cursor.moveToFirst()) {
            do {
                RelatorioVendasClientesDomain vendas = cursorToRelatorioVendasClientes(cursor);
                listaVendas.add(vendas);
            } while (cursor.moveToNext());
        }*/

        return listaVendasS;
    }

    //
    public ArrayList<FinanceiroVendasDomain> getRelatorioContasReceber() {
        ArrayList<FinanceiroVendasDomain> listaVendas = new ArrayList<>();

        String query = "SELECT " +
                "codigo_financeiro, " +
                "unidade_financeiro, " +
                "data_financeiro, " +
                "codigo_cliente_financeiro, " +
                "fpagamento_financeiro, " +
                "documento_financeiro, " +
                "vencimento_financeiro, " +
                "valor_financeiro valor_financeiro, " +
                "status_autorizacao, " +
                "SUM(pago), " +
                "vasilhame_ref, " +
                "usuario_atual, " +
                "data_inclusao, " +
                "nosso_numero_financeiro, " +
                "id_vendedor_financeiro, " +
                "id_financeiro_app " +
                "FROM recebidos " +
                "GROUP BY fpagamento_financeiro";

        //Log.e("SQL = ", query);


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                FinanceiroVendasDomain vendas = cursorToFinanceiroVendasDomain(cursor);
                listaVendas.add(vendas);
            } while (cursor.moveToNext());
        }

        return listaVendas;
    }

    //
    public FinanceiroVendasDomain getBaixaRecebida(String codigo_finan) {
        FinanceiroVendasDomain listaVendas = new FinanceiroVendasDomain(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        String query = "SELECT " +
                "codigo_financeiro, unidade_financeiro, data_financeiro, " +
                "codigo_cliente_financeiro, fpagamento_financeiro, " +
                "documento_financeiro, vencimento_financeiro, " +
                "SUM(valor_financeiro) valor_financeiro, status_autorizacao, " +
                "pago, vasilhame_ref, usuario_atual, data_inclusao, " +
                "nosso_numero_financeiro, id_vendedor_financeiro, id_financeiro_app " +
                "FROM recebidos " +
                "WHERE codigo_financeiro = " + codigo_finan + " " +
                "GROUP BY fpagamento_financeiro";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                listaVendas = cursorToFinanceiroVendasDomain(cursor);
            } while (cursor.moveToNext());
        }

        return listaVendas;
    }

    public String getTotalRecebido(String codigo_finan) {
        String valor_financeiro = "0";
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        //
        String selectQuery = "SELECT pago " +
                "FROM recebidos " +
                "WHERE codigo_financeiro = " + codigo_finan + " " +
                "LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    valor_financeiro = cursor.getString(0);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ContasReceber", e.getMessage());
        }

        db.endTransaction();
        db.close();
        return valor_financeiro;
    }

    public String getTotalRecebidoList(String codigo_finan) {
        String valor_financeiro = "0";
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        //
        String selectQuery = "SELECT valor_financeiro " +
                "FROM recebidos " +
                "WHERE codigo_financeiro = " + codigo_finan + " " +
                "LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    valor_financeiro = cursor.getString(cursor.getColumnIndex("valor_financeiro"));
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.endTransaction();
        db.close();
        return valor_financeiro;
    }

    public String getValorFinReceberCli(String codigo_finan) {
        String valor_financeiro = "0";
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        //
        String selectQuery = "SELECT fir.valor_financeiro FROM financeiro_receber fir WHERE fir.codigo_financeiro = '" + codigo_finan + "' LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    valor_financeiro = cursor.getString(cursor.getColumnIndex("valor_financeiro"));
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.endTransaction();
        db.close();
        return valor_financeiro;
    }


    //########## FORMAS PAGAMENTO CLIENTE ############

    //CONSTANTES FORMAS PAGAMENTO CLIENTE
    private static final String TABELA_FORMAS_PAGAMENTO = "formas_pagamento";
    private static final String CODIGO_PAGAMENTO = "codigo_pagamento";
    private static final String DESCRICAO_FORMA_PAGAMENTO = "descricao_forma_pagamento";
    private static final String TIPO_FORMA_PAGAMENTO = "tipo_forma_pagamento";
    private static final String AUTO_NUM_PAGAMENTO = "auto_num_pagamento";
    private static final String BAIXA_FORMA_PAGAMENTO = "baixa_forma_pagamento";
    private static final String USUARIO_ATUAL_FORMA_PAGAMENTO = "usuario_atual";
    private static final String DATA_CADASTRO_FORMA_PAGAMENTO = "data_cadastro";
    private static final String ATIVO_FORMA_PAGAMENTO = "ativo";
    private static final String CONTA_BANCARIA_FORMA_PAGAMENTO = "conta_bancaria";


    private static final String[] COLUNAS_FORMAS_PAGAMENTO = {
            CODIGO_PAGAMENTO,
            DESCRICAO_FORMA_PAGAMENTO,
            TIPO_FORMA_PAGAMENTO,
            AUTO_NUM_PAGAMENTO,
            BAIXA_FORMA_PAGAMENTO,
            USUARIO_ATUAL_FORMA_PAGAMENTO,
            DATA_CADASTRO_FORMA_PAGAMENTO,
            ATIVO_FORMA_PAGAMENTO,
            CONTA_BANCARIA_FORMA_PAGAMENTO
    };

    //
    private FormasPagamentoDomain cursorToFormasPagamentoDomain(Cursor cursor) {
        FormasPagamentoDomain formasPagamentoDomain = new FormasPagamentoDomain(null, null, null, null, null, null, null, null, null);

        formasPagamentoDomain.setCodigo_pagamento(cursor.getString(0));
        formasPagamentoDomain.setDescricao_forma_pagamento(cursor.getString(1));
        formasPagamentoDomain.setTipo_forma_pagamento(cursor.getString(2));
        formasPagamentoDomain.setAuto_num_pagamento(cursor.getString(3));
        formasPagamentoDomain.setBaixa_forma_pagamento(cursor.getString(4));
        formasPagamentoDomain.setUsuario_atual(cursor.getString(5));
        formasPagamentoDomain.setData_cadastro(cursor.getString(6));
        formasPagamentoDomain.setAtivo(cursor.getString(7));
        formasPagamentoDomain.setConta_bancaria(cursor.getString(8));

        return formasPagamentoDomain;
    }

    // ######### POS #############################
    public String getPosBaixaPrazo() {
        String baixa_a_prazo = "0";
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        //
        String selectQuery = "SELECT * " +
                "FROM pos ";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    baixa_a_prazo = cursor.getString(cursor.getColumnIndex("baixa_a_prazo"));
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.endTransaction();
            //db.close();
        }

        db.endTransaction();
        db.close();
        return baixa_a_prazo;
    }

//########## FORMAS PAGAMENTO CLIENTE ############

    //CONSTANTES FORMAS PAGAMENTO CLIENTE
    private static final String TABELA_FORMAS_PAGAMENTO_CLIENTE = "formas_pagamento_cliente";
    private static final String CODIGO_PAGAMENTO_CLIENTE = "codigo_pagamento_cliente";
    private static final String PAGAMENTO_CLIENTE = "pagamento_cliente";
    private static final String PAGAMENTO_PRAZO_CLIENTE = "pagamento_prazo_cliente";
    private static final String CLIENTE_PAGAMENTO = "cliente_pagamento";
    private static final String USUARIO = "usuario";

    private static final String[] COLUNAS_FORMAS_PAGAMENTO_CLIENTE = {
            CODIGO_PAGAMENTO_CLIENTE,
            PAGAMENTO_CLIENTE,
            PAGAMENTO_PRAZO_CLIENTE,
            CLIENTE_PAGAMENTO,
            USUARIO
    };

    //
    private FormasPagamentoClienteDomain cursorToFormasPagamentoClienteDomain(Cursor cursor) {
        FormasPagamentoClienteDomain formasPagamentoClienteDomain = new FormasPagamentoClienteDomain(null, null, null, null, null);

        formasPagamentoClienteDomain.setCodigo_pagamento_cliente(cursor.getString(0));
        formasPagamentoClienteDomain.setPagamento_cliente(cursor.getString(1));
        formasPagamentoClienteDomain.setPagamento_prazo_cliente(cursor.getString(2));
        formasPagamentoClienteDomain.setCliente_pagamento(cursor.getString(3));
        formasPagamentoClienteDomain.setUsuario(cursor.getString(4));

        return formasPagamentoClienteDomain;
    }

    // Kleilson Teste
    public ArrayList<String> getFormasPagamentoCliente(String codigoCliente) {
        ArrayList<String> list = new ArrayList<>();
        String baixa = this.getPosBaixaPrazo();
        //
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        /*
        fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria
        FROM formas_pagamento fpg
        WHERE fpg.tipo_forma_pagamento = 'A VISTA'
        UNION ALL
        SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento,
        fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria
        FROM formas_pagamento fpg
        INNER JOIN formas_pagamento_cliente fpc ON fpc.pagamento_cliente = fpg.descricao_forma_pagamento
        WHERE fpc.cliente_pagamento = '813'
         */
        //
        String selectQuery = "SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento,\n" +
                "fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria\n" +
                "FROM formas_pagamento fpg\n" +
                "WHERE fpg.tipo_forma_pagamento = 'A VISTA' AND fpg.ativo\n" +
                "UNION ALL\n" +
                "SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento,\n" +
                "fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria\n" +
                "FROM formas_pagamento fpg\n" +
                "INNER JOIN formas_pagamento_cliente fpc ON fpc.pagamento_cliente = fpg.descricao_forma_pagamento\n" +
                "WHERE fpc.cliente_pagamento = '" + codigoCliente + "' AND fpg.ativo";


        Cursor cursor = db.rawQuery(selectQuery, null);
        //list.add("DINHEIRO" + " _ " + "A VISTA");// + " _ " + "1"
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //String codigo_pagamento_cliente = cursor.getString(cursor.getColumnIndex("codigo_pagamento_cliente"));
                    String pagamento_cliente = cursor.getString(cursor.getColumnIndex("descricao_forma_pagamento"));
                    String tipo_pagamento = cursor.getString(cursor.getColumnIndex("tipo_forma_pagamento"));
                    list.add(
                            pagamento_cliente + " _ " +
                                    tipo_pagamento + " _ " +
                                    cursor.getString(cursor.getColumnIndex("auto_num_pagamento")) + " _ " +
                                    cursor.getString(cursor.getColumnIndex("baixa_forma_pagamento"))
                    );
                    //list.add(descricao_produto);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.endTransaction();
            //db.close();
        }

        /*//
        String selectQuery = "Select * From " + TABELA_FORMAS_PAGAMENTO +
                " WHERE tipo_forma_pagamento = 'A VISTA'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        //list.add("DINHEIRO" + " _ " + "A VISTA");// + " _ " + "1"
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //String codigo_pagamento_cliente = cursor.getString(cursor.getColumnIndex("codigo_pagamento_cliente"));
                    String pagamento_cliente = cursor.getString(cursor.getColumnIndex("descricao_forma_pagamento"));
                    list.add(pagamento_cliente + " _ " + "A VISTA");
                    //list.add(descricao_produto);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.endTransaction();
            //db.close();
        }

        //
        String selectQueryFPC = "Select * From " + TABELA_FORMAS_PAGAMENTO_CLIENTE +
                " WHERE cliente_pagamento = '" + codigoCliente + "'";
        Cursor cursorFPC = db.rawQuery(selectQueryFPC, null);
        //list.add("DINHEIRO" + " _ " + "A VISTA");// + " _ " + "1"
        try {
            if (cursorFPC.getCount() > 0) {
                while (cursorFPC.moveToNext()) {
                    //String codigo_pagamento_cliente = cursor.getString(cursor.getColumnIndex("codigo_pagamento_cliente"));
                    String pagamento_cliente = cursorFPC.getString(cursorFPC.getColumnIndex("pagamento_cliente"));
                    list.add(pagamento_cliente + " _ " + "A PRAZO");
                    //list.add(descricao_produto);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }*/

        db.endTransaction();
        db.close();
        return list;
    }

    // Kleilson Teste
    public ArrayList<String> getFormasPagamentoClienteBaixa(String codigoCliente) {
        ArrayList<String> list = new ArrayList<>();
        String baixa = this.getPosBaixaPrazo();
        //
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        /*
        fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria
        FROM formas_pagamento fpg
        WHERE fpg.tipo_forma_pagamento = 'A VISTA'
        UNION ALL
        SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento,
        fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria
        FROM formas_pagamento fpg
        INNER JOIN formas_pagamento_cliente fpc ON fpc.pagamento_cliente = fpg.descricao_forma_pagamento
        WHERE fpc.cliente_pagamento = '813'
         */
        //
        String selectQuery = "SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento,\n" +
                "fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria\n" +
                "FROM formas_pagamento fpg\n" +
                "WHERE fpg.tipo_forma_pagamento = 'A VISTA' AND fpg.ativo";

        //String baixa = this.getPosBaixaPrazo();
        if (baixa.equalsIgnoreCase("1")) {
            selectQuery += "\n" +
                    "UNION ALL\n" +
                    "SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento,\n" +
                    "fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria\n" +
                    "FROM formas_pagamento fpg\n" +
                    "INNER JOIN formas_pagamento_cliente fpc ON fpc.pagamento_cliente = fpg.descricao_forma_pagamento\n" +
                    "WHERE fpc.cliente_pagamento = '" + codigoCliente + "' AND fpg.ativo";
        }


        Cursor cursor = db.rawQuery(selectQuery, null);
        //list.add("DINHEIRO" + " _ " + "A VISTA");// + " _ " + "1"
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //String codigo_pagamento_cliente = cursor.getString(cursor.getColumnIndex("codigo_pagamento_cliente"));
                    String pagamento_cliente = cursor.getString(cursor.getColumnIndex("descricao_forma_pagamento"));
                    String tipo_pagamento = cursor.getString(cursor.getColumnIndex("tipo_forma_pagamento"));
                    list.add(
                            pagamento_cliente + " _ " +
                                    tipo_pagamento + " _ " +
                                    cursor.getString(cursor.getColumnIndex("auto_num_pagamento")) + " _ " +
                                    cursor.getString(cursor.getColumnIndex("baixa_forma_pagamento"))
                    );
                    //list.add(descricao_produto);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.endTransaction();
            //db.close();
        }

        /*//
        String selectQuery = "Select * From " + TABELA_FORMAS_PAGAMENTO +
                " WHERE tipo_forma_pagamento = 'A VISTA'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        //list.add("DINHEIRO" + " _ " + "A VISTA");// + " _ " + "1"
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //String codigo_pagamento_cliente = cursor.getString(cursor.getColumnIndex("codigo_pagamento_cliente"));
                    String pagamento_cliente = cursor.getString(cursor.getColumnIndex("descricao_forma_pagamento"));
                    list.add(pagamento_cliente + " _ " + "A VISTA");
                    //list.add(descricao_produto);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.endTransaction();
            //db.close();
        }

        //
        String selectQueryFPC = "Select * From " + TABELA_FORMAS_PAGAMENTO_CLIENTE +
                " WHERE cliente_pagamento = '" + codigoCliente + "'";
        Cursor cursorFPC = db.rawQuery(selectQueryFPC, null);
        //list.add("DINHEIRO" + " _ " + "A VISTA");// + " _ " + "1"
        try {
            if (cursorFPC.getCount() > 0) {
                while (cursorFPC.moveToNext()) {
                    //String codigo_pagamento_cliente = cursor.getString(cursor.getColumnIndex("codigo_pagamento_cliente"));
                    String pagamento_cliente = cursorFPC.getString(cursorFPC.getColumnIndex("pagamento_cliente"));
                    list.add(pagamento_cliente + " _ " + "A PRAZO");
                    //list.add(descricao_produto);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }*/

        db.endTransaction();
        db.close();
        return list;
    }

    //************ TABELA FINANCEIRO **************

    //CONSTANTES FINANCEIRO
    private static final String TABELA_FINANCEIRO = "financeiro";
    private static final String CODIGO_FINANCEIRO = "codigo_financeiro";
    private static final String UNIDADE_FINANCEIRO = "unidade_financeiro";
    private static final String DATA_FINANCEIRO = "data_financeiro";
    private static final String CODIGO_CLIENTE_FINANCEIRO = "codigo_cliente_financeiro";
    private static final String FPAGAMENTO_FINANCEIRO = "fpagamento_financeiro";
    private static final String DOCUMENTO_FINANCEIRO = "documento_financeiro";
    private static final String VENCIMENTO_FINANCEIRO = "vencimento_financeiro";
    private static final String VALOR_FINANCEIRO = "valor_financeiro";
    private static final String STATUS_AUTORIZACAO = "status_autorizacao";
    private static final String PAGO = "pago";
    private static final String VASILHAME_REF = "vasilhame_ref";
    private static final String USUARIO_ATUAL_FINANCEIRO = "usuario_atual";
    private static final String DATA_INCLUSAO = "data_inclusao";
    private static final String NOSSO_NUMERO_FINANCEIRO = "nosso_numero_financeiro";
    private static final String ID_VENDEDOR_FINANCEIRO = "id_vendedor_financeiro";
    private static final String ID_FINANCEIRO_APP = "id_financeiro_app";

    private static final String[] COLUNAS_FINANCEIRO = {
            CODIGO_FINANCEIRO,
            UNIDADE_FINANCEIRO,
            DATA_FINANCEIRO,
            CODIGO_CLIENTE_FINANCEIRO,
            FPAGAMENTO_FINANCEIRO,
            DOCUMENTO_FINANCEIRO,
            VENCIMENTO_FINANCEIRO,
            VALOR_FINANCEIRO,
            STATUS_AUTORIZACAO,
            PAGO,
            VASILHAME_REF,
            USUARIO_ATUAL_FINANCEIRO,
            DATA_INCLUSAO,
            NOSSO_NUMERO_FINANCEIRO,
            ID_VENDEDOR_FINANCEIRO,
            ID_FINANCEIRO_APP
    };

    //
    private FinanceiroVendasDomain cursorToFinanceiroVendasDomain(Cursor cursor) {
        FinanceiroVendasDomain financeiroVendasDomain = new FinanceiroVendasDomain(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        financeiroVendasDomain.setCodigo_financeiro(cursor.getString(0));
        financeiroVendasDomain.setUnidade_financeiro(cursor.getString(1));
        financeiroVendasDomain.setData_financeiro(cursor.getString(2));
        financeiroVendasDomain.setCodigo_cliente_financeiro(cursor.getString(3));
        financeiroVendasDomain.setFpagamento_financeiro(cursor.getString(4));
        financeiroVendasDomain.setDocumento_financeiro(cursor.getString(5));
        financeiroVendasDomain.setVencimento_financeiro(cursor.getString(6));
        financeiroVendasDomain.setValor_financeiro(cursor.getString(7));
        financeiroVendasDomain.setStatus_autorizacao(cursor.getString(8));
        financeiroVendasDomain.setPago(cursor.getString(9));
        financeiroVendasDomain.setVasilhame_ref(cursor.getString(10));
        financeiroVendasDomain.setUsuario_atual(cursor.getString(11));
        financeiroVendasDomain.setData_inclusao(cursor.getString(12));
        financeiroVendasDomain.setNosso_numero_financeiro(cursor.getString(13));
        financeiroVendasDomain.setId_vendedor_financeiro(cursor.getString(14));
        financeiroVendasDomain.setId_financeiro_app(cursor.getString(15));

        return financeiroVendasDomain;
    }

    //
    private FormasPagamentoReceberTemp cursorToFormasPagamentoReceberTemp(Cursor cursor) {
        FormasPagamentoReceberTemp formasPagamentoReceberTemp = new FormasPagamentoReceberTemp(null, null, null, null);

        formasPagamentoReceberTemp.setId(cursor.getString(0));
        formasPagamentoReceberTemp.setId_cliente(cursor.getString(1));
        formasPagamentoReceberTemp.setId_forma_pagamento(cursor.getString(2));
        formasPagamentoReceberTemp.setValor(cursor.getString(3));

        return formasPagamentoReceberTemp;
    }

    //
    public void addFinanceiro(FinanceiroVendasDomain financeiroVendasDomain) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CODIGO_FINANCEIRO, financeiroVendasDomain.getCodigo_financeiro());
        values.put(UNIDADE_FINANCEIRO, financeiroVendasDomain.getUnidade_financeiro());
        values.put(DATA_FINANCEIRO, financeiroVendasDomain.getData_financeiro());
        values.put(CODIGO_CLIENTE_FINANCEIRO, financeiroVendasDomain.getCodigo_cliente_financeiro());
        values.put(FPAGAMENTO_FINANCEIRO, financeiroVendasDomain.getFpagamento_financeiro());
        values.put(DOCUMENTO_FINANCEIRO, financeiroVendasDomain.getDocumento_financeiro());
        values.put(VENCIMENTO_FINANCEIRO, financeiroVendasDomain.getVencimento_financeiro());
        values.put(VALOR_FINANCEIRO, financeiroVendasDomain.getValor_financeiro());
        values.put(STATUS_AUTORIZACAO, financeiroVendasDomain.getStatus_autorizacao());
        values.put(PAGO, financeiroVendasDomain.getPago());
        values.put(VASILHAME_REF, financeiroVendasDomain.getVasilhame_ref());
        values.put(USUARIO_ATUAL_FINANCEIRO, financeiroVendasDomain.getUsuario_atual());
        values.put(DATA_INCLUSAO, financeiroVendasDomain.getData_inclusao());
        values.put(NOSSO_NUMERO_FINANCEIRO, financeiroVendasDomain.getNosso_numero_financeiro());
        values.put(ID_VENDEDOR_FINANCEIRO, financeiroVendasDomain.getId_vendedor_financeiro());
        values.put(ID_FINANCEIRO_APP, financeiroVendasDomain.getId_financeiro_app());
        db.insert(TABELA_FINANCEIRO, null, values);
        db.close();
    }

    //
    public void addFinanceiroRecebidos(FinanceiroVendasDomain financeiroVendasDomain) {
        myDataBase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CODIGO_FINANCEIRO, financeiroVendasDomain.getCodigo_financeiro());
        values.put(UNIDADE_FINANCEIRO, financeiroVendasDomain.getUnidade_financeiro());
        values.put(DATA_FINANCEIRO, financeiroVendasDomain.getData_financeiro());
        values.put(CODIGO_CLIENTE_FINANCEIRO, financeiroVendasDomain.getCodigo_cliente_financeiro());
        values.put(FPAGAMENTO_FINANCEIRO, financeiroVendasDomain.getFpagamento_financeiro());
        values.put(DOCUMENTO_FINANCEIRO, financeiroVendasDomain.getDocumento_financeiro());
        values.put(VENCIMENTO_FINANCEIRO, financeiroVendasDomain.getVencimento_financeiro());
        values.put(VALOR_FINANCEIRO, financeiroVendasDomain.getValor_financeiro());
        values.put(STATUS_AUTORIZACAO, financeiroVendasDomain.getStatus_autorizacao());
        values.put(PAGO, financeiroVendasDomain.getPago());
        values.put(VASILHAME_REF, financeiroVendasDomain.getVasilhame_ref());
        values.put(USUARIO_ATUAL_FINANCEIRO, financeiroVendasDomain.getUsuario_atual());
        values.put(DATA_INCLUSAO, financeiroVendasDomain.getData_inclusao());
        values.put(NOSSO_NUMERO_FINANCEIRO, financeiroVendasDomain.getNosso_numero_financeiro());
        values.put(ID_VENDEDOR_FINANCEIRO, financeiroVendasDomain.getId_vendedor_financeiro());
        values.put(ID_FINANCEIRO_APP, financeiroVendasDomain.getId_financeiro_app());
        myDataBase.insert("recebidos", null, values);
    }

    //
    public void addValorFinReceber(String id_cliente, String id_forma_pagamento, String valor) {
        myDataBase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id_cliente", id_cliente);
        values.put("id_forma_pagamento", id_forma_pagamento);
        values.put("valor", valor);
        myDataBase.insert("formas_pagamento_receber", null, values);
    }

    // VERIFICA SE A FORMA DE PAGAMENTO ESCOLHIDA JÁ EXISTE EM RECEBIDOS
    public String[] verForPagRecTemp(String fpagamento_financeiro, String codigo_financeiro_app) {

        myDataBase = this.getReadableDatabase();
        //db.beginTransaction();

        String query = "SELECT id, valor " +
                "FROM formas_pagamento_receber " +
                "WHERE id_forma_pagamento = '" + fpagamento_financeiro + "'";
        Log.e("SQL", "verForPagRecTemp - " + query);

        Cursor cursor = myDataBase.rawQuery(query, null);

        String[] codigo_financeiro = new String[]{"0", ""};
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                codigo_financeiro = new String[]{cursor.getString(0), cursor.getString(1)};
            }
            //myDataBase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return codigo_financeiro;
    }

    // ATUALIZA OS VALORES DAS BAIXAS RECEBIDAS
    public int updateFinRecTemp(String id, String valor) {
        myDataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("valor", valor);

        int i = myDataBase.update(
                "formas_pagamento_receber",
                values,
                "id" + " = ?",
                new String[]{id}
        );

        return i;
    }


    // ATUALIZA OS VALORES DAS BAIXAS RECEBIDAS
    public int updateFinanceiroRecebidos(String codigo_financeiro, String valor) {
        myDataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("valor_financeiro", valor);

        int i = myDataBase.update(
                "recebidos",
                values,
                "codigo_financeiro" + " = ?",
                new String[]{codigo_financeiro}
        );

        return i;
    }


    //LISTAR TODOS OS ITENS DO FINANCEIRO
    public ArrayList<FinanceiroVendasDomain> getFinanceiroCliente(int id_financeiro_app) {
        ArrayList<FinanceiroVendasDomain> listaFinanceiroVendas = new ArrayList<>();

        String query = "SELECT * FROM " + TABELA_FINANCEIRO + " WHERE id_financeiro_app = '" + id_financeiro_app + "'";
        Log.e("SQL", "getFinanceiroCliente - " + query);
        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                FinanceiroVendasDomain financeiro = cursorToFinanceiroVendasDomain(cursor);
                listaFinanceiroVendas.add(financeiro);
            } while (cursor.moveToNext());
        }

        //db.close();
        return listaFinanceiroVendas;
    }


    //LISTAR AS VALORES INSERIDOS AO FINANCEIRO A RECEBER DO CLIENTE
    public ArrayList<FormasPagamentoReceberTemp> getFinanceiroClienteRecebidos(int id_cliente) {
        //SQLiteDatabase
        myDataBase = this.getReadableDatabase();
        ArrayList<FormasPagamentoReceberTemp> listaFormasPagamentoReceber = new ArrayList<>();

        String query = "SELECT * " +
                "FROM formas_pagamento_receber " +
                "WHERE id_cliente = '" + id_cliente + "' ";

        Log.e("SQL", "getFinanceiroClienteRecebidos - " + query);
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                FormasPagamentoReceberTemp temp = cursorToFormasPagamentoReceberTemp(cursor);
                listaFormasPagamentoReceber.add(temp);

                Log.e("SQL", "getFinanceiroClienteRecebidos - " + temp.getId_forma_pagamento());
            } while (cursor.moveToNext());
        }

        //db.close();
        return listaFormasPagamentoReceber;
    }
    /*public ArrayList<FinanceiroVendasDomain> getFinanceiroClienteRecebidos(int id_financeiro_app) {
        //SQLiteDatabase
        myDataBase = this.getReadableDatabase();
        ArrayList<FinanceiroVendasDomain> listaFinanceiroVendas = new ArrayList<>();

        //
        StringBuilder filtro = new StringBuilder();
        for (int i = 0; i < IdsCR.size(); i++) {
            if (i > 0) {
                filtro.append(" OR");
            }
            filtro.append(" codigo_financeiro = '").append(IdsCR.get(i)).append("'");
        }

        //id_financeiro_app = '" + id_financeiro_app + "'" filtro.toString() + " " +
        String query = "SELECT codigo_financeiro, unidade_financeiro, data_financeiro, " +
                "codigo_cliente_financeiro, fpagamento_financeiro, documento_financeiro, " +
                "vencimento_financeiro, SUM(valor_financeiro) valor_financeiro, status_autorizacao, " +
                "pago, vasilhame_ref, usuario_atual, data_inclusao, nosso_numero_financeiro, " +
                "id_vendedor_financeiro, id_financeiro_app " +
                "FROM recebidos " +
                "WHERE" + filtro.toString() + " " +
                "GROUP BY fpagamento_financeiro";

        Log.e("SQL", "getFinanceiroClienteRecebidos - " + query);
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                FinanceiroVendasDomain financeiro = cursorToFinanceiroVendasDomain(cursor);
                listaFinanceiroVendas.add(financeiro);

                Log.e("SQL", "getFinanceiroClienteRecebidos - " + financeiro.getCodigo_financeiro());
            } while (cursor.moveToNext());
        }

        //db.close();
        return listaFinanceiroVendas;
    }
*/

    //LISTAR TODOS OS ITENS DO FINANCEIRO
    public ArrayList<FinanceiroReceberClientes> getContasReceberCliente(String id_cliente) {
        ArrayList<FinanceiroReceberClientes> listaFinanceiroVendas = new ArrayList<>();

        /*String query = "SELECT * " +
                "FROM financeiro_receber " +
                "WHERE codigo_cliente = '" + id_cliente + "' AND status_app = '1'" +
                " AND baixa_finalizada_app = '0'";*/
        //String query = "SELECT * FROM financeiro_receber WHERE codigo_cliente = '" + id_cliente + "' AND valor_financeiro != total_pago";

        String query = "SELECT *" +
                "  FROM financeiro_receber AS fir" +
                " WHERE fir.codigo_cliente = '" + id_cliente + "' AND " +
                "       fir.valor_financeiro > (" +
                "                                  SELECT (CASE WHEN Sum(rec.pago) IS NOT NULL THEN Sum(rec.pago) ELSE 0 END) AS pago" +
                "                                    FROM recebidos rec" +
                "                                   WHERE rec.codigo_financeiro = fir.codigo_financeiro" +
                "                              )";

        Log.e("SQL", "getContasReceberCliente - " + query);

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                FinanceiroReceberClientes financeiro = cursorToContasReceberCliente(cursor);
                listaFinanceiroVendas.add(financeiro);
            } while (cursor.moveToNext());
        }

        //db.close();
        return listaFinanceiroVendas;
    }

    //RETORNA AS CONTAS A RECEBER QUE ESTÃO PENDENTE
    public ArrayList<FinanceiroReceberClientes> getListFormContasReceberCliente(String id_cliente) {
        ArrayList<FinanceiroReceberClientes> listaFinanceiroVendas = new ArrayList<>();

        // AND valor_financeiro != total_pago
        String query = "SELECT * FROM financeiro_receber WHERE codigo_cliente = '" + id_cliente + "' ORDER BY data_financeiro, codigo_financeiro";
        Log.e("SQL", "getListFormContasReceberCliente - " + query);

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                FinanceiroReceberClientes financeiro = cursorToContasReceberCliente(cursor);
                listaFinanceiroVendas.add(financeiro);
            } while (cursor.moveToNext());
        }
        return listaFinanceiroVendas;
    }

    /*//LISTAR TODOS OS CLIENTES
    public ArrayList<Clientes> getAllClientesContasReceber() {
        ArrayList<Clientes> listaClientes = new ArrayList<>();

        String query = "SELECT * FROM " + TABELA_CLIENTES + " " +
                "INNER JOIN financeiro_receber ON " +
                "financeiro_receber.codigo_cliente_financeiro = " + TABELA_CLIENTES + "." + CODIGO_CLIENTE +
                " GROUP BY " + TABELA_CLIENTES + "." + NOME_CLIENTE +
                " ORDER BY " + TABELA_CLIENTES + "." + NOME_CLIENTE;
        Log.e("SQL", query);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Clientes clientes = cursorToCliente(cursor);
                listaClientes.add(clientes);
            } while (cursor.moveToNext());
        }

        return listaClientes;
    }*/

    //
    private FinanceiroReceberClientes cursorToContasReceberCliente(Cursor cursor) {
        FinanceiroReceberClientes clientes = new FinanceiroReceberClientes(null, null, null, null, null, null, null, null, null, null, null, null, null);
        //
        clientes.setCodigo_financeiro(cursor.getString(0));
        clientes.setNosso_numero_financeiro(cursor.getString(1));
        clientes.setData_financeiro(cursor.getString(2));
        clientes.setCodigo_cliente(cursor.getString(3));
        clientes.setNome_cliente(cursor.getString(4));
        clientes.setDocumento_financeiro(cursor.getString(5));
        clientes.setFpagamento_financeiro(cursor.getString(6));
        clientes.setVencimento_financeiro(cursor.getString(7));
        clientes.setValor_financeiro(cursor.getString(8));
        clientes.setTotal_pago(cursor.getString(9));
        clientes.setCodigo_pagamento(cursor.getString(10));
        clientes.setStatus_app(cursor.getString(11));
        clientes.setBaixa_finalizada_app(cursor.getString(12));
        return clientes;
    }

    //LISTAR TODOS OS CLIENTES
    public ArrayList<FinanceiroReceberClientes> getAllClientesContasReceber() {
        ArrayList<FinanceiroReceberClientes> listaClientes = new ArrayList<>();

        //
        /*String query = "SELECT * FROM financeiro_receber " +
                "INNER JOIN " + TABELA_CLIENTES + " ON " +
                TABELA_CLIENTES + "." + CODIGO_CLIENTE + " = financeiro_receber.codigo_cliente" +
                " WHERE status_app = '1'" +
                " GROUP BY " + TABELA_CLIENTES + "." + CODIGO_CLIENTE +
                " ORDER BY " + TABELA_CLIENTES + "." + NOME_CLIENTE;*/

        /*String query = "SELECT * " +
                "FROM financeiro_receber " +
                "INNER JOIN clientes ON clientes.codigo_cliente = financeiro_receber.codigo_cliente " +
                "WHERE status_app = '0' " +
                "GROUP BY clientes.codigo_cliente ORDER BY clientes.nome_cliente";*/

        /*String query = "SELECT * " +
                "FROM financeiro_receber " +
                "INNER JOIN clientes ON clientes.codigo_cliente = financeiro_receber.codigo_cliente " +
                "GROUP BY clientes.codigo_cliente ORDER BY clientes.nome_cliente";*/
        String query = "SELECT *" +
                "  FROM financeiro_receber fir" +
                "       INNER JOIN" +
                "       clientes cli ON cli.codigo_cliente = fir.codigo_cliente" +
                " WHERE fir.valor_financeiro > (" +
                "                                  SELECT (CASE WHEN Sum(rec.pago) IS NOT NULL THEN Sum(rec.pago) ELSE 0 END) AS pago" +
                "                                    FROM recebidos rec" +
                "                                   WHERE rec.codigo_financeiro = fir.codigo_financeiro" +
                "                              )" +
                " GROUP BY cli.codigo_cliente" +
                " ORDER BY cli.nome_cliente";

        Log.e("SQL", "getAllClientesContasReceber - " + query);

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        //
        if (cursor.moveToFirst()) {
            do {
                FinanceiroReceberClientes clientes = cursorToContasReceberCliente(cursor);
                listaClientes.add(clientes);
            } while (cursor.moveToNext());
        }

        return listaClientes;
    }


    //ALTERAR CLIENTE
    public int updateFinanceiroReceber(String codigo_financeiro, String status, int id_baixa_app) {
        myDataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status_app", status);
        values.put("id_baixa_app", String.valueOf(id_baixa_app));

        int i = myDataBase.update(
                "financeiro_receber",
                values,
                "codigo_financeiro" + " = ?",
                new String[]{String.valueOf(codigo_financeiro)}
        );
        //db.close();
        return i;
    }


    //ALTERAR CLIENTE
    public int updateFinalizarVenda(String codigo_venda_app) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VENDA_FINALIZADA_APP, "1");

        int i = db.update(
                TABELA_VENDAS,
                values,
                CODIGO_VENDA_APP + " = ?",
                new String[]{String.valueOf(codigo_venda_app)}
        );
        db.close();
        return i;
    }

    //SOMAR O VALOR DO FINANCEIRO
    public String getValorTotalFinanceiro(String codigo_financeiro_app) {

        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        String selectQuery = "SELECT SUM(valor_financeiro) FROM " + TABELA_FINANCEIRO + " WHERE " + ID_FINANCEIRO_APP + " = '" + codigo_financeiro_app + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        String total = "0.0";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                total = cursor.getString(0);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return total;
    }

    //SOMAR O VALOR DO FINANCEIRO A RECEBER
    public String SomaValTotFinReceber(String id_cliente) {

        myDataBase = this.getReadableDatabase();
        //db.beginTransaction();

        String selectQuery = "SELECT SUM(valor) FROM " + "formas_pagamento_receber" + " WHERE id_cliente = '" + id_cliente + "'";

        Cursor cursor = myDataBase.rawQuery(selectQuery, null);

        String total = "0.0";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                total = cursor.getString(0);
            }
            //db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }/* finally {
            db.endTransaction();
            db.close();
        }*/

        return total;
    }

    //SOMAR O VALOR DO FINANCEIRO A RECEBER
    public String getValorTotalFinanceiroReceber(String codigo_financeiro_app) {

        myDataBase = this.getReadableDatabase();
        //db.beginTransaction();

        String selectQuery = "SELECT SUM(valor_financeiro) FROM " + "recebidos" + " WHERE " + ID_FINANCEIRO_APP + " = '" + codigo_financeiro_app + "'";

        Cursor cursor = myDataBase.rawQuery(selectQuery, null);

        String total = "0.0";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                total = cursor.getString(0);
            }
            //db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }/* finally {
            db.endTransaction();
            db.close();
        }*/

        return total;
    }

    // VERIFICA SE A FORMA DE PAGAMENTO ESCOLHIDA JÁ EXISTE EM RECEBIDOS
    public String[] verFormaPagamentoRecebidos(String fpagamento_financeiro, String codigo_financeiro_app) {

        myDataBase = this.getReadableDatabase();
        //db.beginTransaction();

        String query = "SELECT codigo_financeiro, valor_financeiro " +
                "FROM recebidos " +
                "WHERE fpagamento_financeiro = '" + fpagamento_financeiro + "' AND id_financeiro_app = '" + codigo_financeiro_app + "'";
        Log.e("SQL", "verFormaPagamentoRecebidos - " + query);

        Cursor cursor = myDataBase.rawQuery(query, null);

        String[] codigo_financeiro = new String[]{"0", ""};
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                codigo_financeiro = new String[]{cursor.getString(0), cursor.getString(1)};
            }
            //myDataBase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return codigo_financeiro;
    }

    //
    public int deleteFinanceiroRecebidos(int id_baixa_app) {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(
                "recebidos",
                ID_FINANCEIRO_APP + " = ?",
                new String[]{String.valueOf(id_baixa_app)}
        );

        //
        updateFinanceiroReceber(id_baixa_app);

        //
        db.close();
        return i;
    }

    //
    private int updateFinanceiroReceber(int id_baixa_app) {
        SQLiteDatabase db = this.getWritableDatabase();

        //
        ContentValues values = new ContentValues();
        values.put("status_app", "1");
        values.put("id_baixa_app", "0");
        int a = 0;

        try {
            a = db.update(
                    "financeiro_receber",
                    values,
                    "id_baixa_app" + " = ?",
                    new String[]{String.valueOf(id_baixa_app)}
            );
        } catch (Exception e) {

        }

        //
        db.close();
        return a;
    }

    //
    public int deleteItemFinanceiro(FinanceiroVendasDomain financeiroVendasDomain) {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(
                TABELA_FINANCEIRO,
                CODIGO_FINANCEIRO + " = ?",
                new String[]{String.valueOf(financeiroVendasDomain.getCodigo_financeiro())}
        );
        db.close();
        return i;
    }

    //
    public int deleteItemFinanceiroReceberTemp(FormasPagamentoReceberTemp temp) {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(
                "formas_pagamento_receber",
                "id = ?",
                new String[]{String.valueOf(temp.getId())}
        );
        db.close();
        return i;
    }

    //
    public int deleteFinanceiroReceberTemp() {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(
                "formas_pagamento_receber",
                null,
                null
        );
        db.close();
        return i;
    }

    //
    public int deleteItemFinanceiroReceber(FinanceiroVendasDomain financeiroVendasDomain) {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(
                "recebidos",
                CODIGO_FINANCEIRO + " = ?",
                new String[]{String.valueOf(financeiroVendasDomain.getCodigo_financeiro())}
        );
        db.close();
        return i;
    }

    /*################### GERENCIAR VENDAS #####################*/
    //
    public int apagarVendasNaoFinalizadas(FinanceiroVendasDomain financeiroVendasDomain) {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(
                TABELA_VENDAS,
                CODIGO_FINANCEIRO + " = ?",
                new String[]{String.valueOf(financeiroVendasDomain.getCodigo_financeiro())}
        );
        db.close();
        return i;
    }

    /*################### MARGEN CLIENTES #####################*/
    //
    public String getMargemCliente(String produto, String id) {
        //BANCO DE DADOS
        SQLiteDatabase db = this.getReadableDatabase();

        //
        ClassAuxiliar cAux = new ClassAuxiliar();

        //
        String preco_unidade = null;
        String margem_cliente = null;
        String preco;

        try {

            //
            Cursor cursor;
            String query = "" +
                    "SELECT unp.preco_unidade, mac.margem_cliente " +
                    "FROM unidades_precos unp " +
                    "INNER JOIN margens_clientes mac ON mac.produto_margem_cliente = unp.produto_preco " +
                    "WHERE unp.produto_preco = '" + produto + "' AND mac.codigo_cliente_margem_cliente = '" + id + "'" +
                    "";
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    preco_unidade = cursor.getString(cursor.getColumnIndex("preco_unidade"));
                    margem_cliente = cursor.getString(cursor.getColumnIndex("margem_cliente"));

                } while (cursor.moveToNext());
            }

            //
            String[] sub = new String[]{preco_unidade, margem_cliente};

            preco = cAux.maskMoney(new BigDecimal(String.valueOf(cAux.subitrair(sub))));

        } catch (Exception e) {
            preco = "0,00";
        }

        Log.e("MARGEN ", preco);

        return preco;
    }

    //
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return myDataBase.query(table, null, null, null, null, null, null);
    }

    // ** Enviar dados
    public String IdProduto(String produto) {

        String query = "SELECT pro.codigo_produto " +
                "FROM " + TABELA_PRODUTOS + " pro " +
                "WHERE pro.descricao_produto = '" + produto + "'";

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);
        StringBuilder str = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                str.append(cursor.getString(cursor.getColumnIndex("codigo_produto")));
            } while (cursor.moveToNext());
        }

        myDataBase.close();
        return str.toString();

    }

    // ** Enviar dados
    public String IdFormaPagamento(String fpg) {

        String query = "SELECT tfp.codigo_pagamento " +
                "FROM " + TABELA_FORMAS_PAGAMENTO + " tfp " +
                "WHERE tfp.descricao_forma_pagamento = '" + fpg + "'";

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);
        StringBuilder str = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                str.append(cursor.getString(cursor.getColumnIndex("codigo_pagamento")));
            } while (cursor.moveToNext());
        }

        myDataBase.close();
        return str.toString();

    }

    // ** Enviar dados VENDAS
    public String[] EnviarDados() {

        // **
        StringBuilder VENDAS = new StringBuilder();
        StringBuilder CLIENTES = new StringBuilder();
        StringBuilder PRODUTOS = new StringBuilder();
        StringBuilder QUANTIDADES = new StringBuilder();
        StringBuilder DATAS = new StringBuilder();
        StringBuilder VALORES = new StringBuilder();

        String query = "SELECT *, (ven.preco_unitario * 100) as valPreVen " +
                "FROM " + TABELA_VENDAS + " ven " +
                "WHERE ven.venda_finalizada_app = '1'";

        //Log.e("SQL = ", query);

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                // **
                VENDAS.append(",");
                VENDAS.append(cursor.getString(cursor.getColumnIndex("codigo_venda")));

                // **
                CLIENTES.append(",");
                CLIENTES.append(cursor.getString(cursor.getColumnIndex("codigo_cliente")));

                // **
                PRODUTOS.append(",");
                PRODUTOS.append(IdProduto(cursor.getString(cursor.getColumnIndex("produto_venda"))));

                // **
                QUANTIDADES.append(",");
                QUANTIDADES.append(cursor.getString(cursor.getColumnIndex("quantidade_venda")));

                // **
                DATAS.append(",");
                DATAS.append(aux.exibirData(cursor.getString(cursor.getColumnIndex("data_movimento"))));

                // **
                VALORES.append(",");
                /*String pre_unit = "";
                //aux.soNumeros(cursor.getString(cursor.getColumnIndex("preco_unitario")));
                String[] valMlt = {aux.soNumeros(cursor.getString(cursor.getColumnIndex("preco_unitario"))), "100"};
                String valUnit = String.valueOf(aux.multiplicar(valMlt));*/
                /*if (valUnit.length() < 4) {
                    pre_unit = aux.soNumeros(cursor.getString(cursor.getColumnIndex("preco_unitario"))) + "00";
                } else {
                    pre_unit = aux.soNumeros(cursor.getString(cursor.getColumnIndex("preco_unitario")));
                }*/
                VALORES.append(cursor.getString(cursor.getColumnIndex("valPreVen")));
                Log.i(TAG + " Peço unit.", cursor.getString(cursor.getColumnIndex("valPreVen")));
            } while (cursor.moveToNext());
        }

        myDataBase.close();

        String[] ret = {
                VENDAS.toString(),
                CLIENTES.toString(),
                PRODUTOS.toString(),
                QUANTIDADES.toString(),
                DATAS.toString(),
                VALORES.toString()
        };

        return ret;
    }

    // ** Enviar dados VENDAS
    public String[] EnviarDadosFinanceiro() {

        // **
        /*StringBuilder VENDAS = new StringBuilder();
        StringBuilder CLIENTES = new StringBuilder();
        StringBuilder PRODUTOS = new StringBuilder();
        StringBuilder QUANTIDADES = new StringBuilder();
        StringBuilder DATAS = new StringBuilder();
        StringBuilder VALORES = new StringBuilder();*/
        StringBuilder FINANCEIROS = new StringBuilder();
        StringBuilder FINVEN = new StringBuilder();
        StringBuilder VENCIMENTOS = new StringBuilder();
        StringBuilder VALORESFIN = new StringBuilder();
        StringBuilder FPAGAMENTOS = new StringBuilder();
        StringBuilder DOCUMENTOS = new StringBuilder();

        String query = "SELECT *, (fin.valor_financeiro * 100) as valFin " +
                "FROM " + TABELA_VENDAS + " ven " +
                "INNER JOIN " + TABELA_FINANCEIRO + " fin ON fin.id_financeiro_app = ven.codigo_venda_app " +
                "WHERE ven.venda_finalizada_app = '1'";

        //Log.e("SQL = ", query);

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                // ---------------------------------- ** FINANCEIRO

                // **
                FINANCEIROS.append(",");
                FINANCEIROS.append(cursor.getString(cursor.getColumnIndex("codigo_financeiro")));

                // **
                FINVEN.append(",");
                FINVEN.append(cursor.getString(cursor.getColumnIndex("codigo_venda")));

                // **
                VENCIMENTOS.append(",");
                VENCIMENTOS.append(aux.exibirData(cursor.getString(cursor.getColumnIndex("vencimento_financeiro"))));

                // **
                VALORESFIN.append(",");
                /*String val_fin = "";
                if (aux.soNumeros(cursor.getString(cursor.getColumnIndex("valor_financeiro"))).length() < 4) {
                    val_fin = aux.soNumeros(cursor.getString(cursor.getColumnIndex("valor_financeiro"))) + "00";
                } else {
                    val_fin = aux.soNumeros(cursor.getString(cursor.getColumnIndex("valor_financeiro")));
                }*/
                //{aux.soNumeros()
                //String[] valMlt = {"100", cursor.getString(cursor.getColumnIndex("valor_financeiro"))};
                //String val_fin = String.valueOf(aux.multiplicar(valMlt));

                VALORESFIN.append(cursor.getString(cursor.getColumnIndex("valFin")));
                Log.i(TAG + " Valor Fin.", cursor.getString(cursor.getColumnIndex("valFin")));
                //VALORESFIN.append(aux.soNumeros(cursor.getString(cursor.getColumnIndex("valor_financeiro"))));

                // **
                FPAGAMENTOS.append(",");
                FPAGAMENTOS.append(IdFormaPagamento(cursor.getString(cursor.getColumnIndex("fpagamento_financeiro"))));

                // **
                DOCUMENTOS.append(",");
                DOCUMENTOS.append(cursor.getString(cursor.getColumnIndex("documento_financeiro")));
            } while (cursor.moveToNext());
        }

        myDataBase.close();

        /*String[] ret = {
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
        };*/

        String[] ret = {
                FINANCEIROS.toString(),
                FINVEN.toString(),
                VENCIMENTOS.toString(),
                VALORESFIN.toString(),
                FPAGAMENTOS.toString(),
                DOCUMENTOS.toString()
        };

        return ret;
    }

    // ** Enviar dados CONTAS A RECEBER
    public String[] EnviarDadosContasReceber() {
        // **
        StringBuilder codigo_financeiro = new StringBuilder();
        StringBuilder unidade_financeiro = new StringBuilder();
        StringBuilder data_financeiro = new StringBuilder();
        StringBuilder codigo_cliente_financeiro = new StringBuilder();
        StringBuilder fpagamento_financeiro = new StringBuilder();
        StringBuilder documento_financeiro = new StringBuilder();
        StringBuilder vencimento_financeiro = new StringBuilder();
        StringBuilder valor_financeiro = new StringBuilder();
        StringBuilder status_autorizacao = new StringBuilder();
        StringBuilder pago = new StringBuilder();
        StringBuilder vasilhame_ref = new StringBuilder();
        StringBuilder usuario_atual = new StringBuilder();
        StringBuilder data_inclusao = new StringBuilder();
        StringBuilder nosso_numero_financeiro = new StringBuilder();
        StringBuilder id_vendedor_financeiro = new StringBuilder();

        String query = "SELECT *, (pago * 100) as valPago  FROM recebidos";

        //Log.e("SQL = ", query);

        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // **
                codigo_financeiro.append(",");
                codigo_financeiro.append(cursor.getString(cursor.getColumnIndex("codigo_financeiro")));
                // **
                unidade_financeiro.append(",");
                unidade_financeiro.append(cursor.getString(cursor.getColumnIndex("unidade_financeiro")));
                // **
                data_financeiro.append(",");
                data_financeiro.append(aux.exibirData(cursor.getString(cursor.getColumnIndex("data_financeiro"))));
                // **
                codigo_cliente_financeiro.append(",");
                codigo_cliente_financeiro.append(cursor.getString(cursor.getColumnIndex("codigo_cliente_financeiro")));
                // **
                fpagamento_financeiro.append(",");
                fpagamento_financeiro.append(cursor.getString(cursor.getColumnIndex("fpagamento_financeiro")));
                // **
                documento_financeiro.append(",");
                documento_financeiro.append(cursor.getString(cursor.getColumnIndex("documento_financeiro")));
                // **
                vencimento_financeiro.append(",");
                vencimento_financeiro.append(aux.exibirData(cursor.getString(cursor.getColumnIndex("vencimento_financeiro"))));
                // **
                valor_financeiro.append(",");
                valor_financeiro.append(cursor.getString(cursor.getColumnIndex("valor_financeiro")));
                // **
                status_autorizacao.append(",");
                status_autorizacao.append(cursor.getString(cursor.getColumnIndex("status_autorizacao")));
                // **
                pago.append(",");
                pago.append(cursor.getString(cursor.getColumnIndex("valPago")));
                // **
                vasilhame_ref.append(",");
                vasilhame_ref.append(cursor.getString(cursor.getColumnIndex("vasilhame_ref")));
                // **
                usuario_atual.append(",");
                usuario_atual.append(cursor.getString(cursor.getColumnIndex("usuario_atual")));
                // **
                data_inclusao.append(",");
                data_inclusao.append(aux.exibirData(cursor.getString(cursor.getColumnIndex("data_inclusao"))));
                // **
                nosso_numero_financeiro.append(",");
                nosso_numero_financeiro.append(cursor.getString(cursor.getColumnIndex("nosso_numero_financeiro")));
                // **
                id_vendedor_financeiro.append(",");
                id_vendedor_financeiro.append(cursor.getString(cursor.getColumnIndex("id_vendedor_financeiro")));


                //Log.i(TAG + " Peço unit.", cursor.getString(cursor.getColumnIndex("valPreVen")));
            } while (cursor.moveToNext());
        }

        myDataBase.close();

        String[] ret = {
                codigo_financeiro.toString(),
                unidade_financeiro.toString(),
                data_financeiro.toString(),
                codigo_cliente_financeiro.toString(),
                fpagamento_financeiro.toString(),
                documento_financeiro.toString(),
                vencimento_financeiro.toString(),
                valor_financeiro.toString(),
                status_autorizacao.toString(),
                pago.toString(),
                vasilhame_ref.toString(),
                usuario_atual.toString(),
                data_inclusao.toString(),
                nosso_numero_financeiro.toString(),
                id_vendedor_financeiro.toString()
        };

        return ret;
    }

    // ** Enviar dados
    public int DiasPrazoCliente(String fpg, String cod) {
        int result = 0;

        String query = "SELECT fpc.pagamento_prazo_cliente FROM formas_pagamento_cliente fpc WHERE fpc.pagamento_cliente = '" + fpg + "' AND fpc.cliente_pagamento = '" + cod + "'";
        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                result = Integer.parseInt(cursor.getString(cursor.getColumnIndex("pagamento_prazo_cliente")));
            } while (cursor.moveToNext());
        }

        myDataBase.close();
        return result;
    }

    public void FecharConexao() {
        myDataBase.close();
        SQLiteDatabase db = this.getReadableDatabase();
        db.close();
    }
}
