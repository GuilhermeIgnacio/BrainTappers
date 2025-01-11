package com.guilherme.braintappers.data

import android.accounts.NetworkErrorException
import android.content.ContentValues.TAG
import android.net.http.NetworkException
import android.util.Log
import com.guilherme.braintappers.domain.DataError
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.domain.model.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.json.Json

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

    override suspend fun fetchTriviaByCategory(
        numberOfQuestions: String,
        categoryId: String,
        difficulty: String,
        type: String
    ): Result<ApiResponse, DataError> {
        return try {
            val response =
                httpClient.get("https://opentdb.com/api.php?$numberOfQuestions&category=$categoryId$difficulty$type")

            when (response.status) {
                HttpStatusCode.OK -> {
                    println(response.body<String>())
                    Log.e(TAG, "API RESPONSE:" + response.body<ApiResponse>())
                    Result.Success(response.body<ApiResponse>())
                }

                HttpStatusCode.NotFound -> {
                    Log.e(TAG, "Error 404")
                    Result.Error(DataError.NOT_FOUND)
                }

                HttpStatusCode.ServiceUnavailable -> {
                    Result.Error(DataError.SERVICE_UNAVAILABLE)
                }

                HttpStatusCode.BadGateway -> {
                    Result.Error(DataError.BAD_GATEWAY)
                }

                HttpStatusCode.Forbidden -> {
                    Result.Error(DataError.FORBIDDEN)
                }

                HttpStatusCode.Unauthorized -> {
                    Result.Error(DataError.UNAUTHORIZED)
                }

                else -> {
                    Result.Error(DataError.UNKNOWN)
                }

            }


        } catch (e: UnresolvedAddressException) { // Internet Exceptions

            Log.e(TAG, "IOException", e)
            Result.Error(DataError.NO_INTERNET)
        } catch (e: Exception) {

            e.printStackTrace()
            Result.Error(DataError.UNKNOWN)

        }
    }
}