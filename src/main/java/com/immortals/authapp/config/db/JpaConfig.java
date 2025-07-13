package com.immortals.authapp.config.db;

import com.immortals.authapp.model.properties.JpaProperties;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.immortals.authapp.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class JpaConfig {
    private final JpaProperties jpaProperties;

    public JpaConfig(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        if (jpaProperties.getDatabasePlatform() != null) {
            adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        }
        return adapter;
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
            JpaVendorAdapter jpaVendorAdapter,
            ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {

        return new EntityManagerFactoryBuilder(
                jpaVendorAdapter,
                new HashMap<>(),
                persistenceUnitManager.getIfAvailable()
        );
    }

    @Bean(name = "entityManagerFactory")
    @DependsOn("routingDataSource")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("routingDataSource") DataSource routingDataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(routingDataSource);
        emf.setPackagesToScan("com.immortals.authapp.model.entity");
        emf.setJpaVendorAdapter(jpaVendorAdapter());


        Map<String, Object> jpaProps = new HashMap<>();

        if (jpaProperties.getHibernate() != null)
            jpaProps.putAll(jpaProperties.getHibernate());

        if (jpaProperties.getOrg() != null) {

            Map<String, Object> org = jpaProperties.getOrg();
            if (org.containsKey("hibernate")) {
                Object envers = ((Map<?, ?>) org.get("hibernate")).get("envers");
                if (envers instanceof Map<?, ?> enversMap) {
                    enversMap.forEach((k, v) -> {
                        jpaProps.put("org.hibernate.envers." + k, v);
                    });
                }
            }
        }

        emf.setJpaPropertyMap(jpaProps);

        return emf;
    }


    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}
