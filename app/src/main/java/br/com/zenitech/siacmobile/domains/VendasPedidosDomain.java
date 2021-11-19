package br.com.zenitech.siacmobile.domains;

public class VendasPedidosDomain {

    private String codigo_venda;
    private String codigo_cliente;
    private String unidade_venda;
    private String produto_venda;
    private String data_movimento;
    private String quantidade_venda;
    private String preco_unitario;
    private String valor_total;
    private String vendedor_venda;
    private String status_autorizacao_venda;
    private String entrega_futura_venda;
    private String entrega_futura_realizada;
    private String usuario_atual;
    private String data_cadastro;
    private String codigo_venda_app;
    private String venda_finalizada_app;
    private String chave_importacao;
    private String formas_pagamento;

    public VendasPedidosDomain(String codigo_venda, String codigo_cliente, String unidade_venda, String produto_venda, String data_movimento, String quantidade_venda, String preco_unitario, String valor_total, String vendedor_venda, String status_autorizacao_venda, String entrega_futura_venda, String entrega_futura_realizada, String usuario_atual, String data_cadastro, String codigo_venda_app, String venda_finalizada_app, String chave_importacao, String formas_pagamento) {
        this.codigo_venda = codigo_venda;
        this.codigo_cliente = codigo_cliente;
        this.unidade_venda = unidade_venda;
        this.produto_venda = produto_venda;
        this.data_movimento = data_movimento;
        this.quantidade_venda = quantidade_venda;
        this.preco_unitario = preco_unitario;
        this.valor_total = valor_total;
        this.vendedor_venda = vendedor_venda;
        this.status_autorizacao_venda = status_autorizacao_venda;
        this.entrega_futura_venda = entrega_futura_venda;
        this.entrega_futura_realizada = entrega_futura_realizada;
        this.usuario_atual = usuario_atual;
        this.data_cadastro = data_cadastro;
        this.codigo_venda_app = codigo_venda_app;
        this.venda_finalizada_app = venda_finalizada_app;
        this.chave_importacao = chave_importacao;
        this.formas_pagamento = formas_pagamento;
    }

    public String getCodigo_venda() {
        return codigo_venda;
    }

    public void setCodigo_venda(String codigo_venda) {
        this.codigo_venda = codigo_venda;
    }

    public String getCodigo_cliente() {
        return codigo_cliente;
    }

    public void setCodigo_cliente(String codigo_cliente) {
        this.codigo_cliente = codigo_cliente;
    }

    public String getUnidade_venda() {
        return unidade_venda;
    }

    public void setUnidade_venda(String unidade_venda) {
        this.unidade_venda = unidade_venda;
    }

    public String getProduto_venda() {
        return produto_venda;
    }

    public void setProduto_venda(String produto_venda) {
        this.produto_venda = produto_venda;
    }

    public String getData_movimento() {
        return data_movimento;
    }

    public void setData_movimento(String data_movimento) {
        this.data_movimento = data_movimento;
    }

    public String getQuantidade_venda() {
        return quantidade_venda;
    }

    public void setQuantidade_venda(String quantidade_venda) {
        this.quantidade_venda = quantidade_venda;
    }

    public String getPreco_unitario() {
        return preco_unitario;
    }

    public void setPreco_unitario(String preco_unitario) {
        this.preco_unitario = preco_unitario;
    }

    public String getValor_total() {
        return valor_total;
    }

    public void setValor_total(String valor_total) {
        this.valor_total = valor_total;
    }

    public String getVendedor_venda() {
        return vendedor_venda;
    }

    public void setVendedor_venda(String vendedor_venda) {
        this.vendedor_venda = vendedor_venda;
    }

    public String getStatus_autorizacao_venda() {
        return status_autorizacao_venda;
    }

    public void setStatus_autorizacao_venda(String status_autorizacao_venda) {
        this.status_autorizacao_venda = status_autorizacao_venda;
    }

    public String getEntrega_futura_venda() {
        return entrega_futura_venda;
    }

    public void setEntrega_futura_venda(String entrega_futura_venda) {
        this.entrega_futura_venda = entrega_futura_venda;
    }

    public String getEntrega_futura_realizada() {
        return entrega_futura_realizada;
    }

    public void setEntrega_futura_realizada(String entrega_futura_realizada) {
        this.entrega_futura_realizada = entrega_futura_realizada;
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

    public String getCodigo_venda_app() {
        return codigo_venda_app;
    }

    public void setCodigo_venda_app(String codigo_venda_app) {
        this.codigo_venda_app = codigo_venda_app;
    }

    public String getVenda_finalizada_app() {
        return venda_finalizada_app;
    }

    public void setVenda_finalizada_app(String venda_finalizada_app) {
        this.venda_finalizada_app = venda_finalizada_app;
    }

    public String getChave_importacao() {
        return chave_importacao;
    }

    public void setChave_importacao(String chave_importacao) {
        this.chave_importacao = chave_importacao;
    }

    public String getFormas_pagamento() {
        return formas_pagamento;
    }

    public void setFormas_pagamento(String formas_pagamento) {
        this.formas_pagamento = formas_pagamento;
    }
}
