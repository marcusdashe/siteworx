package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.sharedmodel.Job
import org.springframework.data.jpa.repository.JpaRepository

interface JobRepo: JpaRepository<Job, Long> {
}