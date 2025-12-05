package com.example.tournamaker.data.repository

import com.example.tournamaker.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun getAll(): List<User> {
        return try {
            usersCollection.get().await().documents.mapNotNull {
                it.toObject<User>()?.copy(id = it.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getById(id: String): User? {
        return try {
            usersCollection.document(id).get().await().toObject<User>()?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user!!

            if (!firebaseUser.isEmailVerified) {
                return Result.failure(Exception("Por favor, verifica tu correo electrónico para poder iniciar sesión."))
            }

            val userProfile = getById(firebaseUser.uid)
                ?: return Result.failure(Exception("El perfil del usuario no fue encontrado."))

            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(Exception("El email o la contraseña son incorrectos."))
        }
    }

    suspend fun registerUser(email: String, password: String, username: String, name: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user!!
            firebaseUser.sendEmailVerification().await()

            val newUser = User(
                id = firebaseUser.uid,
                username = username,
                name = name,
                email = email,
                avatar = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png" // URL de avatar por defecto
            )

            usersCollection.document(firebaseUser.uid).set(newUser).await()
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun update(id: String, data: Map<String, Any>): Result<Unit> {
        return try {
            usersCollection.document(id).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(id: String): Result<Unit> {
        return try {
            usersCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeById(id: String): Flow<User?> = callbackFlow {
        val listener = usersCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject<User>()?.copy(id = snapshot.id))
            }
        awaitClose { listener.remove() }
    }
}