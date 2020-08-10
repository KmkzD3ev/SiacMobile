package br.com.zenitech.siacmobile.domains;

import java.io.Serializable;

public class Produtos implements Serializable {

    private String codigo_produto;
    private String descricao_produto;

    public Produtos(String codigo_produto, String descricao_produto) {
        this.codigo_produto = codigo_produto;
        this.descricao_produto = descricao_produto;
    }

    public String getCodigo_produto() {
        return codigo_produto;
    }

    public void setCodigo_produto(String codigo_produto) {
        this.codigo_produto = codigo_produto;
    }

    public String getDescricao_produto() {
        return descricao_produto;
    }

    public void setDescricao_produto(String descricao_produto) {
        this.descricao_produto = descricao_produto;
    }
}
