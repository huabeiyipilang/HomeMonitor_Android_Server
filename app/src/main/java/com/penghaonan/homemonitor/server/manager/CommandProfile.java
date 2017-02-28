package com.penghaonan.homemonitor.server.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.penghaonan.homemonitor.server.command.ACommand;
import com.penghaonan.homemonitor.server.command.GetProfile;
import com.penghaonan.homemonitor.server.command.TakePic;
import com.penghaonan.homemonitor.server.command.Torch;
import com.penghaonan.homemonitor.server.command.videocall.VideoCall;
import com.penghaonan.homemonitor.server.transfer.CmdRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 服务端配置文件
 * Created by carl on 3/16/16.
 */
public class CommandProfile {
    private static List<Class<? extends ACommand>> sCommandCls;

    //所有命令均需要写到此处
    static {
        sCommandCls = new LinkedList<>();
        sCommandCls.add(GetProfile.class);
        sCommandCls.add(Torch.class);
        sCommandCls.add(TakePic.class);
        sCommandCls.add(VideoCall.class);
    }

    public static List<Class<? extends ACommand>> getCommandClassList() {
        return sCommandCls;
    }

    /**
     * 客户端预置功能按钮
     *
     * @return
     */
    public static String getProfile() {
        List<CommandData> profile = new ArrayList<>();
        CmdRequest request = new CmdRequest();
        request.cmd = TakePic.class.getSimpleName().toLowerCase();
        CommandData cmdData = new CommandData();
        cmdData.command = JSON.toJSONString(request);
        cmdData.description = "拍照";
        profile.add(cmdData);

        cmdData = new CommandData();
        request = new CmdRequest();
        request.cmd = Torch.class.getSimpleName().toLowerCase();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "on");
        request.data = JSON.toJSONString(jsonObject);
        cmdData.command = JSON.toJSONString(request);
        cmdData.description = "开灯";
        profile.add(cmdData);

        cmdData = new CommandData();
        request = new CmdRequest();
        request.cmd = Torch.class.getSimpleName().toLowerCase();
        jsonObject = new JSONObject();
        jsonObject.put("action", "off");
        request.data = JSON.toJSONString(jsonObject);
        cmdData.command = JSON.toJSONString(request);
        cmdData.description = "关灯";
        profile.add(cmdData);

        cmdData = new CommandData();
        request = new CmdRequest();
        request.cmd = VideoCall.class.getSimpleName().toLowerCase();
        cmdData.command = JSON.toJSONString(request);
        cmdData.description = "视频";
        profile.add(cmdData);

        return JSON.toJSONString(profile);
    }
}
