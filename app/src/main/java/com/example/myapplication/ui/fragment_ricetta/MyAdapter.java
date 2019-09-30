package com.example.myapplication.ui.fragment_ricetta;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Ricetta> mDataset;
//SI OCCUPA DELLA GESTIONE DEI SINGOLI ELEMENTI DELLA LISTA
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nome_ricetta;
        public TextView nome_cuoco;
        public ImageView image;
        public CardView card;
        public String nom,ricetta, info,descr,foto, id_cuoco;
        public int rot;
        public String id_ricetta;
        public AppCompatActivity activity;

        @SuppressLint("RestrictedApi")
        public ViewHolder(View v){
            super(v);

            nome_ricetta= (TextView)v.findViewById(R.id.nome_ricetta);
            nome_cuoco=(TextView)v.findViewById(R.id.nome_cuoco);
            image=(ImageView)v.findViewById(R.id.image_dolce);
//            nome_cuoco.setClickable(false);
           // descrzione=(TextView) v.findViewById(R.id.)
           // img=(LinearLayout) v.findViewById(R.id.id_linearLayout_cardCake);
           // img.setClickable(false);

            card=(CardView) v.findViewById(R.id.id_card__ricetta);
            FloatingActionButton floatingActionButton=(FloatingActionButton)v.findViewById(R.id.fab_search);

           //ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) nome_cuoco.getLayoutParams();
            activity = (AppCompatActivity) v.getContext();
            card.setOnClickListener((view)->{

                Bundle bundle = new Bundle();
                bundle.putString("nome",nom);
                bundle.putString("descr",descr);
                bundle.putString("foto",foto);
                bundle.putString("info",info);
                bundle.putString("id_cuoco",id_cuoco);
                bundle.putString("id",id_ricetta);
                bundle.putString("rot",""+rot);
                FragmentRicetta ricettaFragment = new FragmentRicetta();
                ricettaFragment.setArguments(bundle);

                ricettaFragment.onAttach(v.getContext());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, ricettaFragment).addToBackStack(null).commit();
            });

        }



    }

    public MyAdapter(List<Ricetta> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        StorageReference storage= FirebaseStorage.getInstance().getReference();
        Log.d("myTag", mDataset.toString());

        if(getItemCount()!=0){
            Ricetta tmp=mDataset.get(position);

            holder.descr=tmp.getDescrizione();
            holder.nom=tmp.getNome();
            holder.foto=tmp.getFoto();
            holder.info=tmp.getIngredienti();
            holder.id_cuoco=tmp.getId_cuoco();
            holder.id_ricetta=tmp.getId_ricetta();
            holder.rot=tmp.getRot();



            System.out.println(tmp.getNome());
            holder.nome_ricetta.setText(holder.nom);
            holder.nome_cuoco.setText(tmp.getId_cuoco());

            String immagine= tmp.getFoto();
            /*byte[] immag = Base64.decode(immagine, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(immag, 0, immag.length);
            holder.image.setImageBitmap(bitmap);*/

            caricaImg(tmp.getFoto(), tmp.getRot(),holder);

        }
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void caricaImg(String foto,int rot,ViewHolder holder){
        StorageReference storage=FirebaseStorage.getInstance().getReference();
        if(foto !=null){
            try {
                storage.child(foto).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(holder.activity).load(uri).rotate(rot).fit().centerCrop().into(holder.image);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

