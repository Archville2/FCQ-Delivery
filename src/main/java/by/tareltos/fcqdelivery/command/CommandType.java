package by.tareltos.fcqdelivery.command;

import by.tareltos.fcqdelivery.command.impl.*;
import by.tareltos.fcqdelivery.receiver.CourierReceiver;
import by.tareltos.fcqdelivery.receiver.UserReceiver;

public enum CommandType {
    MAIN(new MainCommand()),
    SING_IN(new SinginCommand()),
    LOG_IN(new LoginCommand(new UserReceiver())),
    REGISTRATION(new RegistrationCommand(new UserReceiver())),
    RESET_PASS(new ResetPasswordCommand(new UserReceiver())),
    LOG_OUT(new LogoutCommand()),
    SAVE_USER(new SaveUserCommand(new UserReceiver())),
    GET_USERS(new AllUsersCommand(new UserReceiver())),
    CREATE_USER(new CreateUserCommand(new UserReceiver())),
    CHANGE_STATUS(new ChangeUserStatusCommand(new UserReceiver())),
    GET_COURIERS(new GetCouriersCommand(new CourierReceiver()));
    private Command command;

    CommandType(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }


}
