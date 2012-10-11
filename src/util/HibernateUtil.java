package util;

import java.io.File;
import java.io.IOException;

import health.database.models.Datastream;
import health.database.models.DatastreamBlocks;
import health.database.models.DatastreamTriggers;
import health.database.models.DatastreamUnits;
import health.database.models.Debug;
import health.database.models.DeviceBinding;
import health.database.models.DeviceTemplate;
import health.database.models.Follower;
import health.database.models.JobsTable;
import health.database.models.Subject;
import health.database.models.UserAvatar;
import health.database.models.Users;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateUtil {

    private static SessionFactory factory;
    private static ServiceRegistry serviceRegistry;

    public static void closeAllConnections() {
        if (factory != null) {
            factory.close();
        }
    }

    public static Configuration getInitializedConfiguration() {
        AnnotationConfiguration config = new AnnotationConfiguration();

        config.addAnnotatedClass(Datastream.class);
        config.addAnnotatedClass(DatastreamTriggers.class);
        config.addAnnotatedClass(Follower.class);
        config.addAnnotatedClass(Subject.class);
        config.addAnnotatedClass(Users.class);
        config.addAnnotatedClass(DatastreamBlocks.class);
        config.addAnnotatedClass(DatastreamUnits.class);
        
        config.configure();
//        config.setProperty("hibernate.connection.username", "leoncool");
//        config.setProperty("hibernate.connection.password", "leoncool");
        return config;
    }

    public static Session getSession() {
        if (factory == null) {
        	
        	File configFolder=new File(AllConstants.ServerConfigs.configsFolderPath);
        	if(!configFolder.exists())
        	{
        		System.out.println("Cannot Find Config folder!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Config folder!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Config folder!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Config folder!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Config folder!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Config folder!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Config folder!!!!!!!!!!!!!!");
        		configFolder.mkdir();
        	}
        	
        	File configFile=new File(AllConstants.ServerConfigs.configsFolderPath+"hibernate.cfg.xml");
        	if(!configFile.exists())
        	{
        		System.out.println("Cannot Find Hibernate Config File!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Hibernate Config File!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Hibernate Config File!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Hibernate Config File!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Hibernate Config File!!!!!!!!!!!!!!");
        		System.out.println("Cannot Find Hibernate Config File!!!!!!!!!!!!!!");
        	}else{
        		System.out.println("Loading Hibernate Config File.......................");
        		System.out.println("Loading Hibernate Config File.......................");
        		System.out.println("Loading Hibernate Config File.......................");
        		System.out.println("Loading Hibernate Config File.......................");
        	}
            Configuration config = new Configuration().configure(new File(AllConstants.ServerConfigs.configsFolderPath+"hibernate.cfg.xml"));

            config.addAnnotatedClass(DatastreamTriggers.class);
            config.addAnnotatedClass(Follower.class);
            config.addAnnotatedClass(Subject.class);
            config.addAnnotatedClass(Users.class);
            config.addAnnotatedClass(DatastreamBlocks.class);
            config.addAnnotatedClass(DatastreamUnits.class);
            config.addAnnotatedClass(Datastream.class);
            config.addAnnotatedClass(DeviceBinding.class);
            config.addAnnotatedClass(DeviceTemplate.class);
            config.addAnnotatedClass(Debug.class);
            config.addAnnotatedClass(UserAvatar.class);
            config.addAnnotatedClass(JobsTable.class);
            serviceRegistry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
            factory = config.buildSessionFactory(serviceRegistry);

            //       factory=config.buildSessionFactory()
        }
        Session session = factory.getCurrentSession();
        return session;
    }

    public static void closeSession() {
        HibernateUtil.getSession().close();
    }

    public static void recreateDatabase() {
        Configuration configuration = HibernateUtil.getInitializedConfiguration();
        SchemaExport schemaExport = new SchemaExport(configuration);
        //   schemaExport.setOutputFile("C:/Users/leon/Documents/My Dropbox/SQL.txt");
        schemaExport.create(true, true);
    }

    public static Session beginTransaction() {
        Session hibernateSession;
        hibernateSession = HibernateUtil.getSession();
        hibernateSession.beginTransaction();
        return hibernateSession;
    }

    public static void commitTransaction() {
    	if(!HibernateUtil.getSession().getTransaction().wasCommitted())
    	{
    		System.out.println("committing");
        HibernateUtil.getSession().getTransaction().commit();
    	}
        if (HibernateUtil.getSession().isOpen()) {
            HibernateUtil.getSession().close();
        }

    }

    public static void rollBackTransaction() {
    	if(HibernateUtil.getSession().getTransaction().wasCommitted())
    	{
    		System.out.println("rollback");
    		HibernateUtil.getSession().getTransaction().rollback();
    	}
        
    }

    public static void main(String args[]) {
//       HibernateUtil.recreateDatabase();
    }
}
