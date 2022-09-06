package com.example.snapshots.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.snapshots.R
import com.example.snapshots.Snapshot
import com.example.snapshots.SnapshotsApplication
import com.example.snapshots.databinding.FragmentAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class AddFragment : Fragment() {
    private val PATH_SHAPSHOT= "snapshots"  // the name folde in the server

    private lateinit var  mBinding: FragmentAddBinding
    private lateinit var  mStorageReference: StorageReference
    private lateinit var mDatabaseReference: DatabaseReference
    private   var mPhoneSelectUrl:Uri?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding= FragmentAddBinding.inflate(inflater,container,false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtonAction()
        setupFirebaseInstance()
    }

    private fun setupButtonAction(){
        with(mBinding){
            btnPost.setOnClickListener { postSnapshot() }
            btnSelect.setOnClickListener { openGallery() }
        }
    }

    private fun setupFirebaseInstance(){
        mStorageReference=FirebaseStorage.getInstance().reference.child(SnapshotsApplication.PATH_SNAPSHOTS)
        mDatabaseReference=FirebaseDatabase.getInstance().reference.child(SnapshotsApplication.PATH_SNAPSHOTS)
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

    private fun postSnapshot() {
        mBinding.prograssBar.visibility=View.VISIBLE
        val key=mDatabaseReference.push().key!!  // Create new  nodo   as well as extract its key
        //val storageReference= mStorageReference.child(FirebaseAuth.getInstance().currentUser!!.uid).child(key)
        val storageReference= mStorageReference.child(SnapshotsApplication.currentUser.uid).child(key)
        mPhoneSelectUrl?.let {phoneSelectUrl->
            storageReference.putFile(phoneSelectUrl).addOnProgressListener {
                val process =(100* it.bytesTransferred/it.totalByteCount).toDouble()
                mBinding.prograssBar.progress=process.toInt()
                mBinding.tvMessage.text="$process"
            }
                // -- -=-=-=-=-=-=-=- Complente  =-=-=-=-=-=-=-=-= -- //
                .addOnCompleteListener{mBinding.prograssBar.visibility=View.INVISIBLE}
                // -- -=-=-=-=-=-=-=- Succeess  =-=-=-=-=-=-=-=-= -- //
                .addOnSuccessListener {
                    Snackbar.make(mBinding.root,"Publicada",Snackbar.LENGTH_SHORT).show()
                    it.storage.downloadUrl.addOnSuccessListener {
                        saveSnapshots(key,it.toString(),mBinding.etTitle.text.toString().trim())
                        mBinding.tilTitle.visibility=View.GONE
                        mBinding.tvMessage.text=getString(R.string.post_message_title)
                    }
                }
                // -- -=-=-=-=-=-=-=- Fault or Error  =-=-=-=-=-=-=-=-= -- //
                .addOnFailureListener{ Snackbar.make(mBinding.root,"No be can push",Snackbar.LENGTH_SHORT).show()}
        }


    }

    private fun saveSnapshots(key:String,url:String,title:String){
        val snapshot=Snapshot(title =title, photoUrl = url )
        mDatabaseReference.child(key).setValue(snapshot)
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

}