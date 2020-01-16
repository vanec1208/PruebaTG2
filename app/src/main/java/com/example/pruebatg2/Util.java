package com.example.pruebatg2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rw.loadingdialog.LoadingView;

import java.text.DecimalFormat;

public class Util {

    public static LoadingView getLoadingView(Context context) {
        LoadingView loadingView = new LoadingView.Builder(context)
                .setProgressColorResource(R.color.colorPrimaryDark)
                .setBackgroundColorRes(R.color.colorTransparent)
                .setProgressStyle(LoadingView.ProgressStyle.CYCLIC)
                .setCustomMargins(0, 0, 0, 0)
                .attachTo((Activity) context);

        return loadingView;
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager conectividad = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectividad != null) {
            NetworkInfo networkInfo = conectividad.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.isConnected()) return true;
            }
        }

        return false;
    }

    public static String getKtoC(Double k){
        DecimalFormat formatter = new DecimalFormat("#.0#");
        return k == 0 ? "0.0" : formatter.format(k - 273.15);
    }
}
