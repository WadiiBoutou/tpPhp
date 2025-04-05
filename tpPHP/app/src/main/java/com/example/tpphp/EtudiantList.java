package com.example.tpphp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tpphp.adapter.EtudiantAdapter;
import com.example.tpphp.classes.Etudiant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EtudiantList extends AppCompatActivity {
    private ListView listView;
    private RequestQueue requestQueue;
    private String url = "http://10.0.2.2/PhpProject1/ws/loadEtudiant.php";
    private ArrayList<Etudiant> etudiants;
    private EtudiantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etudiant_list);

        listView = findViewById(R.id.etudiantListView);
        etudiants = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        loadEtudiants();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
        });
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Voulez-vous vraiment supprimer cet étudiant ?");

        builder.setPositiveButton("Oui", (dialog, which) -> {
            Etudiant etudiant = etudiants.get(position);
            deleteStudent(etudiant, position);
        });

        builder.setNegativeButton("Non", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.create().show();
    }

    private void deleteStudent(Etudiant etudiant, int position) {
        String deleteUrl = "http://10.0.2.2/PhpProject1/controller/deleteEtudiant.php";

        JsonObjectRequest deleteRequest = new JsonObjectRequest(
                Request.Method.POST,
                deleteUrl,
                createJsonBody(etudiant.getId()),
                response -> {
                    try {
                        // Check if response has "success" field
                        if (response.has("success") && response.getBoolean("success")) {
                            runOnUiThread(() -> {
                                Toast.makeText(EtudiantList.this,
                                        "Étudiant supprimé avec succès",
                                        Toast.LENGTH_SHORT).show();
                                // Reload the list
                                loadEtudiants();
                            });
                        } else {
                            // Handle server-reported error
                            String errorMsg = response.optString("message", "Échec de la suppression");
                            runOnUiThread(() ->
                                    Toast.makeText(EtudiantList.this, errorMsg, Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        runOnUiThread(() ->
                                Toast.makeText(EtudiantList.this, "Format de réponse invalide", Toast.LENGTH_SHORT).show());
                    }
                },
                error -> {
                    runOnUiThread(() ->
                            Toast.makeText(EtudiantList.this,
                                    "Erreur de connexion: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(deleteRequest);
    }

    private void loadEtudiants() {
        etudiants.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                etudiants.add(new Etudiant(
                                        obj.getInt("id"),
                                        obj.getString("nom"),
                                        obj.getString("prenom"),
                                        obj.getString("ville"),
                                        obj.getString("sexe"),
                                        obj.isNull("dateNaissance") ? null : obj.getString("dateNaissance"),
                                        obj.getString("image")
                                ));
                            }
                            adapter = new EtudiantAdapter(EtudiantList.this, etudiants);
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(EtudiantList.this,
                                    "Aucun étudiant trouvé",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(EtudiantList.this,
                                "Erreur de traitement des données",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(EtudiantList.this,
                            "Erreur de chargement: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(request);
    }

    private JSONObject createJsonBody(int id) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", id);
        } catch (JSONException e) {
            // Ignore
        }
        return jsonBody;
    }
}