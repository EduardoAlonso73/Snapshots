package com.example.snapshots.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.snapshots.FragmentAux
import com.example.snapshots.R
import com.example.snapshots.SnapshotsApplication
import com.example.snapshots.databinding.FragmentProfileBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment(),FragmentAux {
private lateinit var mBinding:FragmentProfileBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding= FragmentProfileBinding.inflate(layoutInflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh()
        setupButton()


    }

    private fun setupButton() {
        mBinding.btnSingOut.setOnClickListener {
            context?.let {
                MaterialAlertDialogBuilder(it)
                    .setTitle("¿Desea cerrar la sesion actual?")
                    .setPositiveButton("Close") { _, _ ->
                        singOut()
                    }

                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    private fun singOut() {
        context?.let {
            AuthUI.getInstance().signOut(it)
                .addOnCompleteListener {
                    Toast.makeText(context, "You see later", Toast.LENGTH_SHORT).show()
                    mBinding.tvName.text = ""
                    mBinding.tvGmail.text = ""

                    (activity?.findViewById(R.id.bottomNav) as? BottomNavigationView)?.selectedItemId =
                        R.id.action_home
                }
        }
    }


    override fun refresh() {
        with(mBinding) {
            tvName.text=FirebaseAuth.getInstance().currentUser?.displayName?:" Nama user"
            tvGmail.text=FirebaseAuth.getInstance().currentUser?.email?:" @gmail.com"
        }
    }


}