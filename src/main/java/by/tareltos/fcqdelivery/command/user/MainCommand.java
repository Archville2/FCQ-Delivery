package by.tareltos.fcqdelivery.command.user;

import by.tareltos.fcqdelivery.command.Command;
import by.tareltos.fcqdelivery.command.PagePath;

import javax.servlet.http.HttpServletRequest;

public class MainCommand implements Command {

    public MainCommand() {
    }

    @Override
    public String execute(HttpServletRequest request) {
        return PagePath.PATH_MAIN_PAGE.getPath();
    }
}
