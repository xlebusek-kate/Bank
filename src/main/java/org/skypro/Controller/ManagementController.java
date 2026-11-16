package org.skypro.Controller;

import org.skypro.Repository.BankRepository;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Служебные эндпоинты управления сервисом: сброс кешей и информация о версии.
 */
@RestController
@RequestMapping("/management")
public class ManagementController {

    private final BankRepository bankRepository;
    private final BuildProperties buildProperties;

    /**
     * Создаёт контроллер.
     *
     * @param bankRepository  репозиторий базы знаний, в котором лежат кеши
     * @param buildProperties данные сборки (имя и версия из pom.xml)
     */
    public ManagementController(BankRepository bankRepository, BuildProperties buildProperties) {
        this.bankRepository = bankRepository;
        this.buildProperties = buildProperties;
    }

    /**
     * Очищает все закешированные результаты запросов к базе знаний.
     *
     * @return ответ 200 OK без тела
     */
    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        bankRepository.clearAllCaches();
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает имя и версию сервиса, взятые из pom.xml.
     *
     * @return объект с полями {@code name} и {@code version}
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "name", buildProperties.getName(),
                "version", buildProperties.getVersion()
        ));
    }
}