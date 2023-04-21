package com.example.sip_registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import org.linphone.core.RegistrationState

class MainActivity : AppCompatActivity() {
    private val mViewModel: MainViewModel by viewModels()

    private lateinit var mRegisterButton: Button
    private lateinit var mLoginEdit: EditText
    private lateinit var mPasswordEdit: EditText
    private lateinit var mStatusText: TextView
    private lateinit var mMessageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRegisterButton = findViewById(R.id.registerButton)
        mLoginEdit = findViewById(R.id.loginEdit)
        mPasswordEdit = findViewById(R.id.passwordEdit)
        mStatusText = findViewById(R.id.statusText)
        mMessageText = findViewById(R.id.messageText)

        //Data binding with LiveData
        mViewModel.getRegistrationState().observe(this) {
            when(it) {
                RegistrationState.None -> mStatusText.text = "Unregistered"
                RegistrationState.Progress -> mStatusText.text = "Progress"
                RegistrationState.Ok -> {
                    mStatusText.text = "Successful"
                    mRegisterButton.isEnabled = true
                }
                RegistrationState.Cleared -> {
                    mStatusText.text = "Cleared"
                    mRegisterButton.isEnabled = true
                }
                RegistrationState.Failed -> {
                    mStatusText.text = "Failed"
                    mRegisterButton.isEnabled = true
                }
            }
        }
        mViewModel.getRegistrationMessage().observe(this) {value -> mMessageText.text = value}

        mRegisterButton.setOnClickListener {
            mRegisterButton.isEnabled = false
            val login = mLoginEdit.text.toString()
            val password = mPasswordEdit.text.toString()
            if (login.isNotEmpty() and password.isNotEmpty()) {
                mViewModel.stopSession()
                mViewModel.registration(login, password)
            }
        }
    }
}