package br.com.zenitech.siacmobile;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

class GPStracker {
    //
    SharedPreferences prefs;
    Context context;
    private static String TAG = "GPStracker";

    private double lat = 0;
    private double lon = 0;

    private float precisao = 0;

    GPStracker(Context c) {
        context = c;
    }

    String getLocation() {

        try {

            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        precisao = location.getVerticalAccuracyMeters();
                    }

                    Log.d(TAG, location.getLatitude() + "," + location.getLongitude());

                    // SALVA A POSIÇÃO
                    //_salvarPosicao();
                    temporizador();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                    Log.d(TAG, "onStatusChanged: " + s);

                }

                @Override
                public void onProviderEnabled(String s) {

                    Log.d(TAG, "onProviderEnabled: " + s);

                }

                @Override
                public void onProviderDisabled(String s) {

                    Log.d(TAG, "onProviderDisabled: " + s);

                }
            };

            //LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            //boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (this.isGPSEnabled()) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    // int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return "";
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, locationListener);
                //locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 3000, 1, locationListener);
            }

            return lat + "," + lon;

        } catch (Exception ignored) {

        }

        return "";
    }

    String getLatLon() {
        return lat + "," + lon;
    }

    String getPrecisao(){
        return String.valueOf(precisao);
    }

    // RETORNA SE O GPS ESTÁ ATIVO OU INATIVO
    boolean isGPSEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /*private void _salvarPosicao() {
        //
        prefs = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        // VERIFICA SE TEM INTERNET
        if (new VerificarOnline().isOnline(context)) {
            try {
                //
                final IPosicoes iPosicoes = IPosicoes.retrofit.create(IPosicoes.class);
                final Call<PosicoesDomains> call = iPosicoes.Posicoes(
                        prefs.getString("id_empresa", ""),
                        prefs.getString("telefone", ""),
                        "salvar",
                        lat,
                        lon
                );
                call.enqueue(new Callback<PosicoesDomains>() {
                    @Override
                    public void onResponse(@NonNull Call<PosicoesDomains> call, @NonNull Response<PosicoesDomains> response) {
                        if (response.isSuccessful()) {
                            PosicoesDomains dados = response.body();
                            if (dados != null) {
                                if (!dados.getStatus().equalsIgnoreCase("erro")) {
                                    //Toast.makeText(context, dados.getStatus(), Toast.LENGTH_SHORT).show();
                                    //VERIFICA SE A ENTREGA JÁ FOI GRAVADA NO BANCO DE DADOS
                                    Log.i(TAG, dados.getStatus());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PosicoesDomains> call, @NonNull Throwable t) {
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }*/

    private boolean tempo = true;

    private void temporizador() {
        if (tempo) {

            tempo = false;
            try {
                Log.d(TAG, "Chegou aqui!");

                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    //VERIFICA SE O APARELHO ESTÁ CONECTADO A INTERNET
                    //_salvarPosicao();

                    //CHAMA O TEMPORIZADOR NOVAMENTE
                    //temporizador(60000);

                    tempo = true;
                }, 60000);

            } catch (Exception ignored) {

            }
        }
    }
}
