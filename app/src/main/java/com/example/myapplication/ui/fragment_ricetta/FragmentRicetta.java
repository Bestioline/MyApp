package com.example.myapplication.ui.fragment_ricetta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.ui.fragment_commenti.ItemCommentoFragment;

public class FragmentRicetta extends Fragment {
    private String id,nome,ricetta,descr,foto,info,id_cuoco;
    private boolean isPref=false;
    private Context mContext;
    private boolean sezione_commenti=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view= inflater.inflate(R.layout.fragment_home_ricetta, parent, false);
        Bundle bundle = this.getArguments();
        if(bundle != null){
           id_cuoco=bundle.get("id_cuoco").toString();
           id = bundle.get("id").toString();
           nome=bundle.get("nome").toString();
            //ricetta=bundle.get("ricetta").toString();
           descr=bundle.get("descr").toString();
           info=bundle.get("info").toString();
           foto=bundle.get("foto").toString();
        }
        bundle.putString("descr",descr);
        bundle.putString("info",info);
        bundle.putString("id_ricetta",id);
        FragmentDescrizione descrizioneFragment = new FragmentDescrizione();
        descrizioneFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().add(R.id.id_frame_layout,descrizioneFragment).addToBackStack(null).commit();

        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //VA AGGIUNTO TASTO PREFERITI
        TextView textView=(TextView) view.findViewById(R.id.tNomeRicetta);
        textView.setText(nome);

        TextView textDesc=(TextView) view.findViewById(R.id.tDescrizione);
        textDesc.setText(descr);

        ImageView img= (ImageView) view.findViewById(R.id.imageHomeRicetta);

        byte[] immag = Base64.decode(foto, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(immag, 0, immag.length);
        img.setImageBitmap(bitmap);

        Button commenti= (Button)view.findViewById(R.id.button_commenti);

        commenti.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(sezione_commenti==false) {
                    //ENTRA IN GIOCO IL FRAMMENTO DEI COMMENTI
                    ItemCommentoFragment fragment_commenti = new ItemCommentoFragment();
                    //IL METODO doSomething(String value) E' USATO PER PRENDERE SOLO I COMMENTI
                    // DI QUELLA RICETTA
                    fragment_commenti.doSomething(id);
                    FragmentManager manager = getChildFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.id_frame_layout, fragment_commenti);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    sezione_commenti = true;
                }
            }
        });

        Button descrizione_button= (Button)view.findViewById(R.id.button_descr);

        descrizione_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(sezione_commenti==true) {
                    getChildFragmentManager().popBackStackImmediate();
                    sezione_commenti = false;
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
