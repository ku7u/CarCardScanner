package com.olequacircuits.carcardscanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        bottomNav = findViewById(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->

            when(item.itemId) {

                R.id.nav_control -> {
                    showFragment(ControlFragment())
                    true
                }

                R.id.nav_scanner -> {
                    showFragment(ScannerFragment())
                    true
                }

                else -> false
            }
        }

        if (savedInstanceState == null) {
            showFragment(ControlFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment)
            .commit()

    }
}
