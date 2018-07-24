package uk.co.perfecthomecomputers.yoyocinema

import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.CircularProgressDrawable
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
import uk.co.perfecthomecomputers.yoyocinema.objects.SearchResults
import uk.co.perfecthomecomputers.yoyocinema.retrofit.YoyoClient
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper.get
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.util.DisplayMetrics



class MovieDetailActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var imageBaseUrlW200: String
    private lateinit var imageBaseUrlW500: String
    lateinit var result: Result
    lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        val result: Result = intent.extras.get("result") as Result
        this.result = result
        sharedPref = PreferenceHelper.defaultPrefs(this)
        imageBaseUrlW200 = sharedPref["imageBaseUrlW200"]!!
        imageBaseUrlW500 = sharedPref["imageBaseUrlW500"]!!

        //  Populate the view with the information available from the search Result object
        createView()
        //  Get full movie details
        getMovie()
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
        val client = retrofit.create<YoyoClient>(YoyoClient::class.java!!)
        val call = client.getMovieDetails(movieID.toString())

        call.enqueue(
                object : Callback<Movie> {
                    override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                        Log.d("YOYO", "RESPONSE CODE: "+ response.code())
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

                            val formatter = DecimalFormat("$###,###,###.00")

                            budget_details_text.setText(formatter.format(movie.budget))
                            revenue_details_text.setText(formatter.format(movie.revenue))
                            runtime_details_text.setText(movie.runtime.toString() + " " + resources.getString(R.string.runtime_details))
                        }
                    }

                    override fun onFailure(call: Call<Movie>, t: Throwable) {
                        Log.d("YOYO", "RETROFIT ERROR")
                    }
                })

    }
}
