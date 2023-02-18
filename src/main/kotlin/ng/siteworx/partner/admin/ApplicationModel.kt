package ng.siteworx.partner.admin

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name="application_details")
class ApplicationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "app_name", nullable = false)
    var appName: String? = "Siteworx"

    @Column(name = "app_desc", nullable = false)
    var appDescription: String? = "Construction Skills and Services Marketplace"

    @Column(name = "app_version", nullable = false)
    var appVersion: String? = "0.0.1"

    @Column(name = "release_date", nullable = false)
    var releaseDate: Instant = Instant.now()

    @Column(name = "developer_name", nullable = false)
    var developerName: String? = "Marcus Dashe"

    @Column(name = "developer_email", nullable = false)
    var developerEmail: String? = "marcusdashe.developer@gmail.com"

    @Column(name = "app_category", nullable = false)
    var appCategory: String? = "construction"
}