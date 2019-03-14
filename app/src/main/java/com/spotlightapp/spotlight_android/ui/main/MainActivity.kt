package com.spotlightapp.spotlight_android.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spotlightapp.spotlight_android.R
import com.spotlightapp.spotlight_android.databinding.ActivityMainBinding
import com.spotlightapp.spotlight_android.util.InjectorUtils
import com.spotlightapp.spotlight_android.util.UserState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainActivityViewModel
    lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
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

    fun validateUser(): MutableLiveData<UserState> {
        val result: MutableLiveData<UserState> = MutableLiveData()
        vm.validateUser().observe(this, Observer { userState ->
            // They are not logged in, should be booted to start page
            if (userState == enumValueOf<UserState>(UserState.LoggedOut.toString()) || userState == enumValueOf<UserState>(UserState.EmailNotVerified.toString())) {
                vm.setBottomNavVisibility(false)
                vm.setAppBarVisibility(false)
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.landingFragment, true).build()
                navController.navigate(R.id.landingFragment, null, navOptions)
                result.value = userState
                return@Observer
            }
            // They are logged in, transfer control to fragments
            result.value = userState
        })
        return result
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
