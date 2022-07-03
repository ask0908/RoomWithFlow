package com.example.roomtest.data

import com.example.roomtest.data.local.MovieDao
import com.example.roomtest.data.remote.MovieRemoteDataSource
import com.example.roomtest.model.MovieDesc
import com.example.roomtest.model.TrendingMovieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieDao: MovieDao
) {
    suspend fun fetchTrendingMovies(): Flow<com.example.roomtest.model.Result<TrendingMovieResponse>?> {
        return flow {
            emit(fetchTrendingMoviesCached())
            emit(com.example.roomtest.model.Result.loading(""))
            val result = movieRemoteDataSource.fetchTrendingMovies()

            // Cache to database if response is successful
            if (result.status == com.example.roomtest.model.Result.Status.SUCCESS) {
                result.data?.results?.let { it ->
                    movieDao.deleteAll(it)
                    movieDao.insertAll(it)
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO) as Flow<com.example.roomtest.model.Result<TrendingMovieResponse>?>
    }

    private fun fetchTrendingMoviesCached(): com.example.roomtest.model.Result<TrendingMovieResponse>? =
        movieDao.getAll()?.let {
            com.example.roomtest.model.Result.success(TrendingMovieResponse(it))
        }

    suspend fun fetchMovie(id: Int): Flow<com.example.roomtest.model.Result<MovieDesc>> {
        return flow {
            emit(com.example.roomtest.model.Result.loading(MovieDesc(0, "", "", "", mutableListOf())))
            emit(movieRemoteDataSource.fetchMovie(id))
        }.flowOn(Dispatchers.IO)
    }
}