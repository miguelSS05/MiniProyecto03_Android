package com.example.miniproyecto03.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicacionDao {
    @Query("SELECT * FROM Publicacion")
    fun obtenerTodos(): Flow<List<Publicacion>>

    @Query("SELECT * FROM Publicacion WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Publicacion?

    @Insert
    suspend fun insertar(publicacion: Publicacion)

    @Delete
    suspend fun eliminar(publicacion: Publicacion): Int

    @Update
    suspend fun actualizar(publicacion: Publicacion): Int
}