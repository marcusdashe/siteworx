package ng.siteworx.partner.serviceprovider.sharedmodel

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import ng.siteworx.partner.client.model.Client
import ng.siteworx.partner.enums.SiteworxEnums
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*

@Entity
@Table(name="feedback")
class FeedbackModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "feedback_comment")
    var feedbackComment: String = ""

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var satisfactionType: SiteworxEnums.SatisfactionType = SiteworxEnums.SatisfactionType.NOT_SATISFY

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var created: Date = Date(System.currentTimeMillis())

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