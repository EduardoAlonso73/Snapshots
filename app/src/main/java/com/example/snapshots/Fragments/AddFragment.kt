package com.example.snapshots.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.snapshots.R
import com.example.snapshots.databinding.FragmentAddBinding

class AddFragment : Fragment() {
    private lateinit var  mBinding: FragmentAddBinding
    private   var mPhoneSelectUrl:Uri?=null
    private val RC_GALLERY=12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding= FragmentAddBinding.inflate(inflater,container,false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(mBinding){
            btnPost.setOnClickListener { postSnapshot() }
            btnSelect.setOnClickListener { openGallery() }
        }

    }

    private fun openGallery() {
     val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getResult.launch(intent)
    }

   private  val  getResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
       if (it.resultCode==Activity.RESULT_OK){
               mPhoneSelectUrl=it.data?.data
               with(mBinding){
                   imgPhoto.setImageURI(mPhoneSelectUrl)
                   tilTitle.visibility=View.VISIBLE
                   println(" ================= $mPhoneSelectUrl====================")
                   tvMessage.text=getString(R.string.post_message_valid_title)
               }


       }
   }


/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK){
            if(requestCode==RC_GALLERY){
                mPhoneSelectUrl=data?.data
                with(mBinding){
                    imgPhoto.setImageURI(mPhoneSelectUrl)
                    tilTitle.visibility=View.VISIBLE
                    println(" ================= $mPhoneSelectUrl====================")
                    tvMessage.text=getString(R.string.post_message_valid_title)
                }

            }
        }
    }*/


    private fun postSnapshot() {

    }


}