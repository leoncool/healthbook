package util;

import health.database.models.Datastream;
import health.database.models.DatastreamBlocks;
import health.database.models.DatastreamTriggers;
import health.database.models.DatastreamUnits;
import health.database.models.Debug;
import health.database.models.DeviceBinding;
import health.database.models.DeviceTemplate;
import health.database.models.Follower;
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
            Configuration config = new Configuration().configure("hibernate.cfg.xml");

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
        HibernateUtil.getSession().getTransaction().commit();
        if (HibernateUtil.getSession().isOpen()) {
            HibernateUtil.getSession().close();
        }

    }

    public static void rollBackTransaction() {
        HibernateUtil.getSession().getTransaction().rollback();
    }

    public static void main(String args[]) {
//       HibernateUtil.recreateDatabase();
    }
}
