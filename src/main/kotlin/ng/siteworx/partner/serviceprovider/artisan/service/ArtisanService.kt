package ng.siteworx.partner.serviceprovider.artisan.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import ng.siteworx.partner.serviceprovider.artisan.dto.ArtisanDTO
import ng.siteworx.partner.serviceprovider.artisan.dto.LoginDTO
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.artisan.repo.ArtisanRepo
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import ng.siteworx.partner.serviceprovider.sharedservices.EmailService
import ng.siteworx.partner.serviceprovider.sharedservices.VerifyTokenService
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailSendException
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PathVariable
import java.util.*
import javax.crypto.SecretKey

@Service
class ArtisanService(private val artisanRepo: ArtisanRepo, private val emailService: EmailService, private val verifyTokenService: VerifyTokenService) {
    @Value("\${siteworx.ng.baseUrl}")
    private val baseURL: String? = null

    companion object {
        private val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512)
        private val secretString : String = Encoders.BASE64.encode(key.getEncoded())
    }

// Create new Artisan
    fun register(payload : ArtisanDTO): ResponseEntity<Message> {
    if(payload.password != payload.passwordConfirm) {
        return ResponseEntity.badRequest().body(Message(false, "password and passwordConfirm are not the same", null))
    }
        val artisan = Artisan()
        artisan.firstName = payload.firstName
        artisan.lastName = payload.lastName
        artisan.email = payload.email
        artisan.username = payload.username
        artisan.password = payload.password
        var verifyToken : String = UUID.randomUUID().toString().take(10)
        try{
            val createdArtisan = this.artisanRepo.save(artisan)
            var tokenStr = this.verifyTokenService.signUpToken(verifyToken).token
            var emailHtmlSubject = "Welcome to Siteworx! Please verify your e-mail"
            var emailHtmlBody = """
                <header style="color: '#cb4154'; font-size: '2rem'">This is a confirmation email to confirm your e-mail address. </header 
                <p>The verification link will expire in 24 hours</p>
                <p>Please click on the following link to confirm your e-mail address:</p>
                ${baseURL+"/api/v1/verify-token"+"/"+createdArtisan.id+"/"+tokenStr}
            """.trimIndent()
            emailService.sendHTMLEmail(createdArtisan.email, emailHtmlSubject, emailHtmlBody)
            return ResponseEntity.ok().body(Message(true, "Artisan Created Successfully", createdArtisan))
        } catch(e: DataIntegrityViolationException){
            return ResponseEntity.badRequest().body(Message(false, "Artisan Registered already", e.message))
        } catch(e: MailSendException){
            return ResponseEntity.badRequest().body(Message(false, "Artisan Created Successfully, but couldn't Send mail", e.message))
        }
    }

//    Login with username and password
    fun login(payload : LoginDTO, response: HttpServletResponse) : ResponseEntity<Message>{
        val artisan = this.artisanRepo.findByEmail(payload.email) ?: this.artisanRepo.findByUsername(payload.email) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false,"User not found", null))

        if(!artisan.comparePassword(payload.password))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Message(false, "Invalid Password", null))

        val issuer = artisan.id.toString()

        val jwt = Jwts.builder().setIssuer(issuer).setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // a day
            .signWith(SignatureAlgorithm.HS512, secretString).compact()
        var cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)
        return ResponseEntity.ok().body(Message(true, "Login Successfully", null))
    }

//    Read Artisan Entity by jwt token
    fun artisan(jwt: String) : ResponseEntity<Message>{
        try{
            if(jwt.isBlank() || jwt == null){
                return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access"))
            }
            val body = Jwts.parser().setSigningKey(secretString).parseClaimsJws(jwt).getBody()
            return ResponseEntity.ok().body(Message(true, "Artisan Found", this.artisanRepo.findById(body.issuer.toLong()).orElse(null)))
        } catch(e: Exception){
            return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access", null))
        }
    }

//    Return all Artisans
    fun getAllArtisans(): ResponseEntity<Message>{
        val artisans = this.artisanRepo.findAll()
        return ResponseEntity.ok().body(Message(true, "Artisans Found", artisans))
    }

//    Read an Artisan by id
    fun getArtisanByID(id: Long): ResponseEntity<Message>{
        val artisan = this.artisanRepo.findById(id)
    if(artisan == null){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan Not Found", null))
    }
        return ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
    }

//    Read an Artisan by email
    fun getArtisanByEmail(email: String): ResponseEntity<Message>{
        val artisan = this.artisanRepo.findByEmail(email)
    if(artisan == null){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan Not Found", null))
    }
        return ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
    }

//    Read an Artisan by username
    fun getArtisanByUsername(username: String): ResponseEntity<Message>{
        val artisan = this.artisanRepo.findByUsername(username)
    if(artisan == null){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artist not found", null))
    }
        return ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
    }

//    Update Artisan
    fun selfUpdate(jwt: String, payload: ArtisanDTO): ResponseEntity<Message>{
    try{
        if(jwt.isBlank() || jwt == null)
            return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access", null))

        val body = Jwts.parser().setSigningKey(secretString).parseClaimsJws(jwt).getBody()
        val artisan = this.artisanRepo.getReferenceById(body.issuer.toLong())

         if(payload.firstName.isBlank()) {} else artisan.firstName =  payload.firstName
         if(payload.lastName.isBlank()) {} else artisan.lastName =  payload.lastName
         if(payload.email.isBlank()) {} else artisan.email =  payload.email
         if(payload.username.isBlank()) {} else artisan.username =  payload.username
         if(payload.password.isBlank()) {} else artisan.password =  payload.password

        return ResponseEntity.ok().body(Message(true, "Artisan updated successfully", this.artisanRepo.save(artisan)))

    } catch(e: Exception){
        return ResponseEntity.status(401).body(Message(false, "Error occured while updating artisan by self", null))
    }}

//    Admin update of Artisan
    fun adminUpdate(id: String, payload: ArtisanDTO): ResponseEntity<Message>{
        try{
            if(id.isBlank() || id == null)
                return ResponseEntity.badRequest().body(Message(false, "Bad Request"))
            val artisan: Artisan = this.artisanRepo.getReferenceById(id.toLong())

            if(payload.firstName.isBlank()) {} else artisan.firstName =  payload.firstName
            if(payload.lastName.isBlank()) {} else artisan.lastName =  payload.lastName
            if(payload.email.isBlank()) {} else artisan.email =  payload.email
            if(payload.username.isBlank()) {} else artisan.username =  payload.username
            if(payload.password.isBlank()) {} else artisan.password =  payload.password
            return ResponseEntity.ok().body(Message(true, "Artisan updated successfully", this.artisanRepo.save(artisan)))
        }catch(e: Exception){
            return ResponseEntity.status(401).body(Message(false, "Error occured while updating artisan by admin", null))
        }}


//    Delete Artisan by id
fun deleteByID(id: String): ResponseEntity<Message>{
    if(id.isBlank() || this.artisanRepo.getReferenceById(id.toLong()) == null){
        return ResponseEntity.badRequest().body(Message(false, "Bad Request"))
    }
    else{
        try{
            return ResponseEntity.ok().body(Message(true, "Deleted Artisan Successfully", this.artisanRepo.deleteById(id.toLong())))
    } catch (e: Exception){
            return ResponseEntity.status(401).body(Message(false, "Error occured while deleting artisan", null))
        }
    }
}

    //    Delete Artisan by Email
    fun deleteByEmail(email: String = ""): ResponseEntity<Message>{
        if(email.isBlank()){
            return ResponseEntity.badRequest().body(Message(false, "Bad Request", null))
        } else if(!this.artisanRepo.existsByEmail(email)){
            return ResponseEntity.badRequest().body(Message(false, "Requested Email not Found", null))
        } else{
            try{
                return ResponseEntity.ok().body(this.artisanRepo.deleteByEmail(email)
                    ?.let { Message(true, "Deleted Artisan Successfully", it) })
            } catch (e: Exception){
                return ResponseEntity.status(401).body(Message(false, "Error occured while deleting artisan", null))
            }
        }
    }


//    Logout Artisan
    fun logout(response: HttpServletResponse): ResponseEntity<Message>{
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        cookie.isHttpOnly = true
        response.addCookie(cookie)
        return ResponseEntity.ok().body(Message(true, "Logged Out", null))
    }
}