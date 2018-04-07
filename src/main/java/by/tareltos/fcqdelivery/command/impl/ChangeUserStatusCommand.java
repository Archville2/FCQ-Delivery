package by.tareltos.fcqdelivery.command.impl;

import by.tareltos.fcqdelivery.command.Command;
import by.tareltos.fcqdelivery.command.PagePath;
import by.tareltos.fcqdelivery.entity.User;
import by.tareltos.fcqdelivery.receiver.ReceiverException;
import by.tareltos.fcqdelivery.receiver.UserReceiver;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class ChangeUserStatusCommand implements Command {
    final static Logger LOGGER = LogManager.getLogger();

    private static final String EMAIL_PRM = "mail";
    private UserReceiver receiver;


    public ChangeUserStatusCommand(UserReceiver userReceiver) {
        receiver = userReceiver;
    }

    @Override
    public String execute(HttpServletRequest request) throws ReceiverException {

        HttpSession session = request.getSession(true);
        User admin = (User) session.getAttribute("loginedUser");
        if (null != admin) {
            String email = request.getParameter(EMAIL_PRM);
            LOGGER.log(Level.DEBUG, email);
            if (!receiver.checkEmail(email) && receiver.changeUserStatus(email)) {
                request.setAttribute(EMAIL_PRM, null);
                request.setAttribute("successfulMsg", "Статус успешно изменен!");
                return PagePath.PATH_INF_PAGE.getPath();
            } else {
                request.setAttribute("errorMessage", "Статус Пользователя не изменен");
                return PagePath.PATH_INF_PAGE.getPath();
            }
        } else {
            return PagePath.PATH_SINGIN_PAGE.getPath();
        }
    }
}
