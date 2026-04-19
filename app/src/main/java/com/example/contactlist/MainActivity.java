package com.example.contactlist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactlist.R;
import com.example.contactlist.adapter.ContactAdapter;
import com.example.contactlist.model.Contact;
import com.example.contactlist.service.ContactService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ContactAdapter.OnContactListener {

    private ContactService service;
    private ContactAdapter adapter;
    private List<Contact>  contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ FIX 1 : Lier la Toolbar custom → titre "Contact List" s'affiche
        // ✅ FIX 2 : Le menu (SearchView) sera gonflé dans cette Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialisation couche métier
        service = new ContactService(this);
        service.initialiserDonneesDemo();

        // Chargement données
        contacts = service.getTousLesContacts();

        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(this, contacts, this);
        recyclerView.setAdapter(adapter);

        // FAB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> afficherDialogueAjouter());
    }

    // ── Menu SearchView ──────────────────────────────────────
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        // ✅ FIX 3 : Utiliser androidx.appcompat.widget.SearchView
        //    et non android.widget.SearchView (compatible avec Toolbar)
        androidx.appcompat.widget.SearchView searchView =
                (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        searchView.setQueryHint("Rechercher un contact...");

        searchView.setOnQueryTextListener(
                new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String q) { return false; }

                    @Override
                    public boolean onQueryTextChange(String q) {
                        contacts = service.rechercherContacts(q);
                        adapter.setListe(contacts);
                        return true;
                    }
                });

        return true;
    }

    // ── Dialogue Ajouter ─────────────────────────────────────
    private void afficherDialogueAjouter() {
        View vue = getLayoutInflater().inflate(R.layout.dialog_contact, null);
        EditText etNom = vue.findViewById(R.id.etNom);
        EditText etTel = vue.findViewById(R.id.etTelephone);

        new AlertDialog.Builder(this)
                .setTitle("Ajouter un contact")
                .setView(vue)
                .setPositiveButton("Ajouter", (d, w) -> {
                    try {
                        Contact c = service.ajouterContact(
                                etNom.getText().toString(),
                                etTel.getText().toString());
                        contacts.add(c);
                        adapter.notifyItemInserted(contacts.size() - 1);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    // ── Dialogue Modifier ────────────────────────────────────
    private void afficherDialogueModifier(int position) {
        Contact c = contacts.get(position);
        View vue = getLayoutInflater().inflate(R.layout.dialog_contact, null);
        EditText etNom = vue.findViewById(R.id.etNom);
        EditText etTel = vue.findViewById(R.id.etTelephone);
        etNom.setText(c.getNom());
        etTel.setText(c.getTelephone());

        new AlertDialog.Builder(this)
                .setTitle("Modifier le contact")
                .setView(vue)
                .setPositiveButton("Modifier", (d, w) -> {
                    try {
                        Contact modifie = service.modifierContact(
                                c.getId(),
                                etNom.getText().toString(),
                                etTel.getText().toString());
                        contacts.set(position, modifie);
                        adapter.notifyItemChanged(position);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    // ── Dialogue Supprimer ───────────────────────────────────
    private void confirmerSuppression(int position) {
        Contact c = contacts.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Supprimer " + c.getNom() + " ?")
                .setPositiveButton("Oui", (d, w) -> {
                    try {
                        service.supprimerContact(c.getId());
                        contacts.remove(position);
                        adapter.notifyItemRemoved(position);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override public void onModifier(int position)  { afficherDialogueModifier(position); }
    @Override public void onSupprimer(int position) { confirmerSuppression(position); }
}
