package com.example.data.di

import com.example.data.auth.AuthRepositoryImpl
import com.example.data.local.NoteDao
import com.example.data.remote.FirebaseNoteDataSource
import com.example.data.remote.NoteRemoteDataSource
import com.example.data.repository.NoteRepositoryImpl
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    fun provideNoteRepository(
        dao: NoteDao,
        remote: NoteRemoteDataSource
    ): NoteRepository = NoteRepositoryImpl(dao, remote)

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): NoteRemoteDataSource = FirebaseNoteDataSource(firestore, auth)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
