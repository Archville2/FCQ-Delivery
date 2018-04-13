package by.tareltos.fcqdelivery.receiver;

import by.tareltos.fcqdelivery.entity.User;
import by.tareltos.fcqdelivery.entity.UserRole;
import by.tareltos.fcqdelivery.entity.UserStatus;
import by.tareltos.fcqdelivery.repository.RepositoryException;
import by.tareltos.fcqdelivery.repository.impl.UserRepository;
import by.tareltos.fcqdelivery.specification.impl.AllUserSpecification;
import by.tareltos.fcqdelivery.specification.impl.UserByEmailSpecification;
import by.tareltos.fcqdelivery.util.EmailSender;
import by.tareltos.fcqdelivery.util.PasswordGenerator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class UserReceiver {

    final static Logger LOGGER = LogManager.getLogger(UserReceiver.class);
    private UserRepository repository = new UserRepository();

    public boolean checkUserStatus(String email) throws ReceiverException {
        try {
            List<User> listUser = repository.query(new UserByEmailSpecification(email));
            if (!listUser.isEmpty()) {
                LOGGER.log(Level.DEBUG, "Found : " + listUser.size() + " users, must be 1");
                User u = listUser.get(0);
                if ("blocked".equals(u.getStatus().getStatus())) {  //отрицание наоборот
                    LOGGER.log(Level.DEBUG, "User :" + u.getEmail() + " is blocked");
                    return false;
                }
                return true;
            }
        } catch (RepositoryException e) {
            throw new ReceiverException("Exception in checkUserStatus", e);
        }
        return false;
    }

    public boolean checkUser(String email, String password) throws ReceiverException {
        try {
            List<User> listUser = repository.query(new UserByEmailSpecification(email));
            if (!listUser.isEmpty()) {
                LOGGER.log(Level.DEBUG, "Found : " + listUser.size() + " users, must be 1");
                User u = listUser.get(0);
                if ("blocked".equals(u.getStatus().getStatus())) {
                    LOGGER.log(Level.DEBUG, "User :" + u.getEmail() + " is blocked");
                    return false;
                }
                if (email.equals(u.getEmail()) & password.equals(u.getPassword())) {
                    LOGGER.log(Level.DEBUG, "Result:" + true);
                    return true;
                }
            }
        } catch (RepositoryException e) {
            throw new ReceiverException("Exception in checkUser", e);
        }
        return false;
    }

    public boolean checkEmail(String email) throws ReceiverException {
        try {
            List<User> listUser = repository.query(new UserByEmailSpecification(email));
            LOGGER.log(Level.DEBUG, "Found : " + listUser.size() + " users, must be 0");
            if (listUser.size() == 0) {
                return true;
            }
        } catch (RepositoryException e) {
            throw new ReceiverException("Exception in checkEmail", e);
        }
        return false;
    }

    public boolean resetUserPassword(String email, Properties props) throws ReceiverException {
        boolean result = false;
        try {
            String password = PasswordGenerator.generatePassword(email);
            List<User> listUser = repository.query(new UserByEmailSpecification(email));
            LOGGER.log(Level.DEBUG, "Found : " + listUser.size() + " users, must be 1");
            if (listUser.size() > 0) {
                User u = listUser.get(0);
                u.setPassword(password);
                result = repository.update(u);
                EmailSender.sendMail(email, "FCQ-Delivery-Info", "New Password:" + password, props);
            }
        } catch (RepositoryException e) {
            throw new ReceiverException("Exception in resetPassword method", e);
        }
        return result;
    }

    public boolean createUser(String email, String fName, String lName, String phone, String role, Properties props) throws ReceiverException {
        boolean result;
        try {
            String pass = PasswordGenerator.generatePassword(email);
            UserRole userRole = null;
            switch (role.toUpperCase()) {
                case "CUSTOMER":
                    userRole = UserRole.CUSTOMER;
                    break;
                case "MANAGER":
                    userRole = UserRole.MANAGER;
                    break;
                case "ADMIN":
                    userRole = UserRole.ADMIN;
                    break;
            }
            User newUser = new User(email, pass, fName, lName, phone, userRole, UserStatus.ACTIVE);
            result = repository.add(newUser);
            EmailSender.sendMail(newUser.getEmail(), "FCQ-Delivery-Registration", "Password:" + newUser.getPassword(), props);
        } catch (RepositoryException e) {
            throw new ReceiverException("Exception in createUser method", e);
        }
        return result;
    }

    public User getUserForSession(String email) throws ReceiverException {
        User user = null;
        try {
            List<User> listUser = repository.query(new UserByEmailSpecification(email));
            LOGGER.log(Level.DEBUG, "Found : " + listUser.size() + " users, must be 1");
            if (listUser.size() > 0) {
                user = listUser.get(0);
            }
        } catch (RepositoryException e) {
            throw new ReceiverException("Exception in getUserForSession method", e);
        }
        return user;
    }

    public boolean updateUser(User loginedUser) throws ReceiverException {
        boolean result;
        try {
            result = repository.update(loginedUser);
        } catch (RepositoryException e) {
            throw new ReceiverException("Exception in updateUser method", e);
        }
        return result;
    }

    public List<User> getAllUsers() throws ReceiverException {
        List<User> users;
        try {
            users = repository.query(new AllUserSpecification());
            LOGGER.log(Level.DEBUG, "Found : " + users.size());
        } catch (RepositoryException e) {
            throw new ReceiverException("Exception in getAllUser method", e);
        }
        return users;
    }

    public boolean changeUserStatus(String email) throws ReceiverException {
        boolean result;
        User u;
        try {
            u = (User) repository.query(new UserByEmailSpecification(email)).get(0);
            UserStatus currentStatus = u.getStatus();
            switch (currentStatus) {
                case ACTIVE:
                    u.setStatus(UserStatus.BLOCKED);
                    break;
                case BLOCKED:
                    u.setStatus(UserStatus.ACTIVE);
                    break;
            }
            result = repository.update(u);
        } catch (RepositoryException e) {
            throw new ReceiverException("Exception in changeUser method", e);
        }
        return result;
    }
}
