package com.example.snapshots

import android.app.Application
import com.google.firebase.auth.FirebaseUser


class SnapshotsApplication : Application() {
    companion object {
        const val PATH_SNAPSHOTS = "snapshots"
        const val PROPERTY_LIKE_LIST = "likeList"
        const val  CODE_VALUE_EXP=-13010

        lateinit var currentUser: FirebaseUser
    }
}
