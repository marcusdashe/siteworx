package ng.siteworx.partner.serviceprovider.sharedrepo

import ng.siteworx.partner.serviceprovider.sharedmodel.VerifyTokenModel
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Date

interface VerifyTokenRepo:JpaRepository<VerifyTokenModel,Long> {
    fun findByToken(token:String):VerifyTokenModel
}