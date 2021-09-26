package com.example.daggerhiltexample.di

import com.example.daggerhiltexample.repository.MainHttpInterface
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
    fun provideApiHttpInterface(retrofit: Retrofit): MainHttpInterface {
        return retrofit.create(MainHttpInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideMainRepositoryImpl(mainHttpInterface: MainHttpInterface): MainRepository {
        return MainRepositoryImpl(mainHttpInterface = mainHttpInterface)
    }
}