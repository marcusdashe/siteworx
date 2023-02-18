package ng.siteworx.partner.serviceprovider.sharedmodel

import jakarta.persistence.*
import ng.siteworx.partner.enums.SiteworxEnums
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import java.nio.file.Path
import java.time.LocalDate
import java.util.Date


@Entity
@Table(name = "artisan_profile")
class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name="bio")
    var bio: String? = null

    @Column(name = "trade_category")
    @Enumerated(EnumType.STRING)
    var tradeCategory: SiteworxEnums.Category = SiteworxEnums.Category.NONE

    @Column(name="date_of_birth")
    var dateOfBirth: LocalDate? = null

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    var gender: SiteworxEnums.Gender= SiteworxEnums.Gender.NONE

    @Column(name="year_of_experience")
    var yearsOfExperience: Int? = null

    @Column(name="order_count")
    var orderCount: Int? = null

    @Column(name = "photo_url")
    var photoUrl: String?  = null

    @Column(name = "highest_certificates")
    @Enumerated(EnumType.STRING)
    var highestCertificatesObtained: SiteworxEnums.HighestCertificate = SiteworxEnums.HighestCertificate.PSLC

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_id")
    var artisan: Artisan? = null
}