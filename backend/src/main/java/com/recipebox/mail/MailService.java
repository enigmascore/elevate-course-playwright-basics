package com.recipebox.mail;

import org.springframework.stereotype.Service;

/** Composes the app's emails and QUEUES them; EmailSender delivers them later. */
@Service
public class MailService {

    private final EmailJobRepository emailJobRepository;

    public MailService( EmailJobRepository emailJobRepository ) {
        this.emailJobRepository = emailJobRepository;
    }

    public void sendWelcome( String email, String name ) {
        emailJobRepository.save( new EmailJob( email, "Welcome to Recipe Box, " + name,
                "Hello " + name + ",\n\nYou have been added as a cook in Recipe Box. "
                        + "Your recipes will appear on your page.\n\nHappy cooking." ) );
    }
}
