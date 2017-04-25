package com.stepango.archetype.player.data.db.memory

import org.junit.Before
import org.junit.Test


class InMemoryKeyValueRepoTest {

    val inMemoryKeyValueRepo = InMemoryKeyValueRepo<String, MyTestClass>()

    @Before
    fun setUp() {
        assertEmpty()
    }

    @Test
    fun saveSingleItem() {
        inMemoryKeyValueRepo.save("a", MyTestClass("aa")).subscribe()
        inMemoryKeyValueRepo.save("b", MyTestClass("bb")).subscribe()
        inMemoryKeyValueRepo.save("b", MyTestClass("cc")).subscribe()

        inMemoryKeyValueRepo.observeAll()
                .firstOrError()
                .test()
                .await()
                .assertValueCount(1)
                .assertValue { it.size == 2 }
                .assertValue { it.none { it.name == "bb" } }
                .assertValue { it.any { it.name == "aa" } }
                .assertValue { it.any { it.name == "cc" } }
    }


    @Test
    fun saveMap() {
        inMemoryKeyValueRepo.save(
                mapOf("a" to MyTestClass("aa"),
                        "b" to MyTestClass("bb")))
                .subscribe()

        inMemoryKeyValueRepo.save(mapOf("b" to MyTestClass("cc")))
                .subscribe()

        inMemoryKeyValueRepo.observeAll()
                .firstOrError()
                .test()
                .await()
                .assertValueCount(1)
                .assertValue { it.size == 2 }
                .assertValue { it.none { it.name == "bb" } }
                .assertValue { it.any { it.name == "aa" } }
                .assertValue { it.any { it.name == "cc" } }
    }

    @Test
    fun removeAll() {
        inMemoryKeyValueRepo.save(
                mapOf("a" to MyTestClass("aa"),
                        "b" to MyTestClass("bb")))
                .subscribe()

        inMemoryKeyValueRepo.removeAll()
                .subscribe()

        assertEmpty()
    }

    @Test
    fun removeKey() {
        inMemoryKeyValueRepo.save(
                mapOf("a" to MyTestClass("aa"),
                        "b" to MyTestClass("bb")))
                .subscribe()

        val testObserver = inMemoryKeyValueRepo.observeAll().test()

        testObserver
                .assertValueCount(1)
                .assertValue { it.size == 2 }

        inMemoryKeyValueRepo.remove("a")
                .subscribe()

        testObserver
                .assertValueCount(2)
                .assertValueAt(1) { it.size == 1 }
                .assertValueAt(1) { it[0].name == "bb" }

        inMemoryKeyValueRepo.remove("b")
                .subscribe()

        assertEmpty()
    }


    @Test
    fun removeKeys() {
        inMemoryKeyValueRepo.save(
                mapOf("a" to MyTestClass("aa"),
                        "b" to MyTestClass("bb"),
                        "c" to MyTestClass("cc")))
                .subscribe()

        val testObserver = inMemoryKeyValueRepo.observeAll().test()

        testObserver
                .assertValueCount(1)
                .assertValue { it.size == 3 }

        inMemoryKeyValueRepo.remove(setOf("a", "c"))
                .subscribe()

        testObserver
                .assertValueCount(2)
                .assertValueAt(1) { it.size == 1 }
                .assertValueAt(1) { it[0].name == "bb" }
    }


    @Test
    fun observeAll() {
        inMemoryKeyValueRepo.save(
                mapOf("a" to MyTestClass("aa"),
                        "b" to MyTestClass("bb")))
                .subscribe()

        val testObservable = inMemoryKeyValueRepo.observeAll().test()

        testObservable
                .assertValueCount(1)
                .assertValue { it.size == 2 }
                .assertValue { it.any { it.name == "bb" } }
                .assertValue { it.any { it.name == "aa" } }

        inMemoryKeyValueRepo.save(
                mapOf("b" to MyTestClass("cc")))
                .subscribe()

        testObservable.assertValueCount(2)
                .assertValueAt(1) { it.any { it.name == "cc" } }
    }

    @Test
    fun observe() {
        inMemoryKeyValueRepo.save(
                mapOf("a" to MyTestClass("aa")))
                .subscribe()

        val testObservable = inMemoryKeyValueRepo.observe("c").test()

        testObservable
                .assertValueCount(1)
                .assertValue { !it.isPresent }

        inMemoryKeyValueRepo.save(
                mapOf("c" to MyTestClass("cc")))
                .subscribe()

        testObservable.assertValueCount(2)
                .assertValueAt(1) { it.isPresent && it.get().name == "cc" }

        inMemoryKeyValueRepo.remove("c")
                .subscribe()

        testObservable.assertValueCount(3)
                .assertValueAt(2) { !it.isPresent }
    }

    private fun assertEmpty() {
        inMemoryKeyValueRepo.observeAll()
                .firstOrError()
                .test()
                .await()
                .assertValueCount(1)
                .assertValue { it.isEmpty() }
    }
}

data class MyTestClass(val name: String)