package by.tareltos.fcqdelivery.command.impl;

import by.tareltos.fcqdelivery.command.Command;
import by.tareltos.fcqdelivery.command.PagePath;
import by.tareltos.fcqdelivery.entity.Courier;
import by.tareltos.fcqdelivery.entity.User;
import by.tareltos.fcqdelivery.receiver.CourierReceiver;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GetCouriersCommand implements Command {

    final static Logger LOGGER = LogManager.getLogger();
    private static final String LOGINED_USER_PRM = "loginedUser";
    private CourierReceiver receiver;


    public GetCouriersCommand(CourierReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public String execute(HttpServletRequest request){
        HttpSession session = request.getSession(true);
        User loginedUser = (User) session.getAttribute(LOGINED_USER_PRM);
        if (null == loginedUser) {
            LOGGER.log(Level.DEBUG, "Пользователь =null");
            return PagePath.PATH_SINGIN_PAGE.getPath();
        }
        if ("admin".equals(loginedUser.getRole().getRole())) {
            LOGGER.log(Level.DEBUG, "Нарушение прав доступа Пользователь: " + loginedUser.getRole().getRole());
            request.setAttribute("errorMessage", "У Вас нет прав доступа к этой странице");
            return PagePath.PATH_INF_PAGE.getPath();
        }
        List<Courier> courierList = receiver.getCouriers();
        if (null == courierList) {
            request.setAttribute("errorMessage", "Курьеров не найдено");
            return PagePath.PATH_INF_PAGE.getPath();
        }
        request.setAttribute("courierList", courierList);
        return PagePath.PATH_COURIERS_PAGE.getPath();
    }
}
