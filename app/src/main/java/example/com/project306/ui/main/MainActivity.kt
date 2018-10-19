package example.com.project306.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import example.com.project306.R
import example.com.project306.databinding.MainActivityBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory: MainActivityViewModelFactory = InjectorUtils.provideMainActivityViewModelFactory()
        mainActivityViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)
        val binding = DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.main_activity)
        val navHost: NavHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment?
                ?: return

        val navController = navHost.navController

        initializeBottomNav(navController)

        NavigationUI.setupActionBarWithNavController(this, navController)

        binding.viewModel = mainActivityViewModel
    }

    private fun initializeBottomNav(navController: NavController) {
        findViewById<BottomNavigationView>(R.id.bottom_nav)?.let { bottomNavigationView ->
            NavigationUI.setupWithNavController(bottomNavigationView, navController)
        }
    }

    override fun onBackPressed() {
        val currentDestination = NavHostFragment.findNavController(main_nav_host_fragment).currentDestination
        when (currentDestination?.id) {
            R.id.loginStartFragment -> {
                finish()
                return
            }
        }
        super.onBackPressed()
    }
}
