package uk.co.perfecthomecomputers.yoyocinema

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import uk.co.perfecthomecomputers.yoyocinema.adapters.MoviesAdapter
import uk.co.perfecthomecomputers.yoyocinema.objects.Result

class MovieListFragment : Fragment() {

    private var listener: MovieListFragment.OnFragmentInteractionListener? = null

    private lateinit var adapter: MoviesAdapter
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_movie_list, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerview) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = layoutManager

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onItemClicked(item: Result)
    }

    public fun setData(context: Context, results: List<Result>) {
        adapter = MoviesAdapter(context, results, { result : Result -> itemClicked(result) })
        recyclerView!!.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    //  Click Listener for RecyclerView
    private fun itemClicked(result : Result) {
        //  Pass clicked item up to MainActivity
        listener?.onItemClicked(result)
    }
}
