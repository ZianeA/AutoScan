package com.meteoalgerie.autoscan.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.presentation.desk.DeskFragment
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
    lateinit var presenter: MainPresenter

    var savedInstanceState: Bundle? = null
    val fragNavController: FragNavController =
        FragNavController(supportFragmentManager, R.id.fragmentContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        fragNavController.rootFragmentListener = this
        this.savedInstanceState = savedInstanceState
    }

    override fun onStart() {
        super.onStart()
        presenter.start(savedInstanceState == null)
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        savedInstanceState = outState
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
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)
    }

    override fun displayDeskScreen() {
        fragNavController.initialize(FragNavController.TAB2, savedInstanceState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = androidInjector

    override val numberOfRootFragments: Int = 2

    override fun getRootFragment(index: Int): Fragment {
        if (index == FragNavController.TAB1) return LoginFragment.newInstance()
        if (index == FragNavController.TAB2) return DeskFragment.newInstance()
        else throw IllegalArgumentException("Unknown index")
    }
}
