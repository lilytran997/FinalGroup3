package com.example.finalgroup3

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.finalgroup3.model.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_info.*

@Suppress("PrivatePropertyName", "LocalVariableName")
class EditInfoActivity : AppCompatActivity() {
    private lateinit var UserDatabase: DatabaseReference
    private lateinit var StorageImage: StorageReference
    private val GALLERY_PICK : Int = 1
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_info)
        setSupportActionBar(this.findViewById(R.id.toolbar))
        supportActionBar!!.title = "Edit Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        //get Data from FirebaseDatabase
        val current = FirebaseAuth.getInstance().currentUser
        val currentId = current!!.uid
        UserDatabase = FirebaseDatabase.getInstance().getReference("Users")
        StorageImage = FirebaseStorage.getInstance().reference
        UserDatabase.child(currentId).addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(snap: DataSnapshot) {
                val User : Users = snap.getValue(Users::class.java)!!
                if(User.ImageURL == "default"){
                    editImage.setImageResource(R.drawable.user)
                }else{
                    Glide.with(applicationContext)
                        .load(User.ImageURL)
                        .centerCrop()
                        .into(editImage)
                }
                my_name.setText(User.username)
                editStatus.setText(User.status)
            }
        })
        //delete name
        change_name.setOnClickListener {
            my_name.setText("")
            change_name.visibility = View.GONE
            Edname.visibility = View.VISIBLE
        }
        //delete status
        change_status.setOnClickListener {
            editStatus.setText("")
            change_status.visibility = View.GONE
            Edstatus.visibility = View.VISIBLE
        }
        // update avatar
        change_image.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"), GALLERY_PICK)
        }

        // saved change
        upInfo.setOnClickListener {
            val txt_name = my_name.text.toString()
            val txt_status = editStatus.text.toString()
            updateName(txt_name,txt_status)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val current = auth.currentUser
        val currentId = current!!.uid
        if(requestCode==GALLERY_PICK && resultCode== Activity.RESULT_OK){

            val imageUri: Uri? = data?.data
            CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .start(this)
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result:  CropImage.ActivityResult = CropImage.getActivityResult(data)
            if(resultCode == RESULT_OK){
                val resultUri : Uri = result.uri
                //  upload file and get a download URL
                val  filepath: StorageReference = StorageImage.child("profile_image").child("$currentId.jpg")
                val uploadTask: UploadTask = filepath.putFile(resultUri)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation filepath.downloadUrl
                }).addOnCompleteListener {task ->
                    // write link image to ImageURL children of node currentId
                    if(task.isSuccessful){
                        val download_Uri : Uri? = task.result
                        val downloadUrl: String = download_Uri.toString()
                        val userDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(currentId)
                        userDatabase.child("ImageURL").setValue(downloadUrl).addOnCompleteListener(this){tasks ->
                            if(tasks.isSuccessful){
                                Toast.makeText(this@EditInfoActivity, "Avatar was updated successfully", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else{
                        Toast.makeText(this@EditInfoActivity, "there are some error in changing image", Toast.LENGTH_SHORT).show()
                    }

                }
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
               // val error: Exception = result.error
            }
        }
    }
    //Update data
    private fun updateName(txt_name: String, txt_status: String) {
        val userReferece: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val current = auth.currentUser
        val currentId = current!!.uid
        userReferece.child(currentId).child("username").setValue(txt_name)
            .addOnCompleteListener{
                userReferece.child(currentId).child("status").setValue(txt_status)
                    .addOnSuccessListener {
                        Toast.makeText(this@EditInfoActivity, "Informations were updated Successfully ", Toast.LENGTH_SHORT).show()
                        val intentP = Intent(this@EditInfoActivity,ProfileActivity::class.java)
                        startActivity(intentP)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@EditInfoActivity, "There was some error in updating informations. ", Toast.LENGTH_SHORT).show()
                    }
        }.addOnFailureListener {
                Toast.makeText(this@EditInfoActivity, "There was some error in updating informations. ", Toast.LENGTH_SHORT).show()
            }
    }
}
