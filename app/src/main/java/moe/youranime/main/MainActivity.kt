package moe.youranime.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.view.*
import moe.youranime.main.auth.Authenticator
import moe.youranime.main.auth.Callback
import moe.youranime.main.auth.User
import java.lang.Exception

class MainActivity : AppCompatActivity(), Callback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private lateinit var authenticator: Authenticator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        setupNavbarDrawer()

        authenticator = Authenticator(this)
        runOnUiThread {
            authenticator.checkToken("171cf55d964f935c4aa5a1b42ff1d4e5")
        }
    }

    private fun setupNavbarDrawer(loggedIn: Boolean = false) {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view_logged_off)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationView.menu.clear()
        if (loggedIn) {
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged_on)
        } else {
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged_off)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onLoginSuccessful(user: User) {
        //Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_SHORT).show()
        updateUserInfo(user)
    }

    override fun onLoginFailed(e: Exception?) {
        //Toast.makeText(applicationContext, "Login failed.", Toast.LENGTH_SHORT).show()
        Log.i("MainActivity", e?.message!!)
    }

    override fun onTokenCheckSuccess(user: User) {
        //Toast.makeText(applicationContext, "Token check successful!", Toast.LENGTH_SHORT).show()
        Log.i("MainActivity", user.username)
        updateUserInfo(user)
    }

    override fun onTokenCheckFailed(e: Exception?) {
        //Toast.makeText(applicationContext, "Token check failed.", Toast.LENGTH_SHORT).show()
        Log.i("MainActivity", e?.message!!)
    }

    fun updateUserInfo(user: User) {
        Log.i("TAG", "logged in!")
        val displayName = findViewById<TextView>(R.id.user_name)
        val displayUsername = findViewById<TextView>(R.id.user_username)

        runOnUiThread {
            displayName.setText(user.name)
            displayUsername.setText(user.username)
            setupNavbarDrawer(true)
        }
    }
}
