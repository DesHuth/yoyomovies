package uk.co.perfecthomecomputers.yoyocinema.utils

import android.content.Context
import uk.co.perfecthomecomputers.yoyocinema.utils.PreferenceHelper.set

object FirstRun {

    fun setup(context: Context) {

        //  Set Shared Preferences

        val sharedPref = PreferenceHelper.defaultPrefs(context)
        sharedPref["firstRun"] = false
        sharedPref["baseUrl"] = "https://api.themoviedb.org/3/search/movie/"

        //  Create and Initialise Database

        val db = DataSource(context)
        db.open()
        db.execute("CREATE TABLE favourites (_id INTEGER PRIMARY KEY, id INTEGER, vote_average TEXT, title TEXT, poster_path TEXT, overview TEXT, release_date TEXT)")
        db.close()
    }
}