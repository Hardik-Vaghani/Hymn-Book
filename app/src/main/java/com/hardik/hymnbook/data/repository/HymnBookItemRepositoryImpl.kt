package com.hardik.hymnbook.data.repository

import android.content.Context
import com.google.gson.Gson
import com.hardik.hymnbook.common.Constants
import com.hardik.hymnbook.common.Resource
import com.hardik.hymnbook.data.dto.HymnBookIndexList
import com.hardik.hymnbook.data.dto.HymnBookItem
import com.hardik.hymnbook.domain.repostitroy.HymnBookItemRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HymnBookItemRepositoryImpl @Inject constructor(private val gson: Gson, @ApplicationContext private val context: Context): HymnBookItemRepository {

    override suspend fun getHymnBookItem(fileName: String): Resource<HymnBookItem> {
        return try {
            // Read JSON file from assets
            val jsonFile = context.assets.open(fileName).bufferedReader().use { it.readText() }

            // Parse JSON into HymnBookIndexList object
            val data = gson.fromJson(jsonFile, HymnBookItem::class.java)

            // Return success with parsed data
            Resource.Success(data)
        } catch (exception: Exception) {
            // Return failure with exception message
            Resource.Error("Failed to fetch hymn book index: ${exception.message}")
        }
    }
}