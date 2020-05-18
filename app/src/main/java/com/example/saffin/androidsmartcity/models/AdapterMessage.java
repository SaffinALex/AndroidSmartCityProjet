package com.example.saffin.androidsmartcity.models;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.Social;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.MyViewHolder> {
    public List<String> list = new ArrayList<>();
    public Context t;
    public List<Pair<List<String>, String>> listUsersByGroup = new ArrayList<>();;
    public String uid;

    public AdapterMessage(List<String> list){
        this.list = list;
        this.t = t;
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
        View view = inflater.inflate(R.layout.message_cell, parent, false);
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

        private final TextView name;
        private String list;

        public MyViewHolder(final View itemView) {
            super(itemView);

            name = ((TextView) itemView.findViewById(R.id.name));
        }

        public void display(String liste) {
            name.setText(liste);
        }
    }

}