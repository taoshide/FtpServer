/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ftpserver.command.impl;

import java.io.IOException;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 *
 * <code>CWD  &lt;SP&gt; &lt;pathname&gt; &lt;CRLF&gt;</code><br>
 *
 * This command allows the user to work with a different directory for file
 * storage or retrieval without altering his login or accounting information.
 * Transfer parameters are similarly unchanged. The argument is a pathname
 * specifying a directory.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a> 
 */
public class CWD extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(CWD.class);

    /**
     * Execute command
     */
    public void execute(final FtpIoSession session,
                        final FtpServerContext context, final FtpRequest request)
            throws IOException, FtpException {

        // reset state variables
        session.resetState();

        // get new directory name
        String dirName = "/";
        if (request.hasArgument()) {
            dirName = request.getArgument();
        }

        // change directory
        FileSystemView fsview = session.getFileSystemView();
        boolean success = false;
        try {
            success = fsview.changeWorkingDirectory(dirName);
        } catch (Exception ex) {
            LOG.debug("Failed to change directory in file system", ex);
        }
        if (success) {
            dirName = fsview.getWorkingDirectory().getAbsolutePath();
            session.setAttribute("SaveFilePath",dirName);
            session.write(LocalizedFtpReply.translate(session, request, context,
                    FtpReply.REPLY_250_REQUESTED_FILE_ACTION_OKAY, "CWD",
                    dirName));
        } else {//获取不到远程的文件夹
            //尝试创建一个文件夹
            if(tryCreateDIR(dirName,session,fsview)){
                execute(session,context,request);
            }else{
                session.write(LocalizedFtpReply.translate(session, request, context,
                        FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "CWD", null));
            }

        }
    }

    /**
     * 处理路径上所有不存在的文件夹
     * @param dirName
     * @param session
     * @return
     */
    private boolean tryCreateDIR(String dirName,FtpIoSession session,FileSystemView fsview){
        try{
            if(dirName.startsWith("/")){
                dirName = dirName.substring(1);
            }
            String[] paths =  dirName.split("/");
            String tmpPath = "";
            for(String p:paths){
                tmpPath += ("/"+p);
                boolean success = fsview.changeWorkingDirectory(tmpPath);
                if(!success){
                    createDivAction(tmpPath,session);
                }
            }
        }catch (FtpException ex){
            return false;
        }
        return true;
    }

    /**
     * 尝试创建一个文件夹
     * @param dirName
     * @param session
     * @return
     */
    private boolean createDivAction(String dirName,FtpIoSession session){
        try {
            FtpFile file = session.getFileSystemView().getFile(dirName);
            file.mkdir();
        } catch (FtpException e) {
            LOG.error("切换目录时，因目录不存在，自动创建一个文件夹失败！");
            e.printStackTrace();
            return false;
        } finally {
        }
        return true;
    }
}
