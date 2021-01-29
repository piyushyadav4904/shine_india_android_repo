package com.app.theshineindia.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.theshineindia.R
import com.app.theshineindia.interfgaces.DialogClickCallback

@SuppressLint("StaticFieldLeak")
object CommonDialog {

    private val TAG = "CommonDialog"

    @JvmStatic
    fun showDialog(activity: Context, dialogClickCallback: DialogClickCallback, dialog_title: String,
                   dialog_message: String) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(dialog_title)
        alertDialogBuilder.setMessage(dialog_message)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(R.string.yes) { dialogInterface, i ->
            dialogClickCallback.onConfirm()
            dialogInterface.dismiss()
        }
        alertDialogBuilder.setNegativeButton(R.string.no) { dialog, which ->
            dialogClickCallback.onDismiss()
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }


    fun showDialogWithCustomConfirmAndCancelBTN(activity: Context, dialogClickCallback: DialogClickCallback, dialog_title: String,
                                                dialog_message: String, positiveBtnString: String, negativeBtnString: String) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(dialog_title)
        alertDialogBuilder.setMessage(dialog_message)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(positiveBtnString) { dialogInterface, i ->
            dialogClickCallback.onConfirm()
            dialogInterface.dismiss()
        }
        alertDialogBuilder.setNegativeButton(negativeBtnString) { dialog, which ->
            dialogClickCallback.onDismiss()
            dialog.dismiss()
        }

        alertDialogBuilder.show()
    }


    fun showDialogWithOnlyOneButton(activity: Context, dialogClickCallback: DialogClickCallback, dialog_title: String,
                                    dialog_message: String?, btnLabel: String) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(dialog_title)
        if (dialog_message != null) {
            alertDialogBuilder.setMessage(dialog_message)
        }
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(btnLabel) { dialogInterface, i ->
            dialogClickCallback.onConfirm()
            dialogInterface.dismiss()
        }
        alertDialogBuilder.show()
    }



    /*// Show Image uploading progress
    var dialog: Dialog? = null
    fun showDialogImageUploading(context: Context? = null) {
        *//* for creating dialog for 1st time *//*
        if (context != null) {
            dialog = Dialog(context)
            dialog!!.setContentView(R.layout.dialog_image_upload)
            dialog!!.setCancelable(false)
            dialog!!.setCanceledOnTouchOutside(false)

            dialog!!.show()
        }

        *//* dismiss dialog when work is done *//*
        if (context == null) {
            dialog?.dismiss()
            dialog = null
        }
    }*/
}
