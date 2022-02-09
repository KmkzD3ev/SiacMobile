package br.com.zenitech.siacmobile.domains;

public class ValesDomain {
    private String codigo_vale;
    private String unidade_vale;
    private String codigo_cliente_vale;
    private String numero_vale;
    private String valor_vale;
    private String situacao_vale;
    private String produto_vale;

    public ValesDomain(String codigo_vale, String unidade_vale, String codigo_cliente_vale, String numero_vale, String valor_vale, String situacao_vale, String produto_vale) {
        this.codigo_vale = codigo_vale;
        this.unidade_vale = unidade_vale;
        this.codigo_cliente_vale = codigo_cliente_vale;
        this.numero_vale = numero_vale;
        this.valor_vale = valor_vale;
        this.situacao_vale = situacao_vale;
        this.produto_vale = produto_vale;
    }

    public String getCodigo_vale() {
        return codigo_vale;
    }

    public void setCodigo_vale(String codigo_vale) {
        this.codigo_vale = codigo_vale;
    }

    public String getUnidade_vale() {
        return unidade_vale;
    }

    public void setUnidade_vale(String unidade_vale) {
        this.unidade_vale = unidade_vale;
    }

    public String getCodigo_cliente_vale() {
        return codigo_cliente_vale;
    }

    public void setCodigo_cliente_vale(String codigo_cliente_vale) {
        this.codigo_cliente_vale = codigo_cliente_vale;
    }

    public String getNumero_vale() {
        return numero_vale;
    }

    public void setNumero_vale(String numero_vale) {
        this.numero_vale = numero_vale;
    }

    public String getValor_vale() {
        return valor_vale;
    }

    public void setValor_vale(String valor_vale) {
        this.valor_vale = valor_vale;
    }

    public String getSituacao_vale() {
        return situacao_vale;
    }

    public void setSituacao_vale(String situacao_vale) {
        this.situacao_vale = situacao_vale;
    }

    public String getProduto_vale() {
        return produto_vale;
    }

    public void setProduto_vale(String produto_vale) {
        this.produto_vale = produto_vale;
    }
}
