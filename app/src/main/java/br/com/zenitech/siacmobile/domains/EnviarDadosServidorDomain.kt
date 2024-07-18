package br.com.zenitech.siacmobile.domains

import br.com.zenitech.siacmobile.FinanceiroDaVenda

class EnviarDadosServidorDomain {

    // VENDAS
    var vendas: List<VendasPedidosDomain>? = null
    var financeiro: List<FinanceiroDaVenda>? = null

    // CONTAS RECEBER
    var contas_receber: List<FinanceiroReceberClientes>? = null

    // VALES
    var vales : List<ValesDomain>? = null
}