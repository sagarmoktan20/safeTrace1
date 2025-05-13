package com.example.safetrace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Use the new splash screen API
        installSplashScreen()

        super.onCreate(savedInstanceState)


      val isUserLoggedIn = sharedPref.getBoolean(prefConstants.IS_USER_LOGGED_IN)

        if(isUserLoggedIn){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
       
    }
}