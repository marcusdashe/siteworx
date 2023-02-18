package ng.siteworx.partner.admin

//import ng.siteworx.partner.client.model.Client
import org.springframework.data.jpa.repository.JpaRepository

interface AdminRepo : JpaRepository<Admin, Long> {
    fun findByEmail(email: String): Admin
    fun existsByEmail(email: String): Boolean
    fun findByUsername(username: String): Admin
    fun deleteByEmail(email: String): Admin
}