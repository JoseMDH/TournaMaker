package com.example.tournamaker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.tournamaker.databinding.ActivityMainBinding
import com.example.tournamaker.utils.AuthManager
import com.example.tournamaker.viewModel.NotificationViewModel
import com.example.tournamaker.viewModel.NotificationViewModelFactory
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authManager: AuthManager
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var notificationBadge: BadgeDrawable? = null

    private val notificationViewModel: NotificationViewModel by viewModels {
        NotificationViewModelFactory(AuthManager.getInstance(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager.getInstance(this)

        setupNavigationAndUi()
        setupFab()
        observeNotifications()
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

            if (destination.id == R.id.notificationsFragment) {
                notificationViewModel.markAllNotificationsAsRead()
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

    private fun observeNotifications() {
        notificationViewModel.unreadCount.observe(this) { 
            invalidateOptionsMenu() 
        }
    }

    private fun checkUserAuthentication() {
        if (!authManager.isLoggedIn()) {
            navController.navigate(R.id.loginFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val menuItem = menu.findItem(R.id.notificationsFragment)
        val unreadCount = notificationViewModel.unreadCount.value ?: 0

        if (unreadCount > 0) {
            if (notificationBadge == null) {
                notificationBadge = BadgeDrawable.create(this)
            }
            notificationBadge!!.number = unreadCount
            BadgeUtils.attachBadgeDrawable(notificationBadge!!, binding.toolbar, menuItem.itemId)
        } else {
            if (notificationBadge != null) {
                BadgeUtils.detachBadgeDrawable(notificationBadge!!, binding.toolbar, menuItem.itemId)
                notificationBadge = null
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}