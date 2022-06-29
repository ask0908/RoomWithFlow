package com.example.roomtest.data.remote

import com.example.roomtest.model.MovieDesc
import com.example.roomtest.model.TrendingMovieResponse
import com.example.roomtest.network.services.MovieService
import com.example.roomtest.util.ErrorUtils
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * fetches data from remote source
 */
class MovieRemoteDataSource @Inject constructor(private val retrofit: Retrofit) {

    suspend fun fetchTrendingMovies(): com.example.roomtest.model.Result<TrendingMovieResponse> {
        val movieService = retrofit.create(MovieService::class.java);
        return getResponse(
            request = { movieService.getPopularMovies() },
            defaultErrorMessage = "Error fetching Movie list")

    }

    suspend fun fetchMovie(id: Int): com.example.roomtest.model.Result<MovieDesc> {
        val movieService = retrofit.create(MovieService::class.java);
        return getResponse(
            request = { movieService.getMovie(id) },
            defaultErrorMessage = "Error fetching Movie Description")
    }

    private suspend fun <T> getResponse(request: suspend () -> Response<T>, defaultErrorMessage: String): com.example.roomtest.model.Result<T> {
        return try {
            println("I'm working in thread ${Thread.currentThread().name}")
            val result = request.invoke()
            if (result.isSuccessful) {
                return com.example.roomtest.model.Result.success(result.body())
            } else {
                val errorResponse = ErrorUtils.parseError(result, retrofit)
                com.example.roomtest.model.Result.error(errorResponse?.status_message ?: defaultErrorMessage, errorResponse)
            }
        } catch (e: Throwable) {
            com.example.roomtest.model.Result.error("Unknown Error", null)
        }
    }
}