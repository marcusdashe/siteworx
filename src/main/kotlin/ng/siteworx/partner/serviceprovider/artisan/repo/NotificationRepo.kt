package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.sharedmodel.NotificationModel
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepo: JpaRepository<NotificationModel, Long> {
}