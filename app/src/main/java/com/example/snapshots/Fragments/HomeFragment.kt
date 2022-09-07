package com.example.snapshots.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.snapshots.HomeAux
import com.example.snapshots.R
import com.example.snapshots.Snapshot
import com.example.snapshots.SnapshotsApplication
import com.example.snapshots.databinding.FragmentHomeBinding
import com.example.snapshots.databinding.ItemSnapshotBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException


class HomeFragment : Fragment() , HomeAux{
    private  lateinit var  mFirebaseAdapter:FirebaseRecyclerAdapter<Snapshot,SnapshotHolder>
    private lateinit var  mBinding: FragmentHomeBinding
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mSnapshotsRef: DatabaseReference
    private lateinit var mGridLayout: GridLayoutManager



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding= FragmentHomeBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFirebaseInstance()
        setupAdapter()
        setupRecyclerView()

    }

    private fun setupFirebaseInstance(){
        // it is the location of our model Data  called --snapshots--  in Realtime Firebase
        mSnapshotsRef=FirebaseDatabase.getInstance().reference.child(SnapshotsApplication.PATH_SNAPSHOTS)
    }
    private fun  setupAdapter(){
        val query=mSnapshotsRef
        val options=FirebaseRecyclerOptions.Builder<Snapshot>().setQuery(query, SnapshotParser {
            val snapshot=it.getValue(Snapshot::class.java)
            snapshot!!.id=it.key!!
            snapshot
        }).build()


        //FirebaseRecyclerOptions.Builder<Snapshot>().setQuery(query,Snapshot::class.java).build()
        mFirebaseAdapter=object : FirebaseRecyclerAdapter<Snapshot,SnapshotHolder>(options){
            private  lateinit var  mContext:Context
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
                mContext=parent.context

                val mView=LayoutInflater.from(mContext).inflate(R.layout.item_snapshot,parent,false)
                return  SnapshotHolder(mView)
            }

            override fun onBindViewHolder(holder: SnapshotHolder, position: Int, model: Snapshot) {
                val snapshot=getItem(position)
                with(holder){
                    setListener(snapshot)
                    mBinding.tvTitle.text=snapshot.title
                    mBinding.cbLike.text=snapshot.likeList.keys.size.toString()
                    FirebaseAuth.getInstance().currentUser?.let {
                        mBinding.cbLike.isChecked=snapshot.likeList.containsKey(it.uid)
                    }

                    Glide.with(mContext)
                        .load(snapshot.photoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .dontTransform()
                        .placeholder(R.drawable.loading_spinner)
                        .into(mBinding.imgPhote)
                }
            }
            //Error interno Firebase ui 8.0.0
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChanged() {
                super.onDataChanged()
                mBinding.progressBar.visibility=View.GONE
                notifyDataSetChanged()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                Toast.makeText(mContext,"Error", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onStart() {
        super.onStart()
        mFirebaseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFirebaseAdapter.stopListening()
    }

    private fun setupRecyclerView() {
        mGridLayout= GridLayoutManager(context,resources.getInteger(R.integer.main_columns))
        mLayoutManager=LinearLayoutManager(context)
        mBinding.RecyclerView.apply {
            setHasFixedSize(true)
            layoutManager=mLayoutManager
            adapter=mFirebaseAdapter
            layoutManager=mGridLayout
        }
    }

    private fun deleteSnapshot(snapshot:Snapshot ){
        //We get the name image in the Storage
        val databaseReference= FirebaseStorage.getInstance().reference
            .child(SnapshotsApplication.PATH_SNAPSHOTS)
            .child(SnapshotsApplication.currentUser.uid)
            .child(snapshot.id)

        if ( StorageException.ERROR_OBJECT_NOT_FOUND==SnapshotsApplication.CODE_VALUE_EXP){
            mSnapshotsRef.child(snapshot.id).removeValue()
        }else{
            databaseReference.delete().addOnCompleteListener { result ->
                if (result.isSuccessful ) {
                    mSnapshotsRef.child(snapshot.id).removeValue()
                } else {
                    Toast.makeText(context, " Snapshots not was deleted !!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        //databaseReference.child(snapshot.id).removeValue()
        //println(" ============000000000================== ${snapshot.id} =================000000000================")
    }

    private fun setLike(snapshot: Snapshot,checked:Boolean){
        val databaseReference=FirebaseDatabase.getInstance().reference.child(SnapshotsApplication.PATH_SNAPSHOTS)
        if(checked){
            databaseReference.child(snapshot.id).child(SnapshotsApplication.PROPERTY_LIKE_LIST)
                .child(SnapshotsApplication.currentUser.uid).setValue(checked)
            //.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(checked)
        }else
        {
            databaseReference.child(snapshot.id).child(SnapshotsApplication.PROPERTY_LIKE_LIST)
                .child(SnapshotsApplication.currentUser.uid).setValue(null)
            //    .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(null)
        }
    }

    override fun goToTop() {
        mBinding.RecyclerView.smoothScrollToPosition(0)
    }


/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
      ----------- INNER CLASS ------------
 -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* */

    inner class SnapshotHolder(view: View):RecyclerView.ViewHolder(view){
        val mBinding=ItemSnapshotBinding.bind(view)

        fun setListener(snapshot: Snapshot){
            with(mBinding){
                btnDelete.setOnClickListener {
                    val menu = PopupMenu(context,btnDelete)
                    menu.menuInflater.inflate(R.menu.popup_menu,menu.menu)
                    menu.setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener {
                        override fun onMenuItemClick(item: MenuItem?): Boolean {
                            when(item?.itemId){R.id.delete -> {deleteSnapshot(snapshot)} }
                            return true
                        }
                    })
                    menu.show()
                }
                cbLike.setOnCheckedChangeListener { _, checked -> setLike(snapshot,checked)}
            }

        }

    }
}
