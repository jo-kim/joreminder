package jo.ai.joreminder.controller;

import tools.jackson.databind.ObjectMapper;
import jo.ai.joreminder.domain.ReminderList;
import jo.ai.joreminder.dto.ReminderListRequest;
import jo.ai.joreminder.repository.ReminderListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReminderListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReminderListRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/lists")
    class FindAll {

        @Test
        @DisplayName("200 — 전체 목록을 반환한다")
        void returnsAllLists() throws Exception {
            repository.save(new ReminderList("Work", "RED"));
            repository.save(new ReminderList("Personal", "BLUE"));

            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").exists())
                    .andExpect(jsonPath("$[0].color").exists());
        }

        @Test
        @DisplayName("200 — 목록이 없으면 빈 배열을 반환한다")
        void returnsEmptyList() throws Exception {
            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/lists/{id}")
    class FindById {

        @Test
        @DisplayName("200 — 목록을 반환한다")
        void returnsList() throws Exception {
            var saved = repository.save(new ReminderList("Work", "RED"));

            mockMvc.perform(get("/api/lists/{id}", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(saved.getId()))
                    .andExpect(jsonPath("$.name").value("Work"));
        }

        @Test
        @DisplayName("404 — 존재하지 않는 목록")
        void returnsNotFound() throws Exception {
            mockMvc.perform(get("/api/lists/{id}", 999))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/lists")
    class Create {

        @Test
        @DisplayName("201 — 새 목록을 생성한다")
        void createsNewList() throws Exception {
            var request = new ReminderListRequest("Shopping", "GREEN");

            mockMvc.perform(post("/api/lists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").value("Shopping"))
                    .andExpect(jsonPath("$.color").value("GREEN"))
                    .andExpect(jsonPath("$.isDefault").value(false));
        }

        @Test
        @DisplayName("400 — 이름이 비어있으면 거부한다")
        void rejectBlankName() throws Exception {
            var request = new ReminderListRequest("", "RED");

            mockMvc.perform(post("/api/lists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/lists/{id}")
    class Update {

        @Test
        @DisplayName("200 — 목록을 수정한다")
        void updatesList() throws Exception {
            var saved = repository.save(new ReminderList("Old", "BLUE"));
            var request = new ReminderListRequest("New", "RED");

            mockMvc.perform(put("/api/lists/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New"))
                    .andExpect(jsonPath("$.color").value("RED"));
        }

        @Test
        @DisplayName("404 — 존재하지 않는 목록 수정 시")
        void returnsNotFound() throws Exception {
            var request = new ReminderListRequest("New", "RED");

            mockMvc.perform(put("/api/lists/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/lists/{id}")
    class Delete {

        @Test
        @DisplayName("204 — 목록을 삭제한다")
        void deletesList() throws Exception {
            var saved = repository.save(new ReminderList("Work", "RED"));

            mockMvc.perform(delete("/api/lists/{id}", saved.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("409 — 기본 목록 삭제 시")
        void returnsConflictForDefault() throws Exception {
            var defaultList = repository.save(ReminderList.createDefault("미리 알림"));

            mockMvc.perform(delete("/api/lists/{id}", defaultList.getId()))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("404 — 존재하지 않는 목록 삭제 시")
        void returnsNotFound() throws Exception {
            mockMvc.perform(delete("/api/lists/{id}", 999))
                    .andExpect(status().isNotFound());
        }
    }
}
