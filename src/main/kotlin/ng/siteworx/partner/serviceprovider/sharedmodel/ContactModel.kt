package ng.siteworx.partner.serviceprovider.sharedmodel

import jakarta.persistence.*
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan

@Entity
@Table(name="artisan_contact")
class ContactModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name="phone_number1", unique = true)
    var phoneNumber1: String? = null

    @Column(name="phone_number2", unique = true)
    var phoneNumber2: String? = null

    @Column(name="street_number")
    var streetNumber: String? = null

    @Column(name="zip_code")
    var zipCode: String? = null

    @Column(name="street_name")
    var streetName: String? = null

    @Column(name="city")
    var cityName: String? = null

    @Column(name="state")
    var stateName: String? = null

    @OneToOne(fetch = FetchType.LAZY, optional= false)
    @JoinColumn(name = "artisan_id")
    private var artisan: Artisan? = null
}