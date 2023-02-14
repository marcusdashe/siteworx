package ng.siteworx.partner.serviceprovider.artisan.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import ng.siteworx.partner.serviceprovider.sharedmodel.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@Entity
@Table(name = "artisan")
class Artisan() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Size(max = 30)
    @Size(min = 3, message = "Name must be between 3 and 30 characters")
    @Column(name="first_name", nullable = false)
    var firstName: String? = null

    @Size(max = 30)
    @Size(min = 3, message = "Name must be between 3 and 30 characters")
    @Column(name="last_name", nullable = false)
    var lastName: String? = null

    @Email(message = "Please provide a valid email")
    @Column(name = "email", nullable = false, unique = true)
    var email: String? = null

    @Column(name = "username", nullable = false, unique = true)
    @Size(max = 30)
    @Size(min = 3, message = "Name must be between 3 and 30 characters")
    var username: String? = null

    @Column(name = "verify", nullable = false)
    var isVerify: Boolean = false

    @Column(name = "availability", nullable = false)
    var isAvailable: Boolean = false

    @Column(name = "subscribe", nullable = false)
    var hasSubscribe: Boolean = false

    @Column(name = "password", nullable = false)
    var password: String = ""
        @JsonIgnore
        get() = field
        set(value) {
            val passwordEncoder = BCryptPasswordEncoder()
            field = passwordEncoder.encode(value)
        }

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var created: Date = Date(System.currentTimeMillis())

    @OneToMany(mappedBy = "artisan", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    private val job: Set<Job?> = setOf()

    @OneToMany(mappedBy = "artisan", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    private val feedback: Set<FeedbackModel?> = setOf()

    @OneToMany(mappedBy = "artisan", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    private val notification: Set<NotificationModel?> = setOf()

    @OneToOne(mappedBy = "artisan", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    private var profile: ProfileModel? = null

    @OneToOne(mappedBy = "artisan", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    private var contact: ContactModel? = null

    @OneToOne(mappedBy = "artisan", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    private var account: BankAccountModel? = null

    fun comparePassword(password: String): Boolean{
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}