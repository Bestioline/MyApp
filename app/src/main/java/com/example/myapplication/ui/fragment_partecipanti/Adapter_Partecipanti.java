package com.example.myapplication.ui.fragment_partecipanti;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Partecipanti extends RecyclerView.Adapter <Adapter_Partecipanti.ViewHolder> {
    private List<String> partList;
    private FirebaseFirestore ff=FirebaseFirestore.getInstance();
    private AppCompatActivity activity;

    public Adapter_Partecipanti(List<String> list){
        partList=list;
    }


    @Override
    public Adapter_Partecipanti.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partecipanti, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(getItemCount()!=0){
            String id_utente=partList.get(position);
            holder.pos.setText(position+1+".");
            crea_item(holder,id_utente);
        }
    }



    public void crea_item(ViewHolder holder, String id_utente){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        DocumentReference docRef = db.collection("utenti2").document("" + id_utente);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Utente utente = documentSnapshot.toObject(Utente.class);
                //SETTA NOME UTENTE
                holder.nome_utente.setText(utente.getNome());
                // SETTA IMMAGINE DELL'UTENTE
                if(utente.getImageProf() !=null){
                    try {
                        storage.child(utente.getEmail()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                activity = (AppCompatActivity) holder.mView.getContext();
                               Picasso.with(activity).load(uri).rotate(utente.getRot()).fit().centerCrop().into(holder.foto_utente);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return partList.size();
    }

    //-----------------------------------------------VIEW HOLDER------------------------------------

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private String  id_utente="";
        private final CircleImageView foto_utente;
        private final TextView nome_utente;
        private final TextView pos;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

            nome_utente=(TextView)itemView.findViewById(R.id.nome_partec);
            foto_utente=(CircleImageView) itemView.findViewById(R.id.img_partec);
            pos=(TextView) itemView.findViewById(R.id.posizione);
        }
    }
}
