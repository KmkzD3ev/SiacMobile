package br.com.zenitech.siacmobile.domains;


public class FormasPagamentoClienteDomain {
    private String codigo_pagamento_cliente;
    private String pagamento_cliente;
    private String pagamento_prazo_cliente;
    private String cliente_pagamento;
    private String usuario;

    public FormasPagamentoClienteDomain(String codigo_pagamento_cliente, String pagamento_cliente, String pagamento_prazo_cliente, String cliente_pagamento, String usuario) {
        this.codigo_pagamento_cliente = codigo_pagamento_cliente;
        this.pagamento_cliente = pagamento_cliente;
        this.pagamento_prazo_cliente = pagamento_prazo_cliente;
        this.cliente_pagamento = cliente_pagamento;
        this.usuario = usuario;
    }

    public String getCodigo_pagamento_cliente() {
        return codigo_pagamento_cliente;
    }

    public void setCodigo_pagamento_cliente(String codigo_pagamento_cliente) {
        this.codigo_pagamento_cliente = codigo_pagamento_cliente;
    }

    public String getPagamento_cliente() {
        return pagamento_cliente;
    }

    public void setPagamento_cliente(String pagamento_cliente) {
        this.pagamento_cliente = pagamento_cliente;
    }

    public String getPagamento_prazo_cliente() {
        return pagamento_prazo_cliente;
    }

    public void setPagamento_prazo_cliente(String pagamento_prazo_cliente) {
        this.pagamento_prazo_cliente = pagamento_prazo_cliente;
    }

    public String getCliente_pagamento() {
        return cliente_pagamento;
    }

    public void setCliente_pagamento(String cliente_pagamento) {
        this.cliente_pagamento = cliente_pagamento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
