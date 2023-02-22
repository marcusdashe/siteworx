package ng.siteworx.partner.application

import ng.siteworx.partner.application.ApplicationModel
import org.springframework.data.jpa.repository.JpaRepository

interface ApplicationRepo : JpaRepository<ApplicationModel, Long> {
}