package com.example.daggerhiltexample.di

import com.example.daggerhiltexample.repository.ApiHttpInterface
import com.example.daggerhiltexample.repository.MainRepository
import com.example.daggerhiltexample.repository.MainRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object RepoModule {

    @Singleton
    @Provides
    fun provideApiHttpInterface(retrofit: Retrofit): ApiHttpInterface {
        return retrofit.create(ApiHttpInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideMainRepositoryImpl(apiHttpInterface: ApiHttpInterface): MainRepository {
        return MainRepositoryImpl(apiHttpInterface = apiHttpInterface)
    }
}