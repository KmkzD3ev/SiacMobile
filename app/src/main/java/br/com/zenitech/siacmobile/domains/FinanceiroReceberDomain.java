package br.com.zenitech.siacmobile.domains;


public class FinanceiroReceberDomain {
    private String codigo_financeiro;
    private String unidade_financeiro;
    private String data_financeiro;
    private String codigo_cliente_financeiro;
    private String fpagamento_financeiro;
    private String documento_financeiro;
    private String vencimento_financeiro;
    private String valor_financeiro;
    private String status_autorizacao;
    private String pago;
    private String vasilhame_ref;
    private String usuario_atual;
    private String data_inclusao;
    private String nosso_numero_financeiro;
    private String id_vendedor_financeiro;
    private String id_financeiro_app;

    public FinanceiroReceberDomain(String codigo_financeiro, String unidade_financeiro, String data_financeiro, String codigo_cliente_financeiro, String fpagamento_financeiro, String documento_financeiro, String vencimento_financeiro, String valor_financeiro, String status_autorizacao, String pago, String vasilhame_ref, String usuario_atual, String data_inclusao, String nosso_numero_financeiro, String id_vendedor_financeiro, String id_financeiro_app) {
        this.codigo_financeiro = codigo_financeiro;
        this.unidade_financeiro = unidade_financeiro;
        this.data_financeiro = data_financeiro;
        this.codigo_cliente_financeiro = codigo_cliente_financeiro;
        this.fpagamento_financeiro = fpagamento_financeiro;
        this.documento_financeiro = documento_financeiro;
        this.vencimento_financeiro = vencimento_financeiro;
        this.valor_financeiro = valor_financeiro;
        this.status_autorizacao = status_autorizacao;
        this.pago = pago;
        this.vasilhame_ref = vasilhame_ref;
        this.usuario_atual = usuario_atual;
        this.data_inclusao = data_inclusao;
        this.nosso_numero_financeiro = nosso_numero_financeiro;
        this.id_vendedor_financeiro = id_vendedor_financeiro;
        this.id_financeiro_app = id_financeiro_app;
    }

    public String getCodigo_financeiro() {
        return codigo_financeiro;
    }

    public void setCodigo_financeiro(String codigo_financeiro) {
        this.codigo_financeiro = codigo_financeiro;
    }

    public String getUnidade_financeiro() {
        return unidade_financeiro;
    }

    public void setUnidade_financeiro(String unidade_financeiro) {
        this.unidade_financeiro = unidade_financeiro;
    }

    public String getData_financeiro() {
        return data_financeiro;
    }

    public void setData_financeiro(String data_financeiro) {
        this.data_financeiro = data_financeiro;
    }

    public String getCodigo_cliente_financeiro() {
        return codigo_cliente_financeiro;
    }

    public void setCodigo_cliente_financeiro(String codigo_cliente_financeiro) {
        this.codigo_cliente_financeiro = codigo_cliente_financeiro;
    }

    public String getFpagamento_financeiro() {
        return fpagamento_financeiro;
    }

    public void setFpagamento_financeiro(String fpagamento_financeiro) {
        this.fpagamento_financeiro = fpagamento_financeiro;
    }

    public String getDocumento_financeiro() {
        return documento_financeiro;
    }

    public void setDocumento_financeiro(String documento_financeiro) {
        this.documento_financeiro = documento_financeiro;
    }

    public String getVencimento_financeiro() {
        return vencimento_financeiro;
    }

    public void setVencimento_financeiro(String vencimento_financeiro) {
        this.vencimento_financeiro = vencimento_financeiro;
    }

    public String getValor_financeiro() {
        return valor_financeiro;
    }

    public void setValor_financeiro(String valor_financeiro) {
        this.valor_financeiro = valor_financeiro;
    }

    public String getStatus_autorizacao() {
        return status_autorizacao;
    }

    public void setStatus_autorizacao(String status_autorizacao) {
        this.status_autorizacao = status_autorizacao;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }

    public String getVasilhame_ref() {
        return vasilhame_ref;
    }

    public void setVasilhame_ref(String vasilhame_ref) {
        this.vasilhame_ref = vasilhame_ref;
    }

    public String getUsuario_atual() {
        return usuario_atual;
    }

    public void setUsuario_atual(String usuario_atual) {
        this.usuario_atual = usuario_atual;
    }

    public String getData_inclusao() {
        return data_inclusao;
    }

    public void setData_inclusao(String data_inclusao) {
        this.data_inclusao = data_inclusao;
    }

    public String getNosso_numero_financeiro() {
        return nosso_numero_financeiro;
    }

    public void setNosso_numero_financeiro(String nosso_numero_financeiro) {
        this.nosso_numero_financeiro = nosso_numero_financeiro;
    }

    public String getId_vendedor_financeiro() {
        return id_vendedor_financeiro;
    }

    public void setId_vendedor_financeiro(String id_vendedor_financeiro) {
        this.id_vendedor_financeiro = id_vendedor_financeiro;
    }

    public String getId_financeiro_app() {
        return id_financeiro_app;
    }

    public void setId_financeiro_app(String id_financeiro_app) {
        this.id_financeiro_app = id_financeiro_app;
    }
}
