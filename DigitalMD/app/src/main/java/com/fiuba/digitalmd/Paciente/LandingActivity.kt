package com.fiuba.digitalmd.Paciente

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fiuba.digitalmd.ElegirUsuarioActivity
import com.fiuba.digitalmd.R
import com.fiuba.digitalmd.RecetasActivity
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        btnDermatologia.setOnClickListener {
            val intent = Intent(this, MisDiagnosticosActivity::class.java)
            startActivity(intent)
        }

        btnRecetas.setOnClickListener {
            val intent = Intent(this, MisRecetasActivity::class.java)
            startActivity(intent)
        }
    }



}