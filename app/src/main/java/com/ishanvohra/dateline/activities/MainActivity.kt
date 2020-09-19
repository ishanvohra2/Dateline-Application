package com.ishanvohra.dateline.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ishanvohra.dateline.fragments.SwipeFragment
import com.ishanvohra.dateline.R
import com.ishanvohra.dateline.fragments.MatcheFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = bottom_navigation

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
}
