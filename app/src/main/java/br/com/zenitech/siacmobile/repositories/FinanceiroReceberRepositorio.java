package br.com.zenitech.siacmobile.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.domains.FinanceiroVendasDomain;
import br.com.zenitech.siacmobile.domains.FormasPagamentoReceberTemp;

public class FinanceiroReceberRepositorio {

    private final SQLiteDatabase myDb;
    private final ClassAuxiliar cAux;

    private String tb_formas_pagamento_receber = "formas_pagamento_receber";

    public FinanceiroReceberRepositorio(SQLiteDatabase myDb, ClassAuxiliar cAux) {
        this.myDb = myDb;
        this.cAux = cAux;
    }

    //CONSTANTES FINANCEIRO
    private final String TABELA_FINANCEIRO = "financeiro";
    private final String CODIGO_FINANCEIRO = "codigo_financeiro";
    private final String UNIDADE_FINANCEIRO = "unidade_financeiro";
    private final String DATA_FINANCEIRO = "data_financeiro";
    private final String CODIGO_CLIENTE_FINANCEIRO = "codigo_cliente_financeiro";
    private final String FPAGAMENTO_FINANCEIRO = "fpagamento_financeiro";
    private final String DOCUMENTO_FINANCEIRO = "documento_financeiro";
    private final String VENCIMENTO_FINANCEIRO = "vencimento_financeiro";
    private final String VALOR_FINANCEIRO = "valor_financeiro";
    private final String STATUS_AUTORIZACAO = "status_autorizacao";
    private final String PAGO = "pago";
    private final String VASILHAME_REF = "vasilhame_ref";
    private final String USUARIO_ATUAL_FINANCEIRO = "usuario_atual";
    private final String DATA_INCLUSAO = "data_inclusao";
    private final String NOSSO_NUMERO_FINANCEIRO = "nosso_numero_financeiro";
    private final String ID_VENDEDOR_FINANCEIRO = "id_vendedor_financeiro";
    private final String ID_FINANCEIRO_APP = "id_financeiro_app";
    private final String NOTA_FISCAL = "nota_fiscal";
    private final String CODIGO_ALIQUOTA = "codigo_aliquota";

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
    private FinanceiroVendasDomain cursorToFinanceiroVendasDomain(Cursor cursor) {
        FinanceiroVendasDomain financeiroVendasDomain = new FinanceiroVendasDomain(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

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
        financeiroVendasDomain.setNota_fiscal(cursor.getString(16));
        financeiroVendasDomain.setCodigo_aliquota(cursor.getString(17));

        return financeiroVendasDomain;
    }

    // APAGA O FINANCEIRO TEMPORÁRIO DO CONTAS A RECEBER
    public int deleteFinanceiroReceberTemp() {
        //myDb.delete("recebidos", null, null); USADO PARA TESTES DEBUG
        return myDb.delete(tb_formas_pagamento_receber, null, null);
    }

    // Kleilson Teste
    public ArrayList<String> getFormasPagamentoClienteBaixa(String codigoCliente) {
        ArrayList<String> list = new ArrayList<>();
        String baixa = this.getPosBaixaPrazo();
        //
        String selectQuery = "SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento,\n" +
                "fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria\n" +
                "FROM formas_pagamento fpg\n" +
                "WHERE fpg.tipo_forma_pagamento = 'A VISTA' AND fpg.ativo";

        if (baixa.equalsIgnoreCase("1")) {
            selectQuery += "\n" +
                    "UNION ALL\n" +
                    "SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento,\n" +
                    "fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria\n" +
                    "FROM formas_pagamento fpg\n" +
                    "INNER JOIN formas_pagamento_cliente fpc ON fpc.pagamento_cliente = fpg.descricao_forma_pagamento\n" +
                    "WHERE fpg.baixa_forma_pagamento = '2' AND fpc.cliente_pagamento = '" + codigoCliente + "' AND fpg.ativo";
        }

        Cursor cursor = myDb.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String pagamento_cliente = cursor.getString(cursor.getColumnIndexOrThrow("descricao_forma_pagamento"));
                    String tipo_pagamento = cursor.getString(cursor.getColumnIndexOrThrow("tipo_forma_pagamento"));
                    /*list.add(
                            pagamento_cliente + " _ " +
                                    tipo_pagamento + " _ " +
                                    cursor.getString(cursor.getColumnIndexOrThrow("auto_num_pagamento")) + " _ " +
                                    cursor.getString(cursor.getColumnIndexOrThrow("baixa_forma_pagamento"))
                    );*/

                    list.add(pagamento_cliente);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return list;
    }

    //LISTAR AS VALORES INSERIDOS AO FINANCEIRO A RECEBER DO CLIENTE
    public ArrayList<FormasPagamentoReceberTemp> getFinanceiroClienteRecebidos(String id_cliente) {

        ArrayList<FormasPagamentoReceberTemp> listaFormasPagamentoReceber = new ArrayList<>();

        String query = "SELECT * " +
                "FROM formas_pagamento_receber " +
                "WHERE id_cliente = '" + id_cliente + "' " +
                "ORDER BY valor DESC";

        //Log.e("ErrorCR", query);

        Cursor cursor = myDb.rawQuery(query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    FormasPagamentoReceberTemp temp = cursorToFormasPagamentoReceberTemp(cursor);
                    listaFormasPagamentoReceber.add(temp);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            //Log.e("ErrorCR", "erro = " + e.getMessage());
        }
        cursor.close();
        return listaFormasPagamentoReceber;
    }

    public String getValorFinReceberCli(String codigo_finan) {
        String valor_financeiro = "0";

        //
        String selectQuery = "SELECT fir.valor_financeiro FROM financeiro_receber fir WHERE fir.codigo_financeiro = '" + codigo_finan + "' LIMIT 1";

        //Log.e("ErrorCR", selectQuery);
        Cursor cursor = myDb.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    valor_financeiro = cursor.getString(cursor.getColumnIndexOrThrow("valor_financeiro"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return valor_financeiro;
    }

    public String getTotalRecebido(String codigo_finan) {
        String valor_financeiro = "0";

        //
        String selectQuery = "SELECT pago " +
                "FROM recebidos " +
                "WHERE codigo_financeiro = " + codigo_finan + " " +
                "LIMIT 1";
        Log.e("sql", "ContasReceber: " + selectQuery);
        //Log.e("ErrorCR", selectQuery);
        Cursor cursor = myDb.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    valor_financeiro = cursor.getString(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ContasReceber", e.getMessage());
        }
        cursor.close();
        return valor_financeiro;
    }

    // add financeiro recebidos
    public void addFinanceiroRecebidos(FinanceiroVendasDomain financeiroVendasDomain) {
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

        Log.e("SQL", "addFinanceiroRecebidos: " + values);
        //Log.e("ErrorCR", "addFinanceiroRecebidos: " + values);

        myDb.insert("recebidos", null, values);
        values.clear();
    }

    //
    public String getPosBaixaPrazo() {
        String baixa_a_prazo = "0";
        String selectQuery = "SELECT * FROM pos LIMIT 1";
        Cursor cursor = myDb.rawQuery(selectQuery, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    baixa_a_prazo = cursor.getString(cursor.getColumnIndexOrThrow("baixa_a_prazo"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return baixa_a_prazo;
    }

    // ATUALIZA OS VALORES DAS BAIXAS RECEBIDAS
    public int updateFinRecTemp(String id, String valor) {
        ContentValues values = new ContentValues();
        values.put("valor", valor);
        int i = myDb.update(
                "formas_pagamento_receber",
                values,
                "id" + " = ?",
                new String[]{id}
        );
        values.clear();
        return i;
    }

    //
    public void addValorFinReceber(String id_cliente, String id_forma_pagamento, String valor) {
        ContentValues values = new ContentValues();
        values.put("id_cliente", id_cliente);
        values.put("id_forma_pagamento", id_forma_pagamento);
        values.put("valor", valor);
        Log.e("SQL", "addValorFinReceber: " + values);
        myDb.insert("formas_pagamento_receber", null, values);
        values.clear();
    }

    // VERIFICA SE A FORMA DE PAGAMENTO ESCOLHIDA JÁ EXISTE EM RECEBIDOS
    public String[] verForPagRecTemp(String fpagamento, String codigo_cliente) {
        String query = "SELECT id, valor " +
                "FROM formas_pagamento_receber " +
                "WHERE id_forma_pagamento = '" + fpagamento + "' AND id_cliente = '" + codigo_cliente + "'";
        Log.e("SQL", "verForPagRecTemp - " + query);

        Cursor cursor = myDb.rawQuery(query, null);

        String[] codigo_financeiro = new String[]{"0", ""};
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                codigo_financeiro = new String[]{cursor.getString(0), cursor.getString(1)};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return codigo_financeiro;
    }

    //SOMAR O VALOR DO FINANCEIRO A RECEBER
    public String SomaValTotFinReceber(String id_cliente) {
        String selectQuery = "SELECT SUM(valor) FROM " + "formas_pagamento_receber" + " WHERE id_cliente = '" + id_cliente + "'";
        Cursor cursor = myDb.rawQuery(selectQuery, null);

        String total = "0.0";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                total = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return total;
    }

    //
    public FinanceiroVendasDomain getBaixaRecebida(String codigo_finan) {
        FinanceiroVendasDomain listaVendas = new FinanceiroVendasDomain(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

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

        Cursor cursor = myDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                listaVendas = cursorToFinanceiroVendasDomain(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaVendas;
    }

    // VERIFICA SE A FORMA DE PAGAMENTO ESCOLHIDA JÁ EXISTE EM RECEBIDOS
    public String[] verFormaPagamentoRecebidos(String fpagamento_financeiro, String codigo_financeiro_app) {
        String query = "SELECT codigo_financeiro, valor_financeiro " +
                "FROM recebidos " +
                "WHERE fpagamento_financeiro = '" + fpagamento_financeiro + "' AND id_financeiro_app = '" + codigo_financeiro_app + "'";
        Log.e("SQL", "verFormaPagamentoRecebidos - " + query);

        Cursor cursor = myDb.rawQuery(query, null);

        String[] codigo_financeiro = new String[]{"0", ""};
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                codigo_financeiro = new String[]{cursor.getString(0), cursor.getString(1)};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return codigo_financeiro;
    }

    // ATUALIZA OS VALORES DAS BAIXAS RECEBIDAS
    public int updateFinanceiroRecebidos(String codigo_financeiro, String valor) {
        ContentValues values = new ContentValues();
        values.put("valor_financeiro", valor);

        int i = myDb.update(
                "recebidos",
                values,
                "codigo_financeiro" + " = ?",
                new String[]{codigo_financeiro}
        );
        values.clear();
        return i;
    }

    public String GetTipoFormaPagamento(String formaPagamento) {
        String query = "SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento, fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria " +
                "FROM formas_pagamento fpg " +
                "WHERE fpg.descricao_forma_pagamento = '" + formaPagamento + "' AND fpg.ativo";
        Log.e("SQL", "GetTipoFormaPagamento - " + query);

        Cursor cursor = myDb.rawQuery(query, null);

        //String[] codigo_financeiro = new String[]{"0", ""};
        String tipo_pagamento = "";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                tipo_pagamento = cursor.getString(cursor.getColumnIndexOrThrow("tipo_forma_pagamento"));
                //codigo_financeiro = new String[]{cursor.getString(0), cursor.getString(1)};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return tipo_pagamento;
    }

    public String GetFormaPagamentoAutoNumerada(String formaPagamento) {
        String query = "SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento, fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria " +
                "FROM formas_pagamento fpg " +
                "WHERE fpg.descricao_forma_pagamento = '" + formaPagamento + "' AND fpg.ativo";
        Log.e("SQL", "GetTipoFormaPagamento - " + query);

        Cursor cursor = myDb.rawQuery(query, null);

        String tipo_pagamento = "";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                tipo_pagamento = cursor.getString(cursor.getColumnIndexOrThrow("auto_num_pagamento"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return tipo_pagamento;
    }

    public String GetFormaPagamentoBaixaAutomatica(String formaPagamento) {
        String query = "SELECT fpg.codigo_pagamento, fpg.descricao_forma_pagamento, fpg.tipo_forma_pagamento, fpg.auto_num_pagamento, fpg.baixa_forma_pagamento, fpg.usuario_atual, fpg.data_cadastro, fpg.ativo, fpg.conta_bancaria " +
                "FROM formas_pagamento fpg " +
                "WHERE fpg.descricao_forma_pagamento = '" + formaPagamento + "' AND fpg.ativo";
        Log.e("SQL", "GetTipoFormaPagamento - " + query);

        Cursor cursor = myDb.rawQuery(query, null);

        String tipo_pagamento = "";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                tipo_pagamento = cursor.getString(cursor.getColumnIndexOrThrow("baixa_forma_pagamento"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return tipo_pagamento;
    }
}
