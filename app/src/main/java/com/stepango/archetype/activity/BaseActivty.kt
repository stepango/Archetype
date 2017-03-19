package com.stepango.archetype.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.stepango.archetype.fragment.replaceIn
import com.stepango.archetype.fragment.BaseFragment
import com.trello.navi2.component.NaviActivity

abstract class BaseActivity : NaviActivity() {

    open val onBackPressedHandler: (activity: Activity) -> Boolean by lazy {
        fragment.onBackPressedHandler
    }
    open val fragmentProducer: () -> BaseFragment<*> = { throw IllegalArgumentException()    }
    open val containerId = android.R.id.content
    lateinit var fragment: BaseFragment<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityState(savedInstanceState)
    }

    private fun initActivityState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            fragment = fragmentProducer()
            fragment.replaceIn(this, containerId)
        } else {
            fragment = fragmentManager.findFragmentById(containerId) as BaseFragment<*>
        }
    }

    override fun onBackPressed() {
        if (!onBackPressedHandler(this)) super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragment.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * use this function because android's AppCompatActivity not allowing to mock startActivityForResult method
     */
    fun startActivityForResultOverrode(intent: Intent?, requestCode: Int, options: Bundle?) {
        startActivityForResult(intent, requestCode, options)
    }
}

fun BaseActivity?.showKeyboardOnStart()
        = this?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

tailrec fun Context.asBaseActivity(): BaseActivity? {
    if (this is BaseActivity) {
        return this
    } else if (this is ContextWrapper) {
        return this.baseContext.asBaseActivity()
    }
    return null
}
