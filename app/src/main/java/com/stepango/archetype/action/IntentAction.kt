package com.stepango.archetype.action

interface IntentAction<T : Any> : ContextAction<T>, IntentMaker
