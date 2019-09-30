package com.example.myapplication.ui.fragment_evento;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

public class FragmentDescrEvento extends Fragment {
    private Context context;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.frame_descr_evento, parent, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle bundle= this.getArguments();
        String descr=bundle.getString("descrizione");
        int max=bundle.getInt("num_max");

        TextView descrizione=(TextView)view.findViewById(R.id.descr_evento);
        descrizione.setText(descr);
        TextView num_max=(TextView)view.findViewById(R.id.num_part_max);
        num_max.setText(""+max);
    }

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
}
