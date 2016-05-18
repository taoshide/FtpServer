import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/17.
 */
public class FtpClient {
    private static Logger logger = Logger.getLogger(FtpClient.class);

    public static void main(String[] args) throws Exception{
        int i=0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(sdf.format(new Date()));
        while(i < 1){
            i++;
            new UpClient(i).start();
        }
        System.out.println(sdf.format(new Date()));
    }

    /**
     * 获取FTPClient对象
     * @param ftpHost FTP主机服务器
     * @param ftpPassword FTP 登录密码
     * @param ftpUserName FTP登录用户名
     * @param ftpPort FTP端口 默认为21
     * @return
     */
    public static FTPClient getFTPClient(String ftpHost, String ftpPassword,
                                         String ftpUserName, int ftpPort) {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
            ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                logger.info("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
            } else {
                logger.info("FTP连接成功。");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            logger.info("FTP的IP地址可能错误，请正确配置。");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("FTP的端口错误,请正确配置。");
        }
        return ftpClient;
    }

    /**
     * 本地上传文件到FTP服务器
     *
     * @param ftpPath 远程文件路径FTP
     * @throws IOException
     */
    public static  void upload(String ftpPath,String localFilePath, String ftpUserName, String ftpPassword,
                       String ftpHost, int ftpPort) {
        FTPClient ftpClient = null;
        logger.info("开始上传文件到FTP.");
        try {
            ftpClient = FtpClient.getFTPClient(ftpHost, ftpPassword,
                    ftpUserName, ftpPort);
            // 设置PassiveMode传输
            ftpClient.enterLocalPassiveMode();
            // 设置以二进制流的方式传输
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 对远程目录的处理
            String remoteFileName = ftpPath;
            if (ftpPath.contains("/")) {
                remoteFileName = ftpPath
                        .substring(ftpPath.lastIndexOf("/") + 1);
            }

            File f = new File(localFilePath);
            InputStream in = new FileInputStream(f);
            ftpClient.storeFile(remoteFileName, in);
            in.close();
            logger.info("上传文件" + remoteFileName + "到FTP成功!");
            f.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

class UpClient extends Thread{
    private int i=0;
    public UpClient(int _i){
        i = _i;
    }

    public void run() {
        try {
            FtpUtil ftpUtil=new FtpUtil();
            ftpUtil.connectServer("localhost", FTPClient.DEFAULT_PORT, "taoshide", "taoshide", "/taoshide/111/222/333/444//daaa/faaaa");
            ftpUtil.uploadFile("C:\\Users\\Administrator\\Pictures\\_340400200163168356_长安牌_SC7103M.jpg", i+".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
