package com.hardik.hymnbook.data.repository

import android.content.Context
import com.google.gson.Gson
import com.hardik.hymnbook.common.Constants.BASE_HYMN_BOOK_INDEX
import com.hardik.hymnbook.common.Resource
import com.hardik.hymnbook.data.dto.HymnBookIndexList
import com.hardik.hymnbook.domain.repostitroy.HymnBookIndexRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HymnBookIndexRepositoryImpl @Inject constructor(private val gson: Gson, @ApplicationContext private val context: Context) : HymnBookIndexRepository {


    override suspend fun getHymnBookIndexList(): Resource<HymnBookIndexList> {
        return try {
            // Read JSON file from assets
            val jsonFile = context.assets.open(BASE_HYMN_BOOK_INDEX).bufferedReader().use { it.readText() }

            // Parse JSON into HymnBookIndexList object
            val data = gson.fromJson(jsonFile, HymnBookIndexList::class.java)

            // Return success with parsed data
            Resource.Success(data)
        } catch (exception: Exception) {
            // Return failure with exception message
            Resource.Error("Failed to fetch hymn book index: ${exception.message}")
        }
    }
}