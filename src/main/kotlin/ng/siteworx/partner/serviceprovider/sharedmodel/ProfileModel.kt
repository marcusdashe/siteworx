package ng.siteworx.partner.serviceprovider.sharedmodel

import jakarta.persistence.*
import ng.siteworx.partner.serviceprovider.artisan.constants.Constants
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import java.util.Date


@Entity
@Table(name = "artisan_profile")
class ProfileModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name="bio")
    var bio: String? = null

    @Column(name = "trade_category")
    @Enumerated(EnumType.STRING)
    var tradeCategory: Constants.Category = Constants.Category.NONE

    @Column(name="date_of_birth")
    var dateOfBirth: Date? = null

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    var gender: Constants.Gender = Constants.Gender.NON_BINARY

    @Column(name="year_of_experience")
    var yearOfExperience: Int? = null

    @Column(name="order_count")
    var orderCount: Int? = null

    @Column(name = "photo_url")
    var photoUrl: String? = null

    @Column(name = "highest_certificates")
    @Enumerated(EnumType.STRING)
    var highestCertificates: Constants.HighestCertificate = Constants.HighestCertificate.NONE

    @OneToOne(fetch = FetchType.LAZY, optional= false)
    @JoinColumn(name = "artisan_id")
    private var artisan: Artisan? = null

}