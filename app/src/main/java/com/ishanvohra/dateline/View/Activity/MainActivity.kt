package com.ishanvohra.dateline.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ishanvohra.dateline.View.fragments.SwipeFragment
import com.ishanvohra.dateline.R
import com.ishanvohra.dateline.Model.User
import com.ishanvohra.dateline.View.fragments.MatcheFragment
import com.ishanvohra.dateline.ViewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.profile_view_sheet.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private val PICK_IMAGE_REQUEST = 71
    private lateinit var imageURI: String
    private var filePath: Uri? = null
    private lateinit var profilePic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomSheetBehavior = BottomSheetBehavior.from(profile_sheet)

        val bottomNavigation: BottomNavigationView = bottom_navigation

        loadFragment(SwipeFragment.newInstance())

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.double_take_item -> {
                    loadFragment(SwipeFragment.newInstance())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.matches_item -> {
                    loadFragment(MatcheFragment.newInstance())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile_item -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    loadProfileData(profile_sheet)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun loadProfileData(view: View){
        val nameEt : TextInputEditText = view.findViewById(R.id.name_et)
        val ageEt : TextInputEditText = view.findViewById(R.id.age_et)
        val saveBtn : Button = view.findViewById(R.id.save_btn)
        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        val femaleCheckBox: CheckBox = view.findViewById(R.id.preferred_radio_button_female)
        val maleCheckBox: CheckBox = view.findViewById(R.id.preferred_radio_button_male)
        val otherCheckBox: CheckBox = view.findViewById(R.id.preferred_radio_button_other)
        val logOutBtn: Button = view.findViewById(R.id.logout_btn)

        profilePic = view.findViewById(R.id.profile_pic)
        var interestedList = ArrayList<String>()

        var checkedGender = "Female"

        val viewModel: ProfileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        databaseReference.child("Users").child(userId).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                nameEt.setText(user!!.name)
                ageEt.setText(user.age)
                checkedGender = user.gender.toString()
                interestedList = user.preferredGender!!

                when {
                    user.gender.equals("Male") -> {
                        view.findViewById<RadioButton>(R.id.radio_button_male).isChecked = true
                    }
                    user.gender.equals("Female") -> {
                        view.findViewById<RadioButton>(R.id.radio_button_female).isChecked = true
                    }
                    else -> {
                        view.findViewById<RadioButton>(R.id.radio_button_other).isChecked = true
                    }
                }

                when{
                    user.preferredGender!!.contains("Male") ->{
                        maleCheckBox.isChecked = true
                    }
                    user.preferredGender.contains("Female") ->{
                        femaleCheckBox.isChecked = true
                    }
                    else -> {
                        otherCheckBox.isChecked = true
                    }
                }

                Glide.with(view)
                    .load(user.imageUrl)
                    .into(profilePic)
            }
        })

        radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            val radio: RadioButton = findViewById(checkedId)

            if(radio.isChecked)
                checkedGender = radio.text as String
        }

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

            databaseReference.child("Users").child(userId).child("name").setValue(name)
            databaseReference.child("Users").child(userId).child("age").setValue(age)
            databaseReference.child("Users").child(userId).child("gender").setValue(checkedGender)
            databaseReference.child("Users").child(userId).child("preferredGender").setValue(interestedList)
        }

        logOutBtn.setOnClickListener {
            viewModel.logoutUser()
            finish()
        }

        profilePic.setOnClickListener {
            launchGallery()
        }
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
            val ref = storageReference?.child("uploads/"+FirebaseAuth.getInstance().currentUser?.uid + "/profile")
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

                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                        .child("imageUrl")
                        .setValue(imageURI)
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }
    }
}
