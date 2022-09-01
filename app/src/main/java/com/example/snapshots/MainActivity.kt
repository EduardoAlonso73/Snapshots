package com.example.snapshots


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.snapshots.Fragments.AddFragment
import com.example.snapshots.Fragments.HomeFragment
import com.example.snapshots.Fragments.ProfileFragment
import com.example.snapshots.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity() {
    private val RC_SING_IN =21

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mActivityFragment:Fragment
    private  lateinit var  mFragmentManager: FragmentManager

    private lateinit var mAuthListener:FirebaseAuth.AuthStateListener
    private var mFirebaseAuth:FirebaseAuth?=null

    private val responseAuthUI=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ actResult->
        if(actResult.resultCode== RESULT_OK){
            Toast.makeText(this,"Welcome ....",Toast.LENGTH_SHORT).show()
        }else{
            if(IdpResponse.fromResultIntent(actResult.data)==null){ // Botton Back press
                finish()
            }

        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupAuth()

        initBottomNav()
    }

    private fun setupAuth() {
        mFirebaseAuth= FirebaseAuth.getInstance()
        mAuthListener=FirebaseAuth.AuthStateListener {
            val user =it.currentUser
            if(user==null){

                val authUI= AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(
                        Arrays.asList(
                            AuthUI.IdpConfig.EmailBuilder().build(),
                            AuthUI.IdpConfig.GoogleBuilder().build()
                        )
                    ).build()
                responseAuthUI.launch(authUI)
            }
        }
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

    override fun onResume() {
        super.onResume()
        mFirebaseAuth?.addAuthStateListener(mAuthListener)
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth?.removeAuthStateListener(mAuthListener)

    }

    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if(requestCode==RC_SING_IN){
             if(resultCode== RESULT_OK){
                 Toast.makeText(this,"Welcome ....",Toast.LENGTH_SHORT).show()
             }else{
                 if(IdpResponse.fromResultIntent(data)==null){ // Botton Back press
                     finish()
                 }
             }
         }
     }*/
}