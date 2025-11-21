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
            // 1. Autenticar al usuario con Firebase Authentication
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user!!

            // 2. Si la autenticación es exitosa, obtener los datos del perfil desde Firestore
            val userProfile = getById(firebaseUser.uid)
                ?: return Result.failure(Exception("El perfil del usuario no fue encontrado."))

            Result.success(userProfile)
        } catch (e: Exception) {
            // La excepción puede ser por contraseña incorrecta, usuario no encontrado, etc.
            Result.failure(Exception("El email o la contraseña son incorrectos."))
        }
    }

    suspend fun registerUser(email: String, password: String, username: String, name: String): Result<User> {
        return try {
            // 1. Crear el usuario en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user!!

            // 2. Crear nuestro objeto User para guardarlo en Firestore
            val newUser = User(
                id = firebaseUser.uid, // Usar el UID de Firebase Auth como nuestro ID
                username = username,
                name = name,
                email = email,
                password = "", // NUNCA guardar la contraseña en texto plano
                avatar = "" // Puedes poner una URL de avatar por defecto aquí
            )

            // 3. Guardar el objeto User en la colección "users" de Firestore
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