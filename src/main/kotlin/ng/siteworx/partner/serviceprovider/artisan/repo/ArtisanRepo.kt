package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import org.springframework.data.jpa.repository.JpaRepository

interface ArtisanRepo: JpaRepository<Artisan, Long> {
    fun findByEmail(email: String): Artisan?
    fun existsByEmail(email: String): Boolean
    fun findByUsername(username: String): Artisan?
    fun deleteByEmail(email: String): Artisan?
}