package ng.siteworx.partner.serviceprovider.sharedmodel

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import ng.siteworx.partner.serviceprovider.artisan.model.Artisan
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*

@Entity
@Table(name="notification")
class NotificationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "notification_message")
    var summary: String = ""

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var created: Date = Date(System.currentTimeMillis())

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artisan_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private lateinit var artisan: Artisan
}