package uk.co.perfecthomecomputers.yoyocinema.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SpokenLanguage: Serializable {
//  Required by Movie Class

    @SerializedName("iso_639_1")
    @Expose
    var iso6391: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

}