package com.example.myapplication.ui.fragment_cuoco;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.UtilityImage;
import com.example.myapplication.ui.fragment_ricetta.ListaRicette_Fragment;
import com.example.myapplication.ui.fragment_ricetta.Ricetta;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class Fragment_CreaRicetta extends Fragment {
    private String id_cuoco,categoria;
    private EditText edit_nome, edit_descr, edit_ingr, edit_ricetta;
    private FloatingActionButton fatto;
    private final int SELECT_PICTURE = 100;

    private ImageView image_foto;
    private Uri imageUri;
    private String imageString;//Valore da salvare in ricetta
    private int rotazione;
    private StorageReference storage;
    private int rotazioneImg;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.frag_crea_ricetta, parent, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        id_cuoco=FirebaseAuth.getInstance().getUid();
        edit_nome=(EditText)view.findViewById(R.id.edit_nome);
        edit_descr=(EditText)view.findViewById(R.id.edit_descrizione);
        edit_ingr=(EditText)view.findViewById(R.id.edit_ingredienti);
        edit_ricetta=(EditText)view.findViewById(R.id.edit_ricetta);
        fatto=(FloatingActionButton)view.findViewById(R.id.fatto);
        storage = FirebaseStorage.getInstance().getReference();

        image_foto=(ImageView)view.findViewById(R.id.foto_ricetta);
        image_foto.setOnClickListener((view1) -> {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, SELECT_PICTURE);
        });

        List<String> lista=new ArrayList<>();
        lista.add("torte");
        lista.add("ciamb");
        lista.add("crostata");
        lista.add("mousse");
        lista.add("chess");
        lista.add("biscotti");

        Spinner sp=(Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter adapter=new ArrayAdapter(this.getContext(),android.R.layout.simple_spinner_dropdown_item,lista);
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id)  {
                //TextView txt=(TextView) arg1.findViewById(R.id.rowtext);
                //String s=txt.getText().toString();
                categoria= (String) sp.getItemAtPosition(position);
                Log.d("SPINNER",""+categoria);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            { }
        });

        fatto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String ricetta=edit_ricetta.getText().toString();
                String descr=edit_descr.getText().toString();
                String ingr=edit_ingr.getText().toString();
                String nome=edit_nome.getText().toString();
                if(ricetta!=null && descr!=null && ingr!=null && nome!=null) {
                    //la ricetta si trova nel firebase come nomeid_cuoco.jpg
                    Ricetta r=new Ricetta(nome,ingr,id_cuoco,descr,nome+id_cuoco+".jpg",ricetta,categoria);
                    r.setRot(rotazioneImg);
                    aggiungi_inFirestore(r);
                    aggiungi_immagine_storage(nome,id_cuoco);
                    ricaricaFrammento();
                }
                else{
                    Toast.makeText(v.getContext(), "Inserisci tutti i campi!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void aggiungi_immagine_storage(String nome, String id_cuoco) {
        try {

            //se ha scelto un'immagine
            if (imageUri != null) {
                //riferimento allo storage
                StorageReference sRef = storage.child(nome+id_cuoco + "." + "jpg");
                //inserimento dell'immagine
                sRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        } catch (Exception e) {

        }
    }

    public void ricaricaFrammento(){
        // DA VERIFICARE SE SI PUO' FARE DIVERSAMENTE
        FragmentCuoco frag=(FragmentCuoco)getParentFragment();
        frag.changeVisibility();


       ListaRicette_Fragment ricette_fragment=new ListaRicette_Fragment();
        ricette_fragment.ottieni_lista(id_cuoco);
        getFragmentManager().beginTransaction().replace(R.id.frame_cuoco,ricette_fragment).addToBackStack(null).commit();
    }


    public void aggiungi_inFirestore(Ricetta r){
        FirebaseFirestore.getInstance().collection("ricette").document().set(r)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Ricetta condivisa!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Errore condivisione fallita", e);
                    }
                });
    }





    //---------------------------------------METODI PER CATTURARE L'IMMAGINE------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK
                && data != null && data.getData() != null)
            imageUri = data.getData();

        if (imageUri != null) {
            try {
                String imagePath;
                if (data.toString().contains("content:")) {
                    imagePath = getRealPathFromURI(imageUri);
                } else if (data.toString().contains("file:")) {
                    imagePath = imageUri.getPath();
                } else {
                    imagePath = null;
                }

                ExifInterface exifInterface = new ExifInterface(imagePath);
                int rotation = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
                int rotationInDegrees = exifToDegrees(rotation);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                //se l'immagine ha un'orientamento la giro
                rotazioneImg= rotationInDegrees;
                bitmap = rotate(bitmap, rotationInDegrees);
                image_foto.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null,
                    null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            try {
                Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                return bmOut;
            }catch (Exception e){}


        }
        return bm;
    }
}

