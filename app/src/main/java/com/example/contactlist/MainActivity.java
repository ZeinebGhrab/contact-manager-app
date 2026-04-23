package com.example.contactlist;

import android.os.Bundle;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    // Vues toolbar / recherche
    private Toolbar      toolbar;
    private LinearLayout searchBar;
    private EditText     etSearch;
    private boolean      enModeRecherche = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar normale
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Barre de recherche (cachée par défaut)
        searchBar  = findViewById(R.id.searchBar);
        etSearch   = findViewById(R.id.etSearch);
        ImageButton btnBack = findViewById(R.id.btnBackSearch);

        // Flèche retour → fermer la recherche
        btnBack.setOnClickListener(v -> fermerRecherche());

        // Filtrage en temps réel
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                contacts = service.rechercherContacts(s.toString());
                adapter.setListe(contacts);
            }
        });

        // Couche métier
        service = new ContactService(this);
        service.initialiserDonneesDemo();
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

    // ── Menu : icône loupe ───────────────────────────────────
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            ouvrirRecherche();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ── Basculer en mode recherche ───────────────────────────
    private void ouvrirRecherche() {
        enModeRecherche = true;
        toolbar.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        etSearch.requestFocus();
        // Ouvrir le clavier automatiquement
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    // ── Revenir au mode normal ───────────────────────────────
    private void fermerRecherche() {
        enModeRecherche = false;
        etSearch.setText("");
        searchBar.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        // Fermer le clavier
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        // Recharger tous les contacts
        contacts = service.getTousLesContacts();
        adapter.setListe(contacts);
    }

    // ── Bouton Back système ──────────────────────────────────
    @Override
    public void onBackPressed() {
        if (enModeRecherche) {
            fermerRecherche();
        } else {
            super.onBackPressed();
        }
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
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override public void onModifier(int position)  { afficherDialogueModifier(position); }
    @Override public void onSupprimer(int position) { confirmerSuppression(position); }
}
