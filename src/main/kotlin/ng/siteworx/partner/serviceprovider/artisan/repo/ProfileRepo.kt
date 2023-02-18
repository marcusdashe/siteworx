package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import ng.siteworx.partner.serviceprovider.sharedmodel.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepo: JpaRepository<Profile, Long> {
    fun findByArtisan(artisan: Artisan): Profile?
    fun deleteByArtisan(artisan: Artisan): Profile?
}