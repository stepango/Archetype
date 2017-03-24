package com.ninetyseconds.auckland.core.databindings

import android.databinding.BindingAdapter
import android.view.View
import com.ninetyseconds.auckland.aa.ArgsHolder
import com.ninetyseconds.auckland.di.Injector

@BindingAdapter("performAction", "actionData", requireAll = true)
fun performAction(v: View, actionId: Int, actionData: ArgsHolder?) {
    actionData ?: return
    v.setOnClickListener {
        Injector().contextActionsHandler.handleAction(v.context, actionId, actionData.args())
    }
}
