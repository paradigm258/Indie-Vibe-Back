package com.swp493.ivb.util;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

public class IndieIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Serializable id = session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);

        if (id != null && !id.toString().isEmpty()) {
            return id.toString();
        }
        
        return new RandomValueStringGenerator(20).generate();
    }
}
