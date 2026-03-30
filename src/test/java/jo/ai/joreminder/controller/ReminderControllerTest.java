package jo.ai.joreminder.controller;

import jo.ai.joreminder.domain.Reminder;
import jo.ai.joreminder.domain.ReminderList;
import jo.ai.joreminder.dto.ReminderRequest;
import jo.ai.joreminder.repository.ReminderListRepository;
import jo.ai.joreminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderListRepository listRepository;

    private ReminderList savedList;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        listRepository.deleteAll();
        savedList = listRepository.save(new ReminderList("Work", "RED"));
    }

    @Nested
    @DisplayName("GET /api/lists/{listId}/reminders")
    class FindByListId {

        @Test
        @DisplayName("200 — 목록의 리마인더를 반환한다")
        void returnsReminders() throws Exception {
            reminderRepository.save(new Reminder("Task 1", savedList));
            reminderRepository.save(new Reminder("Task 2", savedList));

            mockMvc.perform(get("/api/lists/{listId}/reminders", savedList.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].title").value("Task 1"));
        }

        @Test
        @DisplayName("404 — 존재하지 않는 목록")
        void returnsNotFound() throws Exception {
            mockMvc.perform(get("/api/lists/{listId}/reminders", 999))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/reminders")
    class Create {

        @Test
        @DisplayName("201 — 리마인더를 생성한다")
        void createsReminder() throws Exception {
            var request = new ReminderRequest("Buy milk", savedList.getId(), null, null, null, null);

            mockMvc.perform(post("/api/reminders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.title").value("Buy milk"))
                    .andExpect(jsonPath("$.completed").value(false))
                    .andExpect(jsonPath("$.listId").value(savedList.getId()));
        }

        @Test
        @DisplayName("400 — 제목이 비어있으면 거부한다")
        void rejectBlankTitle() throws Exception {
            var request = new ReminderRequest("", savedList.getId(), null, null, null, null);

            mockMvc.perform(post("/api/reminders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 — listId가 null이면 거부한다")
        void rejectNullListId() throws Exception {
            mockMvc.perform(post("/api/reminders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Test\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 — 존재하지 않는 목록에 생성 시")
        void returnsNotFound() throws Exception {
            var request = new ReminderRequest("Task", 999L, null, null, null, null);

            mockMvc.perform(post("/api/reminders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/reminders/{id}")
    class Update {

        @Test
        @DisplayName("200 — 리마인더를 수정한다")
        void updatesReminder() throws Exception {
            var saved = reminderRepository.save(new Reminder("Old", savedList));
            var request = new ReminderRequest("New", savedList.getId(), null, null, null, null);

            mockMvc.perform(put("/api/reminders/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("New"));
        }

        @Test
        @DisplayName("404 — 존재하지 않는 리마인더")
        void returnsNotFound() throws Exception {
            var request = new ReminderRequest("New", savedList.getId(), null, null, null, null);

            mockMvc.perform(put("/api/reminders/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/reminders/{id}/toggle")
    class Toggle {

        @Test
        @DisplayName("200 — 완료 상태를 토글한다")
        void togglesCompleted() throws Exception {
            var saved = reminderRepository.save(new Reminder("Task", savedList));

            mockMvc.perform(patch("/api/reminders/{id}/toggle", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        @DisplayName("404 — 존재하지 않는 리마인더")
        void returnsNotFound() throws Exception {
            mockMvc.perform(patch("/api/reminders/{id}/toggle", 999))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/reminders/{id}")
    class Delete {

        @Test
        @DisplayName("204 — 리마인더를 삭제한다")
        void deletesReminder() throws Exception {
            var saved = reminderRepository.save(new Reminder("Task", savedList));

            mockMvc.perform(delete("/api/reminders/{id}", saved.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("404 — 존재하지 않는 리마인더")
        void returnsNotFound() throws Exception {
            mockMvc.perform(delete("/api/reminders/{id}", 999))
                    .andExpect(status().isNotFound());
        }
    }
}
