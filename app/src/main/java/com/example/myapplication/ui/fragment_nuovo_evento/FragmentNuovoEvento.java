package com.example.myapplication.ui.fragment_nuovo_evento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class FragmentNuovoEvento extends Fragment {

    //widget grafici
    private SearchableSpinner spinnerCitta, spinnerLuoghi;
    private View myView;
    //riferimento ad database
    private FirebaseFirestore mDatabase;
    //liste di supporto agli spinner
    private ArrayList<String> cittaList;
    private ArrayList<String> luoghiList;
    //lista dei luoghi della città selezionata
    private ArrayList<String> lista;
    private Citta cittaselezionata;
    private ArrayList<Luogo> luoghiCitta;
    private ArrayAdapter<String> adapter2;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.frag_crea_evento, container, false);
        return myView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        spinnerCitta = view.findViewById(R.id.spinnerCitta);
        spinnerLuoghi = view.findViewById(R.id.spinnerLuogo);
        mDatabase = FirebaseFirestore.getInstance();
        cittaList = new ArrayList<>();
        luoghiList = new ArrayList<>();
        luoghiCitta= new ArrayList<>();
        lista=new ArrayList<>();
        adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, luoghiList);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cittaList);
        //popolo lo spinner città
        CollectionReference citta = mDatabase.collection("Citta");
        citta.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        String city = (String) d.get("nome");
                        cittaList.add(city);
                    }
                }
            }

        });

        adapter.notifyDataSetChanged();
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerCitta.setAdapter(adapter);


        //--------------------------scelta città e aggiornamento secondo spinner---------------------------------------------------
        spinnerCitta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //elemento della lista selezionato
                String city = cittaList.get(i);
            //    luoghiList.clear();
                citta.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                String city2 = (String) d.get("nome");
                                if (city.equals(city2)) {
                                    cittaselezionata = d.toObject(Citta.class);
                                    //lista = (ArrayList<String>) d.get("luoghi");
                                    lista = cittaselezionata.getLuoghi();

                                    try { //la lista è null
                                        int size = lista.size();
                                        if (size != 0) {
                                            aggiungiLuoghi(lista);
                                            break;
                                        }
                                        else{
                                            luoghiList.clear();
                                        }
                                    }catch (Exception e){

                                    }
                                }
                            }
                            //popolo il secondo spinner
                           // adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, luoghiList);
                            adapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
                            adapter2.notifyDataSetChanged();

                            spinnerLuoghi.setAdapter(adapter2);

                        }

                    }

                });

            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerLuoghi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("luogo selezionato: "+ luoghiList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void aggiungiLuoghi(ArrayList<String> lista) {
        luoghiList.clear();
        for (int i = 0; i < lista.size(); i++) {
            DocumentReference docRef = mDatabase.collection("Luoghi").document(lista.get(i));
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    luoghiList.add(documentSnapshot.getString("nome"));
                }
            });
        }System.out.println("lista a "+luoghiList);
    }
}
