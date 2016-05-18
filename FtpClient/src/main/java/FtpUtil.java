import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

public class FtpUtil {
    private FTPClient ftpClient;
    public static final int BINARY_FILE_TYPE = FTP.BINARY_FILE_TYPE;
    public static final int ASCII_FILE_TYPE = FTP.ASCII_FILE_TYPE;

    /**
     * ����FtpConfig���з���������
     * @param ftpConfig ��������Bean��
     * @throws SocketException
     * @throws IOException
     */
    public void connectServer(FtpConfig ftpConfig) throws SocketException,
            IOException {
        String server = ftpConfig.getServer();
        int port = ftpConfig.getPort();
        String user = ftpConfig.getUsername();
        String password = ftpConfig.getPassword();
        String location = ftpConfig.getLocation();
        connectServer(server, port, user, password, location);
    }

    /**
     * ʹ����ϸ��Ϣ���з���������
     * @param server����������ַ����
     * @param port���˿ں�
     * @param user���û���
     * @param password���û�����
     * @param path��ת�Ƶ�FTP������Ŀ¼ 
     * @throws SocketException
     * @throws IOException
     */
    public void connectServer(String server, int port, String user,
                              String password, String path) throws SocketException, IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        System.out.println("Connected to " + server + ".");
        //���ӳɹ���Ļ�Ӧ��
        System.out.println(ftpClient.getReplyCode());
        ftpClient.login(user, password);
        if (path!=null&&path.length() != 0) {
            ftpClient.changeWorkingDirectory(path);
        }
        ftpClient.setBufferSize(1024);//�����ϴ������С
        ftpClient.setControlEncoding("UTF-8");//���ñ���
        ftpClient.setFileType(BINARY_FILE_TYPE);//�����ļ�����
    }

    /**
     * ���ô����ļ�����:FTP.BINARY_FILE_TYPE | FTP.ASCII_FILE_TYPE  
     * �������ļ����ı��ļ�
     * @param fileType
     * @throws IOException
     */
    public void setFileType(int fileType) throws IOException {
        ftpClient.setFileType(fileType);
    }

    /**
     * �ر�����
     * @throws IOException
     */
    public void closeServer() throws IOException {
        if (ftpClient!=null&&ftpClient.isConnected()) {
            ftpClient.logout();//�˳�FTP������
            ftpClient.disconnect();//�ر�FTP���� 
        }
    }

    /**
     * ת�Ƶ�FTP����������Ŀ¼
     * @param path
     * @return
     * @throws IOException
     */
    public boolean changeDirectory(String path) throws IOException {
        return ftpClient.changeWorkingDirectory(path);
    }

    /**
     * �ڷ������ϴ���Ŀ¼
     * @param pathName
     * @return
     * @throws IOException
     */
    public boolean createDirectory(String pathName) throws IOException {
        return ftpClient.makeDirectory(pathName);
    }

    /**
     * �ڷ�������ɾ��Ŀ¼
     * @param path
     * @return
     * @throws IOException
     */
    public boolean removeDirectory(String path) throws IOException {
        return ftpClient.removeDirectory(path);
    }

    /**
     * ɾ�������ļ���Ŀ¼
     * @param path
     * @param isAll true:ɾ�������ļ���Ŀ¼
     * @return
     * @throws IOException
     */
    public boolean removeDirectory(String path, boolean isAll)
            throws IOException {

        if (!isAll) {
            return removeDirectory(path);
        }

        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr == null || ftpFileArr.length == 0) {
            return removeDirectory(path);
        }
        //   
        for (FTPFile ftpFile : ftpFileArr) {
            String name = ftpFile.getName();
            if (ftpFile.isDirectory()) {
                System.out.println("* [sD]Delete subPath ["+path + "/" + name+"]");
                removeDirectory(path + "/" + name, true);
            } else if (ftpFile.isFile()) {
                System.out.println("* [sF]Delete file ["+path + "/" + name+"]");
                deleteFile(path + "/" + name);
            } else if (ftpFile.isSymbolicLink()) {

            } else if (ftpFile.isUnknown()) {

            }
        }
        return ftpClient.removeDirectory(path);
    }

    /**
     * ���Ŀ¼�ڷ��������Ƿ���� true������  false��������
     * @param path
     * @return
     * @throws IOException
     */
    public boolean existDirectory(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        for (FTPFile ftpFile : ftpFileArr) {
            if (ftpFile.isDirectory()
                    && ftpFile.getName().equalsIgnoreCase(path)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * �õ��ļ��б�,listFiles���ذ���Ŀ¼���ļ��������ص���һ��FTPFile����
     * listNames()��ֻ����Ŀ¼���ַ�������
     * String[] fileNameArr = ftpClient.listNames(path); 
     * @param path:�������ϵ��ļ�Ŀ¼:/DF4
     */
    public List<String> getFileList(String path) throws IOException {
        FTPFile[] ftpFiles= ftpClient.listFiles(path);
        //ͨ��FTPFileFilter����ֻ����ļ�
/*      FTPFile[] ftpFiles2= ftpClient.listFiles(path,new FTPFileFilter() {
      @Override
      public boolean accept(FTPFile ftpFile) {
        return ftpFile.isFile();
      }
    });  */
        List<String> retList = new ArrayList<String>();
        if (ftpFiles == null || ftpFiles.length == 0) {
            return retList;
        }
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                retList.add(ftpFile.getName());
            }
        }
        return retList;
    }

    /**
     * ɾ���������ϵ��ļ�
     * @param pathName
     * @return
     * @throws IOException
     */
    public boolean deleteFile(String pathName) throws IOException {
        return ftpClient.deleteFile(pathName);
    }

    /**
     * �ϴ��ļ���ftp������
     * �ڽ����ϴ��������ļ���ʱ�������ļ�����������ǣ�
     * ftpUtil.setFileType(FtpUtil.BINARY_FILE_TYPE)
     * localFilePath:�����ļ�·��������
     * remoteFileName:�������ļ�����
     */
    public boolean uploadFile(String localFilePath, String remoteFileName)
            throws IOException {
        boolean flag = false;
        InputStream iStream = null;
        try {
            iStream = new FileInputStream(localFilePath);
            //���ǿ���ʹ��BufferedInputStream���з�װ
            //BufferedInputStream bis=new BufferedInputStream(iStream);
            //flag = ftpClient.storeFile(remoteFileName, bis); 
            flag = ftpClient.storeFile(remoteFileName, iStream);
        } catch (IOException e) {
            flag = false;
            return flag;
        } finally {
            if (iStream != null) {
                iStream.close();
            }
        }
        return flag;
    }

    /**
     * �ϴ��ļ���ftp���������ϴ��µ��ļ����ƺ�ԭ����һ��
     * @param fileName���ļ�����
     * @return
     * @throws IOException
     */
    public boolean uploadFile(String fileName) throws IOException {
        return uploadFile(fileName, fileName);
    }

    /**
     * �ϴ��ļ���ftp������
     * @param iStream ������
     * @param newName ���ļ�����
     * @return
     * @throws IOException
     */
    public boolean uploadFile(InputStream iStream, String newName)
            throws IOException {
        boolean flag = false;
        try {
            flag = ftpClient.storeFile(newName, iStream);
        } catch (IOException e) {
            flag = false;
            return flag;
        } finally {
            if (iStream != null) {
                iStream.close();
            }
        }
        return flag;
    }

    /**
     * ��ftp�������������ļ�������
     * @param remoteFileName��ftp���������ļ�����
     * @param localFileName�������ļ�����
     * @return
     * @throws IOException
     */
    public boolean download(String remoteFileName, String localFileName)
            throws IOException {
        boolean flag = false;
        File outfile = new File(localFileName);
        OutputStream oStream = null;
        try {
            oStream = new FileOutputStream(outfile);
            //���ǿ���ʹ��BufferedOutputStream���з�װ
            //BufferedOutputStream bos=new BufferedOutputStream(oStream);
            //flag = ftpClient.retrieveFile(remoteFileName, bos);
            flag = ftpClient.retrieveFile(remoteFileName, oStream);
        } catch (IOException e) {
            flag = false;
            return flag;
        } finally {
            oStream.close();
        }
        return flag;
    }

    /**
     * ��ftp�������������ļ�������
     * @param sourceFileName����������Դ�ļ�����
     * @return InputStream ������
     * @throws IOException
     */
    public InputStream downFile(String sourceFileName) throws IOException {
        return ftpClient.retrieveFileStream(sourceFileName);
    }

}