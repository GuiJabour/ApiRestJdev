package curso.api.rest.service;


import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class ServiceEnviaEmail {
	
	private String userName = "projetoangularguijabour@gmail.com";
	private String userPassword = "dddjavaangular";

	public void enviarEmail(String assunto, String emailDestino, String mensagem) throws Exception {
		
		Properties properties = new Properties();
		
		properties.put("mail.smtp.ssl.trust", "*");
		properties.put("mail.smtp.auth", "true"); /*Autorização*/
		properties.put("mail.smtp.starttls", "true"); /*Autenticação*/
		properties.put("mail.smtp.host", "smtp.gmail.com");/*Servidor Google*/
		properties.put("mail.smtp.port", "465"); /*Porta Servidor*/
		properties.put("mail.smtp.socketFactory.port", "465"); /*Especifica Porta Socket*/
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); /*Classe de conexão Socket*/
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				
				return new PasswordAuthentication(userName, userPassword);
			}
		});
		
		Address[] toUser = InternetAddress.parse(emailDestino);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(userName));/*Quem está enviando*/
		message.addRecipients(Message.RecipientType.TO, toUser); /*Para quem vai o email*/
		message.setSubject(assunto);
		message.setText(mensagem);
		
		System.out.println("Inicio Envio Send");
		Transport.send(message);
		System.out.println("Fim Envio Send");
	}
	
}
