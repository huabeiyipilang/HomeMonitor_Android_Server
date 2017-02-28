package com.penghaonan.homemonitor.server.messenger.easemob;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.Client;
import com.penghaonan.homemonitor.server.messenger.ImageMessage;
import com.penghaonan.homemonitor.server.messenger.TextMessage;

class MessageConvert {
    static EMMessage convert(AMessage msg) {
        EMMessage message = null;
        if (msg instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) msg;
            message = EMMessage.createTxtSendMessage(textMessage.mMessage, msg.mClient.getUserName());
        } else if (msg instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) msg;
            message = EMMessage.createImageSendMessage(imageMessage.getImagePath(), false, imageMessage.mClient.getUserName());
        }
        return message;
    }

    static AMessage convert(EMMessage message) {
        TextMessage msg = null;
        EMMessage.Type type = message.getType();
        switch (type) {
            case TXT:
                EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                String cmd = body.getMessage();
                msg = new TextMessage();
                msg.mMessage = cmd;
                msg.mClient = new Client(message.getUserName());
                break;
            case IMAGE:
                break;
            case VIDEO:
                break;
            case LOCATION:
                break;
            case VOICE:
                break;
            case FILE:
                break;
            case CMD:
                break;
        }
        return msg;
    }
}
