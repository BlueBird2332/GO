package org.sampleDatabase;

import org.models.GameState;
import org.models.GameStatus;
import org.models.Move;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            // Create the StandardServiceRegistry
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();

            // Create MetadataSources
            MetadataSources metadataSources = new MetadataSources(standardRegistry);

            metadataSources.addAnnotatedClass(GameStatus.class);

            // Create Metadata
            Metadata metadata = metadataSources.getMetadataBuilder().build();

            // Create SessionFactory
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}