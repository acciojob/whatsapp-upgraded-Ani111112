package com.driver;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {
    WhatsappRepository whatsappRepository = new WhatsappRepository();

    public String createUser(String name, String mobile) {
        try {
            String user = whatsappRepository.createUser(name, mobile);
            return user;
        }catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public Group createGroup(List<User> users) {
        Group group = whatsappRepository.createGroup(users);
        return group;
    }

    public int createMessage(String content) {
        return whatsappRepository.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) {
        try {
            int count = whatsappRepository.sendMessage(message, sender, group);
            return count;
        }catch (RuntimeException exception) {
            return 0;
        }

    }

    public String changeAdmin(User approver, User user, Group group) {
        try {
            return whatsappRepository.changeAdmin(approver, user, group);
        }catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public int removeUser(User user) {
        try {
            return whatsappRepository.removeUser(user);
        }catch (RuntimeException e) {
            return -2;
        }
    }

    public String findMessage(Date start, Date end, int k) {
        try {
            return whatsappRepository.findMessage(start, end, k);
        }catch (RuntimeException e) {
            return e.getMessage();
        }
    }
}
