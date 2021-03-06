package com.example.saffin.androidsmartcity.models;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.saffin.androidsmartcity.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.MyViewHolder> {
    public List<String> list = new ArrayList<>();
    public List<String> listUsers = new ArrayList<>();
    public List<String> listDates = new ArrayList<>();
    public Context t;
    public String uid;

    public AdapterMessage(List<String> list,List<String> listDates,List<String> listUsers){
        this.list = list;
        this.t = t;
        this.listDates = listDates;
        this.listUsers = listUsers;
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
        String date = listDates.get(position);
        String user = listUsers.get(position);
        holder.display(l,date,user);
    }

    public String getUid(){
        return uid;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView user;
        private final TextView date;
        private String list;

        public MyViewHolder(final View itemView) {
            super(itemView);

            name = ((TextView) itemView.findViewById(R.id.name));
            date = ((TextView) itemView.findViewById(R.id.date));
            user = ((TextView) itemView.findViewById(R.id.nameUser));
        }

        public void display(String liste, String date, String user) {
            name.setText(liste);
            this.date.setText(date);
            this.user.setText(user);
        }
    }

}