package ng.siteworx.partner.serviceprovider.sharedmodel

import jakarta.persistence.*
import ng.siteworx.partner.client.model.Client
import ng.siteworx.partner.enums.SiteworxEnums
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan


@Entity
@Table(name="bank_account_details")
class BankAccountModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "bank_name")
    var bankName: String = ""

    @Column(name = "account_number", nullable = false, unique = true)
    var accountNumber: String = ""

    @Column(name = "account_name", nullable = false)
    var accountName: String = ""

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var accountType: SiteworxEnums.AccountType = SiteworxEnums.AccountType.SAVING

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_id")
    var artisan: Artisan? = null

    @OneToOne(fetch = FetchType.LAZY, optional= false)
    @JoinColumn(name = "client_id")
    private var client: Client? = null
}