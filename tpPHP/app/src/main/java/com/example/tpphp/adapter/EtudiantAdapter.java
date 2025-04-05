package com.example.tpphp.adapter;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tpphp.R;
import com.example.tpphp.classes.Etudiant;


import java.util.List;

public class EtudiantAdapter extends BaseAdapter {
    private static final String TAG = "EtudiantAdapter";
    private Context context;
    private List<Etudiant> etudiants;

    public EtudiantAdapter(Context context, List<Etudiant> etudiants) {
        this.context = context;
        this.etudiants = etudiants;
        Log.d(TAG, "Adapter created with " + etudiants.size() + " students");
    }

    @Override
    public int getCount() {
        return etudiants.size();
    }

    @Override
    public Object getItem(int position) {
        return etudiants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView called for position: " + position);

        // 1. First log the student data
        Etudiant etudiant = etudiants.get(position);
        logStudentData(etudiant);

        // 2. Inflate view if needed
        if (convertView == null) {
            Log.d(TAG, "Inflating new view");
            convertView = LayoutInflater.from(context).inflate(R.layout.etudiant_item, parent, false);
            logViewHierarchy(convertView, 0);
        } else {
            Log.d(TAG, "Reusing existing view");
        }

        // 3. Find all views and verify them
        TextView nom = convertView.findViewById(R.id.nomText);
        TextView prenom = convertView.findViewById(R.id.prenomText);
        TextView ville = convertView.findViewById(R.id.villeText);
        TextView sexe = convertView.findViewById(R.id.sexeText);
        TextView dateNaissance = convertView.findViewById(R.id.dateNaissanceText);
        ImageView imageView = convertView.findViewById(R.id.imageView);

        Log.d(TAG, "View references - nom: " + nom + ", prenom: " + prenom +
                ", ville: " + ville + ", sexe: " + sexe +
                ", dateNaissance: " + dateNaissance + ", imageView: " + imageView);

        // 4. Set data to views
        if (nom != null) nom.setText(etudiant.getNom());
        if (prenom != null) prenom.setText(etudiant.getPrenom());
        if (ville != null) ville.setText(etudiant.getVille());
        if (sexe != null) sexe.setText(etudiant.getSexe());
        if (dateNaissance != null) dateNaissance.setText(etudiant.getDateNaissance());

        // 5. Handle image
        handleImageLoading(imageView, etudiant.getImage());

        return convertView;
    }

    private void logStudentData(Etudiant etudiant) {
        Log.d(TAG, "Student Data - " +
                "Nom: " + etudiant.getNom() + ", " +
                "Prenom: " + etudiant.getPrenom() + ", " +
                "Ville: " + etudiant.getVille() + ", " +
                "Sexe: " + etudiant.getSexe() + ", " +
                "Date: " + etudiant.getDateNaissance() + ", " +
                "Image: " + (etudiant.getImage() != null ? "exists" : "null"));
    }

    private void handleImageLoading(ImageView imageView, String imageBase64) {
        if (imageView == null) {
            Log.e(TAG, "ImageView is null - cannot load image");
            return;
        }

        if (imageBase64 == null || imageBase64.isEmpty()) {
            Log.d(TAG, "No image data available, loading placeholder");
            Glide.with(context)
                    .load(R.drawable.ic_launcher_background)
                    .into(imageView);
            return;
        }

        try {
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (decodedBitmap != null) {
                Log.d(TAG, "Successfully decoded image");
                Glide.with(context)
                        .load(decodedBitmap)
                        .into(imageView);
            } else {
                Log.e(TAG, "Decoded bitmap is null");
                Glide.with(context)
                        .load(R.drawable.ic_launcher_background)
                        .into(imageView);
            }
        } catch (Exception e) {
            Log.e(TAG, "Image decoding error: " + e.getMessage());
            Glide.with(context)
                    .load(R.drawable.ic_launcher_foreground)
                    .into(imageView);
        }
    }

    private void logViewHierarchy(View v, int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) sb.append("  ");

        String viewInfo = sb.toString() + v.getClass().getSimpleName() +
                " id=" + v.getId() +
                " visible=" + v.getVisibility() +
                " width=" + v.getWidth() +
                " height=" + v.getHeight();

        Log.d(TAG, viewInfo);

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                logViewHierarchy(vg.getChildAt(i), depth + 1);
            }
        }
    }
}