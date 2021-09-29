package br.com.zenitech.siacmobile.domains;

public class Sincronizador {
    private String serial;
    private String verificar_posicao_cliente;
    private String erro;
    private String print_promissoria;
    private String print_boleto;
    private String mostrar_contas_receber;
    private String codigo_instalacao;

    public Sincronizador(String serial, String verificar_posicao_cliente, String erro, String print_promissoria, String print_boleto, String mostrar_contas_receber, String codigo_instalacao) {
        this.serial = serial;
        this.verificar_posicao_cliente = verificar_posicao_cliente;
        this.erro = erro;
        this.print_promissoria = print_promissoria;
        this.print_boleto = print_boleto;
        this.mostrar_contas_receber = mostrar_contas_receber;
        this.codigo_instalacao = codigo_instalacao;
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
}
