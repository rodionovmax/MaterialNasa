package com.rodionovmax.materialnasa.data.network.model

import com.google.gson.annotations.SerializedName

data class MarsResultsDto(
    @SerializedName("photos")
    var results: ArrayList<MarsPhotoDto>
)

data class MarsPhotoDto(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("sol") var sol: Int = 1000,
    @SerializedName("camera") var camera: CameraDto = CameraDto(),
    @SerializedName("img_src") var imageSrc: String = "",
    @SerializedName("earth_date") var earthDate: String = "2022-06-01",
    @SerializedName("rover") var rover: RoverDto = RoverDto(),
)

data class CameraDto(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("name") var cameraName: String = "FHAZ",
    @SerializedName("rover_id") var roverId: Int = 5,
    @SerializedName("full_name") var cameraFullName: String = "Front Hazard Avoidance Camera",
)

data class RoverDto(
    @SerializedName("id") var id: Int = 5,
    @SerializedName("name") var roverName: String = "Curiosity",
    @SerializedName("landing_date") var landingDate: String = "2012-08-06",
    @SerializedName("launch_date") var launchDate: String = "2011-11-26",
    @SerializedName("status") var status: String = "active",
)

