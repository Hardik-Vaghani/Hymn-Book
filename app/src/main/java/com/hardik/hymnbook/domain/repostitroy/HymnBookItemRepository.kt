package com.hardik.hymnbook.domain.repostitroy

import com.hardik.hymnbook.common.Resource
import com.hardik.hymnbook.data.dto.HymnBookItem

interface HymnBookItemRepository {
    suspend fun getHymnBookItem(fileName: String): Resource<HymnBookItem>
}