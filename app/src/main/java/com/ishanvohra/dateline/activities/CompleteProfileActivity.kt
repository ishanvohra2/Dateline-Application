package com.ishanvohra.dateline.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.core.view.get
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ishanvohra.dateline.R
import kotlinx.android.synthetic.main.activity_complete_profile.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class CompleteProfileActivity : AppCompatActivity() {

    private lateinit var ageEt: TextInputEditText
    private lateinit var nameEt: TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var saveBtn: Button
    private lateinit var femaleCheckBox: CheckBox
    private lateinit var maleCheckBox: CheckBox
    private lateinit var otherCheckBox: CheckBox
    private lateinit var profilePic: ImageView

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private val PICK_IMAGE_REQUEST = 71
    private lateinit var imageURI: String
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        ageEt = age_et
        nameEt = name_et
        genderRadioGroup = radioGroup
        saveBtn = save_btn
        femaleCheckBox = preferred_radio_button_female
        maleCheckBox = preferred_radio_button_male
        otherCheckBox = preferred_radio_button_other
        profilePic = profile_pic

        imageURI = ""

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        //Choosing user's gender
        var checkedGender = "Female"

        radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            val radio: RadioButton = findViewById(checkedId)

            if(radio.isChecked)
                checkedGender = radio.text as String

            Toast.makeText(this@CompleteProfileActivity, checkedGender, Toast.LENGTH_SHORT).show()
        }

        //Choosing Profile Image
        profilePic.setOnClickListener {
            launchGallery()
        }

        //Choosing interested in
        var interestedList = ArrayList<String>()

        femaleCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
                interestedList.add("Female")
            else
                interestedList.remove("Female")
        }

        maleCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if(b)
                interestedList.add("Male")
            else
                interestedList.remove("Male")
        }

        maleCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            if(b)
                interestedList.add("Other")
            else
                interestedList.remove("Other")
        }

        //Saving data
        saveBtn.setOnClickListener {
            val name = nameEt.text.toString()
            val age = ageEt.text.toString()

            if (name.isEmpty()){
                nameEt.setError("Enter Full name")
                return@setOnClickListener
            }

            if (age.isEmpty()){
                ageEt.setError("Enter age")
                return@setOnClickListener
            }

            if(interestedList.isEmpty()){
                Snackbar.make(it, "Select the gender(s) you're interested in", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(imageURI.isEmpty()){
                Snackbar.make(it, "Select a profile picture", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            databaseReference.child("Users").child(auth.currentUser?.uid ?: "").child("name")
                .setValue(name)

            databaseReference.child("Users").child(auth.currentUser?.uid ?: "").child("age")
                .setValue(age)

            databaseReference.child("Users").child(auth.currentUser?.uid ?: "").child("gender")
                .setValue(checkedGender)

            databaseReference.child("Users").child(auth.currentUser?.uid ?: "").child("preferredGender")
                .setValue(interestedList)

            startActivity(MainActivity.newIntent(this@CompleteProfileActivity))
        }

    }

    companion object{
        fun newIntent(context: Context) = Intent(context, CompleteProfileActivity::class.java)
    }

    private fun launchGallery(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                profilePic.setImageBitmap(bitmap)
                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("uploads/"+auth.currentUser?.uid + "/profile")
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    imageURI = downloadUri.toString()
                    databaseReference.child("Users").child(auth.currentUser?.uid ?: "").child("imageUrl")
                        .setValue(imageURI)
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }
    }
}