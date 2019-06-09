package com.fiuba.digitalmd.ObraSocial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

import com.fiuba.digitalmd.Medico.Vaci
import com.fiuba.digitalmd.Models.InfoActual
import com.fiuba.digitalmd.Models.ObraSocial
import com.fiuba.digitalmd.Models.Receta
import com.fiuba.digitalmd.Paciente.ItemReceta

import com.fiuba.digitalmd.R
import com.fiuba.digitalmd.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_obra_social_landing.*


class ObraSocialLandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obra_social_landing)
        val toolbar: Toolbar = findViewById(R.id.toolbarProfile)
        setSupportActionBar(toolbar)
        leerUsuarioDeFirebase()


    }

    override fun onBackPressed() {
        startActivity(Intent(baseContext, SignInActivity::class.java))
    }

    private fun leerUsuarioDeFirebase() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/signup/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                val obraSocial = p0.getValue(ObraSocial::class.java)
                InfoActual.setObraSocialActual(obraSocial!!)
                cargarRecetasDeFirebase()
                //Toast.makeText(baseContext, "Obra Social leido", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun cargarRecetasDeFirebase() {
        val adapter = GroupAdapter<ViewHolder>()
        val database = FirebaseDatabase.getInstance().getReference("/recetas/${InfoActual.getObraSocialActual().nombre}")

        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (!p0.exists()) {
                    adapter.add(Vaci())
                    rvMisRecetasObraSocial.adapter = adapter

                } else {

                    p0.children.forEach {
                        val receta = it.getValue(Receta::class.java)
                        adapter.add(ItemReceta(receta))
                    }

                    rvMisRecetasObraSocial.adapter = adapter
                }
            }
        })
    }




}