package edu.udb.dsm.desafio_practico_03.services

import edu.udb.dsm.desafio_practico_03.model.Recurso
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("recursos")
    suspend fun getRecursos(): List<Recurso>

    @GET("recursos/{id}")
    suspend fun getRecursosById(@Path("id") id: Int): Call<Recurso>

    @POST("recursos")
    suspend fun createRecursos(@Body recurso: Recurso): Call<Recurso>

    @PUT("recursos")
    suspend fun updateRecursos(@Path("id") id: Int, @Body recurso: Recurso): Call<Recurso>

    @DELETE("recursos/{id}")
    suspend fun deleteRecursosById(@Path("id") id: Int): Call<Recurso>
}