package br.com.zenitech.siacmobile.interfaces;

import br.com.zenitech.siacmobile.Configuracoes;
import br.com.zenitech.siacmobile.domains.Conta;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ILogin {

    //RETORNA AS INFORMAÇÕES DA VAGA
    //@GET("index/gerarbanco/{unidade}")
    //Call<Conta> getBancoOnline(@Path("unidade") String unidade);

    //LOGIN DA CONTA DO USUÁRIO
    @FormUrlEncoded
    @POST("index.php")
    Call<Conta> getBancoOnline(
            @Field("opcao") String opcao,
            @Field("unidade") String unidade
    );

    //LOGIN DA CONTA DO USUÁRIO
    @FormUrlEncoded
    @POST("autenticacao_app_siac.php")
    Call<Conta> login(
            @Field("usuario") String usuario,
            @Field("senha") String senha,
            @Field("conta") String conta,
            @Field("serial") String serial
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(String.format("%s%s", new Configuracoes().GetUrlServer(), "/POSSIAC/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
