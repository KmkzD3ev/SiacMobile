package br.com.zenitech.siacmobile.domains;

public class FinanceiroReceberClientes {
    String codigo_financeiro;
    String nosso_numero_financeiro;
    String data_financeiro;
    String codigo_cliente;
    String nome_cliente;
    String documento_financeiro;
    String fpagamento_financeiro;
    String vencimento_financeiro;
    String valor_financeiro;
    String total_pago;
    String codigo_pagamento;
    String status_app;
    String baixa_finalizada_app;

    public FinanceiroReceberClientes(String codigo_financeiro, String nosso_numero_financeiro, String data_financeiro, String codigo_cliente, String nome_cliente, String documento_financeiro, String fpagamento_financeiro, String vencimento_financeiro, String valor_financeiro, String total_pago, String codigo_pagamento, String status_app, String baixa_finalizada_app) {
        this.codigo_financeiro = codigo_financeiro;
        this.nosso_numero_financeiro = nosso_numero_financeiro;
        this.data_financeiro = data_financeiro;
        this.codigo_cliente = codigo_cliente;
        this.nome_cliente = nome_cliente;
        this.documento_financeiro = documento_financeiro;
        this.fpagamento_financeiro = fpagamento_financeiro;
        this.vencimento_financeiro = vencimento_financeiro;
        this.valor_financeiro = valor_financeiro;
        this.total_pago = total_pago;
        this.codigo_pagamento = codigo_pagamento;
        this.status_app = status_app;
        this.baixa_finalizada_app = baixa_finalizada_app;
    }

    public String getCodigo_financeiro() {
        return codigo_financeiro;
    }

    public void setCodigo_financeiro(String codigo_financeiro) {
        this.codigo_financeiro = codigo_financeiro;
    }

    public String getNosso_numero_financeiro() {
        return nosso_numero_financeiro;
    }

    public void setNosso_numero_financeiro(String nosso_numero_financeiro) {
        this.nosso_numero_financeiro = nosso_numero_financeiro;
    }

    public String getData_financeiro() {
        return data_financeiro;
    }

    public void setData_financeiro(String data_financeiro) {
        this.data_financeiro = data_financeiro;
    }

    public String getCodigo_cliente() {
        return codigo_cliente;
    }

    public void setCodigo_cliente(String codigo_cliente) {
        this.codigo_cliente = codigo_cliente;
    }

    public String getNome_cliente() {
        return nome_cliente;
    }

    public void setNome_cliente(String nome_cliente) {
        this.nome_cliente = nome_cliente;
    }

    public String getDocumento_financeiro() {
        return documento_financeiro;
    }

    public void setDocumento_financeiro(String documento_financeiro) {
        this.documento_financeiro = documento_financeiro;
    }

    public String getFpagamento_financeiro() {
        return fpagamento_financeiro;
    }

    public void setFpagamento_financeiro(String fpagamento_financeiro) {
        this.fpagamento_financeiro = fpagamento_financeiro;
    }

    public String getVencimento_financeiro() {
        return vencimento_financeiro;
    }

    public void setVencimento_financeiro(String vencimento_financeiro) {
        this.vencimento_financeiro = vencimento_financeiro;
    }

    public String getValor_financeiro() {
        return valor_financeiro;
    }

    public void setValor_financeiro(String valor_financeiro) {
        this.valor_financeiro = valor_financeiro;
    }

    public String getTotal_pago() {
        return total_pago;
    }

    public void setTotal_pago(String total_pago) {
        this.total_pago = total_pago;
    }

    public String getCodigo_pagamento() {
        return codigo_pagamento;
    }

    public void setCodigo_pagamento(String codigo_pagamento) {
        this.codigo_pagamento = codigo_pagamento;
    }

    public String getStatus_app() {
        return status_app;
    }

    public void setStatus_app(String status_app) {
        this.status_app = status_app;
    }

    public String getBaixa_finalizada_app() {
        return baixa_finalizada_app;
    }

    public void setBaixa_finalizada_app(String baixa_finalizada_app) {
        this.baixa_finalizada_app = baixa_finalizada_app;
    }
}
