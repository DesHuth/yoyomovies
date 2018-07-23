package uk.co.perfecthomecomputers.yoyocinema

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import uk.co.perfecthomecomputers.yoyocinema.utils.FirstRun
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper.get

class MainActivity : FragmentActivity(), AppbarFragment.OnFragmentInteractionListener {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = PreferenceHelper.defaultPrefs(this)
        val firstRun: Boolean? = sharedPref["firstRun", true]
        if (firstRun!!) {
            FirstRun.setup(this)
        }

    }

    override fun onSearchTextChanged(searchText: String) {
        Log.d("YOYO", "SEARCHTEXT: $searchText")
    }
}
