package br.com.zenitech.siacmobile.ui.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import br.com.zenitech.siacmobile.ClassAuxiliar;
import br.com.zenitech.siacmobile.DatabaseHelper;
import br.com.zenitech.siacmobile.EnviarDadosServidor;
import br.com.zenitech.siacmobile.R;
import br.com.zenitech.siacmobile.Sincronizar;
import br.com.zenitech.siacmobile.domains.EnviarDados;
import br.com.zenitech.siacmobile.ftps.MyFTPClientFunctions;
import br.com.zenitech.siacmobile.interfaces.IEnviarDados;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {
    private Context context = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(
                R.layout.fragment_notifications, container, false);
        setHasOptionsMenu(true);
        context = this.getContext();

        view.findViewById(R.id.cv_enviar_dados).setOnClickListener(v -> enviarDados());

        return view;
    }

    private void enviarDados() {
        startActivity(new Intent(context, EnviarDadosServidor.class));
    }
}