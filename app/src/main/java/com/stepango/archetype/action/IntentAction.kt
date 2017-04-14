package com.stepango.archetype.action

import com.stepango.archetype.player.di.Injector

abstract class IntentAction(
        override val intentMaker: IntentMaker = Injector().intentMaker()
) : ContextAction, IntentMakerHolder
