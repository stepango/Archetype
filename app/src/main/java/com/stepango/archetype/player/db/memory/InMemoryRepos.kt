package com.stepango.archetype.player.db.memory

import com.stepango.archetype.db.KeyValueRepo
import com.stepango.archetype.player.db.EpisodesModelRepo
import com.stepango.archetype.player.db.model.EpisodesModel

class InMemoryEpisodesRepo : EpisodesModelRepo, KeyValueRepo<Long, EpisodesModel> by InMemoryKeyValueRepo<Long, EpisodesModel>()