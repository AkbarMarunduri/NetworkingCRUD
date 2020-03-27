package com.akbarprojec.networkingcrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout srl_main;
    RecyclerView rv_main;
    ArrayList<String> arraNoinduk, arrayNama, arrayAlamat, arrayHobi;
    ProgressDialog progressDialog;

    RecylerViewAdapter recylerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        srl_main = findViewById(R.id.srl_main);
        rv_main = findViewById(R.id.rv_main);
        progressDialog = new ProgressDialog(this);

        rv_main.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_main.setLayoutManager(layoutManager);
        srl_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollRefresh();
                srl_main.setRefreshing(false);
            }
        });
        scrollRefresh();
    }

    public void scrollRefresh() {
        progressDialog.setMessage("Mengambil data ....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1200);
    }

    void initializerArray() {
        arraNoinduk = new ArrayList<>();
        arrayNama = new ArrayList<>();
        arrayAlamat = new ArrayList<>();
        arrayHobi = new ArrayList<>();

        arraNoinduk.clear();
        arrayNama.clear();
        arrayAlamat.clear();
        arrayHobi.clear();
    }

    void getData() {
        initializerArray();
        AndroidNetworking.get("http://192.168.1.4/api-mahasiswa.php/getData.php")
                .setTag("Get Data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            Boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray ja = response.getJSONArray("result");
                                Log.d("response", " " + ja);
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    arraNoinduk.add(jo.getString("noinduk"));
                                    arrayNama.add(jo.getString("nama"));
                                    arrayAlamat.add(jo.getString("alamat"));
                                    arrayHobi.add(jo.getString("hobi"));
                                    recylerViewAdapter = new RecylerViewAdapter(MainActivity.this, arraNoinduk, arrayNama, arrayAlamat, arrayHobi);
                                    rv_main.setAdapter(recylerViewAdapter);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                                recylerViewAdapter = new RecylerViewAdapter(MainActivity.this, arraNoinduk, arrayNama, arrayAlamat, arrayHobi);
                                rv_main.setAdapter(recylerViewAdapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutambah,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.mn_tambah){
            Intent intent=new Intent(MainActivity.this,Activity_add.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
