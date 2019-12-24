package com.example.onmbarcode.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.desk.DeskFragment
import com.example.onmbarcode.presentation.login.LoginFragment
import com.example.onmbarcode.service.SyncWorkManager
import com.ncapdevi.fragnav.FragNavController
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector

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
        fragNavController.rootFragmentListener = this
        this.savedInstanceState = savedInstanceState
//        fragNavController.initialize(FragNavController.TAB1, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        presenter.start(savedInstanceState == null)
        //TODO remove
        WorkManager.getInstance(application).getWorkInfosByTagLiveData(SyncWorkManager.TAG_SYNC)
            .observe(this, Observer {
                val workInfo = it.firstOrNull()
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    val builder = NotificationCompat.Builder(this, OnmBarCodeApp.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_cloud_done) //TODO change icon
                        .setContentTitle("Synchronisation terminée")
                        .setColor(ContextCompat.getColor(this, R.color.notification_icon_color))
                        .setContentText("Tous les équipements ont été synchronisés.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    NotificationManagerCompat.from(this).notify(405, builder.build())
                }
            })
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
