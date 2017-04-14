package com.stepango.archetype.db

import io.reactivex.Completable

interface RepoSupervisor {
    fun clear(): Completable
}