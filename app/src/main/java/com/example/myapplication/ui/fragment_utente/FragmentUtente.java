package com.example.myapplication.ui.fragment_utente;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Utente;
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

public class FragmentUtente extends Fragment {
    private String currentId;
    private static final int SELECT_PICTURE = 100;
    private TextView nickname;
    //campi del profilo
    private EditText nomeUtente;
    private EditText biografia;
    private EditText mail;
    private EditText nuova_password;
    private EditText vecchia_password;
    private EditText telefono;
    private EditText password;
    //immagine del profilo
    private CircleImageView img;
    //bottoni
    private FloatingActionButton modificaFoto;
    private  FloatingActionButton modificaProfilo;
    //id user correntemente loggato
    private String currentUsermail;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Utente utente;
    private   String  nome, nick, bio, pass,tel;

    //scegliere l'immagine
    private Uri imageUri;
    private StorageReference storage;
    private boolean modifica_abilitata=false;

    private String id;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_utente, parent, false);

    }

    @SuppressLint({"ResourceAsColor", "RestrictedApi"})
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //--------------------------COMPONENTI GRAFICHE--------------------------------------
        nomeUtente=(EditText)view.findViewById(R.id.nomeCompleto);
        nickname= (TextView) view.findViewById(R.id.nick);
        password=(EditText) view.findViewById(R.id.password);
        telefono=(EditText) view.findViewById(R.id.telefono);
        vecchia_password=(EditText) view.findViewById(R.id.vecchia_password);
        nuova_password=(EditText) view.findViewById(R.id.nuova_password);
        nuova_password.setText("");
        vecchia_password.setText("");
        mail=(EditText) view.findViewById(R.id.mail);
        biografia=(EditText) view.findViewById(R.id.bio);
        img= (CircleImageView) view.findViewById(R.id.imageMenu);
        //------------------------------------------------------------------------------------
        //-----------------------BOTTONI-----------------------------------------------------
        modificaFoto=(FloatingActionButton) view.findViewById(R.id.modificaFoto);
        modificaFoto.setVisibility(View.GONE);
        modificaProfilo= (FloatingActionButton) view.findViewById(R.id.modificaProfilo);
        if(!currentId.equals(FirebaseAuth.getInstance().getUid())){ modificaProfilo.setVisibility(View.GONE);}
        //----------------------TROVA UTENTE----------------------------------------------------
        db= FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("utenti2").document("" + currentId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                utente = documentSnapshot.toObject(Utente.class);
                costruisciProfilo(utente);
            }
        });

        //riferimento allo storage
        storage = FirebaseStorage.getInstance().getReference();
        //--------------------BOTTONE MODIFICA FOTO-----------------------------------
        modificaFoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                chooseImage();
            }
        });

        //-------------MODIFICA PROFILO--------------------------------------------------------------
        modificaProfilo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                //se non è in modalità di modifica
                if(modifica_abilitata==false) {
                    nuova_password.setText("");
                    vecchia_password.setText("");
                    modificaFoto.setVisibility(View.VISIBLE);
                    vecchia_password.setVisibility(View.VISIBLE);
                    nuova_password.setVisibility(View.VISIBLE);
                    modificaProfilo.setImageResource(R.drawable.modifica_abilitata);
                    nomeUtente.setEnabled(true);
                    biografia.setEnabled(true);
                    telefono.setEnabled(true);
                    //se clicca sul bottone
                    modifica_abilitata=true;

                }else{
                    nome = nomeUtente.getText().toString();
                    bio= biografia.getText().toString();
                    tel = telefono.getText().toString();

                    modificaProfilo.setImageResource(R.drawable.modifica);
                    nick = nomeUtente.getText().toString();
                    nickname.setText(nick);
                    nomeUtente.setEnabled(false);
                    biografia.setEnabled(false);
                    telefono.setEnabled(false);
                    modificaFoto.setVisibility(View.GONE);

                    //quando riclicca sul bottone aggiorno le informazioni dell'utente
                    try {
                        //se ha scelto un'immagine
                        if (imageUri != null) {
                            //riferimento allo storage
                            StorageReference sRef = storage.child(currentUsermail+ "." + "jpg");
                            //inserimento dell'immagine
                            sRef.putFile(imageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                        if( !"".equals(vecchia_password.getText().toString()) ){
                            System.out.println(vecchia_password.getText()+"VECCHIA");
                            if(vecchia_password.getText().toString().equals(pass) && !nuova_password.getText().equals("")
                                    && nuova_password.getText().length()>=6
                                    && !vecchia_password.getText().equals(nuova_password.getText())){
                                String nuova_pass=nuova_password.getText().toString();

                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(currentUsermail, utente.getPassword());
                                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(nuova_pass).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                password.setText(nuova_pass);
                                utente.setPassword(nuova_pass);
                            } else{
                                CharSequence text = "Inserisci correttamente le password, ricorda che " +
                                        "la nuova password deve avere almeno 6 caratteri!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast.makeText(getContext(),text, duration).show();
                            }
                        }
                        //salvataggio delle informazioni dell'utente
                        Utente up = new Utente(nome,utente.getEmail(),nick,bio,tel, utente.getPassword(),currentUsermail+".jpg",utente.getRot());
                        db.collection("utenti2").document(currentId).set(up);

                        nuova_password.setText("");
                        vecchia_password.setText("");
                        //nuova_password.setVisibility(View.INVISIBLE);
                        //vecchia_password.setVisibility(View.INVISIBLE);
                       // modifica_abilitata=false;

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    nuova_password.setVisibility(View.INVISIBLE);
                    vecchia_password.setVisibility(View.INVISIBLE);
                    modifica_abilitata=false;
                }
            }
        });
    }


    public void doSomething(String currentID) {

        this.currentId=currentID;

    }
    public void costruisciProfilo(Utente utente) {
        nome=utente.getNome();
        nick=utente.getNick();
        currentUsermail=utente.getEmail();//DA VERIFICARE
        //se il nome o il nick sono null vengono impostati come l'inizio della mail
        if(nome==""){
            nome=currentUsermail.substring(0,currentUsermail.indexOf("@"));
        }
        if(nick==""){
            nick=currentUsermail.substring(0,currentUsermail.indexOf("@"));
        }

        pass=utente.getPassword();
        bio=utente.getBio();
        tel=utente.getTel();

        //--------------SETTARE I DATI NELLA COMPONENTE GRAFICA---------------------------------------

        nomeUtente.setText(nome);
        password.setText(pass);
        if(tel!=null) telefono.setText(tel);
        mail.setText(currentUsermail);
        if(bio!=null) biografia.setText(bio);
        nickname.setText(nick);

        //--------------------CARICA L'IMMAGINE NELLA VIEW------------------------------------------
        if(utente.getImageProf() !=null){
            try {
                storage.child(currentUsermail+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getActivity()).load(uri).rotate(utente.getRot()).fit().centerCrop().into(img);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //------------------LABEL NON MODIFICABILI---------------------------------------------------
        nomeUtente.setEnabled(false);
        biografia.setEnabled(false);
        telefono.setEnabled(false);
        mail.setEnabled(false);
        password.setEnabled(false);
    }
    private void cambiaPassword(String nuova_pass) {
        password.setText(nuova_pass);
        utente.setPassword(nuova_pass);
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
        utente.setImageProf(currentUsermail+".jpg");
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
                utente.setRot(rotationInDegrees);
                bitmap=rotate(bitmap,rotationInDegrees);
                img.setImageBitmap(bitmap);

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
