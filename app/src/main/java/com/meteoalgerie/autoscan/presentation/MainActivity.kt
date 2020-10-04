package com.meteoalgerie.autoscan.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.presentation.desk.DeskFragment
import com.meteoalgerie.autoscan.presentation.download.DownloadFragment
import com.meteoalgerie.autoscan.presentation.login.LoginFragment
import com.ncapdevi.fragnav.FragNavController
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*

import java.lang.IllegalArgumentException
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector,
    FragNavController.RootFragmentListener, MainView {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var storage: PreferenceStorage

    @Inject
    lateinit var presenter: MainPresenter

    val fragNavController: FragNavController =
        FragNavController(supportFragmentManager, R.id.fragmentContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        delegate.localNightMode = storage.themeMode
        setContentView(R.layout.activity_main)

        content.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        fragNavController.rootFragmentListener = this
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragNavController.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        when {
            fragNavController.isRootFragment.not() -> fragNavController.popFragment()
            else -> super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            fragNavController.popFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun displayLoginScreen() {
        fragNavController.replaceFragment(LoginFragment.newInstance())
    }

    override fun displayDeskScreen() {
        fragNavController.replaceFragment(DeskFragment.newInstance())
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = androidInjector

    override val numberOfRootFragments: Int = 1

    override fun getRootFragment(index: Int): Fragment {
        if (index == FragNavController.TAB1) return LoginFragment.newInstance()
        else throw IllegalArgumentException("Unknown index")
    }
}
