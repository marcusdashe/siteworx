package ng.siteworx.partner.serviceprovider.sharedservices

import ng.siteworx.partner.serviceprovider.artisan.repo.ArtisanRepo
import ng.siteworx.partner.serviceprovider.sharedmodel.VerifyTokenModel
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import ng.siteworx.partner.serviceprovider.sharedrepo.VerifyTokenRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class VerifyTokenService (private val tokenRepo: VerifyTokenRepo, private val artisanRepo: ArtisanRepo) {

    fun signUpToken(generatedToken:String): VerifyTokenModel {
        var tokenModel = VerifyTokenModel()
        tokenModel.token = generatedToken
        var token = tokenRepo.save(tokenModel)
        return token
    }

    fun verifyToken(generatedToken : String, id: Long):ResponseEntity<Message> {
        try {
            var token = this.tokenRepo.findByToken(generatedToken);
            var artisan = artisanRepo.findByIdOrNull(id)
            var now: Instant = Instant.now()
            var hasTokenExpire: Boolean = Duration.between(token.createdAt.toInstant(), now).toHours() > 24

            if (artisan == null && token == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Token or Artisan not found"))
            } else if(hasTokenExpire){
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(Message(false, "Token has expired"))
            } else {
                artisan?.isVerify = true
                tokenRepo.delete(token)
                return ResponseEntity.status(HttpStatus.OK).body(Message(true, "Token verified"))
            }
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(Message(status = false, message = "Couldn't Verify Token", e.cause))
        }
    }
}