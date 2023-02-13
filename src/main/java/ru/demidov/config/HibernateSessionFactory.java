package ru.demidov.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import oracle.jdbc.datasource.impl.OracleDataSource;

@Configuration
@ComponentScan("ru.demidov")
@EnableTransactionManagement
public class HibernateSessionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSessionFactory.class);

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        try {
            final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();

            emf.setDataSource(restDataSource());
			emf.setPackagesToScan(new String[] { "ru.demidov" });

            final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

            emf.setJpaVendorAdapter(vendorAdapter);
            emf.setJpaProperties(getHibernateProperties());

            return emf;
        } catch(Exception e) {
			LOGGER.error("entityManagerFactory " + e.getMessage(), e);
            return null;
        }
    }

    @Bean
    public DataSource restDataSource() {
        try {
			final OracleDataSource dataSource = new OracleDataSource();
			// TODO
			dataSource.setURL("jdbc:oracle:thin:@localhost:1521:XE");
			dataSource.setUser("kaban77");
            dataSource.setPassword("kaban77");

            return dataSource;
        } catch(Exception e) {
			LOGGER.error("restDataSource " + e.getMessage(), e);
            return null;
        }
    }

    @Bean
	public SessionFactory sessionFactory() {
		var serviceRegistry = new StandardServiceRegistryBuilder()
				.applySetting(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jdbc")
				.build();

		Metadata metadata = new MetadataSources(serviceRegistry)
				.addPackage("ru.demidov")
				.getMetadataBuilder()
				.build();

		return metadata.getSessionFactoryBuilder().build();
    }

    @Bean
	public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager manager = new HibernateTransactionManager();
		manager.setSessionFactory(sessionFactory());
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