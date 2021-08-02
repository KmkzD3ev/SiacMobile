package br.com.zenitech.siacmobile.domains;

public class ContasBancarias {
    private String codigo;
    private String banco_conta;
    private String agencia;
    private String conta;
    private String dv_conta;
    private String convenio;
    private String contrato;
    private String carteira;
    private String variacao;
    private String conta_cedente;
    private String dv_conta_cedente;
    private String cedente;
    private String cpf_cnpj;
    private String endereco;
    private String cidade_uf;
    private String instrucoes;
    private String inicio_nosso_numero;
    private String dv_agencia;
    private String taxa_boleto;

    public ContasBancarias(String codigo, String banco_conta, String agencia, String conta, String dv_conta, String convenio, String contrato, String carteira, String variacao, String conta_cedente, String dv_conta_cedente, String cedente, String cpf_cnpj, String endereco, String cidade_uf, String instrucoes, String inicio_nosso_numero, String dv_agencia, String taxa_boleto) {
        this.codigo = codigo;
        this.banco_conta = banco_conta;
        this.agencia = agencia;
        this.conta = conta;
        this.dv_conta = dv_conta;
        this.convenio = convenio;
        this.contrato = contrato;
        this.carteira = carteira;
        this.variacao = variacao;
        this.conta_cedente = conta_cedente;
        this.dv_conta_cedente = dv_conta_cedente;
        this.cedente = cedente;
        this.cpf_cnpj = cpf_cnpj;
        this.endereco = endereco;
        this.cidade_uf = cidade_uf;
        this.instrucoes = instrucoes;
        this.inicio_nosso_numero = inicio_nosso_numero;
        this.dv_agencia = dv_agencia;
        this.taxa_boleto = taxa_boleto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getBanco_conta() {
        return banco_conta;
    }

    public void setBanco_conta(String banco_conta) {
        this.banco_conta = banco_conta;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDv_conta() {
        return dv_conta;
    }

    public void setDv_conta(String dv_conta) {
        this.dv_conta = dv_conta;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getCarteira() {
        return carteira;
    }

    public void setCarteira(String carteira) {
        this.carteira = carteira;
    }

    public String getVariacao() {
        return variacao;
    }

    public void setVariacao(String variacao) {
        this.variacao = variacao;
    }

    public String getConta_cedente() {
        return conta_cedente;
    }

    public void setConta_cedente(String conta_cedente) {
        this.conta_cedente = conta_cedente;
    }

    public String getDv_conta_cedente() {
        return dv_conta_cedente;
    }

    public void setDv_conta_cedente(String dv_conta_cedente) {
        this.dv_conta_cedente = dv_conta_cedente;
    }

    public String getCedente() {
        return cedente;
    }

    public void setCedente(String cedente) {
        this.cedente = cedente;
    }

    public String getCpf_cnpj() {
        return cpf_cnpj;
    }

    public void setCpf_cnpj(String cpf_cnpj) {
        this.cpf_cnpj = cpf_cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade_uf() {
        return cidade_uf;
    }

    public void setCidade_uf(String cidade_uf) {
        this.cidade_uf = cidade_uf;
    }

    public String getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(String instrucoes) {
        this.instrucoes = instrucoes;
    }

    public String getInicio_nosso_numero() {
        return inicio_nosso_numero;
    }

    public void setInicio_nosso_numero(String inicio_nosso_numero) {
        this.inicio_nosso_numero = inicio_nosso_numero;
    }

    public String getDv_agencia() {
        return dv_agencia;
    }

    public void setDv_agencia(String dv_agencia) {
        this.dv_agencia = dv_agencia;
    }

    public String getTaxa_boleto() {
        return taxa_boleto;
    }

    public void setTaxa_boleto(String taxa_boleto) {
        this.taxa_boleto = taxa_boleto;
    }
}
