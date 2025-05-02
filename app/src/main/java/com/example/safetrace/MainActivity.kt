package com.example.safetrace

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

val bottombar = findViewById<BottomNavigationView>(R.id.bottomNav_bar)
        bottombar.setOnItemSelectedListener {

            if (it.itemId==R.id.guard){
                inflateFragment(GuardFragment.newInstance())
            }else if (it.itemId==R.id.Home){
                inflateFragment(HomeFragment.newInstance())
            }else if(it.itemId==R.id.dashboard){
                inflateFragment(DashboardFragment.newInstance())
            }else{
                inflateFragment(ProfileFragment.newInstance())
            }

        true//it simply states that the item is selected

        }
    }



    private fun inflateFragment(newInstance: Fragment) {

        var transaction  = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,newInstance)
        transaction.commit()
    }


}