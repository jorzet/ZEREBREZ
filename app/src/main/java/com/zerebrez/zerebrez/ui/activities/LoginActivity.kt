/*
 * Copyright [2018] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerebrez.zerebrez.ui.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.login.SignUpFragment
import com.zerebrez.zerebrez.fragments.login.StartFragment
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.zerebrez.zerebrez.fragments.login.SignInFragment
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.enums.ErrorType
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.zerebrez.zerebrez.fragments.init.InitFragment
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.services.firebase.DownloadImages

/**
 * Created by Jorge Zepeda Tinoco on 27/02/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "LoginActivity"

class LoginActivity : BaseActivityLifeCycle(), GoogleApiClient.OnConnectionFailedListener {

    private val SHOW_START = "show_start"

    companion object {
        val RC_SIGN_IN : Int = 9001
    }

    /*
     * Facebook
     */
    private lateinit var mCallbackManager: CallbackManager

    /*
     * Google
     */
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mGoogleSignInClient : GoogleSignInClient


    private lateinit var mCurrentfragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.base_container)

        // Facebook Login
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        val showStart = intent.getBooleanExtra(SHOW_START, false)

        if (showStart) {
            showStartFragment()
        } else {
            showSigUpFragment()
        }

        // request SMS permissions
        ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE),1);
        ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.READ_EXTERNAL_STORAGE),1);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode.equals(RC_SIGN_IN)) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                if (account != null) {
                    if (mCurrentfragment is SignInFragment) {
                        (mCurrentfragment as SignInFragment).onGoogleResultSuccess(account)
                    } else {
                        (mCurrentfragment as SignUpFragment).onGoogleResultSuccess(account)
                    }
                } else {
                    val error = GenericError()
                    error.setErrorType(ErrorType.NULL_RESPONSE)
                    error.setErrorMessage("Respuesta sin datos")
                    if (mCurrentfragment is SignInFragment) {
                        (mCurrentfragment as SignInFragment).onGoogleResultFaild(error)
                    } else {
                        (mCurrentfragment as SignUpFragment).onGoogleResultFaild(error)
                    }
                }
            } else {
                val error = GenericError()
                error.setErrorType(ErrorType.CANNOT_LOGIN)
                error.setErrorMessage("No se pudo realizar el login")
                if (mCurrentfragment is SignInFragment) {
                    (mCurrentfragment as SignInFragment).onGoogleResultFaild(error)
                } else {
                    (mCurrentfragment as SignUpFragment).onGoogleResultFaild(error)
                }
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.")
            }
        }
    }

    override fun onBackPressed() {
        val otherFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (otherFragment is StartFragment) {
            super.onBackPressed()
            this.finish()
        } else if (otherFragment is SignUpFragment) {
            if (FirebaseAuth.getInstance().currentUser != null)
                FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            SharedPreferencesManager(baseContext).removeSessionData()

            super.onBackPressed()
            this.finish()
        } else {
            showStartFragment()
        }
    }

    private fun showStartFragment() {
        mCurrentfragment = StartFragment()
        val manager = getSupportFragmentManager();
        val transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, mCurrentfragment);
        transaction.commit()
    }

    fun showInitFragment() {
        mCurrentfragment = InitFragment()
        val manager = getSupportFragmentManager();
        val transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, mCurrentfragment);
        transaction.commit()
    }

    private fun showSigUpFragment() {
        mCurrentfragment = SignUpFragment()
        val manager = getSupportFragmentManager();
        val transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, mCurrentfragment);
        transaction.commit()
    }

    fun getCallBackManager() : CallbackManager {
        return this.mCallbackManager
    }

    fun getGoogleApiClient() : GoogleApiClient {
        return this.mGoogleApiClient
    }

    fun getGoogleSignInClient() : GoogleSignInClient {
        return this.mGoogleSignInClient
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult)
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show()
    }

    open fun startDownloadImages() {
        this.startService(Intent(this, DownloadImages::class.java))
        Log.i(TAG, "Started download service **********************")
        this.registerReceiver(br, IntentFilter(DownloadImages.DOWNLOAD_IMAGES_BR))
    }

    fun stopDownloadImagesService() {
        this.stopService(Intent(this, DownloadImages::class.java))
        Log.i(TAG, "Stopped service ***************************")
        val dataHelper = DataHelper(this)
        dataHelper.setImagesDownloaded(true)
        showInitFragment()
    }

    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getExtras() != null) {
                if (intent.getBooleanExtra(DownloadImages.DOWNLOAD_COMPLETE,false)) {
                    stopDownloadImagesService()
                } else {
                    Log.i(TAG, "Downloading ...")
                }
            }
        }
    }

}