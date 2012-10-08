package server.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import java.io.File;

public class ConfigManager {

	//common
    private final String fileCommon = "common.properties";
    private final String fileVersion = "version";
    private Properties prop = null;
    private static ConfigManager instance = new ConfigManager();

    public static ConfigManager getInstance() {
        return instance;
    }

    protected ConfigManager() {
        loadCommon();
        loadVersion();

        listAll();
    }

    private void loadVersion() {
        try {
            if (null == prop) {
                prop = new Properties();
            }
            File file = new File("StorageConf/" + fileVersion);
            if (!file.exists()) {
                file = new File("C:/Program Files/Apache Software Foundation/Apache Tomcat 6.0.26/StorageConf/" + fileVersion);
                /////Program Files/Apache Software Foundation/
            }
            InputStream is = new FileInputStream(file);
            //  InputStream is = new FileInputStream("StorageConf/" + fileVersion);
            //   InputStream is = new FileInputStream("C:/Program Files/Apache Software Foundation/Apache Tomcat 6.0.26/webapps/ROOT/WEB-INF/classes/com/zhumulangma/cloudstorage/server/conf/" + fileVersion);
            prop.load(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Utils.log(this.getClass(), "Version file not found!");
        } catch (IOException e) {
            e.printStackTrace();
            Utils.log(this.getClass(), "Version file error!");
        }
    }

    private void loadCommon() {
        try {
            if (null == prop) {
                prop = new Properties();
            }
            File file = new File("StorageConf/" + fileCommon);
            if (!file.exists()) {
                file = new File("C:/Program Files/Apache Software Foundation/Apache Tomcat 6.0.26/StorageConf/" + fileCommon);
            }
            InputStream is = new FileInputStream(file);
            //   InputStream is=new FileInputStream("StorageConf/"+fileCommon);
            //  InputStream is = new FileInputStream("C:/Program Files/Apache Software Foundation/Apache Tomcat 6.0.26/webapps/ROOT/WEB-INF/classes/com/zhumulangma/cloudstorage/server/conf/" + fileCommon);
            prop.load(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Utils.log(this.getClass(), "Common configuration file not found!");
        } catch (IOException e) {
            e.printStackTrace();
            Utils.log(this.getClass(), "Common configuration file error!");
        }

    }

    private void listAll() {
        Utils.log(this.getClass(), "Load Configurations - ");
        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            Utils.log("\tkey=" + key + ", value=" + prop.getProperty(key));
        }
    }

    public String $FTPHost() {
        return prop.getProperty("FTPHost");
    }

    public Integer $FTPPort() {
        return Integer.parseInt(prop.getProperty("FTPPort"));
    }

    public String $Version() {
        return prop.getProperty("Version");
    }

    public String $ArchiveSign() {
        return prop.getProperty("ArchiveSign");
    }

    public Boolean $SupportHTML5() {
        return Boolean.parseBoolean(prop.getProperty("SupportHTML5"));
    }

    public String $PublicUsername() {
        return prop.getProperty("PublicUsername");
    }

    public String $PublicPassword() {
        return prop.getProperty("PublicPassword");
    }

    public String $SuperUsername() {
        return prop.getProperty("SuperUsername");
    }

    public String $FileDefaultDescription() {
        return prop.getProperty("FileDefaultDescription");
    }

    public String $HDFSHost() {
        return prop.getProperty("HDFSHost");
    }

    public Integer $HDFSPort() {
        return Integer.parseInt(prop.getProperty("HDFSPort"));
    }

    public String $HDFSChunkDir() {
        return prop.getProperty("HDFSChunkDir");
    }

    public String $ZookeeperIP() {
        return prop.getProperty("ZookeeperIP");
    }

    public String $HOST_URI() {
        return prop.getProperty("Host");
    }
     public String $HOST_IP() {
        return prop.getProperty("HostIP");
    }

}
