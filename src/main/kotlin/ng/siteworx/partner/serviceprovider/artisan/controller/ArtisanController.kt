package ng.siteworx.partner.serviceprovider.artisan.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import ng.siteworx.partner.dto.RegistrationDTO
import ng.siteworx.partner.dto.LoginDTO
import ng.siteworx.partner.service.AuthService
import ng.siteworx.partner.serviceprovider.artisan.dto.ProfileDTO
import ng.siteworx.partner.serviceprovider.artisan.service.ArtisanService
import ng.siteworx.partner.serviceprovider.artisan.service.ProfileService
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/api/v1/artisan"])
class ArtisanController(private val artisanService: ArtisanService, private val profileService: ProfileService) {

//    Get Request to fetch artisan while login
    @GetMapping("/")
    fun fetchArtisan(@CookieValue("jwt") jwt: String): ResponseEntity<Message> = this.artisanService.artisan(jwt)

//    Administrative endpoint to get all registered and verified artisans on the platform
    @GetMapping("/all")
    fun fetchAllArtisans(): ResponseEntity<Message> = this.artisanService.getAllArtisans()

//    Get Request to fetch an Artisan by their ID which is pass as path variable
    @GetMapping("/{id}")
    fun fetchArtisanByID(@PathVariable id: String): ResponseEntity<Message> = this.artisanService.getArtisanByID(id.toLong())

//   Get Request to fetch an Artisan by their Email which is pass as path variable
    @GetMapping("/email/{email}")
    fun fetchArtisanByEmail(@PathVariable email: String): ResponseEntity<Message> = this.artisanService.getArtisanByEmail(email)

//    Get Request to fetch an Artisan by their username which is pass as path variable
    @GetMapping("/username/{username}")
    fun fetchArtisanByUsername(@PathVariable("username") username: String): ResponseEntity<Message> = this.artisanService.getArtisanByUsername(username)

//   Get Request to fetch artisan profile while login
    @GetMapping("/profile")
    fun fetchProfileOfArtisan(@CookieValue("jwt") jwt: String): ResponseEntity<Message> = this.artisanService.getArtisanProfile(jwt)

//    Update artisan profile when login
    @PostMapping("/self-update")
    fun updateArtisan(@CookieValue("jwt") jwt: String, payload: RegistrationDTO): ResponseEntity<Message> = this.artisanService.selfUpdate(jwt, payload)

//    Check for the availability of artisan
    @GetMapping("/is-available/{id}")
    fun isAvailable(@PathVariable("id") id: String): ResponseEntity<Boolean> = this.artisanService.isArtisanAvailable(id)

//    Check if artisan is verified
    @GetMapping("/is-verified/{id}")
    fun isVerified(@PathVariable("id") id: String): ResponseEntity<Boolean> = this.artisanService.isVerifiedArtisan(id)

    /* Artisan Profile Based Controllers */
//    Create Profile for artisan
    @PostMapping("/profile")
    fun createProfile(@CookieValue("jwt") jwt: String, payload: ProfileDTO): ResponseEntity<Message> = this.profileService.updateProfileDetails(jwt, payload)

//    Update an existing artisan's profile
    @PutMapping("/profile")
    fun updateProfile(@CookieValue("jwt") jwt: String, payload: ProfileDTO): ResponseEntity<Message> = this.profileService.updateProfile(jwt, payload)

//    Delete an existing artisan's profile
    @DeleteMapping("/profile")
    fun deleteProfile(@CookieValue("jwt") jwt: String): ResponseEntity<Message> = this.profileService.deleteArtisanProfile(jwt)

//    Update profile avatar
    @PostMapping("/profile/avatar")
    fun uploadAvatar(@RequestParam("file") file: MultipartFile, @CookieValue("jwt") jwt: String): ResponseEntity<Message>  = this.profileService.uploadAvatar(file, jwt)

//    Fetch login artisan's profile
    @GetMapping("/my-profile")
    fun fetchMyProfile(@CookieValue("jwt") jwt: String): ResponseEntity<Message> = this.profileService.getProfile(jwt)

}