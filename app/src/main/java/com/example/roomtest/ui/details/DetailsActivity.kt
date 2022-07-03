package com.example.roomtest.ui.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.roomtest.Config
import com.example.roomtest.R
import com.example.roomtest.model.MovieDesc
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailsViewModel>()
    private lateinit var loading: ProgressBar
    private lateinit var vParent: ConstraintLayout
    private lateinit var ivCover: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loading = findViewById(R.id.loading)
        vParent = findViewById(R.id.vParent)
        ivCover = findViewById(R.id.ivCover)
        tvTitle = findViewById(R.id.tvTitle)
        tvGenre = findViewById(R.id.tvGenre)
        tvDescription = findViewById(R.id.tvDescription)

        intent?.getIntExtra(EXTRAS_MOVIE_ID, 0)?.let { id ->
            viewModel.getMovieDetail(id)
            subscribeUi()
        } ?: showError("잘못된 영화 정보입니다")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun subscribeUi() {
        viewModel.movie.observe(this) { result ->
            when (result.status) {
                com.example.roomtest.model.Result.Status.SUCCESS -> {
                    result.data?.let {
                        updateUi(it)
                    }
                    loading.visibility = View.GONE
                }

                com.example.roomtest.model.Result.Status.ERROR -> {
                    result.message?.let {
                        showError(it)
                    }
                    loading.visibility = View.GONE
                }

                com.example.roomtest.model.Result.Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showError(msg: String) {
        Snackbar.make(vParent, msg, Snackbar.LENGTH_INDEFINITE).setAction("DISMISS") {}.show()
    }

    private fun updateUi(movie: MovieDesc) {
        title = movie.title
        tvTitle.text = movie.title
        tvDescription.text = movie.overview
        Glide.with(this).load(Config.IMAGE_URL + movie.poster_path)
            .apply(
                RequestOptions().override(400, 400).centerInside()
                    .placeholder(R.mipmap.ic_launcher)
            ).into(ivCover)

        val genreNames = mutableListOf<String>()
        movie.genres.map {
            genreNames.add(it.name)
        }
        tvGenre.text = genreNames.joinToString(separator = ", ")
    }

    companion object {
        const val EXTRAS_MOVIE_ID = "movie_id"
    }

}