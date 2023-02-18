package ng.siteworx.partner.dto

import ng.siteworx.partner.enums.SiteworxEnums

data class RegistrationDTO(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val profession: String = "",
    val userType: SiteworxEnums.UserType = SiteworxEnums.UserType.ARTISAN
)