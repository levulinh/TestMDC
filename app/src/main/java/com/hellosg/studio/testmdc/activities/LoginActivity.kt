package com.hellosg.studio.testmdc.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.hellosg.studio.testmdc.GlideApp
import com.hellosg.studio.testmdc.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.intentFor

class LoginActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    private val RC_SIGN_IN = 101
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (!account?.displayName.isNullOrEmpty())
            updateUi(account)

        btn_google_login.setOnClickListener {
            signIn()
        }

        cv_logged_in.setOnClickListener {
            signOut()
        }

        btn_next.setOnClickListener {
            startActivity(intentFor<ConnectActivity>())
        }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun updateUi(account: GoogleSignInAccount?) {
        if (account != null)
        {
            GlideApp.with(this@LoginActivity)
                    .load(account.photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.user)
                    .into(img_avatar_photo)
            txt_desc.visibility = View.GONE
            btn_google_login.visibility = View.GONE
            cv_logged_in.visibility = View.VISIBLE
            txt_logged_in_user.text = account.displayName
            btn_next.isEnabled = true
        } else {
            txt_desc.visibility = View.VISIBLE
            btn_google_login.visibility = View.VISIBLE
            cv_logged_in.visibility = View.GONE
            btn_next.isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    updateUi(account)
                } catch (ex: ApiException) {
                    Log.e(TAG, ex.message)
                }

            }
        }
    }
}
