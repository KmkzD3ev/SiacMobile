package br.com.zenitech.siacmobile.interfaces;

import java.util.ArrayList;

import br.com.zenitech.siacmobile.domains.Conta;
import br.com.zenitech.siacmobile.domains.EnviarDados;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IEnviarDados {

    //LOGIN DA CONTA DO USUÁRIO
    /*
        VENDAS.toString(),
                CLIENTES.toString(),
                PRODUTOS.toString(),
                QUANTIDADES.toString(),
                DATAS.toString(),
                VALORES.toString(),
                FINANCEIROS.toString(),
                FINVEN.toString(),
                VENCIMENTOS.toString(),
                VALORESFIN.toString(),
                FPAGAMENTOS.toString(),
                DOCUMENTOS.toString()
        * */
    @FormUrlEncoded
    @POST("index_app_siac.php")
    Call<ArrayList<EnviarDados>> enviarDados(
            @Field("TELA") String TELA,
            @Field("SERIAL") String SERIAL,
            @Field("VENDAS") String VENDAS,
            @Field("CLIENTES") String CLIENTES,
            @Field("PRODUTOS") String PRODUTOS,
            @Field("QUANTIDADES") String QUANTIDADES,
            @Field("DATAS") String DATAS,
            @Field("VALORES") String VALORES,
            @Field("FINANCEIROS") String FINANCEIROS,
            @Field("FINVEN") String FINVEN,
            @Field("VENCIMENTOS") String VENCIMENTOS,
            @Field("VALORESFIN") String VALORESFIN,
            @Field("FPAGAMENTOS") String FPAGAMENTOS,
            @Field("DOCUMENTOS") String DOCUMENTOS
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://emissorweb.com.br/POSSIACN/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
