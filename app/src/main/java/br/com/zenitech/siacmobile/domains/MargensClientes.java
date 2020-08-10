package br.com.zenitech.siacmobile.domains;

public class MargensClientes {

    private String codigo_margem_cliente;
    private String unidade_margem_cliente;
    private String codigo_cliente_margem_cliente;
    private String produto_margem_cliente;
    private String margem_cliente;
    private String margem_vale_produto;
    private String taxa_entrega;

    public MargensClientes(String codigo_margem_cliente, String unidade_margem_cliente, String codigo_cliente_margem_cliente, String produto_margem_cliente, String margem_cliente, String margem_vale_produto, String taxa_entrega) {
        this.codigo_margem_cliente = codigo_margem_cliente;
        this.unidade_margem_cliente = unidade_margem_cliente;
        this.codigo_cliente_margem_cliente = codigo_cliente_margem_cliente;
        this.produto_margem_cliente = produto_margem_cliente;
        this.margem_cliente = margem_cliente;
        this.margem_vale_produto = margem_vale_produto;
        this.taxa_entrega = taxa_entrega;
    }

    public String getCodigo_margem_cliente() {
        return codigo_margem_cliente;
    }

    public void setCodigo_margem_cliente(String codigo_margem_cliente) {
        this.codigo_margem_cliente = codigo_margem_cliente;
    }

    public String getUnidade_margem_cliente() {
        return unidade_margem_cliente;
    }

    public void setUnidade_margem_cliente(String unidade_margem_cliente) {
        this.unidade_margem_cliente = unidade_margem_cliente;
    }

    public String getCodigo_cliente_margem_cliente() {
        return codigo_cliente_margem_cliente;
    }

    public void setCodigo_cliente_margem_cliente(String codigo_cliente_margem_cliente) {
        this.codigo_cliente_margem_cliente = codigo_cliente_margem_cliente;
    }

    public String getProduto_margem_cliente() {
        return produto_margem_cliente;
    }

    public void setProduto_margem_cliente(String produto_margem_cliente) {
        this.produto_margem_cliente = produto_margem_cliente;
    }

    public String getMargem_cliente() {
        return margem_cliente;
    }

    public void setMargem_cliente(String margem_cliente) {
        this.margem_cliente = margem_cliente;
    }

    public String getMargem_vale_produto() {
        return margem_vale_produto;
    }

    public void setMargem_vale_produto(String margem_vale_produto) {
        this.margem_vale_produto = margem_vale_produto;
    }

    public String getTaxa_entrega() {
        return taxa_entrega;
    }

    public void setTaxa_entrega(String taxa_entrega) {
        this.taxa_entrega = taxa_entrega;
    }
}
