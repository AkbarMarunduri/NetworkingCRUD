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
import android.view.LayoutInflater;
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

public class Edit_Activity extends AppCompatActivity {

    com.rengwuxian.materialedittext.MaterialEditText et_noid, et_nama, et_alamat, et_hobi;
    String noinduk, nama, alamat, hobi;
    Button bt_submit;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_);

        et_noid = findViewById(R.id.et_noinduk);
        et_nama = findViewById(R.id.et_nama);
        et_alamat = findViewById(R.id.et_alamat);
        et_hobi = findViewById(R.id.et_hobi);
        bt_submit = findViewById(R.id.bt_submit);

        progressDialog = new ProgressDialog(this);
        getDataInten();
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Menambahkan data.......");
                progressDialog.setCancelable(false);
                progressDialog.show();

                noinduk = et_noid.getText().toString();
                nama = et_nama.getText().toString();
                alamat = et_alamat.getText().toString();
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

    void getDataInten() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            et_noid.setText(bundle.getString("noinduk"));
            et_nama.setText(bundle.getString("nama"));
            et_alamat.setText(bundle.getString("alamat"));
            et_hobi.setText(bundle.getString("hobi"));

        } else {
            et_noid.setText("");
            et_nama.setText("");
            et_alamat.setText("");
            et_hobi.setText("");
        }
    }

    void validasiData() {
        if (et_noid.equals("") || et_nama.equals("") || et_alamat.equals("") || et_hobi.equals("")) {
            progressDialog.dismiss();
            Toast.makeText(Edit_Activity.this, "Periksa kembali data yang anda masukkan", Toast.LENGTH_LONG).show();
        } else {
            updateData();
        }
    }

    void updateData() {
        AndroidNetworking.post("http://192.168.1.4/api-mahasiswa.php/updateSiswa.php?noinduk=8")
                .addBodyParameter("noinduk", "" + noinduk)
                .addBodyParameter("nama", "" + nama)
                .addBodyParameter("alamat", "" + alamat)
                .addBodyParameter("hobi", "" + hobi)
                .setTag("Update Data....")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d("responseEdit", "" + response);
                        try {
                            Boolean status = response.getBoolean("status");
                            if (status) {
                                new AlertDialog.Builder(Edit_Activity.this)
                                        .setMessage("Berhasil mengupdate data")
                                        .setCancelable(false)
                                        .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = getIntent();
                                                setResult(RESULT_OK, intent);
                                                Edit_Activity.this.finish();
                                            }
                                        }).show();
                            } else {
                                new AlertDialog.Builder(Edit_Activity.this)
                                        .setMessage("Gagal memperbaharui data")
                                        .setCancelable(false)
                                        .setPositiveButton("kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = getIntent();
                                                setResult(RESULT_CANCELED, intent);
                                                Edit_Activity.this.finish();
                                            }
                                        }).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError e) {

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
