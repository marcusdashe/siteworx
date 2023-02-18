package ng.siteworx.partner.admin

import org.springframework.data.jpa.repository.JpaRepository

interface ApplicationRepo : JpaRepository<ApplicationModel, Long> {
}