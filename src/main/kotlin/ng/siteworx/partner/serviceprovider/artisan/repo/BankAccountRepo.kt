package ng.siteworx.partner.serviceprovider.artisan.repo

import ng.siteworx.partner.serviceprovider.sharedmodel.BankAccountModel
import org.springframework.data.jpa.repository.JpaRepository

interface BankAccountRepo: JpaRepository<BankAccountModel, Long> {
}