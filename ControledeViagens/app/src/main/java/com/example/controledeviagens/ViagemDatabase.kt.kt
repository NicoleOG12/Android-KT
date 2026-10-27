package com.example.controledeviagens

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ViagemDatabase(context: Context) :
    SQLiteOpenHelper(context, "controle_viagens.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE viagens (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                data TEXT NOT NULL,
                kmInicial REAL NOT NULL,
                kmFinal REAL NOT NULL,
                litros REAL NOT NULL,
                combustivel TEXT NOT NULL,
                pedagio REAL NOT NULL,
                valor REAL NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
    }

    fun inserirViagem(viagem: Viagem): Long {
        val valores = ContentValues().apply {
            put("data", viagem.data)
            put("kmInicial", viagem.kmInicial)
            put("kmFinal", viagem.kmFinal)
            put("litros", viagem.litros)
            put("combustivel", viagem.combustivel)
            put("pedagio", viagem.pedagio)
            put("valor", viagem.valor)
        }

        return writableDatabase.insert(
            "viagens",
            null,
            valores
        )
    }

    fun listarViagens(): List<Viagem> {
        val lista = mutableListOf<Viagem>()

        readableDatabase.query(
            "viagens",
            null,
            null,
            null,
            null,
            null,
            "id DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Viagem(
                        data = cursor.getString(
                            cursor.getColumnIndexOrThrow("data")
                        ),
                        kmInicial = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("kmInicial")
                        ),
                        kmFinal = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("kmFinal")
                        ),
                        litros = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("litros")
                        ),
                        combustivel = cursor.getString(
                            cursor.getColumnIndexOrThrow("combustivel")
                        ),
                        pedagio = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("pedagio")
                        ),
                        valor = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("valor")
                        )
                    )
                )
            }
        }

        return lista
    }
}