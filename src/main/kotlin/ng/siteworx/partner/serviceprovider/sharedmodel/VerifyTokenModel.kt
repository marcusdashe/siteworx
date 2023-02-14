package ng.siteworx.partner.serviceprovider.sharedmodel

import jakarta.persistence.*
import java.util.*
import java.time.Duration
import java.time.Instant

@Entity
@Table(name="verify_token")
class VerifyTokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long?=null

    @Column(name = "token", nullable = false)
    var token:String=""

    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt:Date= Date(System.currentTimeMillis())

}