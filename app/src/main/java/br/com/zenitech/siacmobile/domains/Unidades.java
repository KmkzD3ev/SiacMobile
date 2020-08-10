package br.com.zenitech.siacmobile.domains;

public class Unidades {
    private String id_unidade;
    private String descricao_unidade;

    public Unidades(String id_unidade, String descricao_unidade) {
        this.id_unidade = id_unidade;
        this.descricao_unidade = descricao_unidade;
    }

    public String getId_unidade() {
        return id_unidade;
    }

    public void setId_unidade(String id_unidade) {
        this.id_unidade = id_unidade;
    }

    public String getDescricao_unidade() {
        return descricao_unidade;
    }

    public void setDescricao_unidade(String descricao_unidade) {
        this.descricao_unidade = descricao_unidade;
    }
}
