package com.driver;

import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.*;

@Repository
public class WhatsappRepository {
    int messageId = 1;
    int groupCount = 1;
    int messageCount = 0;
    HashMap<String, User> userDB = new HashMap<>(); //mobileNo -> User
    HashMap<String, Group> groupDB = new HashMap<>(); // adminMobile -> group
    HashMap<String, List<User>> groupsMap = new HashMap<>(); //GroupName -> List<User>
    HashMap<Integer, Message> messageHashMap = new HashMap<>(); //messageId -> Message
    HashMap<String, List<Message>> messageInGroup = new HashMap<>(); //GroupName -> List<Message>
    HashMap<String, String> userToGroup = new HashMap<>(); //userMobile -> groupName
    HashMap<String, List<Integer>> userToMessgae = new HashMap<>(); //userMobile -> List<MessageId>
    List<Message> messages = new ArrayList<>();


    public String createUser(String name, String mobile) {
        if (userDB.containsKey(mobile))throw new RuntimeException("User already exists");
        User user = new User();
        user.setName(name);
        user.setMobile(mobile);
        userDB.put(mobile, user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        if (users.size() > 2) {
            User user = users.get(0);
            String adminMobile = user.getMobile();
            Group group = new Group();
            group.setName("Group " + groupCount);
            group.setNumberOfParticipants(users.size());
            this.groupCount++;
            groupDB.put(adminMobile, group);
            groupsMap.put(group.getName(), users);
            for (User user1 : users) {
                userToGroup.put(user1.getMobile(), group.getName());
            }
            return group;
        }else if (users.size() == 2){
            User user = users.get(0);
            String adminMobile = user.getMobile();
            Group group = new Group();
            group.setName(users.get(1).getName());
            group.setNumberOfParticipants(users.size());
            groupDB.put(adminMobile, group);
            groupsMap.put(group.getName(), users);
            for (User user1 : users) {
                userToGroup.put(user1.getMobile(), group.getName());
            }
            return group;
        }
        return null;
    }

    public int createMessage(String content) {
        Message message = new Message(messageId, content);
        messageHashMap.put(messageId, message);
        messages.add(message);
        this.messageId++;
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) {
        String groupName = group.getName();
        if (!groupsMap.containsKey(groupName))throw new RuntimeException("Group does not exist");
        List<User> groupUsers = groupsMap.get(groupName);
        boolean flag = false;
        for (User user : groupUsers) {
            if (user.getMobile().equals(sender.getMobile())) {
                flag = true;
                break;
            }
        }
        if (!flag)throw new RuntimeException("You are not allowed to send message");
        this.messageCount += 1;
        List<Message> messages = messageInGroup.get(groupName);
        messages.add(message);
        messageInGroup.put(groupName, messages);
        List<Integer>userMessages = userToMessgae.get(sender.getMobile());
        userMessages.add(message.getId());
        userToMessgae.put(sender.getMobile(), userMessages);
        return messages.size();
    }

    public String changeAdmin(User approver, User user, Group group) {
        String groupName = group.getName();
        String approverMobile = approver.getMobile();

        if (!groupsMap.containsKey(groupName))throw new RuntimeException("Group does not exist");
        if (!groupDB.containsKey(approverMobile)) throw new RuntimeException("Approver does not have rights");

        List<User> userList = groupsMap.get(groupName);
        boolean flag = false;
        for (User user1 : userList) {
            if (user1.getMobile().equals(user.getMobile())) {
                flag = true;
                break;
            }
        }
        if (!flag) throw new RuntimeException("User is not a participant");
        Group group1 = groupDB.get(approverMobile);
        groupDB.remove(approverMobile);
        groupDB.put(user.getMobile(), group1);
        return "SUCCESS";
    }

    public int removeUser(User user) {
        String userMobile = user.getMobile();
        if (!userToGroup.containsKey(userMobile))throw new RuntimeException("User not found");
        if (groupDB.containsKey(userMobile))throw new RuntimeException("Cannot remove admin");

        int  totalMessage = 0;

        List<Integer>messageIdList = userToMessgae.get(user.getMobile());
        String groupName = userToGroup.get(user.getMobile());
        List<User> users = groupsMap.get(groupName);
        this.messageCount -= userToMessgae.get(userMobile).size();
        userToMessgae.remove(userMobile);
        userToGroup.remove(userMobile);
        for (User user1 : users) {
            if (user.getMobile().equals(user1.getMobile())) {
                users.remove(user1);
            }
        }
        groupsMap.put(groupName, users);
        List<Message> messageList = messageInGroup.get(groupName);
        for (Message message : messageList) {
            if (messageIdList.contains(message.getId())) {
                messageList.remove(message);
            }
        }
        messageInGroup.put(groupName, messageList);

        totalMessage = messageCount + users.size() + messageList.size();
        return totalMessage;
    }

    public String findMessage(Date start, Date end, int k) {
        String content = "";
        for (Message message : messages) {
            if (message.getTimestamp().after(start) && message.getTimestamp().before(end))k--;
            if(k == 0) {
                return message.getContent();
            }
        }
        if (k > 0)throw new RuntimeException("K is greater than the number of messages");
        return "";
    }
}
