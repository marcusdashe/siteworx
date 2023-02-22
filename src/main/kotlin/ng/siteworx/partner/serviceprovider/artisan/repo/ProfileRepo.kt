package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.sharedmodel.Profile
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity

interface ProfileRepo: JpaRepository<Profile, Long> {
    fun findByArtisan(artisan: Artisan): Profile?
    fun deleteByArtisan(artisan: Artisan): Profile?
}