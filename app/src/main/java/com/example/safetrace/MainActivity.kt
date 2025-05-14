package com.example.safetrace

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.safetrace.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
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




        if (isAllPermissionsGranted()) {
          if(isLocationEnabled(this)){
              setUpLocationListener()
          }else{
              showGPSNotEnabledDialog(this)
          }
        }else {
            askForPermission()
        }


        val bottombar = binding.bottomNavBar
        bottombar.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.guard -> {
                    inflateFragment(guardFragment.newInstance())
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

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        Log.d("Location89", "onLocationResult: latitude ${location.latitude}")
                        Log.d("Location89", "onLocationResult: longitude ${location.longitude}")

                        val currentUser = FirebaseAuth.getInstance().currentUser

                        val mail = currentUser?.email.toString()


                        val db = Firebase.firestore

                        // Create a new user with a first and last name
                        val user = mutableMapOf<String,Any>(
                           "latitude" to location.latitude.toString(),
                            "longitude" to location.longitude.toString()

                        )

                        db.collection("users").document(mail).update(user).addOnSuccessListener {  }.addOnFailureListener {  }


                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()
        )
    }

    fun isAllPermissionsGranted(): Boolean {

        for(items in permissions){
            if(ContextCompat.checkSelfPermission(this, items)!= PackageManager.PERMISSION_GRANTED)
            {
                return false
            }
            }
        return true
        }

    /**
     * Function to check if location of the device is enabled or not
     */
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable GPS")
            .setMessage("Required for this app")
            .setCancelable(false)
            .setPositiveButton("Enable Now") { _, _ ->
                context.startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
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
                setUpLocationListener()
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