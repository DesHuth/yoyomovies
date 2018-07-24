package uk.co.perfecthomecomputers.yoyocinema

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.co.perfecthomecomputers.yoyocinema.objects.Result
import uk.co.perfecthomecomputers.yoyocinema.objects.SearchResults
import uk.co.perfecthomecomputers.yoyocinema.retrofit.YoyoClient
import uk.co.perfecthomecomputers.yoyocinema.utils.FirstRun
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper.get
import java.io.Serializable

class MainActivity : FragmentActivity(), AppbarFragment.OnFragmentInteractionListener , MovieListFragment.OnFragmentInteractionListener {

    private lateinit var sharedPref: SharedPreferences
    private var appbarFragment: AppbarFragment? = null
    private var summaryFragment: InfoFragment? = null
    private var movieListFragment: MovieListFragment? = null
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appbarFragment = supportFragmentManager.findFragmentById(R.id.appbar_fragment) as AppbarFragment
        summaryFragment = supportFragmentManager.findFragmentById(R.id.info_fragment) as InfoFragment
        movieListFragment = supportFragmentManager.findFragmentById(R.id.movie_list_fragment) as MovieListFragment

        //  Grab a context to throw at fragments and adapters
        context = this

        sharedPref = PreferenceHelper.defaultPrefs(this)
        val firstRun: Boolean? = sharedPref["firstRun", true]
        if (firstRun!!) {
            FirstRun.setup(this)
        }

    }

    //  Search Text passed up from AppbarFragment
    override fun onSearchTextChanged(searchText: String) {
        Log.d("YOYO", "SEARCHTEXT: $searchText")
        if (searchText.length > 4) {
            //  Do website query
            sharedPref = PreferenceHelper.defaultPrefs(this)
            val baseUrl: String? = sharedPref["baseUrl"]
            val builder = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())

            val retrofit = builder.build()
            val client = retrofit.create<YoyoClient>(YoyoClient::class.java!!)
            val call = client.getSearchResults(searchText)

            call.enqueue(
                    object : Callback<SearchResults> {
                        override fun onResponse(call: Call<SearchResults>, response: Response<SearchResults>) {
                            Log.d("YOYO", "RESPONSE CODE: "+ response.code())
                            if (response.code() == 200) {
                                val searchResults :SearchResults? = response.body()
                                val results: List<Result> = searchResults!!.results!!
                                movieListFragment?.setData(context, results)
                            }
                        }

                        override fun onFailure(call: Call<SearchResults>, t: Throwable) {
                            Log.d("YOYO", "RETROFIT ERROR")
                        }
                    })
        }
    }

    //  Click Listener for RecyclerView
    override fun onItemClicked(result : Result) {
        intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra("result", result as Serializable)
        startActivity(intent)
    }
}
