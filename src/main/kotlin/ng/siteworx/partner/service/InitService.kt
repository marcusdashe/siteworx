package ng.siteworx.partner.service

import jakarta.validation.ConstraintViolationException
import ng.siteworx.partner.admin.Admin
import ng.siteworx.partner.admin.AdminRepo
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service

@Service
class InitService(private val adminRepo: AdminRepo) : CommandLineRunner {

    @Value("\${siteworx.ng.admin.email}")
    private val adminEmail: String? = null

    @Value("\${siteworx.ng.admin.username}")
    private val adminUsername: String? = null

    @Value("\${siteworx.ng.admin.password}")
    private val adminPassword: String? = null
    override fun run(vararg args: String?) {
        val admin : Admin = Admin()
        admin.email = adminEmail
        admin.username = adminUsername
        admin.password = adminPassword
        try {
            adminRepo.save(admin)
        } catch (e: ConstraintViolationException) {
            admin.email = "marcusdashe.developer@gmail.com"
            var adminObj = adminRepo.save(admin)
            println(adminObj.password)
            println(e.message)
        }

    }
}