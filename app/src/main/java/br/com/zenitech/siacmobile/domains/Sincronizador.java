package br.com.zenitech.siacmobile.domains;

import java.util.List;

public class Sincronizador {

    public List<BaixarDadosDomain> retorno_cartoes_aliquotas;
    public List<Clientes> retorno_clientes;
    public List<BaixarDadosDomain> retorno_configuracoes;
    public List<BaixarDadosDomain> retorno_contas_bancarias;
    public List<BaixarDadosDomain> retorno_financeiro_receber;
    public List<BaixarDadosDomain> retorno_formas_pagamento;
    public List<BaixarDadosDomain> retorno_formas_pagamento_cliente;
    public List<BaixarDadosDomain> retorno_margens_clientes;
    public List<BaixarDadosDomain> retorno_pos;
    public List<BaixarDadosDomain> retorno_produtos;
    public List<BaixarDadosDomain> retorno_rotas_clientes;
    public List<BaixarDadosDomain> retorno_rotas_precos;
    public List<BaixarDadosDomain> retorno_unidades;
    public List<BaixarDadosDomain> retorno_unidades_precos;
    public List<BaixarDadosDomain> retorno_vale;


    private String serial;
    private String verificar_posicao_cliente;
    private String erro;
    private String print_promissoria;
    private String print_boleto;
    private String mostrar_contas_receber;
    private String codigo_instalacao;
    private String baixar_vale;

    public Sincronizador(String serial, String verificar_posicao_cliente, String erro, String print_promissoria, String print_boleto, String mostrar_contas_receber, String codigo_instalacao, String baixar_vale) {
        this.serial = serial;
        this.verificar_posicao_cliente = verificar_posicao_cliente;
        this.erro = erro;
        this.print_promissoria = print_promissoria;
        this.print_boleto = print_boleto;
        this.mostrar_contas_receber = mostrar_contas_receber;
        this.codigo_instalacao = codigo_instalacao;
        this.baixar_vale = baixar_vale;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getVerificar_posicao_cliente() {
        return verificar_posicao_cliente;
    }

    public void setVerificar_posicao_cliente(String verificar_posicao_cliente) {
        this.verificar_posicao_cliente = verificar_posicao_cliente;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getPrint_promissoria() {
        return print_promissoria;
    }

    public void setPrint_promissoria(String print_promissoria) {
        this.print_promissoria = print_promissoria;
    }

    public String getPrint_boleto() {
        return print_boleto;
    }

    public void setPrint_boleto(String print_boleto) {
        this.print_boleto = print_boleto;
    }

    public String getMostrar_contas_receber() {
        return mostrar_contas_receber;
    }

    public void setMostrar_contas_receber(String mostrar_contas_receber) {
        this.mostrar_contas_receber = mostrar_contas_receber;
    }

    public String getCodigo_instalacao() {
        return codigo_instalacao;
    }

    public void setCodigo_instalacao(String codigo_instalacao) {
        this.codigo_instalacao = codigo_instalacao;
    }

    public String getBaixar_vale() {
        return baixar_vale;
    }

    public void setBaixar_vale(String baixar_vale) {
        this.baixar_vale = baixar_vale;
    }
}
