package uk.co.perfecthomecomputers.yoyocinema

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_movie_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.co.perfecthomecomputers.yoyocinema.objects.Genre
import uk.co.perfecthomecomputers.yoyocinema.objects.Movie
import uk.co.perfecthomecomputers.yoyocinema.objects.Result
import uk.co.perfecthomecomputers.yoyocinema.retrofit.YoyoClient
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper.get
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.util.DisplayMetrics
import android.view.View
import uk.co.perfecthomecomputers.yoyocinema.objects.ProductionCompany
import uk.co.perfecthomecomputers.yoyocinema.utils.DataSource
import java.net.URLEncoder


class MovieDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var imageBaseUrlW200: String
    private lateinit var imageBaseUrlW500: String
    lateinit var result: Result
    lateinit var movie: Movie
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        val result: Result = intent.extras.get("result") as Result
        this.result = result
        sharedPref = PreferenceHelper.defaultPrefs(this)
        imageBaseUrlW200 = sharedPref["imageBaseUrlW200"]!!
        imageBaseUrlW500 = sharedPref["imageBaseUrlW500"]!!
        context = this

        movie_favourite_button.setOnClickListener(this)
        //  Populate the view with the information available from the search Result object
        createView()
        //  Get full movie details
        getMovie()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.movie_favourite_button -> {
                val db = DataSource(context)
                db.open()
                if (result.isFavourite!!) {
                    result.isFavourite = false
                    movie_favourite_button.setImageResource(R.drawable.favourite_false)
                    db.execute("DELETE FROM favourites WHERE id=" + result.id)
                } else {
                    result.isFavourite = true
                    movie_favourite_button.setImageResource(R.drawable.favourite_true)
                    val title: String = URLEncoder.encode(result.title, "UTF-8")
                    val overview: String = URLEncoder.encode(result.overview, "UTF-8")
                    db.execute("INSERT INTO favourites (id, vote_average, title, poster_path, overview, release_date) VALUES (${result.id}, '${result.voteAverage}', '$title', '${result.posterPath}', '$overview', '${result.releaseDate}')")
                }
                db.close()
            }
        }
    }

    private fun createView() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 40f
        circularProgressDrawable.start()

        val options = RequestOptions()
                .placeholder(circularProgressDrawable)
                .error(R.drawable.failed)

        Glide.with(this)
                .load(Uri.parse(imageBaseUrlW500 + result.backdropPath))
                .apply(options)
                .into(header_image)

        //  Make the top image full width
        header_image.layoutParams.width = width
        val height: Int = width * 281 / 500
        header_image.layoutParams.height = height

        if (result.isFavourite!!) {
          movie_favourite_button.setImageResource(R.drawable.favourite_true)
        }
        movie_title.setText(result.title)

        Glide.with(this)
                .load(Uri.parse(imageBaseUrlW200 + result.posterPath))
                .apply(options)
                .into(poster_image)

        rating.setText(resources.getString(R.string.rating) + " " + result.voteAverage.toString())
        description.setText(result.overview)
        release_date.setText(formatDate(result.releaseDate!!))
    }

    private fun formatDate(dateText: String): String {
        val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfOut = SimpleDateFormat(" dd MMMM yyyy", Locale.getDefault())
        val date: Date = sdfIn.parse(dateText)
        val dateString = resources.getString(R.string.released) + sdfOut.format(date)
        return dateString
    }

    private fun getMovie() {
        val movieID: Int = result.id!!
        val baseUrl: String? = sharedPref["baseUrl"]
        val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())

        val retrofit = builder.build()
        val client = retrofit.create<YoyoClient>(YoyoClient::class.java)
        val call = client.getMovieDetails(movieID.toString())

        call.enqueue(
                object : Callback<Movie> {
                    override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                        if (response.code() == 200) {
                            movie = response.body()!!

                            if (movie.homepage != null) {
                                website.setText(resources.getString(R.string.website) + " " + movie.homepage)
                            }

                            var genreList: String = resources.getString(R.string.genres)
                            val genres: List<Genre> = movie.genres!!
                            var i: Int = 0
                            while (i < genres.size) {
                                genreList += " " + genres[i].name + ","
                                i++
                            }
                            genreList = genreList.substring(0, genreList.length - 1)
                            genre.setText(genreList)

                            val formatter = DecimalFormat("$###,###,###")

                            budget_details_text.setText(formatter.format(movie.budget))
                            revenue_details_text.setText(formatter.format(movie.revenue))
                            if (movie.runtime == null) {
                                runtime_details_text.setText("")
                            } else {
                                runtime_details_text.setText(movie.runtime.toString() + " " + resources.getString(R.string.runtime_details))
                            }
                        }
                    }

                    override fun onFailure(call: Call<Movie>, t: Throwable) {
                        Log.d("YOYO", "RETROFIT ERROR")
                    }
                })

    }
}
