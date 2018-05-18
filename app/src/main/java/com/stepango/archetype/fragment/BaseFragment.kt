package com.stepango.archetype.fragment

import android.app.Activity
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.stepango.archetype.action.Args
import com.stepango.archetype.action.argsOf
import com.stepango.archetype.activity.BaseActivity
import com.trello.navi2.component.NaviFragment
import io.reactivex.Completable

abstract class BaseFragment<T : ViewDataBinding> : NaviFragment() {

    var onBackPressedHandler: (activity: Activity) -> Boolean = { false }

    abstract val layoutId: Int

    lateinit var binding: T

    abstract fun initBinding(binding: T, state: Bundle?)

    val activity: BaseActivity get() = super.getActivity() as BaseActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        // Workaround for http://stackoverflow.com/questions/27057449/when-switch-fragment-with-swiperefreshlayout-during-refreshing-fragment-freezes
        return if (binding.root is SwipeRefreshLayout) FrameLayout(getActivity()).apply { addView(binding.root) }
        else binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding(binding, savedInstanceState)
    }

}

val Fragment.args: Args get () = arguments ?: Bundle()

inline fun <reified T : BaseFragment<*>> showFragment(
        activity: BaseActivity,
        rootFragmentId: Int,
        containerId: Int,
        args: Args = argsOf()
): Completable = Completable.fromCallable {
    val rootFragment = findFragment(activity, rootFragmentId)
    rootFragment?.let {
        val fragment = findChildFragment(containerId, it)
        if (fragment !is T) T::class.java.newInstance().replaceIn(fragment = rootFragment, containerId = containerId, args = args)
    } ?: throw IllegalArgumentException()
}

fun findChildFragment(containerId: Int, it: BaseFragment<*>) = it.childFragmentManager?.findFragmentById(containerId)

fun findFragment(activity: BaseActivity, containerId: Int) = activity.fragmentManager.findFragmentById(containerId) as? BaseFragment<*>