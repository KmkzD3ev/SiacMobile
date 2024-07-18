package br.com.zenitech.siacmobile.repositories;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import br.com.zenitech.siacmobile.domains.Clientes;

public class ClientesRepositorio {

    private final String tabClientes = "clientes";


    //
    public void insertClientes(Clientes cli){
        ContentValues cv = new ContentValues();
        cv.put("codigo_cliente", cli.getCodigo_cliente());
        cv.put("nome_cliente", cli.getNome_cliente());
        cv.put("latitude_cliente", cli.getLatitude_cliente());
        cv.put("longitude_cliente", cli.getLongitude_cliente());
        cv.put("saldo", cli.getSaldo());
        cv.put("cpfcnpj", cli.getCpfcnpj());
        cv.put("endereco", cli.getEndereco());
        cv.put("apelido_cliente", cli.getApelido_cliente());
    }
}
