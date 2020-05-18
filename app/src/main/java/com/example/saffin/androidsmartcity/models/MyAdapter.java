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

import com.example.saffin.androidsmartcity.GroupTchat;
import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.Social;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    public List<String> list = new ArrayList<>();
    public Context t;
    public List<Pair<List<String>, String>> listUsersByGroup = new ArrayList<>();;
    public String uid;
    public String gid;
    public final FirebaseUser user;

    public MyAdapter(List<String> list, Context t, List<Pair<List<String>, String>> listUsersByGroup, String uid, FirebaseUser user){
        this.list = list;
        this.t = t;
        this.user = user;
        this.listUsersByGroup = listUsersByGroup;
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
        return new MyViewHolder(view);
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

        public MyViewHolder(final View itemView) {
            super(itemView);

            name = ((TextView) itemView.findViewById(R.id.name));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gid = list;
                    displayGroupConfirm(gid, getUid());
                }
            });
        }

        public void display(String liste) {
            name.setText(liste);
        }

        public void displayGroupConfirm(final String name, final String uid){
            boolean estInscrit = false;
            if(!name.isEmpty())
                Log.d("gid", name);
            AlertDialog.Builder builder = new AlertDialog.Builder(t);
            builder.setCancelable(true);
            builder.setTitle(name);
            for(Pair<List<String>, String> p : listUsersByGroup){
                if(p.second.equals(name)){
                    for(String user : p.first){
                        if(user.equals(uid)){
                            estInscrit = true;
                        }
                    }
                }
            }
            builder.setMessage("Que souhaitez vous faire ?");
            if(estInscrit){
                builder.setPositiveButton("Se Désinscrire",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(t, Social.class);
                                GroupHelper.deleteGroupUser(name,uid);
                                t.startActivity(i);
                            }
                        });
                builder.setPositiveButton("Continuer",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(t, GroupTchat.class);
                                i.putExtra("gid", name);
                                t.startActivity(i);
                            }
                        });
            }
            else {
                Log.d("gid", name);
                builder.setPositiveButton("Inscription",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("gid", name);
                                GroupHelper.createGroupUsers(name, user);
                                Intent i = new Intent(t, GroupTchat.class);
                                i.putExtra("gid", gid);
                                t.startActivity(i);
                            }
                        });
            }

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}