package uk.co.perfecthomecomputers.yoyocinema.objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProductionCompany {
//  Required by Movie Class

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("logo_path")
    @Expose
    var logoPath: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("origin_country")
    @Expose
    var originCountry: String? = null

}