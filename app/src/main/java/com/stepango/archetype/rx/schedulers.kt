package com.stepango.archetype.rx

import io.reactivex.Scheduler
import kotlin.properties.Delegates


var networkScheduler by Delegates.notNull<Scheduler>()
var actionScheduler by Delegates.notNull<Scheduler>()
var nonDisposableActionScheduler by Delegates.notNull<Scheduler>()
var uiScheduler by Delegates.notNull<Scheduler>()