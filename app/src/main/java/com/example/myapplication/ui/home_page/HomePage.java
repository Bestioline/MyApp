package com.example.myapplication.ui.home_page;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomePage extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private TextView link1,linkUtente;
    private EditText codice;
    private Button verifica;
    private String codiceInserito;
    private FirebaseFirestore firestore;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            Intent i =new Intent(HomePage.this, MainActivity.class);
            startActivity(i);
        }
        else {
            setContentView(R.layout.activity_home_page);
            link1 = findViewById(R.id.link);
            linkUtente = findViewById(R.id.linkUtente);
            codice = findViewById(R.id.codice);
            codice.setVisibility(View.GONE);
            verifica = findViewById(R.id.verifica);
            verifica.setVisibility(View.GONE);
//-------------------------clicca sul link di cuoco------------------------------------------------------------
            link1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    codice.setVisibility(View.VISIBLE);
                    verifica.setVisibility(View.VISIBLE);
                    linkUtente.setVisibility(View.GONE);
                }
            });
            verifica.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    codiceInserito = codice.getText().toString().trim();
                    CollectionReference col = firestore.collection("codiciCuochi");
                    col.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    System.out.println(doc.get("codice") + " codice");
                                    if (doc.get("codice").equals(codice.getText().toString())) {
                                        Toast.makeText(HomePage.this, "Codice valido!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(HomePage.this, LoginActivity.class);
                                        i.putExtra("utente", "cuoco");
                                        startActivity(i);
                                    }
                                }
                            }
                        }
                    });
                }
            });
//------------------------clicca sul link utente----------------------------------------------------------------------
            linkUtente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(HomePage.this, LoginActivity.class);
                    i.putExtra("utente", "utente");
                    startActivity(i);
                }
            });
        }
//------------------------------------------------------------------------------------------------------------------------------


    }
}
