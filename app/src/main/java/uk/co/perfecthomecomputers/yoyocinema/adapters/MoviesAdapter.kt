package uk.co.perfecthomecomputers.yoyocinema.adapters

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import uk.co.perfecthomecomputers.yoyocinema.R
import uk.co.perfecthomecomputers.yoyocinema.objects.Result
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper.get
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
        holder.favouriteButton.setImageResource(R.drawable.favourite_true)
        holder.description.setText(result.overview)
        holder.releaseDate.setText(formatDate(result.releaseDate!!))

        (holder as ViewHolder).bind(moviesList[position], clickListener)
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
