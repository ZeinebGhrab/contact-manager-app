package com.example.contactlist.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactlist.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public TextView    tvNom, tvTelephone;
    public ImageButton btnModifier, btnSupprimer;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        tvNom        = itemView.findViewById(R.id.tvNom);
        tvTelephone  = itemView.findViewById(R.id.tvTelephone);
        btnModifier  = itemView.findViewById(R.id.btnModifier);
        btnSupprimer = itemView.findViewById(R.id.btnSupprimer);
    }
}