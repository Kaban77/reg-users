package ru.demidov.config;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan("ru.demidov")
@EnableTransactionManagement
public class HibernateSessionFactory {

    private static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactory.class);

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        try {
            final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();

            emf.setDataSource(restDataSource());
            emf.setPackagesToScan(new String[]{"ru.demidov"});

            final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

            emf.setJpaVendorAdapter(vendorAdapter);
            emf.setJpaProperties(getHibernateProperties());

            return emf;
        } catch(Exception e) {
            logger.info("entityManagerFactory " + e.getMessage());
            return null;
        }
    }

    @Bean
    public DataSource restDataSource() {
        try {
            final BasicDataSource dataSource = new BasicDataSource();

            dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
            dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:XE");
            dataSource.setUsername("kaban77");
            dataSource.setPassword("kaban77");

            return dataSource;
        } catch(Exception e) {
            logger.info("restDataSource " + e.getMessage());
            return null;
        }
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        try {
            final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

            sessionFactory.setDataSource(restDataSource());
            sessionFactory.setPackagesToScan(new String[]{"ru.demidov"});
            sessionFactory.setHibernateProperties(getHibernateProperties());
            //sessionFactory.setAnnotatedClasses(Users.class);
            //sessionFactory.setAnnotatedClasses(Authorities.class);
            sessionFactory.setAnnotatedPackages("ru.demidov.objects");

            return sessionFactory;
        } catch(Exception e) {
            logger.info("sessionFactory " + e.getMessage());
            return null;
        }
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager manager = new HibernateTransactionManager();
        manager.setSessionFactory(sessionFactory().getObject());
        return manager;
    }

    private Properties getHibernateProperties() {
        final Properties hibernateProperties = new Properties();

        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
        hibernateProperties.setProperty("hibernate.show_sql", "true");

        return hibernateProperties;
    }
}