package com.vedri.mtp.frontend.web.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// FIXME
public class UserResourceTest extends AbstractTestNGSpringContextTests {

//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserService userService;
//
//    private MockMvc restUserMockMvc;
//
//    @BeforeClass
//    public void setup() {
//        UserResource userResource = new UserResource();
//        ReflectionTestUtils.setField(userResource, "userRepository", userRepository);
//        ReflectionTestUtils.setField(userResource, "userService", userService);
//        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource).build();
//    }
//
//    @Test
//    public void testGetExistingUser() throws Exception {
//        restUserMockMvc.perform(get("/api/users/admin")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.lastName").value("Administrator"));
//    }
//
//    @Test
//    public void testGetUnknownUser() throws Exception {
//        restUserMockMvc.perform(get("/api/users/unknown")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
}
