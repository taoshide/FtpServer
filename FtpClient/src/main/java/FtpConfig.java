/**
 * Created by Administrator on 2016/5/18.
 */
/**
 * ftp��½������Ϣ
 * @author HarderXin
 *
 */
public class FtpConfig {
    //��������ַ����
    private String server;
    //�˿ں�
    private int port;
    //�û�����
    private String username;
    //����
    private String password;
    //����Ŀ¼
    private String location;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}