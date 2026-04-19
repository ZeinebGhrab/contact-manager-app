package com.example.contactlist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.contactlist.model.Contact;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {

    private ContactDatabaseHelper dbHelper;

    public ContactDAO(Context context) {
        dbHelper = new ContactDatabaseHelper(context);
    }

    // SQL pur — aucune validation ici
    public long inserer(Contact c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nom", c.getNom());
        cv.put("telephone", c.getTelephone());
        long id = db.insert("contacts", null, cv);
        db.close();
        return id;
    }

    public List<Contact> findAll() {
        List<Contact> liste = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("contacts", null, null,
                null, null, null, "nom ASC");
        if (cursor.moveToFirst()) {
            do {
                liste.add(cursorToContact(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return liste;
    }

    public List<Contact> findByNom(String query) {
        List<Contact> liste = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("contacts", null,
                "nom LIKE ?",
                new String[]{"%" + query + "%"},
                null, null, "nom ASC");
        if (cursor.moveToFirst()) {
            do {
                liste.add(cursorToContact(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return liste;
    }

    public int update(Contact c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nom", c.getNom());
        cv.put("telephone", c.getTelephone());
        int rows = db.update("contacts", cv,
                "id=?", new String[]{String.valueOf(c.getId())});
        db.close();
        return rows;
    }

    public void delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("contacts", "id=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public int count() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM contacts", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        db.close();
        return count;
    }

    // Méthode utilitaire privée — évite la répétition
    private Contact cursorToContact(Cursor cursor) {
        return new Contact(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nom")),
                cursor.getString(cursor.getColumnIndexOrThrow("telephone"))
        );
    }
}
