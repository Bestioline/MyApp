package com.example.myapplication.ui.fragment_cuoco;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utente;
import com.example.myapplication.ui.fragment_evento.Lista_Fragment_Evento;
import com.example.myapplication.ui.fragment_nuovo_evento.FragmentNuovoEvento;
import com.example.myapplication.ui.fragment_ricetta.ListaRicette_Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentCuoco extends Fragment {
   private String currentId;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_cuoco, container, false);
        //OTTENIAMO LA LISTA DI RICETTE DA LUI FORNITO: RICORDIAMO CHE IN RICETTE E' PRESENTE IL CAMPO
        //ID_CUOCO CHE MANTIENE L'ID DEL CUOCO CHE L'HA CREATO
        ListaRicette_Fragment ricette_fragment=new ListaRicette_Fragment();
        ricette_fragment.ottieni_lista(currentId);
        getChildFragmentManager().beginTransaction().add(R.id.frame_cuoco,ricette_fragment).addToBackStack(null).commit();
        return view;

    }

    private TextView emailCuoco;
    private EditText nomeCuoco;

    private CircleImageView foto_cuoco;
    private Button ricette,eventi;
    private FloatingActionButton add;

    private Cuoco cuoco;
    private FirebaseFirestore db;
    private StorageReference storage=FirebaseStorage.getInstance().getReference();
    private boolean sezione_eventi=false;


    //****per la modifica del profilo
    private FloatingActionButton modificaProfilo;
    private FloatingActionButton modificaFoto;
    private boolean modificaAbilitata;
    private EditText nuovaPassword;
    private EditText vecchiaPassword;
    private Uri imageUri;
    private static final int SELECT_PICTURE = 100;



    @SuppressLint({"ResourceAsColor", "RestrictedApi"})
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nomeCuoco=view.findViewById(R.id.nome_cuoco);
        emailCuoco=(TextView)view.findViewById(R.id.email_cuoco);
        nuovaPassword=(EditText)view.findViewById(R.id.edit_pass_cuoco);
        foto_cuoco=(CircleImageView)view.findViewById(R.id.image_cuoco);
        ricette=(Button)view.findViewById(R.id.button_lista);
        eventi=(Button)view.findViewById(R.id.button_eventi);
        add=(FloatingActionButton)view.findViewById(R.id.add_cuoco);

        //********modifica profilo*****************
        modificaFoto = view.findViewById(R.id.modificaFotoCuoco);
        modificaFoto.setVisibility(View.GONE);
        modificaProfilo = view.findViewById(R.id.modificaCuoco);
        nuovaPassword.setVisibility(View.GONE);
        vecchiaPassword=view.findViewById(R.id.vecchiaPass);
        vecchiaPassword.setVisibility(View.GONE);
        //PRELEVIAMO I DATI APPARTENENTI AL CUOCO E SETTIAMO LE LABEL
        ottieni_dati();

        //OTTENIAMO LA LISTA DI EVENTI A CUI PARTECIPA
        eventi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(sezione_eventi==false) {
                    crea_lista_eventi(currentId);
                    ricette.setClickable(true);
                    eventi.setClickable(false);
                    sezione_eventi = true;
                    modificaProfilo.setVisibility(View.GONE);
                }
            }
        });


        ricette.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(sezione_eventi==true) {
                    getChildFragmentManager().popBackStackImmediate();
                    ricette.setClickable(false);
                    eventi.setClickable(true);
                    sezione_eventi = false;
                    modificaProfilo.setVisibility(View.GONE);
                }
            }
        });

        //SE NON E' IL CUOCO CORRENTE NON PUO' MODIFICARE IL PROPRIO PROFILO
        if(!currentId.equals(FirebaseAuth.getInstance().getUid())){
            add.setVisibility(View.INVISIBLE);
            add.setClickable(false);
            modificaProfilo.setVisibility(View.GONE);
        }

        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                add.setVisibility(View.INVISIBLE);
                if(sezione_eventi==true){
                    aggiungi_evento();
                    modificaProfilo.setVisibility(View.GONE);
                }else {
                    aggiungi_ricetta();
                    modificaProfilo.setVisibility(View.GONE);
                }
            }
        });

        //*******************modifica profilo
        modificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!modificaAbilitata){
                    abilitaModifica();
                }else {
                    aggiornaDati();
                }
            }
        });

        modificaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });


    }

    @SuppressLint("RestrictedApi")
    private void abilitaModifica() {
        modificaFoto.setVisibility(View.VISIBLE);
        ricette.setVisibility(View.GONE);
        eventi.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
        modificaProfilo.setImageResource(R.drawable.modifica_abilitata);
        nomeCuoco.setEnabled(true);
        nuovaPassword.setEnabled(true);
        vecchiaPassword.setEnabled(true);
        nuovaPassword.setVisibility(View.VISIBLE);
        vecchiaPassword.setVisibility(View.VISIBLE);
        modificaAbilitata=true;

    }

    @SuppressLint("RestrictedApi")
    private void aggiornaDati(){
        String nome,pass1,pass2;
        nome=nomeCuoco.getText().toString();
        pass1=vecchiaPassword.getText().toString();
        pass2=nuovaPassword.getText().toString();
        modificaProfilo.setImageResource(R.drawable.modifica);
        nomeCuoco.setEnabled(false);
        nuovaPassword.setEnabled(false);
        vecchiaPassword.setEnabled(false);
        modificaFoto.setVisibility(View.GONE);
        nuovaPassword.setVisibility(View.GONE);
        vecchiaPassword.setVisibility(View.GONE);
        ricette.setVisibility(View.VISIBLE);
        eventi.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        vecchiaPassword.setText("");
        nuovaPassword.setText("");
        try {
            //se ha scelto un'immagine
            if (imageUri != null) {
                //riferimento allo storage
                StorageReference sRef = storage.child(cuoco.getEmail() + "." + "jpg");
                //inserimento dell'immagine
                sRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            }
                        });
            }
            if( !"".equals(pass1) ){
                if(pass1.equals(cuoco.getPassword()) && !pass2.equals("")
                        && pass2.length()>=6
                        && !pass1.equals(pass2)){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(cuoco.getEmail(), cuoco.getPassword());
                    FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseAuth.getInstance().getCurrentUser().updatePassword(pass2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                } else {
                                                    CharSequence text = "Inserisci correttamente la tua vecchia  password!";
                                                    int duration = Toast.LENGTH_SHORT;
                                                    Toast.makeText(getContext(),text, duration).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                    cuoco.setPassword(pass2);
                } else{
                    CharSequence text = "Inserisci correttamente le password, ricorda che " +
                            "la nuova password deve avere almeno 6 caratteri!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(getContext(),text, duration).show();
                }
            }
            //salvataggio delle informazioni dell'utente
            Cuoco up = new Cuoco(nome,"",cuoco.getPassword(),cuoco.getEmail(),cuoco.getEmail()+".jpg",cuoco.getRot());
            db.collection("utenti2").document(currentId).set(up);
            modificaAbilitata=false;


        }catch (Exception e){

        }



    }


    private void aggiungi_evento(){
        FragmentNuovoEvento fragmentNuovoEvento = new FragmentNuovoEvento();
        getChildFragmentManager().beginTransaction().replace(R.id.frame_cuoco,fragmentNuovoEvento).addToBackStack(null).commit();
    }


    private void crea_lista_eventi(String currentId) {
        Lista_Fragment_Evento fragment_evento= new Lista_Fragment_Evento();
        fragment_evento.doSomething(currentId);
        getChildFragmentManager().beginTransaction().replace(R.id.frame_cuoco,fragment_evento).addToBackStack(null).commit();

    }

    public void aggiungi_ricetta(){
        Fragment_CreaRicetta fragment_creaRicetta=new Fragment_CreaRicetta();
        getChildFragmentManager().beginTransaction().replace(R.id.frame_cuoco,fragment_creaRicetta).addToBackStack("FRAG_CUOCO").commit();
    }

    @SuppressLint("RestrictedApi")
    public void changeVisibility(){
        add.setVisibility(View.VISIBLE);
    }

    private void ottieni_dati() {
        db= FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("utenti2").document("" + currentId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                cuoco = documentSnapshot.toObject(Cuoco.class);
                nomeCuoco.setText(cuoco.getNome());
                emailCuoco.setText(cuoco.getEmail());

                if(currentId.equals(FirebaseAuth.getInstance().getUid()))
                   // password.setText(cuoco.getPassword());
              //  else
                    //password.setVisibility(View.INVISIBLE);
                    if(cuoco.getImageProf() !=null){
                    try {
                        storage.child(cuoco.getEmail()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(getActivity()).load(uri).rotate(cuoco.getRot()).fit().centerCrop().into(foto_cuoco);
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
    public void onAttach(Context context) {

        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void doSomething(String currentID) {
        this.currentId=currentID;
    }

    //metodo per scegliere l'immagine dalla galleria
    private void chooseImage() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK
                && data != null && data.getData() != null )
            imageUri = data.getData();
        cuoco.setImageProf(cuoco.getEmail()+".jpg");
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
                //se l'immagine ha un'orientazione la giro
                cuoco.setRot(rotationInDegrees);
                bitmap=rotate(bitmap,rotationInDegrees);
                foto_cuoco.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            System.out.println("rota");
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
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
