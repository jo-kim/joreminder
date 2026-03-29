package jo.ai.joreminder.config;

import jo.ai.joreminder.domain.ReminderList;
import jo.ai.joreminder.repository.ReminderListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final ReminderListRepository reminderListRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (reminderListRepository.count() == 0) {
            reminderListRepository.save(ReminderList.createDefault("미리 알림"));
        }
    }
}
