package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.others.Protocol;
import com.kelsos.mbrc.services.SocketService;

public class ProcessUserAction implements ICommand {
    @Inject
    SocketService socket;
    @Override
    public void execute(IEvent e) {
        socket.sendData(new SocketMessage(((UserAction)e.getData()).getContext(), Protocol.Request, ((UserAction)e.getData()).getData()));
    }
}