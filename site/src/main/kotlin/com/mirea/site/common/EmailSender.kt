package com.mirea.site.common

import com.typesafe.config.ConfigFactory
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {
    private val config = ConfigFactory.load().getConfig("mail")
    val user = config.getString("user")
    val password = config.getString("password")

    val props = Properties()

    val session = Session.getDefaultInstance(props, object : javax.mail.Authenticator() {
        override fun getPasswordAuthentication() = PasswordAuthentication(user, password)
    })

    init {
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.host", "smtp.gmail.com")
        props.put("mail.smtp.port", "587")
    }

    fun send(sendTo: String, title: String, text: String) {
        //Compose the message
        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(user))
            message.setRecipient(Message.RecipientType.TO, InternetAddress(sendTo))
            message.setSubject(title)
            message.setText(text)

            //send the message
            Transport.send(message)

            System.out.println("message sent successfully...")

        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}