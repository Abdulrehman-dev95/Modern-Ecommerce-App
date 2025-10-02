package com.example.razashop.dialogs

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.razashop.R
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setUpBottomSheetDialog(onSendClick: (String) -> Unit) {
    val dialog = BottomSheetDialog(requireContext(), R.style.dialogStyle)
    val view = layoutInflater.inflate(R.layout.dialog_reset_password, null)
    dialog.setContentView(view)
    dialog.show()

    val sendButton = view.findViewById<Button>(R.id.sendButton)
    val cancelButton = view.findViewById<Button>(R.id.cancelButton)
    val emailEditText = view.findViewById<EditText>(R.id.email_ed)

    sendButton.setOnClickListener {
        val email = emailEditText.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }
    cancelButton.setOnClickListener {
        dialog.dismiss()
    }

}