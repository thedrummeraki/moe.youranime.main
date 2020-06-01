package moe.youranime.main.auth

import java.lang.Exception

interface Callback {
    fun onLoginSuccessful(user: User)
    fun onLoginFailed(e: Exception?)
    fun onTokenCheckSuccess(user: User)
    fun onTokenCheckFailed(e: Exception?)
}