package ng.siteworx.partner.serviceprovider.sharedpayload

data class Message(public val status: Boolean, public val message: String, public val entity: Any? = "")