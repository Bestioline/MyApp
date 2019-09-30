package com.example.myapplication.ui.fragment_partecipanti;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.ui.fragment_evento.Evento;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class Fragment_ListaPartecipanti extends Fragment {
    private ArrayList<String> list=new ArrayList<>();
    private RecyclerView recyclerView;
    private View myView;
    private CollectionReference colR;
    private FirebaseFirestore ff= FirebaseFirestore.getInstance();
    private View view;
    private Bundle savedInstanceState;
    private Adapter_Partecipanti tutorAdapter;

    private TextView label_part;
    private Button iscriviti;
    private String id_evento;
    private String utente_corrente=FirebaseAuth.getInstance().getUid();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorAdapter = new Adapter_Partecipanti(list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_lista_partecipanti, container, false);
        recyclerView = myView.findViewById(R.id.lista_partecipanti);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.startLayoutAnimation();
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.startLayoutAnimation();
        recyclerView.setAdapter(tutorAdapter);
        return myView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle bundle=this.getArguments();
        id_evento=bundle.getString("id");
        label_part=(TextView) view.findViewById(R.id.text_part);
        iscriviti=(Button) view.findViewById(R.id.button_iscrizione);

        iscriviti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_button(); // SET A SECONDA SE SEI GIA' ISCRITTO O NO ALL'EVENTO
            }
        });
    }



    public void set_button(){
        ff.collection("eventi").document(""+id_evento).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Evento e= documentSnapshot.toObject(Evento.class);
                int num=e.getMax_partecipanti();
                ArrayList<String> lista_p=(ArrayList<String>) e.getLista_part();

                if(!lista_p.contains(utente_corrente) && lista_p.size()<num) {
                    add_partecipante(lista_p,num);
                    iscriviti.setText("Esci");
                }
                else {
                    remove_partecipante(lista_p,num);
                    iscriviti.setText("Iscriviti");
                }
            }
        });
    }


    public void doSomething(String id_evento){
        ff.collection("eventi").document(""+id_evento).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Evento evento=documentSnapshot.toObject(Evento.class);
                ArrayList<String> lista_p=(ArrayList<String>) evento.getLista_part();
                int num=evento.getMax_partecipanti();
                label_part.setText(lista_p.size()+"/"+num);

                for(String s:lista_p) {
                    list.add(s);
                    tutorAdapter.notifyDataSetChanged();
                }
                //SETTIAMO IL BOTTONE DI ISCRIZIONE
                if (list.contains(utente_corrente))
                    iscriviti.setText("Esci");
                else if(list.size()==num){
                    iscriviti.setClickable(false);
                    iscriviti.setBackgroundColor(R.color.common_google_signin_btn_text_light_disabled);
                }
            }
        });
    }




    public void remove_partecipante(ArrayList<String> list_p, int num){
        list.clear();
        list.addAll(list_p);
        list.remove(utente_corrente);
        recyclerView.scrollToPosition(tutorAdapter.getItemCount());
        tutorAdapter.notifyDataSetChanged();
        label_part.setText(list.size()+"/"+num);

        ff.collection("eventi").document(""+id_evento).update("lista_part", list).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        modifica_inUtente(false);
    }



    public void add_partecipante(ArrayList<String> list_p, int num){

        list.clear();
        list.addAll(list_p);
        list.add(list.size(),utente_corrente);
        tutorAdapter.notifyItemInserted(list.size()-1);
        recyclerView.scrollToPosition(tutorAdapter.getItemCount()-1);
        label_part.setText(list.size()+"/"+num);
        ff.collection("eventi").document(""+id_evento).update("lista_part", list).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) { }
        });
        modifica_inUtente(true);
    }


    public void modifica_inUtente(boolean aggiungi){
        DocumentReference doc= ff.collection("utenti2").document(""+utente_corrente);
            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> lista_eventi=new ArrayList<>();
                if(documentSnapshot.get("lista_eventi")!=null)
                    lista_eventi=(ArrayList<String>) documentSnapshot.get("lista_eventi");

                if(aggiungi)lista_eventi.add(id_evento);
                else lista_eventi.remove(id_evento);

                doc.update("lista_eventi", lista_eventi).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {}
                });
            }
        });
    }

    private Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
}
