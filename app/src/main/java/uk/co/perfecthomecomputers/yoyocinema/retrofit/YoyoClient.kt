package uk.co.perfecthomecomputers.yoyocinema.retrofit


import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import uk.co.perfecthomecomputers.yoyocinema.objects.Movie
import uk.co.perfecthomecomputers.yoyocinema.objects.SearchResults

interface YoyoClient {

    @GET("search/movie?api_key=4cb1eeab94f45affe2536f2c684a5c9e")
    fun getSearchResults(
            @Query("query") searchText: String
    ): Call<SearchResults>

    @GET("movie/{id}?api_key=4cb1eeab94f45affe2536f2c684a5c9e")
    fun getMovieDetails(
            @Path("id") id: String
    ): Call<Movie>
}
