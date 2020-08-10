package br.com.zenitech.siacmobile.domains;

public class Conta {

    private String codigo_vendedor;
    private String nome_vendedor;
    private String unidade_vendedor;
    private String usuario_atual;
    private String usuario_vendedor;
    private String senha_vendedor;
    private String erro;

    public Conta(String codigo_vendedor, String nome_vendedor, String unidade_vendedor, String usuario_atual, String usuario_vendedor, String senha_vendedor, String erro) {
        this.codigo_vendedor = codigo_vendedor;
        this.nome_vendedor = nome_vendedor;
        this.unidade_vendedor = unidade_vendedor;
        this.usuario_atual = usuario_atual;
        this.usuario_vendedor = usuario_vendedor;
        this.senha_vendedor = senha_vendedor;
        this.erro = erro;
    }

    public String getCodigo_vendedor() {
        return codigo_vendedor;
    }

    public String getNome_vendedor() {
        return nome_vendedor;
    }

    public String getUnidade_vendedor() {
        return unidade_vendedor;
    }

    public String getUsuario_atual() {
        return usuario_atual;
    }

    public String getUsuario_vendedor() {
        return usuario_vendedor;
    }

    public String getSenha_vendedor() {
        return senha_vendedor;
    }

    public String getErro() {
        return erro;
    }
}
