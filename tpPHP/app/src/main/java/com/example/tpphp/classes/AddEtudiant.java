package com.example.tpphp.classes;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tpphp.EtudiantList;
import com.example.tpphp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEtudiant extends AppCompatActivity implements View.OnClickListener {
    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add;
    private Button list ;

    private EditText dateNaissance;
    private ImageView imageView;
    private Uri selectedImageUri;
    RequestQueue requestQueue;
    String insertUrl = "http://10.0.2.2/PhpProject1/ws/createEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);

        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        add = findViewById(R.id.add);
        list = findViewById(R.id.list);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);
        dateNaissance = findViewById(R.id.dateNaissance);
        imageView = findViewById(R.id.imageView);
        list.setOnClickListener(this);
        add.setOnClickListener(this);
        dateNaissance.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == add) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());

            String sexe = m.isChecked() ? "homme" : "femme";
            String date = dateNaissance.getText().toString();

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("nom", nom.getText().toString());
                jsonBody.put("prenom", prenom.getText().toString());
                jsonBody.put("ville", ville.getSelectedItem().toString());
                jsonBody.put("sexe", sexe);
                jsonBody.put("dateNaissance", date); // Add the formatted date
                jsonBody.put("image", getImageBase64()); // Add the base64 image

                Log.d("Request Data", jsonBody.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    insertUrl,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response", response.toString());
                            if (response.has("success")) {
                                Log.d("Response", "Etudiant created successfully");
                                Toast.makeText(AddEtudiant.this, "Etudiant ajouté", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("Response", "Error: " + response.optString("error"));
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley Error", error.toString());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);
        } else if (v == dateNaissance) {
            showDatePickerDialog();
        } else if (v == imageView) {
            openGallery();
        }else if (v == list) {

            Intent intent = new Intent(AddEtudiant.this, EtudiantList.class);
            startActivity(intent);
        }
    }


    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth1) -> {
                    String date = year1 + "-" + (month1 + 1) + "-" + dayOfMonth1;
                    dateNaissance.setText(date);
                },
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);  // Request code 1
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    // Convert selected image to Base64 string
    private String getImageBase64() {
        if (selectedImageUri != null) {
            try {
                Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
