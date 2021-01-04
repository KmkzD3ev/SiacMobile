package br.com.zenitech.siacmobile.domains;

public class FormasPagamentoReceberTemp {
    String id;
    String id_cliente;
    String id_forma_pagamento;
    String valor;

    public FormasPagamentoReceberTemp(String id, String id_cliente, String id_forma_pagamento, String valor) {
        this.id = id;
        this.id_cliente = id_cliente;
        this.id_forma_pagamento = id_forma_pagamento;
        this.valor = valor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getId_forma_pagamento() {
        return id_forma_pagamento;
    }

    public void setId_forma_pagamento(String id_forma_pagamento) {
        this.id_forma_pagamento = id_forma_pagamento;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
