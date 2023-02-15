package ng.siteworx.partner.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import ng.siteworx.partner.dto.LoginDTO
import ng.siteworx.partner.dto.RegistrationDTO
import ng.siteworx.partner.service.AuthService
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/api/v1/auth"])
class AuthContoller(private val authService: AuthService) {
    @PostMapping(value = ["/register", "/create", "/signup-", "/new"])
    fun register(@Valid @RequestBody payload: RegistrationDTO): ResponseEntity<Message> = this.authService.register(payload)

    @PostMapping("/login")
    fun login(@Valid @RequestBody payload: LoginDTO, response: HttpServletResponse): ResponseEntity<Message> = this.authService.login(payload, response)

    @GetMapping("/logout")
    fun signout(response: HttpServletResponse): ResponseEntity<Message> = this.authService.logout(response)
}