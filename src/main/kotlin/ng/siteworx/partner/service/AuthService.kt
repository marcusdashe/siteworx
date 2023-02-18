package ng.siteworx.partner.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import ng.siteworx.partner.admin.Admin
import ng.siteworx.partner.admin.AdminRepo
import ng.siteworx.partner.client.model.Client
import ng.siteworx.partner.client.repo.ClientRepo
import ng.siteworx.partner.dto.LoginDTO
import ng.siteworx.partner.dto.RegistrationDTO
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.artisan.repo.ArtisanRepo
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import ng.siteworx.partner.serviceprovider.sharedservices.EmailService
import ng.siteworx.partner.serviceprovider.sharedservices.VerifyTokenService
import ng.siteworx.partner.template.EmailTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailSendException
import org.springframework.stereotype.Service
import ng.siteworx.partner.enums.SiteworxEnums
import ng.siteworx.partner.serviceprovider.artisan.service.ArtisanService
import org.springframework.http.HttpStatus
import java.util.*
import javax.crypto.SecretKey

@Service
class AuthService(
    private val artisanRepo: ArtisanRepo,
    private val clientRepo: ClientRepo,
    private val emailService: EmailService,
    private val verifyTokenService: VerifyTokenService,
    private val adminRepo: AdminRepo) {
    @Value("\${siteworx.ng.baseUrl}")
    private val baseURL: String? = null

    companion object {
        private val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512)
        private val secretString: String = Encoders.BASE64.encode(key.getEncoded())
    }

    // Create new User
    fun register(payload: RegistrationDTO): ResponseEntity<Message> {
        if (payload.password.isBlank() && payload.password != payload.passwordConfirm) {
            return ResponseEntity.badRequest().body(
                Message(
                    false,
                    "Either password is empty/null or password not equal to passwordConfirm",
                    null
                )
            )
        }
        val result = when (payload.userType) {
            SiteworxEnums.UserType.ARTISAN -> {
                val artisan = Artisan()
                artisan.firstName = payload.firstName
                artisan.lastName = payload.lastName
                artisan.email = payload.email
                artisan.username = payload.username
                artisan.password = payload.password
                val verifyToken: String = UUID.randomUUID().toString().take(20)
                try {
                    val createdArtisan = this.artisanRepo.save(artisan)
                    var tokenStr = this.verifyTokenService.signUpToken(verifyToken).token
                    var artisanEmailBody =
                        "${EmailTemplate.emailHtmlBody} <br /> $baseURL/api/v1/verify-token/$createdArtisan.id/$tokenStr"
                    emailService.sendHTMLEmail(createdArtisan.email, EmailTemplate.emailHtmlSubject, artisanEmailBody)
                    ResponseEntity.ok().body(Message(true, "Artisan Created Successfully", createdArtisan))
                } catch (e: DataIntegrityViolationException) {
                    ResponseEntity.badRequest().body(Message(false, "Artisan Registered already", e.message))
                } catch (e: MailSendException) {
                    ResponseEntity.badRequest()
                        .body(Message(false, "Artisan Created Successfully, but couldn't Send mail", e.message))
                }
            }

            SiteworxEnums.UserType.CLIENT -> {
                val client: Client = Client()
                client.firstName = payload.firstName
                client.email = payload.email
                client.password = payload.password
                val verifyToken: String = UUID.randomUUID().toString().take(20)
                try {
                    var createdClient = this.clientRepo.save(client)
                    var tokenStr = this.verifyTokenService.signUpToken(verifyToken).token
                    var clientEmailBody = "${EmailTemplate.emailHtmlBody} <br /> $baseURL/api/v1/verify-token/$createdClient.id/$tokenStr"
                    emailService.sendHTMLEmail(createdClient.email, EmailTemplate.emailHtmlSubject, clientEmailBody)
                    ResponseEntity.ok().body(Message(true, "Client Created Successfully", createdClient))
                } catch (e: DataIntegrityViolationException) {
                    ResponseEntity.badRequest().body(Message(false, "Client has already registered", e.message))
                } catch (e: MailSendException) {
                    ResponseEntity.badRequest().body(Message(false, "Client Created Successfully, but couldn't send mail", e.message))
                }
            }
            else -> {
                return ResponseEntity.badRequest().body(Message(false, "Unrecognized User"))
            }
        }
        return result
    }

//    Login with Password, Email or Username
    fun login(payload: LoginDTO, response: HttpServletResponse): ResponseEntity<Message> {
        val user = this.artisanRepo.findByEmail(payload.email) ?: this.artisanRepo.findByUsername(payload.username) ?:
                     this.clientRepo.findByEmail(payload.email) ?: this.clientRepo.findByUsername(payload.username) ?:
                     this.adminRepo.findByEmail(payload.email) ?: this.adminRepo.findByUsername(payload.username) ?: ""

        var result = when (user) {
            is Artisan -> {
                if (!user.comparePassword(payload.password)) {
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Message(false, "Invalid Password", null))
                } else {
                    val issuer = user.id.toString()
                    val jwt = Jwts.builder().setIssuer(issuer)
                        .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // a day
                        .signWith(SignatureAlgorithm.HS512, secretString).compact()
                    var cookie = Cookie("jwt", jwt)
                    cookie.isHttpOnly = true
                    response.addCookie(cookie)
                    ResponseEntity.ok().body(Message(true, "Login Successfully", user))
                }
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Message(false, "User not found", null))
            }
            is Client -> {
                if(!user.comparePassword(payload.password)) {
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Message(false, "Invalid Password",null))
                } else {
                    val issuer = user.id.toString()
                    val jwt = Jwts.builder().setIssuer(issuer)
                        .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // a day
                        .signWith(SignatureAlgorithm.HS512, secretString).compact()
                    var cookie = Cookie("jwt", jwt)
                    cookie.isHttpOnly = true
                    response.addCookie(cookie)
                    ResponseEntity.ok().body(Message(true, "Login Successfully", user))
                }
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Message(false, "User not found", null))
            }
            is Admin -> {
                if(!user.comparePassword(payload.password)) {
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Message(false, "Invalid Password",null))
                } else {
                    val issuer = user.id.toString()
                    val jwt = Jwts.builder().setIssuer(issuer)
                        .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // a day
                        .signWith(SignatureAlgorithm.HS512, secretString).compact()
                    var cookie = Cookie("jwt", jwt)
                    cookie.isHttpOnly = true
                    response.addCookie(cookie)
                    ResponseEntity.ok().body(Message(true, "Login Successfully", user))
                }
                ResponseEntity.ok().body(Message(false, "Unable to login successfully", user))
            }
            else -> {
                ResponseEntity.ok().body(Message(true, "Unrecognized User"))
            }
        }
        return result
    }

    // Logout the user
    fun logout(response: HttpServletResponse): ResponseEntity<Message>{
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        cookie.isHttpOnly = true
        response.addCookie(cookie)
        return ResponseEntity.ok().body(Message(true, "Logged Out", null))
    }

}
