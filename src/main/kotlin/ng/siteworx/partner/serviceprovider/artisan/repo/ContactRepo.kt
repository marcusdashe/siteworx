package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.sharedmodel.ContactModel
import org.springframework.data.jpa.repository.JpaRepository

interface ContactRepo: JpaRepository<ContactModel, Long> {
}