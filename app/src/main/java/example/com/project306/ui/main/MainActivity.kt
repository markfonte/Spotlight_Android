package example.com.project306.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import example.com.project306.R
import example.com.project306.databinding.ActivityMainBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        val factory: MainActivityViewModelFactory = InjectorUtils.provideMainActivityViewModelFactory()
        mainActivityViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            viewModel = mainActivityViewModel
            setLifecycleOwner(this@MainActivity)
        }
        val navHost: NavHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment?
                ?: return

        navController = navHost.navController

        initializeBottomNav(navController)

        setSupportActionBar(binding.appMainToolbar)
        NavigationUI.setupWithNavController(binding.appMainToolbar, navController)
    }

    private fun initializeBottomNav(navController: NavController) {
        findViewById<BottomNavigationView>(R.id.bottom_nav)?.let { bottomNavigationView ->
            NavigationUI.setupWithNavController(bottomNavigationView, navController)
        }
    }

    fun validateUser(): Boolean {
        if (mainActivityViewModel.currentUser.value == null || !mainActivityViewModel.currentUser.value?.isEmailVerified!!) {
            with(mainActivityViewModel) {
                setBottomNavVisibility(false)
                setAppBarVisibility(false)
            }
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.landingFragment, true).build()
            navController.navigate(R.id.landingFragment, null, navOptions)
            return false
        } else {
            with(mainActivityViewModel) {
                setBottomNavVisibility(true)
                setAppBarVisibility(true)
            }
        }
        return true
    }

    override fun onBackPressed() {
        val currentDestination = NavHostFragment.findNavController(main_nav_host_fragment).currentDestination
        when (currentDestination?.id) {
            R.id.landingFragment -> {
                finish()
                return
            }
        }
        super.onBackPressed()
    }
}
