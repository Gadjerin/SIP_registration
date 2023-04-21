package com.example.sip_registration

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.linphone.core.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val mApplication: Application = application
    private val TAG: String = "MainViewModel"

    private val DOMAIN: String = "ds-stg.tips.com.ua"
    private val PROXY: String = "fs-stg.tips.com.ua"
    private val TRANSPORT: TransportType = TransportType.Tls

    private var mCore: Core? = null

    private val mCoreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core,
                                                       account: Account,
                                                       state: RegistrationState,
                                                       message: String) {
            mRegistrationState.value = state
            mRegistrationMessage.value = message
            if (state == RegistrationState.Ok) {
                Log.d(TAG, "session expiration = " + core.sessionExpiresValue)
            }

        }
    }

    private var mRegistrationState: MutableLiveData<RegistrationState> =
        MutableLiveData<RegistrationState>(RegistrationState.None)
    private var mRegistrationMessage: MutableLiveData<String> =
        MutableLiveData<String>("")

    fun getRegistrationState() : MutableLiveData<RegistrationState> {
        return mRegistrationState
    }

    fun getRegistrationMessage() : MutableLiveData<String> {
        return mRegistrationMessage
    }

    fun stopSession() {
        if (mCore != null) {
            val account = mCore?.defaultAccount
            mCore?.stopAsync()
            account ?: return
            mCore?.removeAccount(account)
            mCore?.clearAccounts()
            mCore?.clearAllAuthInfo()
            mCore = null
        }
    }

    fun registration(login : String, password : String) {
        Log.d(TAG, "registration process")

        val factory = Factory.instance()
        mCore = factory.createCore(null, null, mApplication)
        val authInfo = factory.createAuthInfo(login, null, password, null, null, DOMAIN, null)

        val clientAddr = factory.createAddress("sip:$login@$DOMAIN")
        val serverAddr = Factory.instance().createAddress("sip:$PROXY")
        serverAddr?.transport = TRANSPORT

        val accountParams = mCore!!.createAccountParams()
        accountParams.identityAddress = clientAddr
        accountParams.serverAddress = serverAddr
        accountParams.isRegisterEnabled = true

        val account = mCore!!.createAccount(accountParams)

        mCore!!.addAuthInfo(authInfo)
        mCore!!.addAccount(account)

        mCore!!.defaultAccount = account
        mCore!!.addListener(mCoreListener)
        mCore!!.start()
    }
}