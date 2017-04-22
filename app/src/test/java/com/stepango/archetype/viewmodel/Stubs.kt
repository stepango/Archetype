package com.stepango.archetype.viewmodel

val onNextStub: (Any) -> Unit = {}
val onErrorStub: (Throwable) -> Unit = {
    System.out.println("On error not implemented")
    it.printStackTrace()
}
val onCompleteStub: () -> Unit = {}