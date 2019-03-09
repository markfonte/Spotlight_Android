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
import com.google.firebase.analytics.FirebaseAnalytics
import example.com.project306.R
import example.com.project306.databinding.ActivityMainBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainActivityViewModel
    lateinit var navController: NavController
    lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val factory: MainActivityViewModelFactory = InjectorUtils.provideMainActivityViewModelFactory()
        vm = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            viewModel = vm
            lifecycleOwner = this@MainActivity
        }
        val navHost: NavHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment?
                ?: return

        navController = navHost.navController

        initializeBottomNav(navController)

        setSupportActionBar(binding.appMainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initializeBottomNav(navController: NavController) {
        findViewById<BottomNavigationView>(R.id.bottom_nav)?.let { bottomNavigationView ->
            NavigationUI.setupWithNavController(bottomNavigationView, navController)
        }
    }

    fun validateUser(): Boolean {
        if (vm.currentUser.value == null || !vm.currentUser.value?.isEmailVerified!!) {
            with(vm) {
                setBottomNavVisibility(false)
                setAppBarVisibility(false)
            }
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.landingFragment, true).build()
            navController.navigate(R.id.landingFragment, null, navOptions)
            return false
        } else {
            with(vm) {
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
