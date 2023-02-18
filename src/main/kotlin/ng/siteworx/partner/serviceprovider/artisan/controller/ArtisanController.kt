package ng.siteworx.partner.serviceprovider.artisan.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import ng.siteworx.partner.dto.RegistrationDTO
import ng.siteworx.partner.dto.LoginDTO
import ng.siteworx.partner.service.AuthService
import ng.siteworx.partner.serviceprovider.artisan.service.ArtisanService
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/api/v1/artisan"])
class ArtisanController(private val artisanService: ArtisanService) {

    @GetMapping("/")
    fun fetchArtisan(@CookieValue("jwt") jwt: String): ResponseEntity<Message> = this.artisanService.artisan(jwt)

    @GetMapping("/all")
    fun fetchAllArtisans(): ResponseEntity<Message> = this.artisanService.getAllArtisans()

    @GetMapping("/{id}")
    fun fetchArtisanByID(@PathVariable id: String): ResponseEntity<Message> = this.artisanService.getArtisanByID(id.toLong())

    @GetMapping("/email/{email}")
    fun fetchArtisanByEmail(@PathVariable email: String): ResponseEntity<Message> = this.artisanService.getArtisanByEmail(email)

    @GetMapping("/username/{username}")
    fun fetchArtisanByUsername(@PathVariable("username") username: String): ResponseEntity<Message> = this.artisanService.getArtisanByUsername(username)

    @GetMapping("/artisan/profile")
    fun fetchProfileOfArtisan(@CookieValue("jwt") jwt: String): ResponseEntity<Message> = this.artisanService.getArtisanProfile(jwt)
    @PostMapping("/self/update")
    fun updateArtisan(@CookieValue("jwt") jwt: String, payload: RegistrationDTO): ResponseEntity<Message> = this.artisanService.selfUpdate(jwt, payload)

    @PutMapping("/admin/update/{id}")
    fun adminUpdateArtisan(@PathVariable("id") id: String, payload: RegistrationDTO): ResponseEntity<Message> = this.artisanService.adminUpdate(id, payload)

    @DeleteMapping("/{id}")
    fun eraseArtisanByID(@PathVariable("id") id: String): ResponseEntity<Message> = this.artisanService.deleteByID(id)

    @DeleteMapping("/erase-artisan/{email}")
    fun eraseArtisanByEmail(@PathVariable("email") email: String): ResponseEntity<Message> = this.artisanService.deleteByEmail(email)

    @GetMapping("/is-available/{id}")
    fun isAvailable(@PathVariable("id") id: String): ResponseEntity<Boolean> = this.artisanService.isArtisanAvailable(id)

    @GetMapping("/is-verified/{id}")
    fun isVerified(@PathVariable("id") id: String): ResponseEntity<Boolean> = this.artisanService.isVerifiedArtisan(id)
}