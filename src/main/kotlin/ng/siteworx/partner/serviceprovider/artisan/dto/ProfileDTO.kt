package ng.siteworx.partner.serviceprovider.artisan.dto

import ng.siteworx.partner.enums.SiteworxEnums
import java.util.Date

data class ProfileDTO(
    val bio: String = "",
    var tradeCategory: String = "",
    var dateOfBirth: String = "",
    val gender: String = "Male",
    val yearsOfExperience: Int = 0,
    val orderCount: Int = 0,
    val photoUrl: String = "",
    val highestCertificatesObtained: String = "PSLC",
)