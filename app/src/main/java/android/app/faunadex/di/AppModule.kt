package android.app.faunadex.di

import android.app.faunadex.data.repository.AnimalRepositoryImpl
import android.app.faunadex.data.repository.AuthRepositoryImpl
import android.app.faunadex.data.repository.UserRepositoryImpl
import android.app.faunadex.domain.repository.AnimalRepository
import android.app.faunadex.domain.repository.AuthRepository
import android.app.faunadex.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): UserRepository = UserRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        userRepository: UserRepository
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth, userRepository)

    @Provides
    @Singleton
    fun provideAnimalRepository(
        firestore: FirebaseFirestore
    ): AnimalRepository = AnimalRepositoryImpl(firestore)
}
