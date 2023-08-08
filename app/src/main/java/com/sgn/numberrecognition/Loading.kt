package com.sgn.numberrecognition

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.airbnb.lottie.LottieAnimationView

class Loading(activity: Activity) {
    var activity: Activity
    var dialog: AlertDialog? = null

    init {
        this.activity = activity
    }

    fun startDialogLoading() {
        val builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity.getLayoutInflater()
        builder.setView(inflater.inflate(R.layout.loading, null))
        builder.setCancelable(true)
        dialog = builder.create()
        dialog!!.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        dialog!!.show()
    }

    fun dismissDialog() {
        dialog!!.dismiss()
    }
}