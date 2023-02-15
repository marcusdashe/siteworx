package ng.siteworx.partner.template

object EmailTemplate {
    var emailHtmlSubject: String= "Welcome to Siteworx! Please verify your e-mail"
    var emailHtmlBody = """
                <header style="color: "#cb4154"; font-size: "2rem"">This is a confirmation email to confirm your e-mail address. </header 
                <p>The verification link will expire in 24 hours</p>
                <p>Please click on the following link to confirm your e-mail address:</p>
            """.trimIndent()
}