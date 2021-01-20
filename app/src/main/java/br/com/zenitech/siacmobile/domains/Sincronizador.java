package br.com.zenitech.siacmobile.domains;

public class Sincronizador {
    private String serial;
    private String verificar_posicao_cliente;
    private String erro;

    public Sincronizador(String serial, String verificar_posicao_cliente, String erro) {
        this.serial = serial;
        this.verificar_posicao_cliente = verificar_posicao_cliente;
        this.erro = erro;
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
}
