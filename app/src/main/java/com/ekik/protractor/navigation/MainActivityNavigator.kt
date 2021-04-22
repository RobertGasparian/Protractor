package com.ekik.protractor.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ekik.protractor.R

class MainActivityNavigator(
    private val fragmentManager: FragmentManager,
    @IdRes private val rootLayoutId: Int
): Navigator {
    override fun navigateTo(fragment: Fragment, withBackStack: Boolean, withAnimation: Boolean) {
        val trans = fragmentManager.beginTransaction()
        if (withAnimation) {
            trans.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
        }

        trans.replace(rootLayoutId, fragment)

        if (withBackStack) {
            trans.addToBackStack(fragment::class.java.simpleName)
        }

        trans.commit()
    }

    override fun back(toTag: String?) {
        toTag?.let { tag ->
            fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } ?: run { fragmentManager.popBackStack() }
    }

    override fun home() {
        //TODO clear backStack
    }
}