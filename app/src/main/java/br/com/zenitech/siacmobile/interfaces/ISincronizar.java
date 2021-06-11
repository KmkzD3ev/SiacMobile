package br.com.zenitech.siacmobile.interfaces;

import java.util.concurrent.TimeUnit;

import br.com.zenitech.siacmobile.Configuracoes;
import br.com.zenitech.siacmobile.domains.Sincronizador;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ISincronizar {

    //SINCRONIZAR
    @FormUrlEncoded
    @POST("sincronizar_banco_app_siac.php")
    Call<Sincronizador> sincronizar(@Field("SERIAL") String SERIAL);

    //SINCRONIZAR
    @FormUrlEncoded
    @POST("ativar_desativa_pos_app.php")
    Call<Sincronizador> ativarDesativarPOS(@Field("opcao") String opcao, @Field("serial") String serial);

    //SINCRONIZAR
    @FormUrlEncoded
    @POST("ativar_desativa_pos_app.php")
    Call<Sincronizador> verificarSerial(@Field("opcao") String opcao, @Field("serial") String serial);

    //SINCRONIZAR
    @FormUrlEncoded
    @POST("ativar_desativa_pos_app.php")
    Call<Sincronizador> verificarVersaoApp(@Field("opcao") String opcao);

    //RESETAR APP
    @FormUrlEncoded
    @POST("ativar_desativa_pos_app.php")
    Call<Sincronizador> resetApp(@Field("opcao") String opcao, @Field("serial") String serial, @Field("codigo") String codigo);

    OkHttpClient okHttpClient = new OkHttpClient.Builder ()
            .connectTimeout (2, TimeUnit.MINUTES)
            .readTimeout (2, TimeUnit.MINUTES)
            .writeTimeout (2, TimeUnit.MINUTES)
            .build ();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(String.format("%s%s", new Configuracoes().GetUrlServer(), "/POSSIAC/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
