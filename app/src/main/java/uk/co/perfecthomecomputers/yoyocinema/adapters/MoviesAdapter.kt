package uk.co.perfecthomecomputers.yoyocinema.adapters

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import uk.co.perfecthomecomputers.yoyocinema.R
import uk.co.perfecthomecomputers.yoyocinema.objects.Result

class MoviesAdapter(val moviesList: List<Result>) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesAdapter.ViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movies, parent, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val debugString = moviesList[position]
//        holder.text.setText(debugString)
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        var text: TextView

        init {
            text = view.findViewById(R.id.text) as TextView
        }
    }
}
