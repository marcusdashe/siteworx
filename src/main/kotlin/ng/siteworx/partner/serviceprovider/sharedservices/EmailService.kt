package ng.siteworx.partner.serviceprovider.sharedservices

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import ng.siteworx.partner.serviceprovider.artisan.repo.ArtisanRepo
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service


@Service
class EmailService(private val artisanRepo: ArtisanRepo, private var mailSender: JavaMailSender) {

    fun sendSimpleEmail(to: String?, subject: String?, text: String?) {
        var message: SimpleMailMessage = SimpleMailMessage()
        message.from = "no-reply@siteworx.ng"
        message.setTo(to)
        message.subject = subject
        message.text = text
        mailSender.send(message)
    }

    fun sendHTMLEmail(to: String?, subject: String?, htmlText: String?){
        val message = mailSender.createMimeMessage()

        message.setFrom(InternetAddress("no-reply@siteworx.ng"))
        message.setRecipients(MimeMessage.RecipientType.TO, to)
        message.subject = subject
        val htmlContent = htmlText
        message.setContent(htmlContent, "text/html; charset=utf-8")
        mailSender.send(message)
    }
}
