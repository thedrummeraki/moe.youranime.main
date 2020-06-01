package moe.youranime.main

import androidx.appcompat.app.AppCompatActivity
import moe.youranime.main.auth.Authenticator
import moe.youranime.main.auth.Callback
import moe.youranime.main.auth.User

abstract class AuthenticatedActivity: AppCompatActivity(), Callback {
    fun checkToken() {
        Authenticator(this, applicationContext).checkToken()
    }

    fun getNavigationDrawerMenu(): Int {
        if (User.hasCurrentUser()) {
            return R.menu.activity_main_drawer_logged_on;
        } else {
            return R.menu.activity_main_drawer_logged_off;
        }
    }
}