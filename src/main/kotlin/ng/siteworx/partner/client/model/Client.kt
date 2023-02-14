package ng.siteworx.partner.client.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import ng.siteworx.partner.serviceprovider.sharedmodel.BankAccountModel
import ng.siteworx.partner.serviceprovider.sharedmodel.FeedbackModel
import ng.siteworx.partner.serviceprovider.sharedmodel.Job
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@Entity
@Table(name="client")
class Client {
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

    @Column(name="profession")
    var profession: String? = null

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

    @Column(name = "count_order_placed")
    var jobOrderCount: Int = 0

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    private val job: Set<Job?> = setOf()

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    private val feedback: Set<FeedbackModel?> = setOf()

    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    private var account: BankAccountModel? = null

    fun comparePassword(password: String): Boolean{
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}