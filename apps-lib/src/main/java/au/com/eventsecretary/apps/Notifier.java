package au.com.eventsecretary.apps;

import au.com.eventsecretary.client.NotificationClient;
import au.com.eventsecretary.simm.DateUtility;
import net.sf.javaprinciples.notification.Message;
import net.sf.javaprinciples.notification.Notification;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Notifier {
    private final NotificationClient notificationClient;
    private final String supportEmailAddress;
    private final String officeEmailAddress;

    private Logger logger = LoggerFactory.getLogger(Notifier.class);

    public Notifier(String supportEmailAddress, String officeEmailAddress, NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
        this.supportEmailAddress = supportEmailAddress;
        this.officeEmailAddress = officeEmailAddress;
    }

    public void notifyError(String msg, Exception e) {
        if (msg == null && e != null) {
            msg = e.getMessage();
            if (msg == null) {
                msg = e.getClass().getName();
            }
        }
        if (e != null) {
            logger.error(msg, e);
        }
        if (StringUtils.isBlank(this.supportEmailAddress)) {
            return;
        }

        Notification notification = new Notification();
        List<Message> messageList = new ArrayList<>();
        notification.setMessage(messageList);
        notification.setFrom("support@eventsecretary.com.au");
        notification.setSubject("EventSecretary ALERT : " + DateUtility.machineTimestamp(DateUtility.now()));

        Message message = new Message();
        message.setTo(supportEmailAddress);
        message.setMessage(msg);
        messageList.add(message);

        try {
            notificationClient.send(notification, null);
        } catch (Exception e2) {
            logger.error("Failed to send error notification", e2);
        }
    }

    public void notifyOffice(String subject, String msg) {
        if (StringUtils.isBlank(this.supportEmailAddress)) {
            return;
        }
        notifyEmail(this.supportEmailAddress, subject, msg);
    }

    public void notifyEmail(String emailAddress, String subject, String msg) {

        Notification notification = new Notification();
        List<Message> messageList = new ArrayList<>();
        notification.setMessage(messageList);
        notification.setFrom("support@eventsecretary.com.au");
        notification.setSubject(subject);

        Message message = new Message();
        message.setTo(emailAddress);
        message.setMessage(msg);
        messageList.add(message);

        try {
            notificationClient.send(notification, null);
        } catch (Exception e2) {
            logger.error("Failed to send error notification", e2);
        }
    }

}
