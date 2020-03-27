package com.akbarprojec.networkingcrud;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.MyAdaptor> {
    private Context mContext;
    private ArrayList<String> arraNoinduk, arrayNama, arrayAlamat, arrayHobi;
    ProgressDialog progressDialog;

    public RecylerViewAdapter(Context mContext, ArrayList<String> arraNoinduk,
                              ArrayList<String> arrayNama, ArrayList<String> arrayAlamat,
                              ArrayList<String> arrayHobi) {
        this.mContext = mContext;
        this.arraNoinduk = arraNoinduk;
        this.arrayNama = arrayNama;
        this.arrayAlamat = arrayAlamat;
        this.arrayHobi = arrayHobi;
    }

    @NonNull
    @Override
    public MyAdaptor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View mView = layoutInflater.inflate(R.layout.tempale_rv, parent, false);
        return new RecylerViewAdapter.MyAdaptor(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyAdaptor holder, final int position) {
        holder.tv_noid.setText(arraNoinduk.get(position));
        holder.tv_nama.setText(arrayNama.get(position));
        holder.tv_alamat.setText(arrayAlamat.get(position));
        holder.tv_hobi.setText(arrayHobi.get(position));
        holder.cv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext,Edit_Activity.class);
                intent.putExtra("noinduk",""+arraNoinduk.get(position));
                intent.putExtra("nama",""+arrayNama.get(position));
                intent.putExtra("alamat",""+arrayAlamat.get(position));
                intent.putExtra("hobi",""+arrayHobi.get(position));
                ((MainActivity)mContext).startActivityForResult(intent,2);
            }
        });
        holder.cv_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder((MainActivity) mContext)
                        .setMessage("Ingin menghapus nomor induk" + arraNoinduk.get(position) + "?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.setMessage("Menghapus ....");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                AndroidNetworking.post("http://192.168.1.4/api-mahasiswa.php/deleteSiswa.php")
                                        .addBodyParameter("noinduk", "" + arraNoinduk.get(position))
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                progressDialog.dismiss();
                                                try {
                                                    Boolean status = response.getBoolean("status");
                                                    Log.d("status", "" + status);
                                                    String result = response.getString("result");
                                                    if (status) {
                                                        if (mContext instanceof MainActivity) {
                                                            ((MainActivity) mContext).scrollRefresh();
                                                        }
                                                    } else {
                                                        Toast.makeText(mContext, "" + result, Toast.LENGTH_SHORT).show();
                                                    }

                                                } catch (Exception e) {

                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                anError.printStackTrace();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraNoinduk.size();
    }


    public class MyAdaptor extends RecyclerView.ViewHolder {
        public TextView tv_noid, tv_nama, tv_alamat, tv_hobi;
        public CardView cv_main;

        public MyAdaptor(@NonNull View itemView) {
            super(itemView);
            cv_main = itemView.findViewById(R.id.cv_main);
            tv_noid = itemView.findViewById(R.id.tv_noinduk);
            tv_nama = itemView.findViewById(R.id.tv_nama);
            tv_alamat = itemView.findViewById(R.id.tv_alamat);
            tv_hobi = itemView.findViewById(R.id.tv_hobi);

            progressDialog = new ProgressDialog(mContext);

        }
    }
}
