package com.example.contactlist.model;

public class Contact {

    private int    id;
    private String nom;
    private String telephone;

    // Constructeur avec id (lecture BD)
    public Contact(int id, String nom, String telephone) {
        this.id        = id;
        this.nom       = nom;
        this.telephone = telephone;
    }

    // Constructeur sans id (insertion)
    public Contact(String nom, String telephone) {
        this.nom       = nom;
        this.telephone = telephone;
    }

    public int    getId()          { return id; }
    public String getNom()         { return nom; }
    public String getTelephone()   { return telephone; }

    public void setId(int id)             { this.id = id; }
    public void setNom(String nom)        { this.nom = nom; }
    public void setTelephone(String tel)  { this.telephone = tel; }
}
