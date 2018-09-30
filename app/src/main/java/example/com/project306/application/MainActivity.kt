package example.com.project306.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import example.com.project306.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val navHost: NavHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment? ?: return

        val navController = navHost.navController

        initializeBottomNav(navController)

        NavigationUI.setupActionBarWithNavController(this, navController)

    }

    private fun initializeBottomNav(navController: NavController) {
        findViewById<BottomNavigationView>(R.id.bottom_nav)?.let { bottomNavigationView ->
            NavigationUI.setupWithNavController(bottomNavigationView, navController)
        }
    }

}
