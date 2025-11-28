package com.example.domain.di

import com.example.domain.repository.AuthRepository
import com.example.domain.repository.NoteRepository
import com.example.domain.usecase.auth.IsAuthorizedUseCase
import com.example.domain.usecase.auth.LoginUseCase
import com.example.domain.usecase.auth.LogoutUseCase
import com.example.domain.usecase.auth.RegisterUseCase
import com.example.domain.usecase.note.AddNoteUseCase
import com.example.domain.usecase.note.DeleteNoteUseCase
import com.example.domain.usecase.note.GetNotesUseCase
import com.example.domain.usecase.note.SyncNotesUseCase
import com.example.domain.usecase.note.ToggleDoneUseCase
import com.example.domain.usecase.note.TogglePinUseCase
import com.example.domain.usecase.note.UpdateNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideLoginUseCase(repo: AuthRepository) = LoginUseCase(repo)
    @Provides
    fun provideRegisterUseCase(repo: AuthRepository) = RegisterUseCase(repo)
    @Provides
    fun provideLogoutUseCase(repo: AuthRepository) = LogoutUseCase(repo)
    @Provides
    fun provideIsAuthorizedUseCase(repo: AuthRepository) = IsAuthorizedUseCase(repo)

    @Provides
    fun provideAddNoteUseCase(repo: NoteRepository) = AddNoteUseCase(repo)
    @Provides
    fun provideUpdateNoteUseCase(repo: NoteRepository) = UpdateNoteUseCase(repo)
    @Provides
    fun provideDeleteNoteUseCase(repo: NoteRepository) = DeleteNoteUseCase(repo)
    @Provides
    fun provideGetNotesUseCase(repo: NoteRepository) = GetNotesUseCase(repo)
    @Provides
    fun provideToggleDoneUseCase(repo: NoteRepository) = ToggleDoneUseCase(repo)
    @Provides
    fun provideTogglePinUseCase(repo: NoteRepository) = TogglePinUseCase(repo)
    @Provides
    fun provideSyncNotesUseCase(repo: NoteRepository, isAuthorizedUseCase: IsAuthorizedUseCase) =
        SyncNotesUseCase(repo, isAuthorizedUseCase)
}
