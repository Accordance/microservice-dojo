package com.msvcdojo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AccountsServiceApplication.class)
public class AccountsServiceApplicationTests {

    @Autowired
    private JdbcTemplate template;

    @Test
    public void testDefaultSettings() throws Exception {
        assertEquals(new Integer(1), this.template
                .queryForObject("SELECT COUNT(*) from account", Integer.class));
    }

}