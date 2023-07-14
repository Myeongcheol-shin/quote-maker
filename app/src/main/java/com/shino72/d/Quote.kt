package com.shino72.d

import android.net.Uri
import java.io.Serializable

data class Quote(
    val name : String?,
    val date : String?,
    val quote : String?,
) : Serializable
