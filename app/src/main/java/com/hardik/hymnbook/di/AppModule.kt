package com.hardik.hymnbook.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hardik.hymnbook.data.repository.HymnBookIndexRepositoryImpl
import com.hardik.hymnbook.data.repository.HymnBookItemRepositoryImpl
import com.hardik.hymnbook.domain.repostitroy.HymnBookIndexRepository
import com.hardik.hymnbook.domain.repostitroy.HymnBookItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    /// return repository from the repositoryImpl
    @Provides
    fun bindHymnBookIndexRepository(hymnBookIndexRepository: HymnBookIndexRepositoryImpl): HymnBookIndexRepository {
        return hymnBookIndexRepository
    }

    /// return repository from the repositoryImpl
    @Provides
    fun bindHymnBookItemRepository(hymnBookItemRepository: HymnBookItemRepositoryImpl): HymnBookItemRepository {
        return hymnBookItemRepository
    }
}
