package com.example.snapshots.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.snapshots.R
import com.example.snapshots.Snapshot
import com.example.snapshots.databinding.FragmentHomeBinding
import com.example.snapshots.databinding.ItemSnapshotBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class HomeFragment : Fragment() {

    private  lateinit var  mFirebaseAdapter:FirebaseRecyclerAdapter<Snapshot,SnapshotHolder>
    private lateinit var  mBinding: FragmentHomeBinding
    private lateinit var mLayoutManager: RecyclerView.LayoutManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding= FragmentHomeBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query=FirebaseDatabase.getInstance().reference.child("snapshots")
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

                val view=LayoutInflater.from(mContext).inflate(R.layout.item_snapshot,parent,false)
                return  SnapshotHolder(view)
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
                        .dontAnimate()
                        .dontTransform()
                        .placeholder(R.drawable.loading_spinner)
                        .into(mBinding.imgPhote)
                }
            }

            @SuppressLint("NotifyDataSetChanged")//Error interno Firebase ui 8.0.0
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

        setupRecyclerView()

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

        mLayoutManager=LinearLayoutManager(context)
        mBinding.RecyclerView.apply {
            setHasFixedSize(true)
            layoutManager=mLayoutManager
            adapter=mFirebaseAdapter
        }
    }

    private fun deleteSnapshot(snapshot:Snapshot ){
        val databaseReference=FirebaseDatabase.getInstance().reference.child("snapshots")
        databaseReference.child(snapshot.id).removeValue()
        println(" ============================== ${snapshot.id} =================================")
    }

    private fun setLike(snapshot: Snapshot,checked:Boolean){
        val databaseReference=FirebaseDatabase.getInstance().reference.child("snapshots")
        if(checked){
          databaseReference.child(snapshot.id).child("likeList")
              .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(checked)
        }else
        {
            databaseReference.child(snapshot.id).child("likeList")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(null)
        }
    }


/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
      ----------- INNER CLASS ------------
 -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* */

    inner class SnapshotHolder(view: View):RecyclerView.ViewHolder(view){
        val mBinding=ItemSnapshotBinding.bind(view)

        fun setListener(snapshot: Snapshot){
            with(mBinding){
                btnDelete.setOnClickListener { deleteSnapshot(snapshot) }
                cbLike.setOnCheckedChangeListener { _, checked -> setLike(snapshot,checked)}
            }

        }
    }





}