package com.example.contactlist.service;

import android.content.Context;

import com.example.contactlist.database.ContactDAO;
import com.example.contactlist.model.Contact;

import java.util.List;

public class ContactService {

    private ContactDAO dao;

    public ContactService(Context context) {
        this.dao = new ContactDAO(context);
    }

    // ── Règles métier : validation avant toute opération ────

    public Contact ajouterContact(String nom, String telephone)
            throws Exception {

        // Règle 1 : champs obligatoires
        if (nom == null || nom.trim().isEmpty())
            throw new Exception("Le nom est obligatoire.");

        if (telephone == null || telephone.trim().isEmpty())
            throw new Exception("Le téléphone est obligatoire.");

        // Règle 2 : format téléphone (8 chiffres tunisien)
        if (!telephone.trim().matches("\\d{8}"))
            throw new Exception("Le numéro doit contenir exactement 8 chiffres.");

        // Règle 3 : capitalisation automatique (logique métier)
        String nomFormate = capitaliser(nom.trim());
        String telFormate = telephone.trim();

        // Règle 4 : unicité du nom (en ignorant la casse)
        if (dao.existeNom(nomFormate, -1))
            throw new Exception("Ce nom existe déjà dans les contacts.");

        // Règle 5 : unicité du numéro de téléphone
        if (dao.existeTelephone(telFormate, -1))
            throw new Exception("Ce numéro de téléphone existe déjà dans les contacts.");

        Contact c = new Contact(nomFormate, telFormate);
        long id = dao.inserer(c);

        if (id == -1)
            throw new Exception("Erreur lors de l'enregistrement.");

        c.setId((int) id);
        return c;
    }

    public Contact modifierContact(int id, String nom, String telephone)
            throws Exception {

        if (nom == null || nom.trim().isEmpty())
            throw new Exception("Le nom est obligatoire.");

        if (telephone == null || telephone.trim().isEmpty())
            throw new Exception("Le téléphone est obligatoire.");

        if (!telephone.trim().matches("\\d{8}"))
            throw new Exception("Le numéro doit contenir exactement 8 chiffres.");

        String nomFormate = capitaliser(nom.trim());
        String telFormate = telephone.trim();

        // Vérifier les doublons en excluant le contact en cours de modification
        if (dao.existeNom(nomFormate, id))
            throw new Exception("Ce nom existe déjà dans les contacts.");

        if (dao.existeTelephone(telFormate, id))
            throw new Exception("Ce numéro de téléphone existe déjà dans les contacts.");

        Contact c = new Contact(id, nomFormate, telFormate);
        dao.update(c);
        return c;
    }

    public void supprimerContact(int id) throws Exception {
        if (id <= 0)
            throw new Exception("Contact invalide.");
        dao.delete(id);
    }

    public List<Contact> getTousLesContacts() {
        return dao.findAll();
    }

    public List<Contact> rechercherContacts(String query) {
        if (query == null || query.trim().isEmpty())
            return dao.findAll();
        return dao.findByNom(query.trim());
    }

    public void initialiserDonneesDemo() {
        if (dao.count() == 0) {
            dao.inserer(new Contact("Foulen BEN FOULEN", "22334455"));
            dao.inserer(new Contact("Ali BEN SALAH",    "21436587"));
        }
    }

    private String capitaliser(String texte) {
        if (texte.isEmpty()) return texte;
        String[] mots = texte.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String mot : mots) {
            if (!mot.isEmpty()) {
                sb.append(Character.toUpperCase(mot.charAt(0)))
                        .append(mot.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
