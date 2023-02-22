package ng.siteworx.partner.serviceprovider.artisan.controller

import ng.siteworx.partner.serviceprovider.artisan.dto.BankAccountDTO
import ng.siteworx.partner.serviceprovider.artisan.service.BankAccountService
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/api/v1/bank-account"])
class BankAccountController(private val bankAccountService: BankAccountService) {

//    Get my bank account details
    @GetMapping(value = ["/"])
    fun fetchMyAccount(@CookieValue("jwt") jwt: String): ResponseEntity<Message>  = this.bankAccountService.findBankAccountDetailsByArtisan(jwt)
//    Adding Bank Account
    @PostMapping("/")
    fun addBankAccount(@CookieValue("jwt") jwt: String, payload: BankAccountDTO): ResponseEntity<Message> = this.bankAccountService.createBankAccount(jwt, payload)

//    Updating Bank Account
    @PutMapping("/update")
    fun updateBankAccount(@CookieValue("jwt") jwt: String, payload: BankAccountDTO): ResponseEntity<Message> = this.bankAccountService.updateBankAccount(jwt, payload)

//  Delete Bank Account
    @DeleteMapping("/delete")
    fun deleteBankAccount(@CookieValue("jwt") jwt: String): ResponseEntity<Message> = this.bankAccountService.deleteArtisanBankAccount(jwt)

//    Find account details by ID
    @GetMapping(value = ["/{id}"])
    fun fetchAccountById(@PathVariable("id") id: String): ResponseEntity<Message>  = this.bankAccountService.findBankAccountDetailsById(id)

//    Find bank account by account number
    @GetMapping(value = ["/account-number/{accountNumber}"])
    fun fetchBankDetailByAccountNumber(@PathVariable("accountNumber") accountNumber: String): ResponseEntity<Message> = this.bankAccountService.findBankAccountByAccountNumber(accountNumber)

}