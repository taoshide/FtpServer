package com.hw.ftpserver;

import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.impl.DefaultConnectionConfig;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.util.ArrayList;
import java.util.List;

/**
 * FTP服务端自定义实现
 */
public class MainAppServer {

    public static void main(String[] args){
        FtpServerFactory factory = new FtpServerFactory();

        BaseUser user = new BaseUser();
        user.setName("taoshide");
        user.setPassword("taoshide");

        //设置用户可写权限
        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        user.setHomeDirectory("D:\\taoshide");

        ConnectionConfig connectionConfig = new DefaultConnectionConfig(true, 500, 300, 300, 3, 0);
        factory.setConnectionConfig(connectionConfig);
        FtpServer server = factory.createServer();


        try {
            factory.getUserManager().save(user);
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
