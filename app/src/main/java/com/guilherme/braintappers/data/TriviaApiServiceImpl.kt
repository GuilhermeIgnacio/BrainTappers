package com.guilherme.braintappers.data

import android.content.ContentValues.TAG
import android.util.Log
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.domain.model.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.json.Json
import com.guilherme.braintappers.domain.Result

class TriviaApiServiceImpl : TriviaApiService {

    companion object {
        const val ENDPOINT = "https://opentdb.com/api.php?"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 1000 * 15 // 15 Seconds
        }
    }

    override suspend fun fetchTriviaByCategory(categoryId: String): Result<ApiResponse, DataError> {
        return try {
            val response = httpClient.get("https://opentdb.com/api.php?amount=10&category=10")

            when (response.status) {
                HttpStatusCode.OK -> {
                    println(response.body<String>())
                    Log.e(TAG, "API RESPONSE:" + response.body<ApiResponse>())
                    Result.Success(response.body<ApiResponse>())
                }

                HttpStatusCode.NotFound -> {
                    Log.e(TAG, "Error 404")
                    Result.Error(DataError.Response.UNKNOWN)
                }

                else -> Result.Error(DataError.Response.UNKNOWN)

            }


        } catch (e: Exception) {

            Log.e(TAG, "Failed Request")
            e.stackTrace
            Result.Error(DataError.Response.UNKNOWN)

        } catch (e: IOException) { // Internet Exceptions

            Log.e(TAG, "IOException", e)
            Result.Error(DataError.Response.UNKNOWN)
        }
    }
}