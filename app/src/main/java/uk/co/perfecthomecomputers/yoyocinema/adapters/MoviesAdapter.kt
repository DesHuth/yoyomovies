package uk.co.perfecthomecomputers.yoyocinema.adapters

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import uk.co.perfecthomecomputers.yoyocinema.R
import uk.co.perfecthomecomputers.yoyocinema.objects.Result
import uk.co.perfecthomecomputers.yoyocinema.utils.DataSource
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper.get
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class MoviesAdapter(c: Context, val moviesList: List<Result>, val clickListener: (Result) -> Unit) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    private  var context: Context = c
    private lateinit var sharedPref: SharedPreferences
    private lateinit var imageBaseUrl: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesAdapter.ViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movies, parent, false)

        sharedPref = PreferenceHelper.defaultPrefs(context)
        imageBaseUrl = sharedPref["imageBaseUrlW200"]!!

        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = moviesList[position]

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 40f
        circularProgressDrawable.start()

        val options = RequestOptions()
                .placeholder(circularProgressDrawable)
                .error(R.drawable.failed)

        Glide.with(context)
                .load(Uri.parse(imageBaseUrl + result.posterPath))
                .apply(options)
                .into(holder.posterImage)

        holder.rating.setText(context.resources.getString(R.string.rating) + " " + result.voteAverage.toString())
        holder.title.setText(result.title)

        val db = DataSource(context)
        db.open()
        val cursor: Cursor = db.query("SELECT id FROM favourites WHERE id=" + result.id)
        cursor.moveToFirst()
        if (cursor.isAfterLast) {
            holder.favouriteButton.setImageResource(R.drawable.favourite_false)
        } else {
            holder.favouriteButton.setImageResource(R.drawable.favourite_true)
        }
        cursor.close()
        db.close()

        holder.description.setText(result.overview)
        holder.releaseDate.setText(formatDate(result.releaseDate!!))

        //  Row click listener, passed to MainActivity onItemClicked
        (holder as ViewHolder).bind(moviesList[position], clickListener)

        //  Favourite Button Click Handler
        holder.favouriteButton.setOnClickListener {
            val db = DataSource(context)
            db.open()
            if (moviesList[position].isFavourite!!) {
                moviesList[position].isFavourite = false
                holder.favouriteButton.setImageResource(R.drawable.favourite_false)
                db.execute("DELETE FROM favourites WHERE id=" + moviesList[position].id)
            } else {
                moviesList[position].isFavourite = true
                holder.favouriteButton.setImageResource(R.drawable.favourite_true)
                val title: String = URLEncoder.encode(moviesList[position].title, "UTF-8")
                val overview: String = URLEncoder.encode(moviesList[position].overview, "UTF-8")
                db.execute("INSERT INTO favourites (id, vote_average, title, poster_path, overview, release_date) VALUES (${moviesList[position].id}, '${moviesList[position].voteAverage}', '$title', '${moviesList[position].posterPath}', '$overview', '${moviesList[position].releaseDate}')")
            }
            db.close()
        }
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        var posterImage: ImageView
        var rating: TextView
        var title: TextView
        var favouriteButton: ImageButton
        var description: TextView
        var releaseDate: TextView

        init {
            posterImage = view.findViewById(R.id.poster_image) as ImageView
            rating = view.findViewById(R.id.rating) as TextView
            title = view.findViewById(R.id.title) as TextView
            favouriteButton = view.findViewById(R.id.movie_favourite_button)
            description = view.findViewById(R.id.description)
            releaseDate = view.findViewById(R.id.release_date)
        }

        fun bind(part: Result, clickListener: (Result) -> Unit) {
            itemView.setOnClickListener { clickListener(part) }
        }
    }

    private fun formatDate(dateText: String): String {
        val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfOut = SimpleDateFormat(" dd MMMM yyyy", Locale.getDefault())
        val date: Date = sdfIn.parse(dateText)
        val dateString = context.resources.getString(R.string.released) + sdfOut.format(date)
        return dateString
    }
}
