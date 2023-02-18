package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.sharedmodel.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface ArtisanRepo: JpaRepository<Artisan, Long> {
    fun findByEmail(email: String): Artisan?
    fun existsByEmail(email: String): Boolean
    fun existsByProfile(profile: Profile): Boolean
    fun findByUsername(username: String): Artisan?
    fun deleteByEmail(email: String): Artisan?
//    fun saveByProfile(profile: Profile): Artisan
    fun findByProfile(profile: Profile): Artisan?
    fun deleteByProfile(profile: Profile): Artisan?
}