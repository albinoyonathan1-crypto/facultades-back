package com.example.facultades.service;

import com.example.facultades.dto.EmailDtoContacto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${correo.destinatario}")
    private String correoDestinatario;

    @Value("${SERVIDOR}")
    private String servidor;// = "http://localhost:8080";
    //@Autowired
    //private TemplateEngine templateEngine;
    @Override
    public void sendMail(EmailDtoContacto email) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(correoDestinatario);
            helper.setSubject(email.getAsunto());
            helper.setText(email.getMensaje() + "\n" + "\nCorreo enviado por: " + email.getNombre() + " " + email.getApellido() + "\nDirección de correo: " + email.getEmisor());


            //Context context = new Context();
            //context.setVariable("mensaje", email.getMensaje());
            //String contentHTML = templateEngine.process("email.html", context);

            //helper.setText(contentHTML, true);
            javaMailSender.send(message);
        }catch (Exception ex){
            throw new RuntimeException("error al enviar el correo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void enviarEmail(String emailDestinatario, String asunto, String mensaje)  {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailDestinatario);
            helper.setSubject(asunto);
            helper.setText(mensaje, true);
            javaMailSender.send(message);
        }catch (Exception ex){
            throw new RuntimeException("error al enviar el correo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void enviarCorreoVerificacionEmail(String email, String token, Long idTokenVerificador) {
        String link = servidor+"/usuario/verificarEmail/"+token+"/"+idTokenVerificador;
        String mensajeHtml = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px; border: 1px solid #ddd; border-radius: 5px; text-align: center;'>"
                + "<h2 style='color: #0066cc;'>Gracias por registrarte en FacusArg</h2>"
                + "<h3 style='color: #0066cc;'>Verificación de Correo Electrónico</h3>"
                + "<p>Por favor, haz clic en el siguiente enlace para verificar tu correo electrónico:</p>"
                + "<p style='text-align: center; margin: 20px 0;'><a href='" + link + "' "
                + "style='display: inline-block; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #28a745; "
                + "text-decoration: none; border-radius: 5px;'>Verificar Correo</a></p>"
                + "<p>Si el botón no funciona, copia y pega el siguiente enlace en tu navegador:</p>"
                + "<p style='background: #f4f4f4; padding: 10px; border-radius: 5px; word-break: break-all; text-align: center;'>" + link + "</p>"
                + "<p style='color: #777;'>Este enlace expirará en 24 horas.</p>"
                + "<p style='color: #777;'>Este es un mensaje automático. No es necesario responder.</p>"
                + "</div>";
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Verifica tu email");
            helper.setText( mensajeHtml, true);

            javaMailSender.send(message);
        }catch (Exception ex){
            throw new RuntimeException("error al enviar el correo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void enviarCorreoRecuperacionContrasena(String email, String token, Long idTokenVerificador) {
        String link = servidor+"/TokenRecuperacionContrasenia/reestablecerContrasenia/"+token+"/"+idTokenVerificador;
        String mensajeHtml = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px; border: 1px solid #ddd; border-radius: 5px; text-align: center;'>"
                + "<h2 style='color: #0066cc;'>Recuperación de Contraseña</h2>"
                + "<h3 style='color: #0066cc;'>Haz clic en el siguiente enlace para restablecer tu contraseña</h3>"
                + "<p style='color: #555;'>Por favor, haz clic en el siguiente botón para restablecer tu contraseña:</p>"
                + "<p style='text-align: center; margin: 20px 0;'><a href='" + link + "' "
                + "style='display: inline-block; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #28a745; "
                + "text-decoration: none; border-radius: 5px;'>Restablecer Contraseña</a></p>"
                + "<p>Si el botón no funciona, copia y pega el siguiente enlace en tu navegador:</p>"
                + "<p style='background: #f4f4f4; padding: 10px; border-radius: 5px; word-break: break-all; text-align: center;'>" + link + "</p>"
                + "<p style='color: #777;'>Este enlace expirará en 30 minutos.</p>"
                +"<p style='color: #777;'>Este es un mensaje automático. No es necesario responder.</p>"
                + "</div>";
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Reestablecer contraseña");
            helper.setText(mensajeHtml, true);

            javaMailSender.send(message);
        } catch (Exception ex) {
            throw new RuntimeException("Error al enviar el correo: " + ex.getMessage(), ex);
        }
    }


    @Override
    public void enviarEmailContraseniaRecuperada(String emailDestinatario, String nuevacContrasenia) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailDestinatario);
            helper.setSubject("Se ha restablecido tu contraseña");
            helper.setText("Tu nueva contraseña es : " + nuevacContrasenia);

            javaMailSender.send(message);
        }catch (Exception ex){
            throw new RuntimeException("error al enviar el correo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void enviarMailContacto(String correoOrigen, String mensaje){
        try {
            String mensajeHtml = "<div style='font-family: Arial, sans-serif; color: #333; padding: 20px; border: 1px solid #ddd; border-radius: 5px; text-align: center;'>"
                    + "<h2 style='color: #0066cc;'>Nuevo Mensaje de Contacto</h2>"
                    + "<p><strong>Correo de origen:</strong> " + correoOrigen + "</p>"
                    + "<p style='color: #555;'>" + mensaje + "</p>"
                    + "<hr style='margin: 20px 0; border: 1px solid #ddd;'>"
                    + "</div>";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo("yonaalbino5@gmail.com");
            helper.setSubject("FacusArg");
            helper.setText(mensajeHtml, true);

            javaMailSender.send(message);
        }catch (Exception ex){
            throw new RuntimeException("error al enviar el correo: " + ex.getMessage(), ex);
        }
    }
}
