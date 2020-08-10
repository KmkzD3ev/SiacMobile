package br.com.zenitech.siacmobile.domains;

public class EnviarDados {

    private String nVenda;
    private String nVendaSiac;

    public EnviarDados(String nVenda, String nVendaSiac) {
        this.nVenda = nVenda;
        this.nVendaSiac = nVendaSiac;
    }

    public String getnVenda() {
        return nVenda;
    }

    public void setnVenda(String nVenda) {
        this.nVenda = nVenda;
    }

    public String getnVendaSiac() {
        return nVendaSiac;
    }

    public void setnVendaSiac(String nVendaSiac) {
        this.nVendaSiac = nVendaSiac;
    }
}
