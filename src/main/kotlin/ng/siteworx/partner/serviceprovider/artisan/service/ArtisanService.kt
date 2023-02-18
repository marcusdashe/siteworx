package ng.siteworx.partner.serviceprovider.artisan.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import ng.siteworx.partner.dto.RegistrationDTO
import ng.siteworx.partner.dto.LoginDTO
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.artisan.repo.ArtisanRepo
import ng.siteworx.partner.serviceprovider.artisan.repo.ProfileRepo
import ng.siteworx.partner.serviceprovider.sharedmodel.Profile
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import ng.siteworx.partner.serviceprovider.sharedservices.EmailService
import ng.siteworx.partner.serviceprovider.sharedservices.VerifyTokenService
import ng.siteworx.partner.template.EmailTemplate.emailHtmlBody
import ng.siteworx.partner.template.EmailTemplate.emailHtmlSubject
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailSendException
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class ArtisanService(private val artisanRepo: ArtisanRepo,
                     private val profileRepo: ProfileRepo,
                     private val emailService: EmailService,
                     private val verifyTokenService: VerifyTokenService
) {

    companion object {
        private val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512)
        public val secretString : String = Encoders.BASE64.encode(key.getEncoded())

    }

    fun extractArtisanByJWT(token: String): Artisan? {
        if(token.isBlank()) return null
        val body = Jwts.parser().setSigningKey(secretString).parseClaimsJws(token).getBody()
        val artisan = this.artisanRepo.findById(body.issuer.toLong())
        if(!artisan.isEmpty && artisan.get().isVerify){
            return artisan.get()
        }
        return null
    }

//    Read Artisan Details using JWT token
    fun artisan(jwt: String) : ResponseEntity<Message>{
        try{
            var returnArtisan = extractArtisanByJWT(jwt)
            if( returnArtisan != null){
                return ResponseEntity.ok().body(Message(true, "Artisan Found", returnArtisan))
            }
            return ResponseEntity.ok().body(Message(false, "Artisan has not verify"))
        } catch(e: Exception){
            return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access", null))
        }
    }

//    Return all Artisans
    fun getAllArtisans(): ResponseEntity<Message>{
        val artisans = this.artisanRepo.findAll()
        val response = if(artisans.isNotEmpty()) ResponseEntity.ok().body(Message(true, "Artisans Found", artisans)) else
            ResponseEntity.ok().body(Message(false, "No Artisan found in our database"))
        return response
    }

//    Read an Artisan Details by id
    fun getArtisanByID(id: Long): ResponseEntity<Message>{
        val artisan = this.artisanRepo.findById(id).orElse(null)
    return if(artisan == null){
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan Not Found", null))
    } else if(artisan.isVerify){
        ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
    } else {
        ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
    }
    }

//    Read an Artisan by email
    fun getArtisanByEmail(email: String): ResponseEntity<Message>{
        val artisan = this.artisanRepo.findByEmail(email)
    return if(artisan == null){
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan Not Found", null))
    } else if(artisan.isVerify){
        ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
    }
    else
        ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
    }

//    Read an Artisan by username
    fun getArtisanByUsername(username: String): ResponseEntity<Message> {
    val artisan = this.artisanRepo.findByUsername(username)
    return if (artisan == null) {
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artist not found", null))
    } else if(artisan.isVerify) {
        ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
    } else {
        ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
    }
//        return ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
    }

fun getArtisanProfile(jwt: String): ResponseEntity<Message> {
    try{
        val returnArtisan = extractArtisanByJWT(jwt)
        if( returnArtisan != null){
            val profile = profileRepo.findByArtisan(returnArtisan)
            return ResponseEntity.ok().body(Message(true, "Artisan Found", profile))
        }
        return ResponseEntity.ok().body(Message(false, "Artisan has not verify"))
    }  catch(e: Exception){
        return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access", null))
    }
}

    fun getProfile(artisan: Artisan) : Profile? {
        return profileRepo.findByArtisan(artisan)
    }
//    Update Artisan
    fun selfUpdate(jwt: String, payload: RegistrationDTO): ResponseEntity<Message>{

    try{
        val returnArtisan = extractArtisanByJWT(jwt)
      if (returnArtisan != null) {
          when {
              payload.firstName.isNotBlank() -> returnArtisan.firstName = payload.firstName
              payload.lastName.isNotBlank() -> returnArtisan.lastName = payload.lastName
              payload.email.isNotBlank() -> returnArtisan.email = payload.email
              payload.username.isNotBlank() -> returnArtisan.username = payload.username
              payload.password.isNotBlank() -> returnArtisan.password = payload.password
          }
          return ResponseEntity.ok().body(Message(true, "Artisan updated successfully", this.artisanRepo.save(returnArtisan)))
      }
    } catch(e: Exception){
        return ResponseEntity.status(401).body(Message(false, "Error occured while updating artisan by self", null))
    }
    return ResponseEntity.status(401).body(Message(false, "Error occured while updating artisan by self", null))
}

//    Admin update of Artisan
    fun adminUpdate(id: String, payload: RegistrationDTO): ResponseEntity<Message>{
    if(id.isBlank())
        return ResponseEntity.badRequest().body(Message(false, "Bad Request"))
    return try{
        val artisan: Artisan = this.artisanRepo.getReferenceById(id.toLong())
        if(artisan != null){
            when{
                payload.firstName.isNotBlank() -> artisan.firstName = payload.firstName
                payload.lastName.isNotBlank() -> artisan.lastName = payload.lastName
                payload.email.isNotBlank() -> artisan.email = payload.email
                payload.username.isNotBlank() -> artisan.username = payload.username
                payload.password.isNotBlank() -> artisan.password = payload.password
            }
        }
        ResponseEntity.ok().body(Message(true, "Artisan updated successfully", this.artisanRepo.save(artisan)))
    }catch(e: Exception){
        ResponseEntity.status(401).body(Message(false, "Error occured while updating artisan by admin", null))
    }
}

//    Delete Artisan by id
fun deleteByID(id: String): ResponseEntity<Message>{
    val artisan = this.artisanRepo.getReferenceById(id.toLong())
    return if(artisan == null || id.isBlank()){
        ResponseEntity.badRequest().body(Message(false, "Bad Request"))
    } else if(artisan.isVerify) {
        try{
            ResponseEntity.ok().body(Message(true, "Deleted Artisan Successfully", this.artisanRepo.deleteById(id.toLong())))
        } catch (e: Exception){
            ResponseEntity.status(401).body(Message(false, "Error occured while deleting artisan", null))
        }
    }
    else {
        ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
    }
}
    //    Delete Artisan by Email
    fun deleteByEmail(email: String = ""): ResponseEntity<Message>{
        return if(email.isBlank()){
            ResponseEntity.badRequest().body(Message(false, "Bad Request", null))
        } else if(!this.artisanRepo.existsByEmail(email)){
            ResponseEntity.badRequest().body(Message(false, "Requested Email not Found", null))
        } else if(this.artisanRepo.findByEmail(email)!!.isVerify) {
            try{
                ResponseEntity.ok().body(this.artisanRepo.deleteByEmail(email)
                    ?.let { Message(true, "Deleted Artisan Successfully", it) })
            } catch (e: Exception){
                ResponseEntity.status(401).body(Message(false, "Error occured while deleting artisan", null))
            }
        } else {
            ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
        }
    }

    fun isVerifiedArtisan(id: String):  ResponseEntity<Boolean> {
        return ResponseEntity.ok().body(this.artisanRepo.findById(id.toLong()).get().isVerify)
    }

    fun isArtisanAvailable(id: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok().body(this.artisanRepo.findById(id.toLong()).get().isAvailable)
    }

}