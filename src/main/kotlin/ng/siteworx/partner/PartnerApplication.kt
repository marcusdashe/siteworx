package ng.siteworx.partner

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class PartnerApplication

fun main(args: Array<String>) {
	runApplication<PartnerApplication>(*args){
		setBannerMode(Banner.Mode.OFF)
	}
}
