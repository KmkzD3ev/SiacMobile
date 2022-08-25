package br.com.zenitech.siacmobile.domains;

//percentual = total da nota

public class PosApp {
    private String codigo;
    private String serial;
    private String unidade;
    private String serie;
    private String ultnfce;
    private String ultboleto;
    private String nota_remessa;
    private String serie_remessa;
    private String limite_credito;
    private String ultpromissoria;
    private String autovencimento;
    private String modulo_pedidos;
    private String baixa_a_prazo;
    private String serie_boleto;
    private String escolher_cliente_vale;

    public PosApp(String codigo, String serial, String unidade, String serie, String ultnfce, String ultboleto, String nota_remessa, String serie_remessa, String limite_credito, String ultpromissoria, String autovencimento, String modulo_pedidos, String baixa_a_prazo, String serie_boleto, String escolher_cliente_vale) {
        this.codigo = codigo;
        this.serial = serial;
        this.unidade = unidade;
        this.serie = serie;
        this.ultnfce = ultnfce;
        this.ultboleto = ultboleto;
        this.nota_remessa = nota_remessa;
        this.serie_remessa = serie_remessa;
        this.limite_credito = limite_credito;
        this.ultpromissoria = ultpromissoria;
        this.autovencimento = autovencimento;
        this.modulo_pedidos = modulo_pedidos;
        this.baixa_a_prazo = baixa_a_prazo;
        this.serie_boleto = serie_boleto;
        this.escolher_cliente_vale = escolher_cliente_vale;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getUltnfce() {
        return ultnfce;
    }

    public void setUltnfce(String ultnfce) {
        this.ultnfce = ultnfce;
    }

    public String getUltboleto() {
        return ultboleto;
    }

    public void setUltboleto(String ultboleto) {
        this.ultboleto = ultboleto;
    }

    public String getNota_remessa() {
        return nota_remessa;
    }

    public void setNota_remessa(String nota_remessa) {
        this.nota_remessa = nota_remessa;
    }

    public String getSerie_remessa() {
        return serie_remessa;
    }

    public void setSerie_remessa(String serie_remessa) {
        this.serie_remessa = serie_remessa;
    }

    public String getLimite_credito() {
        return limite_credito;
    }

    public void setLimite_credito(String limite_credito) {
        this.limite_credito = limite_credito;
    }

    public String getUltpromissoria() {
        return ultpromissoria;
    }

    public void setUltpromissoria(String ultpromissoria) {
        this.ultpromissoria = ultpromissoria;
    }

    public String getAutovencimento() {
        return autovencimento;
    }

    public void setAutovencimento(String autovencimento) {
        this.autovencimento = autovencimento;
    }

    public String getModulo_pedidos() {
        return modulo_pedidos;
    }

    public void setModulo_pedidos(String modulo_pedidos) {
        this.modulo_pedidos = modulo_pedidos;
    }

    public String getBaixa_a_prazo() {
        return baixa_a_prazo;
    }

    public void setBaixa_a_prazo(String baixa_a_prazo) {
        this.baixa_a_prazo = baixa_a_prazo;
    }

    public String getSerie_boleto() {
        return serie_boleto;
    }

    public void setSerie_boleto(String serie_boleto) {
        this.serie_boleto = serie_boleto;
    }

    public String getEscolher_cliente_vale() {
        return escolher_cliente_vale;
    }

    public void setEscolher_cliente_vale(String escolher_cliente_vale) {
        this.escolher_cliente_vale = escolher_cliente_vale;
    }
}
