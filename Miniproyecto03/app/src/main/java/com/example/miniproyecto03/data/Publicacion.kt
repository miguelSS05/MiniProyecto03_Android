package com.example.miniproyecto03.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Publicacion")
data class Publicacion(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "titulo")
    var titulo: String = "",

    @ColumnInfo(name = "descripcion")
    var descripcion: String = "",

    @ColumnInfo(name = "fotoUri")
    var fotoUri: String = "",

    @ColumnInfo(name = "fecha")
    var fecha: String = ""
)