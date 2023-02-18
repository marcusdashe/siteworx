package ng.siteworx.partner.admin

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
@Table(name = "admin")
class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "username", nullable = false, unique = true)
    @Size(max = 30)
    @Size(min = 3, message = "Username must be between 3 and 30 characters")
    var username: String? = ""

    @Email(message = "Please provide a valid email")
    @Column(name = "email", nullable = false, unique = true)
    var email: String? = ""

    @Column(name = "password", nullable = false)
    var password: String? = ""
        @JsonIgnore
        get() = field
        set(value) {
            val passwordEncoder = BCryptPasswordEncoder()
            field = passwordEncoder.encode(value)
        }

    fun comparePassword(password: String): Boolean{
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}