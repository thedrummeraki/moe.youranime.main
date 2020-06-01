package moe.youranime.main.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import moe.youranime.main.R
import moe.youranime.main.SimpleTextWatcher
import moe.youranime.main.auth.Authenticator
import moe.youranime.main.auth.Callback
import moe.youranime.main.auth.User
import java.lang.Exception

class LoginDialogFragment(var callback: Callback): DialogFragment(), Callback {
    private var username: String = "";
    private var password: String = "";

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.login_fragment, container, false)

        val usernameField = rootView.findViewById<EditText>(R.id.username_field)
        val passwordField = rootView.findViewById<EditText>(R.id.password_field)
        val loginButton = rootView.findViewById<Button>(R.id.login_button)

        usernameField.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChange(s: String) {
                username = s
            }
        })

        passwordField.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChange(s: String) {
                password = s
            }
        })

        loginButton.setOnClickListener { view ->
            if (username.isNotEmpty() && password.isNotEmpty()) {
                Authenticator(this, requireContext()).login(username, password)
            }
        }

        return rootView
    }

    override fun onLoginFailed(e: Exception?) {
        requireActivity().runOnUiThread {
            Toast.makeText(context, "Invalid username or password!", Toast.LENGTH_LONG).show()
        }
        callback.onLoginFailed(e)
    }

    override fun onLoginSuccessful(user: User) {
        callback.onLoginSuccessful(user)
        dismiss()
    }

    override fun onTokenCheckFailed(e: Exception?) {}

    override fun onTokenCheckSuccess(user: User) {}
}