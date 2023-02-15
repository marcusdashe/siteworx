package ng.siteworx.partner.client.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import ng.siteworx.partner.client.model.Client
import ng.siteworx.partner.client.repo.ClientRepo
import ng.siteworx.partner.dto.LoginDTO
import ng.siteworx.partner.dto.RegistrationDTO
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.artisan.service.ArtisanService
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import ng.siteworx.partner.serviceprovider.sharedservices.EmailService
import ng.siteworx.partner.serviceprovider.sharedservices.VerifyTokenService
import ng.siteworx.partner.template.EmailTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailSendException
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@Service
class ClientService(private val clientRepo: ClientRepo, private val verifyTokenService: VerifyTokenService, private val emailService: EmailService) {

    companion object {
        private val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512)
        private val secretString: String = Encoders.BASE64.encode(key.getEncoded())
    }

    //    Read Client Details using JWT token
    fun client(jwt: String): ResponseEntity<Message> {
        try {
            if (jwt.isNullOrEmpty()) {
                return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access"))
            }
            var body = Jwts.parser().setSigningKey(secretString).parseClaimsJws(jwt).getBody()
            val clientObj = this.clientRepo.findById(body.issuer.toLong())
            if(clientObj != null)
            return ResponseEntity.ok().body(Message(true, "Client Found", clientObj))
        } catch (e: Exception) {
            return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access", null))
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Message(false, "Error occurred while getting user deetails", null))
    }

    //    Return all Clients Details
    fun getAllClients(): ResponseEntity<Message> {
        val clients = this.clientRepo.findAll()
        var response = if (clients.isNotEmpty())
            ResponseEntity.ok().body(Message(false, "No Client found in our database"))
        else ResponseEntity.ok().body(Message(true, "Artisans Found", clients))
        return response
    }

    //    Get Client Details by ID
    fun getClientByID(id: Long): ResponseEntity<Message> {
        val client = this.clientRepo.findById(id).orElse(null)
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan Not Found", null))
        }
        return ResponseEntity.ok().body(Message(true, "Artisan Found", client))
    }

    //    get Client Details by Email
    fun getClientByEmail(email: String): ResponseEntity<Message> {
        val client = this.clientRepo.findByEmail(email)
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan Not Found", null))
        }
        return ResponseEntity.ok().body(Message(true, "Artisan Found", client))
    }

    //    get Client Details by Name
    fun getClientByUsername(username: String): ResponseEntity<Message> {
        val client = this.clientRepo.findByUsername(username)
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artist not found", null))
        }
        return ResponseEntity.ok().body(Message(true, "Artisan Found", client))
    }

    fun selfUpdateBasicClientInfo(jwt: String, payload: RegistrationDTO): ResponseEntity<Message> {
        if (jwt.isNullOrEmpty())
            return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access", null))
        try {
            val body = Jwts.parser().setSigningKey(secretString).parseClaimsJws(jwt).getBody()
            val client = this.clientRepo.getReferenceById(body.issuer.toLong())

            if (payload.firstName.isBlank()) {
            } else client.firstName = payload.firstName
            if (payload.lastName.isBlank()) {
            } else client.lastName = payload.lastName
            if (payload.email.isBlank()) {
            } else client.email = payload.email
            if (payload.username.isBlank()) {
            } else client.username = payload.username
            if (payload.password.isBlank()) {
            } else client.password = payload.password

            return ResponseEntity.ok().body(Message(true, "Artisan updated successfully", this.clientRepo.save(client)))
        } catch (e: Exception) {
            return ResponseEntity.status(401).body(Message(false, "Error occured while updating artisan by self", null))
        }
    }

    //    Update Sign Data by Admin
    fun adminUpdateBasicClientInfo(id: String, payload: RegistrationDTO): ResponseEntity<Message> {
        if (id.isNullOrEmpty())
            return ResponseEntity.badRequest().body(Message(false, "Bad Request"))
        try {
            val client: Client = this.clientRepo.getReferenceById(id.toLong())

            if (payload.firstName.isBlank()) {
            } else client.firstName = payload.firstName
            if (payload.lastName.isBlank()) {
            } else client.lastName = payload.lastName
            if (payload.email.isBlank()) {
            } else client.email = payload.email
            if (payload.username.isBlank()) {
            } else client.username = payload.username
            if (payload.password.isBlank()) {
            } else client.password = payload.password
            return ResponseEntity.ok().body(Message(true, "Artisan updated successfully", this.clientRepo.save(client)))
        } catch (e: Exception) {
            return ResponseEntity.status(401)
                .body(Message(false, "Error occurred while updating client by admin", null))
        }
    }

//    Delete Client By Id
    fun deleteByID(id: String): ResponseEntity<Message> {

        val client = this.clientRepo.getReferenceById(id.toLong())
        if (client == null || id.isNullOrEmpty()) {
            return ResponseEntity.badRequest().body(Message(false, "Bad Request"))
        } else {
            try {
                return ResponseEntity.ok()
                    .body(Message(true, "Deleted Artisan Successfully", this.clientRepo.deleteById(id.toLong())))
            } catch (e: Exception) {
                return ResponseEntity.status(401).body(Message(false, "Error occurred while deleting artisan", null))
            }
        }
    }

//    Delete Client By Email
    fun deleteByEmail(email: String = ""): ResponseEntity<Message> {
    if(email.isBlank()){
        return ResponseEntity.badRequest().body(Message(false, "Bad Request", null))
    } else if(!this.clientRepo.existsByEmail(email)){
        return ResponseEntity.badRequest().body(Message(false, "Requested Email not Found", null))
    } else{
        try{
            return ResponseEntity.ok().body(this.clientRepo.deleteByEmail(email)
                ?.let { Message(true, "Deleted Artisan Successfully", it) })
        } catch (e: Exception){
            return ResponseEntity.status(401).body(Message(false, "Error occured while deleting artisan", null))
        }
    }
    }
}
