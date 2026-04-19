package com.example.contactlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactlist.R;
import com.example.contactlist.model.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    // Interface bonne pratique (cours slide 173)
    public interface OnContactListener {
        void onModifier(int position);
        void onSupprimer(int position);
    }

    private List<Contact>       liste;
    private Context             context;
    private OnContactListener   listener;

    public ContactAdapter(Context context,
                          List<Contact> liste,
                          OnContactListener listener) {
        this.context  = context;
        this.liste    = liste;
        this.listener = listener;
    }

    @NonNull @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact c = liste.get(position);
        holder.tvNom.setText(c.getNom());
        holder.tvTelephone.setText(c.getTelephone());

        // Délégation à l'Activity via l'interface
        holder.btnModifier.setOnClickListener(v ->
                listener.onModifier(holder.getAdapterPosition()));

        holder.btnSupprimer.setOnClickListener(v ->
                listener.onSupprimer(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() { return liste.size(); }

    public void setListe(List<Contact> liste) {
        this.liste = liste;
        notifyDataSetChanged();
    }
}