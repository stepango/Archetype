package com.stepango.archetype.player.data.db.memory

import com.stepango.archetype.db.KeyValueRepo
import com.stepango.archetype.player.data.db.EpisodesModelRepo
import com.stepango.archetype.player.data.db.model.EpisodesModel

class InMemoryEpisodesRepo : EpisodesModelRepo,
        KeyValueRepo<Long, EpisodesModel> by InMemoryKeyValueRepo<Long, EpisodesModel>()