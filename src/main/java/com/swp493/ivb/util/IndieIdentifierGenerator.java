package com.swp493.ivb.util;

import java.io.Serializable;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class IndieIdentifierGenerator implements IdentifierGenerator {

    String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Random random = new Random();
    int idLength = 20;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        StringBuilder result = new StringBuilder(idLength);
        for (int i = 0; i < idLength; i++) {
            result.append(letters.charAt(random.nextInt(letters.length())));
        }
        return result;
    }
}
