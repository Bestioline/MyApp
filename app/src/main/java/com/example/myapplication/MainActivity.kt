package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

import com.example.myapplication.ui.fragment_cuoco.Cuoco
import com.example.myapplication.ui.home_page.HomePage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_login.*


class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    //image view di nav_header_main.xml
    private lateinit var imageMenu: CircleImageView

    //riferimento all'utente loggato
    private lateinit var mAuth : FirebaseAuth;

    private lateinit var firestore : FirebaseFirestore

    private  var utente : Utente? = Utente()

    private var cuoco: Cuoco? = Cuoco()

    private lateinit var mailMenu : TextView

    private lateinit var nomeMenu: TextView
    private lateinit var storage: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        cuoco=null
        utente=null
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance().reference
        //per collegare all'image view il metodo del login
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val hview = navigationView.getHeaderView(0)
        mailMenu = hview.findViewById(R.id.mailMenu) as TextView
        nomeMenu = hview.findViewById(R.id.nomeMenu) as TextView
        imageMenu = hview.findViewById(R.id.imageMenu) as CircleImageView


        if(mAuth.currentUser!=null) {
            val docRef = firestore.collection("utenti2").document("" + mAuth.uid)

            docRef.get().addOnSuccessListener { documentSnapshot ->
               if(documentSnapshot.get("bio")!=null){

                   // if(prova(documentSnapshot)) {
                            utente = documentSnapshot.toObject(Utente::class.java)!!
                            if (utente!!.imageProf != null) {
                                storage.child(utente!!.email + ".jpg").getDownloadUrl()
                                    .addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                                        //if (utente.rot)
                                        Picasso.with(this@MainActivity).load(uri)
                                            .rotate(utente!!.rot.toFloat()).fit().centerCrop()
                                            .into(imageMenu)
                                    })
                            }
                            mailMenu.setText(utente!!.email)
                            nomeMenu.setText(utente!!.nome)
                    }
               else{
                    cuoco=documentSnapshot.toObject(Cuoco::class.java)!!
                    System.out.println(" cuoco  "+cuoco);
                    if (cuoco!!.imageProf != null) {
                        storage.child(cuoco!!.email + ".jpg").getDownloadUrl()
                            .addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                                //if (utente.rot)
                                Picasso.with(this@MainActivity).load(uri)
                                    .rotate(cuoco!!.rot.toFloat()).fit().centerCrop()
                                    .into(imageMenu)
                            })
                    }
                    mailMenu.setText(cuoco!!.email)
                    nomeMenu.setText(cuoco!!.nome)
               }
            }
        }

        //cliccando sull'image view si apre l'activity login
        imageMenu.setOnClickListener(View.OnClickListener {

            if(mAuth.currentUser!=null){
                val docRef = firestore.collection("utenti2").document("" + mAuth.uid)

                docRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.get("bio") != null)
                        vai_profilo("utente")
                    else
                        vai_profilo("cuoco")
                }
            }
        })


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun vai_profilo(s: String) {
        val i = Intent(this@MainActivity,ProfiloActivity::class.java)
        i.putExtra("tipo", "login")
        i.putExtra("utente", "")
        i.putExtra("tipo_utente", s)
        startActivity(i)
    }


    //i tre puntini al lato aprono un menu con logout ed elimina profilo
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.impostazione_menu, menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        if(id==R.id.esci && mAuth.currentUser!=null) {
            FirebaseAuth.getInstance().signOut();

            val i = Intent(this@MainActivity, HomePage::class.java)
            startActivity(i)
            Toast.makeText(
                applicationContext, "Logout avvenuto con successo", Toast.LENGTH_LONG).show()
            return true;
        }
        if(id==R.id.elimina_account && mAuth.currentUser!=null) {

            val pass :String = if(cuoco!=null) cuoco!!.password
            else utente!!.password

            val user =mAuth.currentUser

            val credential = EmailAuthProvider
                .getCredential(user?.email.toString(),pass )
            val uid = mAuth.uid
            // Prompt the user to re-provide their sign-in credentials
            user!!.reauthenticate(credential)
                .addOnCompleteListener {
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                System.out.println("ID="+uid)
                                val docRef = firestore.collection("utenti2").document(""+uid).delete().addOnCompleteListener {

                                        val i = Intent(this@MainActivity, HomePage::class.java)
                                        startActivity(i)
                                        Toast.makeText(
                                            applicationContext, "Eliminazione avvenuta", Toast.LENGTH_LONG).show()
                                    }.addOnFailureListener {

                                    }
                            }
                        }

                }

            return true
        }

        return super.onOptionsItemSelected(item);
    }
}
