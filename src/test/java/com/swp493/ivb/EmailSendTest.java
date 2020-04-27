package com.swp493.ivb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.swp493.ivb.common.user.EntityUser;
import com.swp493.ivb.common.user.RepositoryUser;
import com.swp493.ivb.util.EmailUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class EmailSendTest {

    @Autowired
    RepositoryUser userRepo;

    @Autowired
    EmailUtils emailUtils;

    @Test
    @Transactional
    public void EmailTest() {
        EntityUser user = userRepo.findById("a98db973kwl8xp1lz94k").get();
        assertDoesNotThrow(() -> emailUtils.sendArtistRequestResponseEmail(user), "message");
    }
}