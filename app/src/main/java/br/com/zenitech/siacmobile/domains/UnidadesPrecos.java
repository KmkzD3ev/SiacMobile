package br.com.zenitech.siacmobile.domains;

public class UnidadesPrecos {

    private String codigo_unidade_preco;
    private String unidade_preco;
    private String produto_preco;
    private String preco_unidade;

    public UnidadesPrecos(String codigo_unidade_preco, String unidade_preco, String produto_preco, String preco_unidade) {
        this.codigo_unidade_preco = codigo_unidade_preco;
        this.unidade_preco = unidade_preco;
        this.produto_preco = produto_preco;
        this.preco_unidade = preco_unidade;
    }

    public String getCodigo_unidade_preco() {
        return codigo_unidade_preco;
    }

    public void setCodigo_unidade_preco(String codigo_unidade_preco) {
        this.codigo_unidade_preco = codigo_unidade_preco;
    }

    public String getUnidade_preco() {
        return unidade_preco;
    }

    public void setUnidade_preco(String unidade_preco) {
        this.unidade_preco = unidade_preco;
    }

    public String getProduto_preco() {
        return produto_preco;
    }

    public void setProduto_preco(String produto_preco) {
        this.produto_preco = produto_preco;
    }

    public String getPreco_unidade() {
        return preco_unidade;
    }

    public void setPreco_unidade(String preco_unidade) {
        this.preco_unidade = preco_unidade;
    }
}
