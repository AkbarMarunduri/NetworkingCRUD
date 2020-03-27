package com.akbarprojec.networkingcrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.net.Inet4Address;
import java.util.ArrayList;

public class Activity_add extends AppCompatActivity {
    com.rengwuxian.materialedittext.MaterialEditText et_noinduk, et_nama, et_alamt, et_hobi;
    String noinduk, nama, alamat, hobi;
    Button bt_submit;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        et_noinduk = findViewById(R.id.tv_noinduk);
        et_nama = findViewById(R.id.tv_nama);
        et_alamt = findViewById(R.id.tv_alamat);
        et_hobi = findViewById(R.id.tv_hobi);
        bt_submit = findViewById(R.id.bt_submit);

        progressDialog = new ProgressDialog(this);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Menambah data.......");
                progressDialog.setCancelable(false);
                progressDialog.show();

                noinduk = et_noinduk.getText().toString();
                nama = et_nama.getText().toString();
                alamat = et_alamt.getText().toString();
                hobi = et_hobi.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        validasiData();
                    }
                }, 1000);
            }
        });

    }

    void validasiData() {
        if (noinduk.equals("") || nama.equals("") || alamat.equals("") || hobi.equals("")) {
            progressDialog.dismiss();
            Toast.makeText(Activity_add.this, "Periksa kembali data yang anda masukkan", Toast.LENGTH_LONG).show();
        } else {
            kirimData();
        }
    }

    void kirimData() {
        AndroidNetworking.post("http://192.168.1.4/api-mahasiswa.php/tambahMahasiswa.php")
                .addBodyParameter("noinduk", "" + noinduk)
                .addBodyParameter("nama", "" + nama)
                .addBodyParameter("alamat", "" + alamat)
                .addBodyParameter("hobi", "" + hobi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d("cekTambah", "" + response);
                        try {
                            Boolean status = response.getBoolean("status");
                            String pesan = response.getString("result");
                            Log.d("status", "" + status);
                            if (status) {
                                new AlertDialog.Builder(Activity_add.this)
                                        .setMessage("Berhasil menambah data")
                                        .setCancelable(false)
                                        .setPositiveButton("kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent a = getIntent();
                                                setResult(RESULT_CANCELED, a);
                                                Activity_add.this.finish();
                                            }
                                        })
                                        .show();
                            } else {
                                new AlertDialog.Builder(Activity_add.this)
                                        .setMessage("Gagal menyimpan data........")
                                        .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent a = getIntent();
                                                setResult(RESULT_CANCELED, a);
                                                Activity_add.this.finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
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
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mn_back) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
