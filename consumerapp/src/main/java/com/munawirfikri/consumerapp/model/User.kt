package com.munawirfikri.consumerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: Int = 0,
    var name : String? = null,
    var username : String? = null,
    var userURL : String? = null,
    var avatar : String? = null,
    var company : String? = null,
    var location : String? = null,
    var repository : Int = 0,
    var followers : Int = 0,
    var following : Int = 0
): Parcelable