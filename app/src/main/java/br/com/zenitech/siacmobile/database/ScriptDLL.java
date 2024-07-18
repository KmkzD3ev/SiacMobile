package br.com.zenitech.siacmobile.database;

public class ScriptDLL {
    public static String creatTableCartoesAliquotas() {
        return """
                CREATE TABLE cartoes_aliquotas (
                    codigo                 INTEGER      PRIMARY KEY
                                                        NOT NULL,
                    codigo_forma_pagamento INTEGER      NOT NULL,
                    bandeira               INTEGER      NOT NULL,
                    aliquota               REAL (13, 4) NOT NULL,
                    parcela                INTEGER      NOT NULL,
                    prazo                  INTEGER      NOT NULL
                );
                """;
    }

    public static String creatTableClientes() {
        return """
                    CREATE TABLE clientes (
                        codigo_cliente    INTEGER NOT NULL,
                        nome_cliente      TEXT    NOT NULL,
                        latitude_cliente  TEXT,
                        longitude_cliente TEXT,
                        saldo             TEXT,
                        cpfcnpj           TEXT,
                        endereco          TEXT,
                        apelido_cliente   TEXT,
                        PRIMARY KEY (
                            codigo_cliente
                        )
                    );
                """;
    }

    public static String creatTableConfiguracoes() {
        return """
                    CREATE TABLE configuracoes (
                        codigo           INTEGER NOT NULL,
                        exibir_preco_ref INTEGER NOT NULL
                                                 DEFAULT 0,
                        PRIMARY KEY (
                            codigo
                        )
                    );
                """;
    }

    public static String creatTableContasBancarias() {
        return """
                    CREATE TABLE contas_bancarias (
                        codigo              INTEGER      PRIMARY KEY,
                        banco_conta         TEXT         NOT NULL,
                        agencia             TEXT         NOT NULL,
                        conta               TEXT         NOT NULL,
                        dv_conta            TEXT         NOT NULL,
                        convenio            TEXT         NOT NULL,
                        contrato            TEXT         NOT NULL,
                        carteira            TEXT         NOT NULL,
                        variacao            TEXT         NOT NULL,
                        conta_cedente       TEXT         NOT NULL,
                        dv_conta_cedente    TEXT         NOT NULL,
                        cedente             TEXT         NOT NULL,
                        cpf_cnpj            TEXT         NOT NULL,
                        endereco            TEXT         NOT NULL,
                        cidade_uf           TEXT         NOT NULL,
                        instrucoes          TEXT         NOT NULL,
                        inicio_nosso_numero TEXT         NOT NULL,
                        dv_agencia          TEXT         NOT NULL,
                        taxa_boleto         REAL (13, 2) 
                    );
                """;
    }

    public static String creatTableFinanceiro() {
        return """
                    CREATE TABLE financeiro (
                        codigo_financeiro         INTEGER      NOT NULL,
                        unidade_financeiro        TEXT         NOT NULL,
                        data_financeiro           TEXT         NOT NULL,
                        codigo_cliente_financeiro INTEGER      NOT NULL,
                        fpagamento_financeiro     TEXT         NOT NULL,
                        documento_financeiro      INTEGER      NOT NULL,
                        vencimento_financeiro     TEXT         NOT NULL,
                        valor_financeiro          REAL (13, 2) NOT NULL
                                                               DEFAULT 0,
                        status_autorizacao        TEXT         NOT NULL,
                        pago                      TEXT         NOT NULL,
                        vasilhame_ref             INTEGER      NOT NULL,
                        usuario_atual             TEXT         NOT NULL,
                        data_inclusao             TEXT         NOT NULL,
                        nosso_numero_financeiro   TEXT         NOT NULL,
                        id_vendedor_financeiro    INTEGER      NOT NULL,
                        id_financeiro_app         TEXT         NOT NULL,
                        nota_fiscal               TEXT         NOT NULL,
                        codigo_aliquota           TEXT         NOT NULL,
                        PRIMARY KEY (
                            codigo_financeiro
                        )
                    );
                """;
    }

    public static String creatTableFinanceiroReceber() {
        return """
                    CREATE TABLE financeiro_receber (
                        codigo_financeiro       INTEGER      NOT NULL,
                        nosso_numero_financeiro TEXT         NOT NULL,
                        data_financeiro         TEXT         NOT NULL,
                        codigo_cliente          INTEGER      NOT NULL,
                        nome_cliente            TEXT         NOT NULL,
                        documento_financeiro    INTEGER      NOT NULL,
                        fpagamento_financeiro   TEXT         NOT NULL,
                        vencimento_financeiro   TEXT         NOT NULL,
                        valor_financeiro        REAL (13, 2) NOT NULL
                                                             DEFAULT 0,
                        total_pago              REAL (13, 2) NOT NULL
                                                             DEFAULT 0,
                        codigo_pagamento        TEXT         NOT NULL,
                        status_app              TEXT         NOT NULL,
                        baixa_finalizada_app    TEXT         NOT NULL,
                        id_baixa_app            TEXT         NOT NULL,
                        PRIMARY KEY (
                            codigo_financeiro
                        )
                    );
                """;
    }

    public static String creatTableFormasPagamento() {
        return """
                    CREATE TABLE formas_pagamento (
                        codigo_pagamento          INTEGER NOT NULL,
                        descricao_forma_pagamento TEXT    NOT NULL,
                        tipo_forma_pagamento      TEXT    NOT NULL,
                        auto_num_pagamento        INTEGER NOT NULL,
                        baixa_forma_pagamento     INTEGER NOT NULL,
                        usuario_atual             TEXT    NOT NULL,
                        data_cadastro             TEXT    NOT NULL,
                        ativo                     TEXT    NOT NULL,
                        conta_bancaria            INTEGER NOT NULL,
                        cartao                    INTEGER NOT NULL,
                        PRIMARY KEY (
                            codigo_pagamento
                        )
                    );
                """;
    }

    public static String creatTableFormasPagamentoCliente() {
        return """
                    CREATE TABLE formas_pagamento_cliente (
                        codigo_pagamento_cliente INTEGER NOT NULL,
                        pagamento_cliente        TEXT    NOT NULL,
                        pagamento_prazo_cliente  INTEGER NOT NULL,
                        cliente_pagamento        INTEGER NOT NULL,
                        usuario                  TEXT    NOT NULL,
                        PRIMARY KEY (
                            codigo_pagamento_cliente
                        )
                    );
                """;
    }

    public static String creatTableFormasPagamentoReceber() {
        return """
                    CREATE TABLE formas_pagamento_receber (
                        id                 INTEGER PRIMARY KEY
                                                   NOT NULL,
                        id_cliente         INTEGER,
                        id_forma_pagamento TEXT    NOT NULL,
                        valor              REAL    NOT NULL
                    );
                """;
    }

    public static String creatTableMargensClientes() {
        return """
                    CREATE TABLE margens_clientes (
                        codigo_margem_cliente         INTEGER NOT NULL,
                        unidade_margem_cliente        TEXT    NOT NULL,
                        codigo_cliente_margem_cliente TEXT    NOT NULL,
                        produto_margem_cliente        TEXT    NOT NULL,
                        margem_cliente                TEXT    NOT NULL,
                        margem_vale_produto           TEXT    NOT NULL,
                        taxa_entrega                  TEXT    NOT NULL,
                        PRIMARY KEY (
                            codigo_margem_cliente
                        )
                    );
                """;
    }

    public static String creatTablePos() {
        return """
                    CREATE TABLE pos (
                        codigo                INTEGER NOT NULL,
                        serial                INTEGER NOT NULL,
                        unidade               INTEGER NOT NULL,
                        serie                 INTEGER NOT NULL,
                        ultnfce               INTEGER NOT NULL,
                        ultboleto             INTEGER DEFAULT 0,
                        nota_remessa          INTEGER NOT NULL,
                        serie_remessa         INTEGER NOT NULL,
                        limite_credito        INTEGER NOT NULL,
                        ultpromissoria        INTEGER DEFAULT 0,
                        autovencimento        INTEGER DEFAULT 1,
                        modulo_pedidos        INTEGER DEFAULT 0,
                        baixa_a_prazo         INTEGER DEFAULT 0,
                        serie_boleto          INTEGER DEFAULT 0,
                        escolher_cliente_vale INTEGER DEFAULT 0,
                        PRIMARY KEY (
                            codigo
                        )
                    );
                """;
    }

    public static String creatTableProdutos() {
        return """
                    CREATE TABLE produtos (
                        codigo_produto     INTEGER NOT NULL,
                        descricao_produto  TEXT    NOT NULL,
                        id_produto_emissor TEXT,
                        PRIMARY KEY (
                            codigo_produto
                        )
                    );
                """;
    }

    public static String creatTableRecebidos() {
        return """
                    CREATE TABLE recebidos (
                        codigo_financeiro         INTEGER      NOT NULL,
                        unidade_financeiro        TEXT         NOT NULL,
                        data_financeiro           TEXT         NOT NULL,
                        codigo_cliente_financeiro INTEGER      NOT NULL,
                        fpagamento_financeiro     TEXT         NOT NULL,
                        documento_financeiro      INTEGER      NOT NULL,
                        vencimento_financeiro     TEXT         NOT NULL,
                        valor_financeiro          REAL (13, 2) NOT NULL
                                                               DEFAULT 0,
                        status_autorizacao        TEXT         NOT NULL,
                        pago                      TEXT         NOT NULL,
                        vasilhame_ref             INTEGER      NOT NULL,
                        usuario_atual             TEXT         NOT NULL,
                        data_inclusao             TEXT         NOT NULL,
                        nosso_numero_financeiro   TEXT         NOT NULL,
                        id_vendedor_financeiro    INTEGER      NOT NULL,
                        id_financeiro_app         INTEGER      NOT NULL
                    );
                """;
    }

    public static String creatTableRotasClientes() {
        return """
                    CREATE TABLE rotas_clientes (
                        rota_cliente   TEXT NOT NULL,
                        codigo_cliente TEXT NOT NULL
                    );
                """;
    }

    public static String creatTableRotasPrecos() {
        return """
                    CREATE TABLE rotas_precos (
                        codigo_preco_rota  INTEGER NOT NULL,
                        unidade_preco_rota TEXT    NOT NULL,
                        rota_preco_rota    TEXT    NOT NULL,
                        produto_preco_rota TEXT    NOT NULL,
                        preco_rota         TEXT    NOT NULL,
                        PRIMARY KEY (
                            codigo_preco_rota
                        )
                    );
                """;
    }

    public static String creatTableUnidades() {
        return """
                    CREATE TABLE unidades (
                        id_unidade        INTEGER NOT NULL,
                        descricao_unidade TEXT    NOT NULL,
                        razao_social      TEXT    NOT NULL,
                        cnpj              TEXT,
                        endereco          TEXT,
                        numero            TEXT,
                        bairro            TEXT,
                        cep               TEXT,
                        telefone          TEXT,
                        ie                TEXT,
                        cidade            TEXT,
                        uf                TEXT,
                        codigo_ibge       INTEGER,
                        url_consulta      TEXT,
                        PRIMARY KEY (
                            id_unidade
                        )
                    );
                """;
    }

    public static String creatTableUnidadesPrecos() {
        return """
                    CREATE TABLE unidades_precos (
                        codigo_unidade_preco INTEGER NOT NULL,
                        unidade_preco        TEXT    NOT NULL,
                        produto_preco        TEXT    NOT NULL,
                        preco_unidade        TEXT    NOT NULL,
                        PRIMARY KEY (
                            codigo_unidade_preco
                        )
                    );
                """;
    }

    public static String creatTableVale() {
        return """
                    CREATE TABLE vale (
                        codigo_vale         INTEGER      NOT NULL,
                        unidade_vale        TEXT         DEFAULT NULL,
                        codigo_cliente_vale INTEGER      DEFAULT NULL,
                        numero_vale         INTEGER      DEFAULT NULL,
                        valor_vale          REAL (13, 2) DEFAULT NULL,
                        situacao_vale       TEXT         DEFAULT NULL,
                        produto_vale        TEXT         DEFAULT NULL,
                        PRIMARY KEY (
                            codigo_vale
                        )
                    );
                """;
    }

    public static String creatTableVendasApp() {
        return """
                    CREATE TABLE vendas_app (
                        codigo_venda             INTEGER      NOT NULL,
                        codigo_cliente           INTEGER      NOT NULL,
                        unidade_venda            TEXT         NOT NULL,
                        produto_venda            TEXT         NOT NULL,
                        data_movimento           TEXT         NOT NULL,
                        quantidade_venda         INTEGER      NOT NULL,
                        preco_unitario           REAL (13, 2) NOT NULL
                                                              DEFAULT 0,
                        valor_total              REAL (13, 2) NOT NULL
                                                              DEFAULT 0,
                        vendedor_venda           TEXT         NOT NULL,
                        status_autorizacao_venda TEXT         NOT NULL,
                        entrega_futura_venda     TEXT         NOT NULL,
                        entrega_futura_realizada TEXT         NOT NULL,
                        usuario_atual            TEXT         NOT NULL,
                        data_cadastro            TEXT         NOT NULL,
                        codigo_venda_app         TEXT         NOT NULL,
                        venda_finalizada_app     TEXT         NOT NULL,
                        chave_importacao         TEXT,
                        PRIMARY KEY (
                            codigo_venda
                        )
                    );
                """;
    }
}
