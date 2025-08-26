package com.comics.lounge.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.comics.lounge.R

class DialogUtils {

    lateinit var dialog: Dialog

    fun show(context: Context?) {
        //if (dialog == null) {
        dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes.windowAnimations = R.style.DialogAnimation
        }
        dialog.setContentView(R.layout.loder_layout)
        dialog.setCancelable(false)
        //}
        dialog.show()
    }

    fun hide() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun showInfoAlert(context: Context, title: String, message: String) {
            showInfoAlert(context, title, message, positiveButtonTitle = "Dismiss")
        }


        @JvmStatic
        fun showInfoAlert(
                context: Context, title: String? = null, message: String?,
                positiveButtonTitle: String? = "OK",
                negativeButtonTitle: String? = null,
                cancelable: Boolean = true,
                onButtonClick: ((Boolean) -> Unit)? = null
        ) {
            val builder = AlertDialog.Builder(context)
            title?.let{ builder.setTitle(it) }
            builder.setMessage(message)
            builder.setCancelable(cancelable)
            builder.setPositiveButton(positiveButtonTitle) { dialog, which -> dialog.cancel(); onButtonClick?.invoke(true) }
            negativeButtonTitle?.let{
                builder.setNegativeButton(it) { dialog, which -> dialog.cancel(); onButtonClick?.invoke(false) }
            }
            builder.show()
        }

        fun showUpdateAlert(context: Context?, title: String?, message: String?, forceUpdate: Boolean?,
                            onClickListener: DialogInterface.OnClickListener) {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setCancelable(!forceUpdate!!)
            builder.setPositiveButton(R.string.update) { dialog: DialogInterface, which: Int ->
                onClickListener.onClick(dialog, which)
                dialog.cancel()
            }
            if (!forceUpdate) builder.setNegativeButton(R.string.no_thanks) { dialog: DialogInterface, which: Int -> dialog.cancel() }
            builder.show()
        }
    }
}