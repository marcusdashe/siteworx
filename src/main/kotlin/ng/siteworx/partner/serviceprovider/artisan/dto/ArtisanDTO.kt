package ng.siteworx.partner.serviceprovider.artisan.dto


data class ArtisanDTO(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
)