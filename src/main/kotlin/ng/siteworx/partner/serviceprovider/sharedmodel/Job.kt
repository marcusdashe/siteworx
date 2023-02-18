package ng.siteworx.partner.serviceprovider.sharedmodel

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import ng.siteworx.partner.client.model.Client
import ng.siteworx.partner.enums.SiteworxEnums
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan

@Entity
@Table(name="orders")
class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "job_category", nullable = false)
    @Enumerated(EnumType.STRING)
    var jobType: SiteworxEnums.Category = SiteworxEnums.Category.NONE

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var jobCreationDate: Date = Date(System.currentTimeMillis())

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artisan_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private lateinit var artisan: Artisan

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private lateinit var client: Client
}