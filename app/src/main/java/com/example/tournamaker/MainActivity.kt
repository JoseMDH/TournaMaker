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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationAndUi()
        setupFab()
    }

    private fun setupNavigationAndUi() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                // Define aquí los destinos de nivel superior (no tendrán flecha de "atrás")
                R.id.landingFragment, R.id.allTournamentsFragment, R.id.allMatchesFragment, R.id.userPageFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isAuthScreen = destination.id == R.id.loginFragment || destination.id == R.id.registerFragment

            // Oculta la toolbar y el FAB en las pantallas de autenticación
            if (isAuthScreen) {
                binding.toolbar.visibility = View.GONE
                binding.fabCreate.hide()
            } else {
                binding.toolbar.visibility = View.VISIBLE
                binding.fabCreate.show()
            }

            // Invalida el menú para que se redibuje (y aparezca/desaparezca el botón de perfil)
            invalidateOptionsMenu()
        }
    }

    private fun setupFab() {
        binding.fabCreate.setOnClickListener { view ->
            // Crea un PopupMenu anclado al botón flotante
            val popupMenu = PopupMenu(this, view)
            // Infla el menú de creación que hemos diseñado
            popupMenu.menuInflater.inflate(R.menu.fab_menu, popupMenu.menu)

            // Define qué hacer cuando se pulsa una opción del menú
            popupMenu.setOnMenuItemClickListener { menuItem ->
                // Navega al destino correspondiente al ID del item de menú
                NavigationUI.onNavDestinationSelected(menuItem, navController)
                true
            }
            // Muestra el menú
            popupMenu.show()
        }
    }

    // --- MANEJO DEL MENÚ DE LA TOOLBAR (PERFIL) ---

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Solo muestra el menú de la toolbar (perfil) si no estamos en una pantalla de autenticación
        if (navController.currentDestination?.id != R.id.loginFragment &&
            navController.currentDestination?.id != R.id.registerFragment) {
            menuInflater.inflate(R.menu.toolbar_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Deja que NavigationUI maneje la navegación para el botón de perfil
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item)
    }

    // --- FIN DEL MANEJO DE MENÚS ---

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}
