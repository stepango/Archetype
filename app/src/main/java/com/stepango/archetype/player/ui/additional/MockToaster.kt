package com.stepango.archetype.player.ui.additional

import com.stepango.archetype.logger.logger
import com.stepango.archetype.ui.Toaster

class MockToaster : Toaster {
    override fun showToast(msg: String) {
        logger.i(msg)
    }

    override fun showToast(id: Int, vararg args: Any) {
        logger.i("$id")
    }

    override fun showError(id: Int) {
        logger.e("$id")
    }

    override fun showError(t: Throwable, id: Int) {
        logger.e("$id: ${t.message}")
    }
}