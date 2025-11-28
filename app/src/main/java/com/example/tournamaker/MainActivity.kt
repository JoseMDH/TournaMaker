package com.example.tournamaker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.tournamaker.databinding.ActivityMainBinding
import com.example.tournamaker.utils.AuthManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authManager: AuthManager
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager.getInstance(this)

        setupNavigationAndUi()
        setupFab()
    }

    private fun setupNavigationAndUi() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.landingFragment, R.id.allTournamentsFragment, R.id.allMatchesFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isAuthScreen = destination.id == R.id.loginFragment || destination.id == R.id.registerFragment
            binding.toolbar.visibility = if (isAuthScreen) View.GONE else View.VISIBLE
            if (isAuthScreen) {
                binding.fab.hide()
                binding.fabHome.hide()
            } else {
                binding.fab.show()
                binding.fabHome.show()
            }
        }

        checkUserAuthentication()
    }

    private fun setupFab() {
        binding.fab.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.fab_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                NavigationUI.onNavDestinationSelected(menuItem, navController)
            }
            popupMenu.show()
        }

        binding.fabHome.setOnClickListener {
            navController.navigate(R.id.landingFragment)
        }
    }

    private fun checkUserAuthentication() {
        if (!authManager.isLoggedIn()) {
            navController.navigate(R.id.loginFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}