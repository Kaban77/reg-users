package ru.demidov.config;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("ru.demidov")
@EnableTransactionManagement
public class HibernateSessionFactory {

	@Value(value = "${db.url}")
	private String url;

	@Value(value = "${db.user}")
	private String user;

	@Value(value = "${db.password}")
	private String password;

    @Bean
    public DataSource getDataSource() {
		final PGSimpleDataSource dataSource = new PGSimpleDataSource();

		dataSource.setURL(url);
		dataSource.setUser(user);
		dataSource.setPassword(password);

		return dataSource;
    }

    @Bean
	public SessionFactory sessionFactory() {
		var serviceRegistry = new StandardServiceRegistryBuilder()
				.applySetting(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jdbc")
				.applySetting(AvailableSettings.URL, url)
				.applySetting(AvailableSettings.USER, user)
				.applySetting(AvailableSettings.PASS, password)
				.applySetting(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
				.applySetting(AvailableSettings.SHOW_SQL, "true")
				.build();

		var metadata = new MetadataSources(serviceRegistry)
				.addPackage("ru.demidov")
				.getMetadataBuilder()
				.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
				.build();

		return metadata.getSessionFactoryBuilder().build();
    }

}