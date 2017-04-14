package com.stepango.archetype.databindings

import android.databinding.BindingAdapter
import android.view.View
import com.stepango.archetype.player.data.wrappers.ArgsHolder
import com.stepango.archetype.player.di.Injector

@BindingAdapter("performAction", "actionData", requireAll = true)
fun performAction(v: View, actionId: Int, actionData: ArgsHolder?) {
    actionData ?: return
    v.setOnClickListener {
        Injector().contextActionsHandler().handleAction(v.context, actionId, actionData.args())
    }
}
