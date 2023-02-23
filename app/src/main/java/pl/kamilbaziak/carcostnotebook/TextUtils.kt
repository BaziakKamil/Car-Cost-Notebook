package pl.kamilbaziak.carcostnotebook

import android.view.View
import com.google.android.material.snackbar.Snackbar

object TextUtils {

    fun showSnackbar(view: View, message: String) =
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_LONG
        ).show()

    fun showSnackbarWithAction(
        view: View,
        message: String,
        actionText: String,
        action: (Unit) -> Unit
    ) =
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_LONG
        )
            .setAction(actionText) {
                Unit.apply(action)
            }.show()
}
