package ng.siteworx.partner.admin

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import ng.siteworx.partner.application.ApplicationModel
import ng.siteworx.partner.dto.LoginDTO
import ng.siteworx.partner.dto.RegistrationDTO
import ng.siteworx.partner.serviceprovider.artisan.service.ArtisanService
import ng.siteworx.partner.serviceprovider.artisan.service.BankAccountService
import ng.siteworx.partner.serviceprovider.artisan.service.ProfileService
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/admin"])
class AdminController(
    private val artisanService: ArtisanService,
    private val profileService: ProfileService,
    private val adminService: AdminService,
    private val bankAccountService: BankAccountService
) {
// Returns Application Information/Details
    @GetMapping(value = ["/app-info"])
    fun getAppInfo(): ResponseEntity<ApplicationModel> = this.adminService.fetchAppInfo()

//    Delete Artisan Profile Information by his/her ID
    @DeleteMapping(value = ["/artisan-profile/{id}"])
    fun wipeOutArtisanProfile(@PathVariable("id") id: String): ResponseEntity<Message> = this.profileService.deleteArtisanProfileById(id)

    //    Administrative Update artisan profile by ID
    @PutMapping("/admin/update/{id}")
    fun adminUpdateArtisan(@PathVariable("id") id: String, payload: RegistrationDTO): ResponseEntity<Message> = this.artisanService.adminUpdate(id, payload)

    //    Administrative Delete artisan profile by ID
    @DeleteMapping("/{id}")
    fun eraseArtisanByID(@PathVariable("id") id: String): ResponseEntity<Message> = this.artisanService.deleteByID(id)

    //    Administrative Delete artisan profile by email
    @DeleteMapping("/erase-artisan/{email}")
    fun eraseArtisanByEmail(@PathVariable("email") email: String): ResponseEntity<Message> = this.artisanService.deleteByEmail(email)

//    Administrative Delete Bank Account Number by Account Number
    @DeleteMapping("/bank-account/{account-number}")
    fun eraseBankAccount(@PathVariable("account-number") accountNumber: String): ResponseEntity<Message> = this.bankAccountService.deleteBankAccountByAccountNumber(accountNumber)
}