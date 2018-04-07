package by.tareltos.fcqdelivery.command.impl;

import by.tareltos.fcqdelivery.command.Command;
import by.tareltos.fcqdelivery.command.PagePath;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LogoutCommand implements Command {
    final static Logger LOGGER = LogManager.getLogger();

    public String execute(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("loginedUser", null);
        LOGGER.log(Level.DEBUG, "Kill user in session!");
        return PagePath.PATH_SINGIN_PAGE.getPath();
    }
}
