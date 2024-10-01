package com.hardik.hymnbook.domain.repostitroy

import com.hardik.hymnbook.common.Resource
import com.hardik.hymnbook.data.dto.HymnBookIndexList

interface HymnBookIndexRepository {
    suspend fun getHymnBookIndexList(): Resource<HymnBookIndexList>
}