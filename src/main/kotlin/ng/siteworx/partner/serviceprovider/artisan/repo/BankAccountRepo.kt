package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.sharedmodel.BankAccountModel
import org.springframework.data.jpa.repository.JpaRepository

interface BankAccountRepo: JpaRepository<BankAccountModel, Long> {
    fun findByArtisan(artisan: Artisan): BankAccountModel?
    fun deleteByArtisan(artisan: Artisan): BankAccountModel?
    fun findByAccountNumber(accountNumber: String): BankAccountModel?
    fun deleteByAccountNumber(accountNumber: String): BankAccountModel?
    fun findByBankName(bankName: String): BankAccountModel?
    fun findByClientId(clientId: String): BankAccountModel?
    fun deleteByClientId(clientId: String): BankAccountModel?

}