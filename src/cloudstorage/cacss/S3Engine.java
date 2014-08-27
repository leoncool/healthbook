package cloudstorage.cacss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.zhumulangma.cloudstorage.server.conf.ConfigManager;
import com.zhumulangma.cloudstorage.server.logic.ctrl.GiantEngineS3;
import com.zhumulangma.cloudstorage.server.logic.entity.UsersDAO;
import com.zhumulangma.cloudstorage.server.logic.entity.s3LogDAO;

/**
 *
 * @author Leon
 */
public class S3Engine {

    public static GiantEngineS3 s3 = new GiantEngineS3();
    public static UsersDAO userDao = new UsersDAO();
    public static s3LogDAO s3logDao = new s3LogDAO();
    public static ConfigManager cm = ConfigManager.getInstance();

    public S3Engine() {
        if (s3 == null) {
            s3 = new GiantEngineS3();
        }
        cm = ConfigManager.getInstance();
    }


}
