package com.example.onmbarcode.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.region.RegionFragment
import com.ncapdevi.fragnav.FragNavController
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector

import java.lang.IllegalArgumentException
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector,
    FragNavController.RootFragmentListener {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Fragment>

    val fragNavController: FragNavController =
        FragNavController(supportFragmentManager, R.id.fragmentContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragNavController.rootFragmentListener = this
        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragNavController.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (!fragNavController.popFragment()) {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            fragNavController.popFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = androidInjector

    override val numberOfRootFragments: Int = 1

    override fun getRootFragment(index: Int): Fragment {
        if (index == FragNavController.TAB1) return RegionFragment.newInstance()
        else throw IllegalArgumentException("Unknown index")
    }
}
