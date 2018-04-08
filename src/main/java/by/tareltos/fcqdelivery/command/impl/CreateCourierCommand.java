package by.tareltos.fcqdelivery.command.impl;

import by.tareltos.fcqdelivery.command.Command;
import by.tareltos.fcqdelivery.command.PagePath;
import by.tareltos.fcqdelivery.entity.Courier;
import by.tareltos.fcqdelivery.entity.User;
import by.tareltos.fcqdelivery.receiver.CourierReceiver;
import by.tareltos.fcqdelivery.receiver.ReceiverException;
import by.tareltos.fcqdelivery.validator.DataValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


public class CreateCourierCommand implements Command {
    final static Logger LOGGER = LogManager.getLogger();
    private static final String LOGINED_USER_PRM = "loginedUser";
    private static final String CAR_NUMBER_PRM = "carNumber";
    private static final String CAR_PRODUCER_PRM = "carProducer";
    private static final String CAR_MODEL_PRM = "carModel";
    private static final String CAR_IMG_PRM = "img";
    private static final String DRIVER_EMAIL_PRM = "email";
    private static final String DRIVER_NAME_PRM = "name";
    private static final String DRIVER_PHONE_PRM = "phone";
    private static final String CARGO_PRM = "cargo";
    private static final String TAX_PRM = "tax";
    private static final String STATUS_PRM = "status";
    private CourierReceiver receiver;


    public CreateCourierCommand(CourierReceiver rceiver) {
        this.receiver = rceiver;
    }

    @Override
    public String execute(HttpServletRequest request) throws ReceiverException {
        HttpSession session = request.getSession(true);
        User loginedUser = (User) session.getAttribute(LOGINED_USER_PRM);
        if (null == loginedUser) {
            LOGGER.log(Level.DEBUG, "Пользователь is null");
            return PagePath.PATH_SINGIN_PAGE.getPath();
        }
        if (!"manager".equals(loginedUser.getRole().getRole())) {
            LOGGER.log(Level.DEBUG, "Нарушение прав доступа Пользователь: " + loginedUser.getRole().getRole());
            request.setAttribute("errorMessage", "У Вас нет прав доступа к этой странице");
            return PagePath.PATH_INF_PAGE.getPath();
        }
        String carNumber = request.getParameter(CAR_NUMBER_PRM);
        String carProducer = request.getParameter(CAR_PRODUCER_PRM);
        String carModel = request.getParameter(CAR_MODEL_PRM);
        String carPhotoFullPath = request.getParameter(CAR_IMG_PRM);
        String[] array = carPhotoFullPath.split("/");
        String carPhotoPath = array[array.length - 1];
        String driverName = request.getParameter(DRIVER_NAME_PRM);
        String driverEmail = request.getParameter(DRIVER_EMAIL_PRM);
        String driverPhone = request.getParameter(DRIVER_PHONE_PRM);
        String status = request.getParameter(STATUS_PRM);
        int maxCargo = Integer.parseInt(request.getParameter(CARGO_PRM));
        double tax = Double.parseDouble(request.getParameter(TAX_PRM));
        if (DataValidator.validateCarNumber(carNumber) & DataValidator.validateCarProducer(carProducer)
                & DataValidator.validateCarModel(carModel) & DataValidator.validatePhone(driverPhone)
                & DataValidator.validateName(driverName) & DataValidator.validateEmail(driverEmail)
                & DataValidator.validateCargo(maxCargo) & DataValidator.validateTax(tax)
                & DataValidator.validateStatus(status)) {
            boolean result = receiver.createCourier(carNumber, carProducer, carModel, carPhotoPath, driverName, driverPhone, driverEmail, maxCargo, tax, status);
            if (result) {
                return PagePath.PATH_COURIERS_PAGE.getPath();
            }
            request.setAttribute("errorMessage", "Сохранения данных");
            return PagePath.PATH_NEW_COURIER_FORM.getPath();
        }
        request.setAttribute("errorMessage", "Данные невалидные, невозможно создать такого курьера");
        return PagePath.PATH_NEW_COURIER_FORM.getPath();
    }

}