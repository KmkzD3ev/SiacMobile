package br.com.zenitech.siacmobile.domains;

public class Clientes {
    private String codigo;
    private String nome;
    private String latitude_cliente;
    private String longitude_cliente;
    private String saldo;
    private String cpfcnpj;
    private String endereco;

    public Clientes(String codigo, String nome, String latitude_cliente, String longitude_cliente, String saldo, String cpfcnpj, String endereco) {
        this.codigo = codigo;
        this.nome = nome;
        this.latitude_cliente = latitude_cliente;
        this.longitude_cliente = longitude_cliente;
        this.saldo = saldo;
        this.cpfcnpj = cpfcnpj;
        this.endereco = endereco;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLatitude_cliente() {
        return latitude_cliente;
    }

    public void setLatitude_cliente(String latitude_cliente) {
        this.latitude_cliente = latitude_cliente;
    }

    public String getLongitude_cliente() {
        return longitude_cliente;
    }

    public void setLongitude_cliente(String longitude_cliente) {
        this.longitude_cliente = longitude_cliente;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getCpfcnpj() {
        return cpfcnpj;
    }

    public void setCpfcnpj(String cpfcnpj) {
        this.cpfcnpj = cpfcnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
