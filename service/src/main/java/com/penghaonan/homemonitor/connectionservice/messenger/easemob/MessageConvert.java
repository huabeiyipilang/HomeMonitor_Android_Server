package com.penghaonan.homemonitor.connectionservice.messenger.easemob;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.penghaonan.homemonitor.connectionservice.messenger.AMessage;
import com.penghaonan.homemonitor.connectionservice.messenger.Client;
import com.penghaonan.homemonitor.connectionservice.messenger.ImageMessage;
import com.penghaonan.homemonitor.connectionservice.messenger.TextMessage;

class MessageConvert {
    static EMMessage convert(AMessage msg) {
        EMMessage message = null;
        if (msg instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) msg;
            message = EMMessage.createTxtSendMessage(textMessage.getMessage(), msg.mClient.getUserName());
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
                msg = new TextMessage(cmd);
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