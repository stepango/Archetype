package com.ninetyseconds.auckland.core.databindings

import android.databinding.BindingAdapter
import android.view.View
import com.stepango.archetype.action.Args

@BindingAdapter("performAction", "actionData", requireAll = true)
fun performAction(v: View, actionId: Int, actionData: Args?) {
    //    actionData ?: return
    //    v.setOnClickListener {
    //        Injector().contextActionsHandler.handleAction(v.context, actionId, actionData.args())
    //    }
}
