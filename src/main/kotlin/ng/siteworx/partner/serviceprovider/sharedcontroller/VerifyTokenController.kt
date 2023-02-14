package ng.siteworx.partner.serviceprovider.sharedcontroller

import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import ng.siteworx.partner.serviceprovider.sharedservices.VerifyTokenService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@CrossOrigin(origins = ["*"])
@RequestMapping(value=["/api/v1/verify-token"])
class VerifyTokenController(private val verifyTokenService: VerifyTokenService) {

    @GetMapping("/{id}/{token}")
    fun verifyToken(@PathVariable("id") id: String, @PathVariable("token") token:String): ResponseEntity<Message> = this.verifyTokenService.verifyToken(token, id.toLong())

}