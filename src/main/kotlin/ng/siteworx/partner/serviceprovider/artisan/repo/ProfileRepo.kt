package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.sharedmodel.ProfileModel
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepo: JpaRepository<ProfileModel, Long> {
}