package ng.siteworx.partner.serviceprovider.artisan.service

import ng.siteworx.partner.enums.SiteworxEnums
import ng.siteworx.partner.serviceprovider.artisan.dto.BankAccountDTO
import ng.siteworx.partner.serviceprovider.artisan.dto.ProfileDTO
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.artisan.repo.ArtisanRepo
import ng.siteworx.partner.serviceprovider.artisan.repo.BankAccountRepo
import ng.siteworx.partner.serviceprovider.sharedmodel.BankAccountModel
import ng.siteworx.partner.serviceprovider.sharedmodel.Profile
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class BankAccountService(private val artisanService: ArtisanService, private val bankAccountRepo: BankAccountRepo, private val artisanRepo: ArtisanRepo) {
    var returnedBank: Artisan? = null
    fun createBankAccount(jwt: String, payload: BankAccountDTO): ResponseEntity<Message> {
        val artisan: Artisan? = artisanService.extractArtisanByJWT(jwt)
        if(artisan != null){
            try{
                val bankAccount = BankAccountModel()
                bankAccount.bankName = payload.bankName
                bankAccount.accountNumber = payload.accountNumber
                bankAccount.accountName = payload.accountName
                bankAccount.accountType = when(payload.accountType.lowercase()){
                    "saving", "savings" -> SiteworxEnums.AccountType.SAVING
                    "current" -> SiteworxEnums.AccountType.CURRENT
                    "domiciliary" -> SiteworxEnums.AccountType.DOMICILIARY
                    else -> SiteworxEnums.AccountType.OTHER
                }

                bankAccount.artisan = artisan
                val returnedBank = artisanRepo.save(artisan)
            } catch (e: Exception){
                return ResponseEntity.badRequest().body(Message(false, "Error occurred while updating artisan account detail", null))
            }

            return ResponseEntity.ok().body(Message(true, "Artisan Found", returnedBank))
        }
        return ResponseEntity.ok().body(Message(false, "Artisan not Found", null))
    }

    fun updateBankAccount(jwt: String, payload: BankAccountDTO): ResponseEntity<Message>{
        val artisan: Artisan? = artisanService.extractArtisanByJWT(jwt)
        val bankAccount = artisan?.let{bankAccountRepo.findByArtisan(it) }
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan not found", null))

        try{
            when {
                payload.accountName.isBlank() -> bankAccount.accountName = payload.accountName
                payload.accountNumber.isBlank() -> bankAccount.accountNumber = payload.accountNumber
                payload.bankName.isNotBlank() -> bankAccount.bankName = payload.bankName
                payload.accountType.isNotBlank() -> bankAccount.accountType = when(payload.accountType.lowercase()){
                    "saving", "savings" -> SiteworxEnums.AccountType.SAVING
                    "current" -> SiteworxEnums.AccountType.CURRENT
                    "domiciliary" -> SiteworxEnums.AccountType.DOMICILIARY
                    else -> SiteworxEnums.AccountType.OTHER
                }
            }
            bankAccount.artisan = artisan
            artisanRepo.save(artisan)
        } catch (e: Exception){
            return ResponseEntity.badRequest().body(Message(false, "Error occur while updating artisan/client Bank details", null))
        }
        return ResponseEntity.ok().body(Message(true, "Artisan Found", bankAccount))
    }

    fun deleteArtisanBankAccount(jwt: String): ResponseEntity<Message>{
        val artisan: Artisan? = artisanService.extractArtisanByJWT(jwt)
        val bankAccount = artisan?.let{bankAccountRepo.findByArtisan(it) }
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan not found", null))

            try{
                bankAccountRepo.delete(bankAccount)
            } catch(e: Exception){
                return ResponseEntity.badRequest().body(Message(false, "Error occur while deleting artisan's bank account", null))
            }
        return ResponseEntity.ok().body(Message(true, "Artisan's account details deleted successfully", artisan))
    }

    fun findBankAccountDetailsByArtisan(jwt: String): ResponseEntity<Message>{
        val artisan: Artisan? = artisanService.extractArtisanByJWT(jwt)
        val bankAccount = artisan?.let{bankAccountRepo.findByArtisan(it) }
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan not found", null))
        return ResponseEntity.ok().body(Message(true, "Artisan's account details found", bankAccount))
    }

    fun findBankAccountDetailsById(id: String): ResponseEntity<Message> {
        val bankAccount: BankAccountModel? = this.bankAccountRepo.getReferenceById(id.toLong())
        bankAccount?.let {
            return ResponseEntity.ok().body(Message(true, "Artisan's account details found", it))
        } ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Account not found", null))
    }

    fun findBankAccountByAccountNumber(accountNumber: String): ResponseEntity<Message> {
        val bankAccount: BankAccountModel? = this.bankAccountRepo.findByAccountNumber(accountNumber)
        bankAccount?.let {
            return ResponseEntity.ok().body(Message(true, "Artisan's account details found", it))
        } ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Account not found", null))

        }

    fun deleteBankAccountByAccountNumber(accountNumber: String): ResponseEntity<Message> {
        val bankAccount: BankAccountModel? = this.bankAccountRepo.findByAccountNumber(accountNumber)
        bankAccount?.let {
            val bankAccountR = bankAccountRepo.deleteByAccountNumber(accountNumber)
            return ResponseEntity.ok().body(Message(true, "Artisan's account details deleted", bankAccountR))
        }?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Bank Account details not found"))
    }

}