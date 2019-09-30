package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.ThreadLockedTask;
import com.example.myapplication.ui.fragment_cuoco.Cuoco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    //riferimento al database
    private FirebaseAuth mAuth;
    //label della mail del login
    private AutoCompleteTextView mEmailView;
    //label della password del login
    private EditText mPasswordView;

    //bottone login
    private Button mEmailSignInButton;

    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private FirebaseFirestore firestore;


    private String tipoUtente;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent=getIntent();
        tipoUtente=intent.getStringExtra("utente");


        mAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();


        //inizializza i componenti della grafica di activity_login.xml
        initializeUI();



        //aggiunge un'azione al bottone di login
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });


    }


    private void loginUserAccount() {
        final String email, password;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mEmailView.getText().toString());
        //se l'utente non ha inserito i campi appare un messaggio
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        //controllo formato email, il formato della password è controllato già dal sistema
        if (!matcher.matches()) {
            Toast.makeText(getApplicationContext(), "Formato email non corretta", Toast.LENGTH_LONG).show();
            return;
        }
        // prova a fare l'accesso, se l'account non esiste, lo crea
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //se la password e la mail sono corrette, viene effettuato il login
                        if (task.isSuccessful()) {
                            //APRI USER PROFILE
                            Toast.makeText(getApplicationContext(), "Login avvenuto con successo", Toast.LENGTH_LONG).show();
                            user= FirebaseAuth.getInstance().getCurrentUser() ;
                            DocumentReference docRef = firestore.collection("utenti2").document(""+user.getUid());
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Object obj = documentSnapshot.toObject(Object.class);
                                    if (obj instanceof Utente) {
                                        vaiProfilo("utente");
                                    }
                                    else {
                                        vaiProfilo("cuoco");
                                    }
                                }
                            });


                        }

                        //se la password e la mail non corrispondono a nessun profilo, viene creato un account
                        else {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                Toast.makeText(getApplicationContext(), "Registratione avvenuta!", Toast.LENGTH_LONG).show();
                                                user = FirebaseAuth.getInstance().getCurrentUser();
                                                CollectionReference utenti = firestore.collection("utenti2");
                                                if (tipoUtente.equals("cuoco")){
                                                    Cuoco nuovoCuoco = new Cuoco(user.getEmail(),password);
                                                    nuovoCuoco.setEmail(user.getEmail());
                                                    nuovoCuoco.setNome(user.getEmail().substring(0,user.getEmail().indexOf("@")));
                                                    nuovoCuoco.setImageProf(user.getEmail()+".jpg");
                                                    utenti.document(""+mAuth.getUid()).set(nuovoCuoco);
                                                    //-------vai al profilo del cuoco-------------------
                                                    vaiProfilo("cuoco");
                                                }
                                                else {

                                                    Utente nuovoUtente = new Utente();
                                                    nuovoUtente.setEmail(user.getEmail());
                                                    nuovoUtente.setPassword(password);
                                                    nuovoUtente.setNome(user.getEmail().substring(0, user.getEmail().indexOf("@")));
                                                    nuovoUtente.setNick(user.getEmail().substring(0, user.getEmail().indexOf("@")));
                                                    nuovoUtente.setImageProf(user.getEmail() + ".jpg");
                                                    nuovoUtente.setBio("");

                                                    //la key dell'utente è quella del suo identificativo
                                                    //negli utenti loggati
                                                    utenti.document("" + mAuth.getUid()).set(nuovoUtente);

                                                    //vai a USER PROFILE
                                                    vaiProfilo("utente");
                                               }
                                             }
                                         else
                                        {
                                            Toast.makeText(getApplicationContext(), "User Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    });
                        }
                    }

                });
    }


    //prende i riferimenti alle label della gui
    private void initializeUI() {
        mEmailView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        mEmailSignInButton = findViewById(R.id.login);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }


    public void vaiProfilo(String tipo_utente){
        Intent intent = new Intent(LoginActivity.this, ProfiloActivity.class);
        intent.putExtra("tipo","login");
        intent.putExtra("utente","");
        intent.putExtra("tipo_utente",tipo_utente);
        startActivity(intent);
    }


}

