package uk.co.perfecthomecomputers.yoyocinema.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProductionCountry {
//  Required by Movie Class

    @SerializedName("iso_3166_1")
    @Expose
    var iso31661: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

}