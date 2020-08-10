package br.com.zenitech.siacmobile.domains;


public class FormasPagamentoDomain {

    private String codigo_pagamento;
    private String descricao_forma_pagamento;
    private String tipo_forma_pagamento;
    private String auto_num_pagamento;
    private String baixa_forma_pagamento;
    private String usuario_atual;
    private String data_cadastro;
    private String ativo;
    private String conta_bancaria;

    public FormasPagamentoDomain(String codigo_pagamento, String descricao_forma_pagamento, String tipo_forma_pagamento, String auto_num_pagamento, String baixa_forma_pagamento, String usuario_atual, String data_cadastro, String ativo, String conta_bancaria) {
        this.codigo_pagamento = codigo_pagamento;
        this.descricao_forma_pagamento = descricao_forma_pagamento;
        this.tipo_forma_pagamento = tipo_forma_pagamento;
        this.auto_num_pagamento = auto_num_pagamento;
        this.baixa_forma_pagamento = baixa_forma_pagamento;
        this.usuario_atual = usuario_atual;
        this.data_cadastro = data_cadastro;
        this.ativo = ativo;
        this.conta_bancaria = conta_bancaria;
    }

    public String getCodigo_pagamento() {
        return codigo_pagamento;
    }

    public void setCodigo_pagamento(String codigo_pagamento) {
        this.codigo_pagamento = codigo_pagamento;
    }

    public String getDescricao_forma_pagamento() {
        return descricao_forma_pagamento;
    }

    public void setDescricao_forma_pagamento(String descricao_forma_pagamento) {
        this.descricao_forma_pagamento = descricao_forma_pagamento;
    }

    public String getTipo_forma_pagamento() {
        return tipo_forma_pagamento;
    }

    public void setTipo_forma_pagamento(String tipo_forma_pagamento) {
        this.tipo_forma_pagamento = tipo_forma_pagamento;
    }

    public String getAuto_num_pagamento() {
        return auto_num_pagamento;
    }

    public void setAuto_num_pagamento(String auto_num_pagamento) {
        this.auto_num_pagamento = auto_num_pagamento;
    }

    public String getBaixa_forma_pagamento() {
        return baixa_forma_pagamento;
    }

    public void setBaixa_forma_pagamento(String baixa_forma_pagamento) {
        this.baixa_forma_pagamento = baixa_forma_pagamento;
    }

    public String getUsuario_atual() {
        return usuario_atual;
    }

    public void setUsuario_atual(String usuario_atual) {
        this.usuario_atual = usuario_atual;
    }

    public String getData_cadastro() {
        return data_cadastro;
    }

    public void setData_cadastro(String data_cadastro) {
        this.data_cadastro = data_cadastro;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getConta_bancaria() {
        return conta_bancaria;
    }

    public void setConta_bancaria(String conta_bancaria) {
        this.conta_bancaria = conta_bancaria;
    }
}
