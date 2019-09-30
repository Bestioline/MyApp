package com.example.myapplication.ui.fragment_evento;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.ProfiloActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.fragment_cuoco.Cuoco;
import com.example.myapplication.ui.fragment_partecipanti.Fragment_ListaPartecipanti;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Fragment_Evento extends Fragment {
    private Bundle bundle;
    private TextView cuoco,nome_evento,ora,data,luogo;
    private FloatingActionButton add_part;
    private String id_evento,id_cuoco;
    public Evento e;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home_evento, parent, false);
        bundle = this.getArguments();

        if(bundle != null){
            id_evento=bundle.getString("id_evento");
            id_cuoco=bundle.getString("id_cuoco");
        }
        preleva_evento(id_evento);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nome_evento=(TextView)view.findViewById(R.id.nome_home_evento);
        ora=(TextView)view.findViewById(R.id.home_ora);
        data=(TextView)view.findViewById(R.id.home_data);
        luogo=(TextView)view.findViewById(R.id.home_luogo);

        //CLICK SUL NOME DEL CUOCO, APPRE IL PROFILO
        cuoco=(TextView)view.findViewById(R.id.cuoco_home);
        cuoco.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                apri_profilo();
            }
        });

        Button descrizione= (Button)view.findViewById(R.id.button_descr_evento);
        descrizione.setClickable(false);//Si apre direttamente nella sezione descrizione
        Button partecipanti=(Button)view.findViewById(R.id.button_partec);


        descrizione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getChildFragmentManager().popBackStack("DESCRIZIONE",FragmentManager.POP_BACK_STACK_INCLUSIVE);
               descrizione.setClickable(false);
               partecipanti.setClickable(true);
            }
        });



        partecipanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_ListaPartecipanti fragment = new Fragment_ListaPartecipanti();
                Bundle b=new Bundle();
                b.putString("id",id_evento);
                fragment.setArguments(b);
                fragment.doSomething(id_evento);
                FragmentManager manager = getChildFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.frame_home_ricetta, fragment);
                transaction.addToBackStack("DESCRIZIONE");
                transaction.commit();
                descrizione.setClickable(true);
                partecipanti.setClickable(false);
            }
        });
    }


    private void apri_profilo(){
        Intent myIntent = new Intent(getActivity().getBaseContext(), ProfiloActivity.class);
        myIntent.putExtra("tipo", "commento");//Optional parameters
        myIntent.putExtra("utente", id_cuoco);
        myIntent.putExtra("tipo_utente","cuoco");
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().getBaseContext().startActivity(myIntent);
    }


    private void set_cuoco(String id_cuoco) {
        FirebaseFirestore ff= FirebaseFirestore.getInstance();
        DocumentReference doc=ff.collection("utenti2").document("" + id_cuoco);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null) {
                    Cuoco prof_cuoco = documentSnapshot.toObject(Cuoco.class);
                    cuoco.setText(prof_cuoco.getNome());
                }
            }
        });
    }


    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }



    private void preleva_evento(String id_evento) {
        FirebaseFirestore ff= FirebaseFirestore.getInstance();
        DocumentReference doc=ff.collection("eventi").document("" + id_evento);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null) {
                    e = documentSnapshot.toObject(Evento.class);
                    set_profilo(e);
                }
            }
        });
    }


    public void set_profilo(Evento e){
        nome_evento.setText(e.getNome());
        ora.setText(e.getOra());
        data.setText(e.getData());
        luogo.setText(e.getLuogo());
        set_cuoco(e.getId_cuoco());
        aggiungiFragmentDescr(e);

    }

    public void aggiungiFragmentDescr(Evento e){
        bundle.putString("descrizione", e.getDescrizione());
        bundle.putInt("num_max", e.getMax_partecipanti());
        bundle.putInt("num_attuali",50);
        FragmentDescrEvento descrizioneFragment = new FragmentDescrEvento();
        descrizioneFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().add(R.id.frame_home_ricetta,descrizioneFragment).addToBackStack(null).commit();
    }
}
