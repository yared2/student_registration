package cs544_2020_01_light_attendanceproject.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs544_2020_01_light_attendanceproject.domain.Role;
import cs544_2020_01_light_attendanceproject.domain.User;
import cs544_2020_01_light_attendanceproject.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    static List<User> listOfMockUsers;

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        listOfMockUsers = new ArrayList<>();

        Set<Role> roles1 = new HashSet<>();
        roles1.add(new Role("ROLE_ADMIN"));

        User user = new User();
        user.setUsername("mockuser");
        user.setFirstName("Mock user firstname");
        user.setLastName("Mock user lastname");
        user.setPassword("12345");
        user.setEnabled(true);
        user.setEmail("mockuser@email.com");
        user.setBarCodeId("000-XX-YYYY");
        user.setAttendances(new ArrayList<>());
        user.setRoles(roles1);

        listOfMockUsers.add(user);

        Set<Role> roles2 = new HashSet<>();
        roles1.add(new Role("ROLE_FACULTY"));

        User user2 = new User();
        user2.setUsername("mockuser2");
        user2.setFirstName("Mock user 2 firstname");
        user2.setLastName("Mock user 2 lastname");
        user2.setPassword("123452");
        user2.setEnabled(true);
        user2.setEmail("mockuser2@email.com");
        user2.setBarCodeId("001-XX-YYYY");
        user2.setAttendances(new ArrayList<>());
        user2.setRoles(roles2);

        listOfMockUsers.add(user2);
    }

    @Test
    @WithMockUser(value = "admin",roles={"ADMIN"})
    void addUser() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/user")
                .content(asJsonString(listOfMockUsers.get(0)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("**/user/mockuser"));
    }

    @Test
    @WithMockUser(value = "admin",roles={"ADMIN"})
    void updateUserExistingUser() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(null);
        when(userService.updateUser(listOfMockUsers.get(0))).thenReturn(listOfMockUsers.get(0));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .put("/user/mockuser")
                .content(asJsonString(listOfMockUsers.get(0)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        User returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);

        assertThat(returnedUser).isInstanceOf(User.class);
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getUsername()).isEqualTo("mockuser");
    }

    @Test
    @WithMockUser(value = "admin",roles={"ADMIN"})
    void deleteNonExistingUser() throws Exception {
        when(userService.findUserByUsername("nonExistingUser")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/user/nonExistingUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser(value = "admin",roles={"ADMIN"})
    void deleteNormalUser() throws Exception {
        when(userService.findUserByUsername("mockuser")).thenReturn(Optional.of(listOfMockUsers.get(0)));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/user/mockuser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        User returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);

        assertThat(returnedUser).isInstanceOf(User.class);
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(listOfMockUsers.get(0));
    }

    @Test
    @WithMockUser(value = "admin",roles={"ADMIN"})
    void deleteYourself() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/user/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(value = "admin",roles={"ADMIN"})
    void testFindAll() throws Exception{
        when(userService.listUsers()).thenReturn(listOfMockUsers);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

        List<User> returnedListOfUsers = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<User>>() {});

        assertThat(returnedListOfUsers).isEqualTo(listOfMockUsers);
    }

    @Test
    @WithMockUser(value = "admin",roles={"ADMIN"})
    void testFindExistingUser() throws Exception {
        when(userService.findUserByUsername("mockuser2")).thenReturn(Optional.of(listOfMockUsers.get(1)));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/user/mockuser2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        User returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);

        assertThat(returnedUser).isEqualTo(listOfMockUsers.get(1));
    }

    @Test
    @WithMockUser(value = "admin",roles={"ADMIN"})
    void testFindNonExistingUser() throws Exception {
        when(userService.findUserByUsername(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/user/nonExistingUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
