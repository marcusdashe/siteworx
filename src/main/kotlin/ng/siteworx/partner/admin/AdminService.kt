package ng.siteworx.partner.admin

import io.jsonwebtoken.Jwts
import jakarta.servlet.http.HttpServletResponse
import ng.siteworx.partner.application.AppInfoDTO
import ng.siteworx.partner.application.ApplicationModel
import ng.siteworx.partner.application.ApplicationRepo
import ng.siteworx.partner.dto.LoginDTO
import ng.siteworx.partner.service.AuthService
import ng.siteworx.partner.serviceprovider.artisan.service.ArtisanService
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AdminService(private val applicationRepo: ApplicationRepo, private val authService: AuthService) {

    fun adminLogin(payload: LoginDTO, response: HttpServletResponse): ResponseEntity<Message> = authService.login(payload, response)

    fun fetchAppInfo(): ResponseEntity<ApplicationModel> = ResponseEntity(applicationRepo.findAll()[0], HttpStatus.OK)

    fun updateApplicationInfo(payload: AppInfoDTO, jwt: String): ResponseEntity<Message>{
        if(jwt.isBlank()){
            return ResponseEntity.status(401).body(Message(false, "Unauthenticated Access, Login is required to update App Info"))
        }
        val body = Jwts.parser().setSigningKey(ArtisanService.secretString).parseClaimsJws(jwt).getBody()
        val appInfo = this.applicationRepo.findById(body.issuer.toLong())

        if(appInfo != null){
            when{
                payload.appName.isNotBlank() -> appInfo.get().appName = payload.appName
                payload.appDesc.isNotBlank() -> appInfo.get().appDescription = payload.appDesc
                payload.appVersion.isNotBlank() -> appInfo.get().appVersion = payload.appVersion
                payload.appCategory.isNotBlank() -> appInfo.get().appCategory = payload.appCategory
            }
            ResponseEntity.ok().body(Message(true, "Application Info Updated Successfully", appInfo))
        }
        return ResponseEntity.badRequest().body(Message(false, "Oh shit! App Info not found in the database"))
    }
}