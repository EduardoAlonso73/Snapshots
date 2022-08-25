package com.example.snapshots.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snapshots.R
import com.example.snapshots.Snapshot
import com.example.snapshots.databinding.ItemSnapshotBinding


class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }




/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
      ----------- INNER CLASS ------------
 -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* */

inner class SnapshotHolder(view: View):RecyclerView.ViewHolder(view){
    val binding=ItemSnapshotBinding.bind(view)

    fun setListener(snapshot: Snapshot){

    }
}





}