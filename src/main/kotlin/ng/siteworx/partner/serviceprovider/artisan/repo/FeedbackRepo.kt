package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.sharedmodel.FeedbackModel
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepo : JpaRepository<FeedbackModel, Long> {
}