package ng.siteworx.partner.client.controller

import ng.siteworx.partner.client.service.ClientService
import ng.siteworx.partner.dto.RegistrationDTO
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/api/v1/client"])
class ClientController(private val clientService: ClientService) {
//    @GetMapping("/username/{username}")
//    fun fetchArtisanByUsername(@PathVariable("username") username: String): ResponseEntity<Message> = this.artisanService.getArtisanByUsername(username)
//
//    @PostMapping("/self/update")
//    fun updateArtisan(@CookieValue("jwt") jwt: String, payload: RegistrationDTO): ResponseEntity<Message> = this.artisanService.selfUpdate(jwt, payload)

      @GetMapping(value = ["/"])
      fun fetchClient(@CookieValue("jwt") jwt: String): ResponseEntity<Message> = this.clientService.client(jwt)

      @GetMapping("/all")
      fun fetchAllClients(): ResponseEntity<Message> = this.clientService.getAllClients()

      @GetMapping("/{id}")
      fun fetchClientByID(@PathVariable id: String): ResponseEntity<Message> = this.clientService.getClientByID(id.toLong())

      @GetMapping("/email/{email}")
      fun fetchClientByEmail(@PathVariable email: String): ResponseEntity<Message> = this.clientService.getClientByEmail(email)

      @GetMapping("/username/{username}")
      fun fetchClientByUsername(@PathVariable("username") username: String): ResponseEntity<Message> = this.fetchClientByUsername(username)

      @PostMapping("/self/update")
      fun updateClient(@CookieValue("jwt") jwt: String, payload: RegistrationDTO): ResponseEntity<Message> = this.clientService.selfUpdateBasicClientInfo(jwt, payload)

      @PutMapping("/admin/update/{id}")
      fun adminUpdateClient(@PathVariable("id") id: String, payload: RegistrationDTO): ResponseEntity<Message> = this.clientService.selfUpdateBasicClientInfo(id, payload)

      @DeleteMapping("/{id}")
      fun eraseClientByID(@PathVariable("id") id: String): ResponseEntity<Message> = this.eraseClientByID(id)

      @DeleteMapping("/erase-client/{email}")
      fun eraseClientByEmail(@PathVariable("email") email: String): ResponseEntity<Message> = this.clientService.deleteByEmail(email)


}