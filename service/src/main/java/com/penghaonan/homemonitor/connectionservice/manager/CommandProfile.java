package com.penghaonan.homemonitor.connectionservice.manager;

import com.alibaba.fastjson.JSON;
import com.penghaonan.homemonitor.connectionservice.command.ACommand;
import com.penghaonan.homemonitor.connectionservice.command.DeviceInfo;
import com.penghaonan.homemonitor.connectionservice.command.GetProfile;
import com.penghaonan.homemonitor.connectionservice.command.Reset;
import com.penghaonan.homemonitor.connectionservice.command.TakePic;
import com.penghaonan.homemonitor.connectionservice.command.Torch;
import com.penghaonan.homemonitor.connectionservice.command.terminal.Terminal;
import com.penghaonan.homemonitor.connectionservice.command.videocall.VideoCall;

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
        sCommandCls.add(Reset.class);
        sCommandCls.add(Terminal.class);
        sCommandCls.add(DeviceInfo.class);
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
        CommandData cmdData = new CommandData();
        cmdData.index = 1;
        cmdData.command = TakePic.class.getSimpleName().toLowerCase();
        cmdData.description = "拍照";
        cmdData.imgUrl = "https://raw.githubusercontent.com/huabeiyipilang/HomeMonitor_Resources/master/img/ic_float_menu_takepic.png";
        profile.add(cmdData);

        cmdData = new CommandData();
        cmdData.index = 2;
        cmdData.command = "torch on";
        cmdData.description = "开灯";
        cmdData.imgUrl = "https://raw.githubusercontent.com/huabeiyipilang/HomeMonitor_Resources/master/img/ic_float_menu_torch_on.png";
        profile.add(cmdData);

        cmdData = new CommandData();
        cmdData.index = 3;
        cmdData.command = "torch off";
        cmdData.description = "关灯";
        cmdData.imgUrl = "https://raw.githubusercontent.com/huabeiyipilang/HomeMonitor_Resources/master/img/ic_float_menu_torch_off.png";
        profile.add(cmdData);

        cmdData = new CommandData();
        cmdData.index = 4;
        cmdData.command = VideoCall.class.getSimpleName().toLowerCase();
        cmdData.description = "视频";
        cmdData.imgUrl = "https://raw.githubusercontent.com/huabeiyipilang/HomeMonitor_Resources/master/img/ic_float_menu_video.png";
        profile.add(cmdData);

        cmdData = new CommandData();
        cmdData.index = 5;
        cmdData.command = Reset.class.getSimpleName().toLowerCase();
        cmdData.description = "重置";
        profile.add(cmdData);

        return JSON.toJSONString(profile);
    }
}
