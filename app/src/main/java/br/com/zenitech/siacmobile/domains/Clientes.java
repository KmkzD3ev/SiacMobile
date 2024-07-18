package br.com.zenitech.siacmobile.domains;

public class Clientes {
    public String sql_insert;
    private String codigo_cliente;
    private String nome_cliente;
    private String latitude_cliente;
    private String longitude_cliente;
    private String saldo;
    private String cpfcnpj;
    private String endereco;
    private String apelido_cliente;

    public Clientes(String codigo_cliente, String nome_cliente, String latitude_cliente, String longitude_cliente, String saldo, String cpfcnpj, String endereco, String apelido_cliente) {
        this.codigo_cliente = codigo_cliente;
        this.nome_cliente = nome_cliente;
        this.latitude_cliente = latitude_cliente;
        this.longitude_cliente = longitude_cliente;
        this.saldo = saldo;
        this.cpfcnpj = cpfcnpj;
        this.endereco = endereco;
        this.apelido_cliente = apelido_cliente;
    }

    public String getCodigo_cliente() {
        return codigo_cliente;
    }

    public void setCodigo_cliente(String codigo_cliente) {
        this.codigo_cliente = codigo_cliente;
    }

    public String getNome_cliente() {
        return nome_cliente;
    }

    public void setNome_cliente(String nome_cliente) {
        this.nome_cliente = nome_cliente;
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

    public String getApelido_cliente() {
        return apelido_cliente;
    }

    public void setApelido_cliente(String apelido_cliente) {
        this.apelido_cliente = apelido_cliente;
    }
}
