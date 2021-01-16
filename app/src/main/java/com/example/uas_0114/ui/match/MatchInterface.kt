package com.example.uas_0114.ui.match

import android.app.Activity
import android.content.Context
import com.example.uas_0114.model.EventItem
import com.example.uas_0114.utils.ServerCallback

interface MatchInterface {
    fun isNetworkAvailable(context: Activity): Boolean
    fun getSpinnerData(context: Activity,  callback: ServerCallback)
    fun getNextMatch(context: Activity, id : String,  callback: ServerCallback)
    fun isSuccess(response: String): Boolean
    fun parsingData(context: Activity, response: String): ArrayList<EventItem>
    fun getFavoritesAll(context: Context) : ArrayList<EventItem>
}