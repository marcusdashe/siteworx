package ng.siteworx.partner.client.repo

import ng.siteworx.partner.client.model.Client
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import org.springframework.data.jpa.repository.JpaRepository

interface ClientRepo : JpaRepository<Client, Long> {
    fun findByEmail(email: String): Client?
    fun existsByEmail(email: String): Boolean
    fun findByUsername(username: String): Client?
    fun deleteByEmail(email: String): Client?
}