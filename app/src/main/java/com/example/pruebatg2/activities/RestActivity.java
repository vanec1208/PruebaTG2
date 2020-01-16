package com.example.pruebatg2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebatg2.R;
import com.example.pruebatg2.Util;
import com.rw.loadingdialog.LoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class RestActivity extends AppCompatActivity {

    private Context context;

    private TextView lblTemp;
    private TextView lblFeelsLike;
    private TextView lblTempMin;
    private TextView lblTempMax;
    private TextView lblHumidity;
    private Button btnRest;

    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);

        context = this;

        lblTemp = findViewById(R.id.lblTemp);
        lblFeelsLike = findViewById(R.id.lblFeelsLike);
        lblTempMin = findViewById(R.id.lblTempMin);
        lblTempMax = findViewById(R.id.lblTempMax);
        lblHumidity = findViewById(R.id.lblHumidity);
        btnRest = findViewById(R.id.btnRest);

        loadingView = Util.getLoadingView(this);

        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingView.show();
                getInfo();
            }
        });
    }

    public void getInfo(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                if(!Util.hasInternet(context)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingView.hide();
                                            Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    throw new IOException(getString(R.string.txt_timeout));
                                }

                                try {
                                    Request original = chain.request();
                                    Response response = chain.proceed(original);
                                    return response;
                                } catch (SocketTimeoutException e) {
                                    throw new SocketTimeoutException(getString(R.string.txt_timeout));
                                }
                            }
                        })
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.openweathermap.org/data/2.5/weather?q=Barranquilla&appid=cfddf71b4fc02db57d3ef6c5e7b838b7")
                        .get()
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String strResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(strResponse);
                    int code = jsonObject.getInt("cod");

                    if(code == 200) {
                        JSONObject jsonMain = jsonObject.getJSONObject("main");
                        DecimalFormat formatter = new DecimalFormat("#.0#");

                        final String strTemp = Util.getKtoC(jsonMain.getDouble("temp"));
                        final String strFeelsLike = Util.getKtoC(jsonMain.getDouble("feels_like"));
                        final String strTempMin = Util.getKtoC(jsonMain.getDouble("temp_min"));
                        final String strTempMax = Util.getKtoC(jsonMain.getDouble("temp_max"));
                        final String strHumidity = formatter.format(jsonMain.getDouble("humidity"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lblTemp.setText(strTemp + " " + getString(R.string.txt_C));
                                lblFeelsLike.setText(strFeelsLike + " " + getString(R.string.txt_C));
                                lblTempMin.setText(strTempMin + " " + getString(R.string.txt_C));
                                lblTempMax.setText(strTempMax + " " + getString(R.string.txt_C));
                                lblHumidity.setText(strHumidity + " %");
                                loadingView.hide();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RestActivity.this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
                                loadingView.hide();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
