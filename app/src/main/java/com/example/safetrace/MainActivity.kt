package com.example.safetrace

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.safetrace.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainBinding
//
    val permissions = arrayOf(
       Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS

    )
    val permissionCode = 78

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        askForPermission()



        val bottombar = binding.bottomNavBar
        bottombar.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.guard -> {
                    inflateFragment(GuardFragment.newInstance())
                }

                R.id.Home -> {
                    inflateFragment(HomeFragment.newInstance())
                }

                R.id.dashboard -> {
                    inflateFragment(MapsFragment())
                }

                else -> {
                    inflateFragment(ProfileFragment.newInstance())
                }
            }

            true//it simply states that the item is selected

        }
        bottombar.selectedItemId = R.id.Home


        val currentUser = FirebaseAuth.getInstance().currentUser
        val name = currentUser?.displayName.toString()
        val mail = currentUser?.email.toString()
        val phoneNumber = currentUser?.phoneNumber.toString()
        val photoUrl = currentUser?.photoUrl.toString()

        val db = Firebase.firestore

        // Create a new user with a first and last name
        val user = hashMapOf(
            "name" to name,
            "mail" to mail,
            "phone" to phoneNumber,
            "photoUrl" to photoUrl
        )

        db.collection("users").document(mail).set(user).addOnSuccessListener {  }.addOnFailureListener {  }


    }

    private fun askForPermission() {
        ActivityCompat.requestPermissions(this, permissions, permissionCode)
    }


    private fun inflateFragment(newInstance: Fragment) {

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, newInstance)
        transaction.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == permissionCode) {
            if (allPermissionGranted()) {
                openCamera()
            } else {

            }
        }
    }

    private fun openCamera() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        startActivity(intent)
    }

    private fun allPermissionGranted(): Boolean {
        for (item in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    item
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) return false
        }
        return true

    }
}