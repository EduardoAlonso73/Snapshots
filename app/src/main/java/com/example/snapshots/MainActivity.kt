package com.example.snapshots

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.snapshots.Fragments.AddFragment
import com.example.snapshots.Fragments.HomeFragment
import com.example.snapshots.Fragments.ProfileFragment
import com.example.snapshots.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mActivityFragment:Fragment
    private  lateinit var  mFragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initBottomNav()
    }

    private fun initBottomNav() {
        val mFragmentManager=supportFragmentManager
        val homeFragment=HomeFragment()
        val addFragment=AddFragment()
        val profileFragment=ProfileFragment()

        mActivityFragment=homeFragment
        with(mFragmentManager){
            beginTransaction().add(R.id.hostFragments,profileFragment,ProfileFragment::class.java.name).hide(profileFragment).commit()
            beginTransaction().add(R.id.hostFragments,addFragment,AddFragment::class.java.name).hide(addFragment).commit()
            beginTransaction().add(R.id.hostFragments,homeFragment,HomeFragment::class.java.name).commit()
        }

        mBinding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_home->{ optionShowFragment(homeFragment)}
                R.id.action_add->{ optionShowFragment(addFragment) }
                R.id.action_profile->{ optionShowFragment(profileFragment) }
                else -> false
            }
        }
    }

    private fun optionShowFragment(nameFragment: Fragment):Boolean {
        val mFragmentManager=supportFragmentManager
        mFragmentManager.beginTransaction().hide(mActivityFragment).show(nameFragment).commit()
        mActivityFragment=nameFragment
        return  true
    }
}