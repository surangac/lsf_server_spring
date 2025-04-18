package com.dfn.lsf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * JMS Configuration for JBoss/WildFly integration
 * Reads configuration values from application.properties
 */
@Configuration
@EnableJms
@ConditionalOnProperty(name = "lsf.jms.enabled",
                       havingValue = "true")
public class JmsConfiguration {

    @Value("${spring.jms.jndi-name:java:/JmsXA}")
    private String jmsJndiName;

    @Value("${lsf.jms.queue.to-lsf:java:/queue/TO_LSF_QUEUE}")
    private String toLsfQueueName;

    @Value("${lsf.jms.initial-context-factory:org.jboss.naming.remote.client.InitialContextFactory}")
    private String initialContextFactory;

    @Value("${lsf.jms.provider-url:http-remoting://localhost:8080}")
    private String providerUrl;

    @Value("${lsf.jms.connection-factory-jndi-name:java:/JmsXA}")
    private String connectionFactoryJndiName;

    /**
     * Create a JNDI template for looking up JMS resources
     */
    @Bean
    public JndiTemplate jndiTemplate() {
        Properties jndiProps = new Properties();
        jndiProps.setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        jndiProps.setProperty(javax.naming.Context.PROVIDER_URL, providerUrl);

        return new JndiTemplate(jndiProps);
    }

    /**
     * Get JMS connection factory from JNDI
     */
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() throws NamingException {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiTemplate(jndiTemplate());
        jndiObjectFactoryBean.setJndiName(connectionFactoryJndiName);
        jndiObjectFactoryBean.setResourceRef(true);
        jndiObjectFactoryBean.setProxyInterface(ConnectionFactory.class);
        jndiObjectFactoryBean.afterPropertiesSet();
        return (ConnectionFactory) jndiObjectFactoryBean.getObject();
    }

    /**
     * Configure JMS listener container factory
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    /**
     * Get the TO_LSF_QUEUE from JNDI
     */
    @Bean(name = "lsfQueue")
    public Queue toLsfQueue() throws NamingException {
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiTemplate(jndiTemplate());
        factoryBean.setJndiName(toLsfQueueName);
        factoryBean.setResourceRef(true);
        factoryBean.afterPropertiesSet();
        return (Queue) factoryBean.getObject();
    }

    @Bean
    public Queue jmsQueue() throws NamingException {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiTemplate(jndiTemplate());
        jndiObjectFactoryBean.setJndiName(jmsJndiName);
        jndiObjectFactoryBean.setResourceRef(true);
        jndiObjectFactoryBean.setProxyInterface(Queue.class);
        jndiObjectFactoryBean.afterPropertiesSet();
        return (Queue) jndiObjectFactoryBean.getObject();
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        return jmsTemplate;
    }
} 