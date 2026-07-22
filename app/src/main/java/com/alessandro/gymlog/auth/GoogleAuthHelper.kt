package com.alessandro.gymlog.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object GoogleAuthHelper {
    fun getSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("496244914061-algm7tcden053bmo6f3rtcdovfr0fbh.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getLastSigninAccount(context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }
}