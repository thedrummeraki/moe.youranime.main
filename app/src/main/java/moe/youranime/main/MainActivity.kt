package moe.youranime.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import moe.youranime.main.auth.User
import moe.youranime.main.ui.fragments.LoginDialogFragment
import java.lang.Exception

class MainActivity : AuthenticatedActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar

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
        setupNavigationDrawer()
        checkToken()
    }

    private fun setupNavigationDrawer() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view_logged_off)

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationView.menu.clear()
        navigationView.inflateMenu(getNavigationDrawerMenu())
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.nav_login -> showLoginDialog()
            }
            false
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
        updateUserInfo(user)
    }

    override fun onLoginFailed(e: Exception?) {
        Log.i("MainActivity", e?.message!!)
    }

    override fun onTokenCheckSuccess(user: User) {
        Log.i("MainActivity", user.username)
        updateUserInfo(user)
    }

    override fun onTokenCheckFailed(e: Exception?) {
        Log.i("MainActivity", e?.message!!)
    }

    fun showLoginDialog() {
        val loginDialog: DialogFragment = LoginDialogFragment(this)
        loginDialog.show(supportFragmentManager, "login_dialog")
    }

    fun updateUserInfo(user: User) {
        Log.i("TAG", "logged in!")
        val displayName = findViewById<TextView>(R.id.user_name)
        val displayUsername = findViewById<TextView>(R.id.user_username)

        runOnUiThread {
            findViewById<ProgressBar>(R.id.profile_loading).visibility = View.GONE
            findViewById<ImageView>(R.id.profile_image).visibility = View.VISIBLE

            displayName.setText(user.name)
            displayUsername.setText(user.username)
            setupNavigationDrawer()
        }
    }
}
