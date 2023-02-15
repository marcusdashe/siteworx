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
class ArtisanService(private val artisanRepo: ArtisanRepo, private val emailService: EmailService, private val verifyTokenService: VerifyTokenService) {

    companion object {
        private val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512)
        private val secretString : String = Encoders.BASE64.encode(key.getEncoded())
    }

//    Read Artisan Details using JWT token
    fun artisan(jwt: String) : ResponseEntity<Message>{
        try{
            if(jwt.isNullOrEmpty()){
                return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access"))
            }
            val body = Jwts.parser().setSigningKey(secretString).parseClaimsJws(jwt).getBody()
            val artisanObj = this.artisanRepo.findById(body.issuer.toLong())
            if(artisanObj != null && artisanObj.get().isVerify){
                return ResponseEntity.ok().body(Message(true, "Artisan Found", artisanObj.get()))
            }
            return ResponseEntity.ok().body(Message(false, "Artisan has not verify"))
        } catch(e: Exception){
            return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access", null))
        }
    }

//    Return all Artisans
    fun getAllArtisans(): ResponseEntity<Message>{
        val artisans = this.artisanRepo.findAll()
        var response = if(artisans.isNotEmpty()) ResponseEntity.ok().body(Message(true, "Artisans Found", artisans)) else
            ResponseEntity.ok().body(Message(false, "No Artisan found in our database"))
        return response
    }

//    Read an Artisan Details by id
    fun getArtisanByID(id: Long): ResponseEntity<Message>{
        val artisan = this.artisanRepo.findById(id).orElse(null)
        if(artisan == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan Not Found", null))
        } else if(artisan.isVerify){
            return ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
        } else {
            return ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
        }
    }

//    Read an Artisan by email
    fun getArtisanByEmail(email: String): ResponseEntity<Message>{
        val artisan = this.artisanRepo.findByEmail(email)
    if(artisan == null){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan Not Found", null))
    } else if(artisan.isVerify){
        return ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))}
    else
        return ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
    }

//    Read an Artisan by username
    fun getArtisanByUsername(username: String): ResponseEntity<Message> {
    val artisan = this.artisanRepo.findByUsername(username)
    if (artisan == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artist not found", null))
    } else if(artisan.isVerify) {
         return ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
    } else {
            return ResponseEntity.ok().body(Message(true, "Artisan Found", artisan))
        }
        return ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
    }


//    Update Artisan
    fun selfUpdate(jwt: String, payload: RegistrationDTO): ResponseEntity<Message>{
        if(jwt.isNullOrEmpty())
        return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access", null))
    try{
        val body = Jwts.parser().setSigningKey(secretString).parseClaimsJws(jwt).getBody()
        val artisan = this.artisanRepo.getReferenceById(body.issuer.toLong())

        if(artisan.isVerify) {
            if(payload.firstName.isBlank()) {} else artisan.firstName =  payload.firstName
            if(payload.lastName.isBlank()) {} else artisan.lastName =  payload.lastName
            if(payload.email.isBlank()) {} else artisan.email =  payload.email
            if(payload.username.isBlank()) {} else artisan.username =  payload.username
            if(payload.password.isBlank()) {} else artisan.password =  payload.password
            return ResponseEntity.ok().body(Message(true, "Artisan updated successfully", this.artisanRepo.save(artisan)))
        }
        return ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
    } catch(e: Exception){
        return ResponseEntity.status(401).body(Message(false, "Error occured while updating artisan by self", null))
    }}

//    Admin update of Artisan
    fun adminUpdate(id: String, payload: RegistrationDTO): ResponseEntity<Message>{
    if(id.isNullOrEmpty())
        return ResponseEntity.badRequest().body(Message(false, "Bad Request"))
    try{
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
    val artisan = this.artisanRepo.getReferenceById(id.toLong())
    if(artisan == null || id.isNullOrEmpty()){
        return ResponseEntity.badRequest().body(Message(false, "Bad Request"))
    } else if(artisan.isVerify) {
        try{
            return ResponseEntity.ok().body(Message(true, "Deleted Artisan Successfully", this.artisanRepo.deleteById(id.toLong())))
        } catch (e: Exception){
            return ResponseEntity.status(401).body(Message(false, "Error occured while deleting artisan", null))
        }
    }
    else {
        return ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
    }
}
    //    Delete Artisan by Email
    fun deleteByEmail(email: String = ""): ResponseEntity<Message>{
        if(email.isBlank()){
            return ResponseEntity.badRequest().body(Message(false, "Bad Request", null))
        } else if(!this.artisanRepo.existsByEmail(email)){
            return ResponseEntity.badRequest().body(Message(false, "Requested Email not Found", null))
        } else if(this.artisanRepo.findByEmail(email)!!.isVerify) {
            try{
                return ResponseEntity.ok().body(this.artisanRepo.deleteByEmail(email)
                    ?.let { Message(true, "Deleted Artisan Successfully", it) })
            } catch (e: Exception){
                return ResponseEntity.status(401).body(Message(false, "Error occured while deleting artisan", null))
            }
        } else {
            return ResponseEntity.ok().body(Message(false, "Artisan has not been verified"))
        }
    }

    fun isArtisanVerified(id: String):  ResponseEntity<Boolean> {
        return ResponseEntity.ok().body(this.artisanRepo.findById(id.toLong()).get().isVerify)
    }

    fun isArtisanAvailable(id: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok().body(this.artisanRepo.findById(id.toLong()).get().isAvailable)
    }
}