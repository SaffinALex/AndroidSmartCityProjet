package com.example.saffin.androidsmartcity.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.GroupCreate;
import com.example.saffin.androidsmartcity.GroupTchat;
import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.Social;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapterMyGroup extends RecyclerView.Adapter<MyAdapterMyGroup.MyViewHolder> {
    public List<String> list = new ArrayList<>();
    public Context t;
    public List<Pair<List<String>, String>> listUsersByGroup = new ArrayList<>();
    public String uid;
    public String gid;
    boolean estInscrit = false;
    public final FirebaseUser user;

    public MyAdapterMyGroup(List<String> list, Context t, List<Pair<List<String>, String>> listUsersByGroup, String uid, FirebaseUser user){
        this.t = t;
        this.user = user;
        List<Pair<List<String>, String>> listMyGroup = new ArrayList<>();
        for(Pair<List<String>, String> p : listUsersByGroup){
            for(String userId : p.first) {
                if (userId.equals(uid)) {
                    this.listUsersByGroup.add(p);
                    this.list.add(p.second);
                }
            }
        }

        this.uid = uid;

        /*Log.d("houdies",list.get(0));
        Log.d("houdies",list.get(1));*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_cell, parent, false);
        return new MyViewHolder(view, uid, listUsersByGroup);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String l = list.get(position);
        holder.display(l);
    }

    public String getUid(){
        return uid;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public String list;
        public MyViewHolder(final View itemView, final String uid, final List<Pair<List<String>, String>> listUsersByGroup) {
            super(itemView);
            name = ((TextView)itemView.findViewById(R.id.name));
            list = name.getText().toString();
            Log.d("coucou", list);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayGroupConfirm( uid, listUsersByGroup);
                }
            });
        }

        public void display(String liste) {
            name.setText(liste);
        }

        public void displayGroupConfirm( final String uid, final List<Pair<List<String>, String>> listUsersByGroup){
            Log.d("coucou", listUsersByGroup.get(0).second);
            estInscrit=false;
            name = ((TextView)itemView.findViewById(R.id.name));
            list = name.getText().toString();
            for(Pair<List<String>, String> p : listUsersByGroup){
                if(p.second.equals(list)){
                    for(String user : p.first){
                        if(user.equals(uid)){
                            estInscrit = true;
                        }
                    }
                }
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(t);
            builder.setCancelable(true);
            builder.setTitle(list);
            builder.setMessage("Que souhaitez vous faire ?");
            if(estInscrit){
                builder.setNegativeButton("Se Désinscrire",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(t, Social.class);
                                GroupHelper.deleteGroupUser(list,uid);
                                Toast.makeText(t.getApplicationContext(),
                                        "Vous êtes désinscrit de votre groupe",
                                        Toast.LENGTH_SHORT).show();
                                t.startActivity(i);
                            }
                        });
                builder.setPositiveButton("Continuer",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(t, GroupTchat.class);
                                i.putExtra("gid", list);
                                t.startActivity(i);
                            }
                        });
            }
            else {

                builder.setPositiveButton("Inscription",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GroupHelper.createGroupUsers(list,uid);
                                Intent i = new Intent(t, GroupTchat.class);
                                Toast.makeText(t.getApplicationContext(),
                                        "Vous êtes inscrit",
                                        Toast.LENGTH_SHORT).show();
                                i.putExtra("gid", list);
                                t.startActivity(i);
                            }
                        });
            }

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}