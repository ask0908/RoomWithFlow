package com.example.roomtest.util

import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * parses error response body
 */
object ErrorUtils {

    fun parseError(response: Response<*>, retrofit: Retrofit): com.example.roomtest.model.Error? {
        val converter = retrofit.responseBodyConverter<com.example.roomtest.model.Error>(com.example.roomtest.model.Error::class.java, arrayOfNulls(0))
        return try {
            converter.convert(response.errorBody()!!)
        } catch (e: IOException) {
            com.example.roomtest.model.Error()
        }
    }
}