package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.ui.fragment_cuoco.FragmentCuoco;
import com.example.myapplication.ui.fragment_utente.FragmentUtente;
import com.google.firebase.auth.FirebaseAuth;

public class ProfiloActivity extends AppCompatActivity {
    private String currentID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        Intent intent=getIntent();
        String tipo_utente=intent.getStringExtra("tipo_utente");
        String value=intent.getStringExtra("tipo");
        String id_utente=intent.getStringExtra("utente");


        if(value.equals("commento")){
            currentID=id_utente;
        }else {
            currentID=FirebaseAuth.getInstance().getUid(); //RICORDARE DA CAMBIARE IN COMMENTO
        }

        if(tipo_utente.equals("cuoco")){
            FragmentCuoco fragmentCuoco= new FragmentCuoco();
            fragmentCuoco.doSomething(currentID);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragmentCuoco);
            fragmentTransaction.commit();


        }else {//è un utente normale
            FragmentUtente fragmentUtente= new FragmentUtente();
            fragmentUtente.doSomething(currentID);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragmentUtente);
            fragmentTransaction.commit();
        }
    }
}
