package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.sharedmodel.BankAccountModel
import ng.siteworx.partner.serviceprovider.sharedmodel.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface ArtisanRepo: JpaRepository<Artisan, Long> {
    fun findByEmail(email: String): Artisan?
    fun existsByEmail(email: String): Boolean
    fun existsByProfile(profile: Profile): Boolean
    fun findByUsername(username: String): Artisan?
    fun deleteByEmail(email: String): Artisan?

// Profile Related abstract functions
    fun findByProfile(profile: Profile): Artisan?
    fun deleteByProfile(profile: Profile): Artisan?

//    Bank Account Related abstract functions
    fun findByAccount(account: BankAccountModel): Artisan?
}