package com.stepango.archetype.fragment

import android.R
import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.annotation.IdRes
import com.stepango.archetype.action.Args
import com.stepango.archetype.action.argsOf

fun Fragment.replaceIn(
        activity: Activity,
        @IdRes containerId: Int = R.id.content,
        map: Args = argsOf()
) {
    replace(activity.intent?.extras?.apply { putAll(map) }, containerId, activity.fragmentManager)
}

fun Fragment.replaceIn(
        fragment: BaseFragment<*>,
        @IdRes containerId: Int,
        args: Args = argsOf()
) {
    replace(fragment.arguments?.apply { putAll(args) }, containerId, fragment.childFragmentManager)
}

private fun Fragment.replace(bundle: Bundle?, containerId: Int, fm: FragmentManager) {
    val fmt = this
    bundle?.let { if (fmt.arguments == null) fmt.arguments = it else fmt.arguments.putAll(it) }
    fm.beginTransaction()
            .replace(containerId, fmt)
            .commit()
}
