package com.ekik.protractor.activities

import android.os.Bundle
import com.ekik.protractor.R
import com.ekik.protractor.di.MAIN_NAVIGATOR
import com.ekik.protractor.fragments.HomeFragment
import com.ekik.protractor.navigation.MainActivityNavigator
import com.ekik.protractor.navigation.Navigator

class MainActivity : BaseActivity() {

    override val contentId: Int
        get() = R.layout.activity_main

    override val rootId: Int
        get() = R.id.root_layout

    override val navigator: Navigator = MainActivityNavigator(supportFragmentManager, rootId)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initHomeFragment()
    }

    private fun initHomeFragment() {
        navigator.navigateTo(HomeFragment.newInstance(), false)
    }
}