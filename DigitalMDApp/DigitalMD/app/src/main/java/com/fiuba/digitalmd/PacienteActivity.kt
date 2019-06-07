package com.fiuba.digitalmd

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.provider.MediaStore

import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_paciente.*
import kotlinx.android.synthetic.main.activity_signin_user.*
import java.util.jar.Manifest

class PacienteActivity : AppCompatActivity() {
    var uri: Uri? = null
    var photoUri: Uri? = null
    var image_uri: Uri? = null
    private val Image_CODE: Int = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente)

        btnSacarFoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    //permission was not enable
                    val permission =
                        arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, 1000)
                } else {
                    openCamera()
                }
            } else {
                openCamera()
            }

        }
        btnSubirFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        btnCConfirmarConsulta.setOnClickListener {
            subirDatosAFirebase()
            val intent = Intent(this, DermatologiaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun subirDatosAFirebase() {
        if (etName.text.toString().isEmpty()) {
            etName.error = "Por favor ingresa tu nombre"
            etName.requestFocus()
        }

        val name = etName.text.toString()
        val uid = FirebaseAuth.getInstance().uid + name

        val storage = FirebaseStorage.getInstance().getReference("/images/dermatologia/$uid")

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating user, just wait")
        progressDialog.show()
        storage.putFile(uri!!)
            .addOnSuccessListener { taskSnapshot ->
                storage.downloadUrl.addOnCompleteListener { taskSnapshot ->
                    var url = taskSnapshot.result
                    createUser(url.toString(), name)
                    Log.d("ProfileAcitivity", "Image added to firebase: ${url.toString()}")
                }
            }
            .addOnProgressListener { taskSnapShot ->
                val progress = 100 * taskSnapShot.bytesTransferred / taskSnapShot.totalByteCount
                progressDialog.setMessage("% ${progress}")
            }
    }

    private fun createUser(url: String, name: String) {
        val uid = FirebaseAuth.getInstance().uid + name
        val database = FirebaseDatabase.getInstance().getReference("/users/$uid")
        var user = User(name,url)
        database.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User added to database")
            }
    }


    private fun openCamera() {
        val values = ContentValues()
        values.put (MediaStore.Images.Media.TITLE, "Nueva Foto")
        values.put (MediaStore.Images.Media.DESCRIPTION, "foto de la camara")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent,Image_CODE)

    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1000 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }
                else {
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }

        }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            CircleImageView.setImageBitmap(bitmap)
            uri = photoUri

        }

        if (resultCode == Activity.RESULT_OK && requestCode == 1001 ) {
            //image_uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, image_uri)
            uri = image_uri
            CircleImageView.setImageBitmap(bitmap)
        }
    }


}