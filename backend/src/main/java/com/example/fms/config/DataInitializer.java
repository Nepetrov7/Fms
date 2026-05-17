package com.example.fms.config;

import com.example.fms.entity.DisplayRule;
import com.example.fms.entity.PatentType;
import com.example.fms.entity.RoadmapTask;
import com.example.fms.repository.DisplayRuleRepository;
import com.example.fms.repository.PatentTypeRepository;
import com.example.fms.repository.RoadmapTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Начальное наполнение БД при первом запуске (не в профиле test).
 * Создаёт типы патентов и демонстрационную дорожную карту с правилами отображения.
 */
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer {

    private final RoadmapTaskRepository taskRepository;
    private final DisplayRuleRepository displayRuleRepository;
    private final PatentTypeRepository patentTypeRepository;

    private int nextGroupId = 1;

    /**
     * Вызывается после готовности приложения: справочник патентов и сиды задач.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        initPatentTypes();
        if (taskRepository.count() > 0) {
            return;
        }
        nextGroupId = 1;
        seedRoadmap();
    }

    private void seedRoadmap() {
        // —— Общие шаги (всем) ——
        RoadmapTask migration1 = task(
                "Поставить на учёт по месту пребывания",
                "Постановка на учёт",
                "В течение 7 рабочих дней с даты въезда подайте уведомление о прибытии в территориальный орган МВД.",
                7);
        rulesForAll(migration1.getId());

        RoadmapTask migration2 = task(
                "Получить уведомление о постановке на учёт",
                "Постановка на учёт",
                "Сохраните копию уведомления — оно понадобится для следующих шагов.",
                10);
        rulesForAll(migration2.getId());

        // —— Сертификат русского языка (только если сертификата ещё нет) ——
        RoadmapTask cert1 = task(
                "Уточнить адрес отделения МВД по месту пребывания",
                "Сертификат русского языка",
                "На сайте МВД или по телефону горячей линии узнайте отдел, который принимает документы на сертификат.",
                5);
        rulesWithoutCertificate(cert1.getId());

        RoadmapTask cert2 = task(
                "Подготовить документы для сертификата",
                "Сертификат русского языка",
                "Паспорт, миграционная карта, уведомление о прибытии, фото, квитанция об оплате госпошлины (если требуется).",
                10);
        rulesWithoutCertificate(cert2.getId());

        RoadmapTask cert3 = task(
                "Подать заявление на сертификат в территориальный орган МВД",
                "Сертификат русского языка",
                "Подайте комплект документов лично или через МФЦ по месту жительства или пребывания.",
                14);
        rulesWithoutCertificate(cert3.getId());

        RoadmapTask cert4 = task(
                "Получить расписку о приёме документов",
                "Сертификат русского языка",
                "Проверьте дату и время явки, указанные в расписке.",
                15);
        rulesWithoutCertificate(cert4.getId());

        RoadmapTask cert5 = task(
                "Явиться в отделение МВД в назначенный день",
                "Сертификат русского языка",
                "Возьмите оригиналы документов и расписку о приёме.",
                18);
        rulesWithoutCertificate(cert5.getId());

        RoadmapTask cert6 = task(
                "Записаться на экзамен по русскому языку",
                "Сертификат русского языка",
                "Запись через отделение МВД или аккредитованную организацию — уточните способ в вашем регионе.",
                22);
        rulesWithoutCertificate(cert6.getId());

        RoadmapTask cert7 = task(
                "Пройти экзамен по русскому языку, истории и основам законодательства",
                "Сертификат русского языка",
                "Экзамен включает тест и устную часть. При необходимости можно пересдать.",
                28);
        rulesWithoutCertificate(cert7.getId());

        RoadmapTask cert8 = task(
                "Получить сертификат владения русским языком",
                "Сертификат русского языка",
                "После успешной сдачи заберите сертификат в отделении и отметьте это в профиле.",
                30);
        rulesWithoutCertificate(cert8.getId());

        // —— Патент (есть сертификат, патента ещё нет) ——
        RoadmapTask pat1 = task(
                "Собрать документы для оформления патента",
                "Патент на работу",
                "Паспорт, миграционная карта, сертификат русского языка, трудовой договор или гарантии работодателя, фото.",
                35);
        rulesWithoutPatentWithCertificate(pat1.getId());

        RoadmapTask pat2 = task(
                "Заполнить заявление на патент",
                "Патент на работу",
                "Бланк выдаётся в МФЦ или скачивается на сайте МВД. Заполните без ошибок.",
                38);
        rulesWithoutPatentWithCertificate(pat2.getId());

        RoadmapTask pat3 = task(
                "Подать заявление на патент в территориальный орган МВД",
                "Патент на работу",
                "Подайте документы лично или через МФЦ. Сохраните отметку о приёме.",
                42);
        rulesWithoutPatentWithCertificate(pat3.getId());

        RoadmapTask pat4 = task(
                "Оплатить госпошлину за выдачу патента",
                "Госпошлина",
                "Оплатите по реквизитам, указанным в отделении. Сохраните квитанцию.",
                45);
        rulesWithoutPatentWithCertificate(pat4.getId());

        RoadmapTask pat5 = task(
                "Пройти медицинское освидетельствование",
                "Патент на работу",
                "Сдайте анализы и получите справки из аккредитованной клиники для пакета документов на патент.",
                50);
        rulesWithoutPatentWithCertificate(pat5.getId());

        RoadmapTask pat6 = task(
                "Сдать отпечатки пальцев (дактилоскопия)",
                "Патент на работу",
                "Процедура проводится в МВД после подачи документов — уточните очередь в вашем отделении.",
                52);
        rulesWithoutPatentWithCertificate(pat6.getId());

        RoadmapTask pat7 = task(
                "Дождаться решения о выдаче патента",
                "Патент на работу",
                "Срок рассмотрения — до 10 рабочих дней с даты подачи полного комплекта документов.",
                55);
        rulesWithoutPatentWithCertificate(pat7.getId());

        RoadmapTask pat8 = task(
                "Получить патент на работу",
                "Патент на работу",
                "Заберите патент в отделении и укажите данные в профиле (тип, номер, даты).",
                60);
        rulesWithoutPatentWithCertificate(pat8.getId());

        // —— После получения патента (для проверки правил «патент есть») ——
        RoadmapTask afterPatent = task(
                "Продлить патент до истечения срока",
                "Продление патента",
                "Подайте заявление на продление не позднее чем за 20 рабочих дней до окончания действия патента.",
                300);
        rulesWithPatent(afterPatent.getId());

        // —— Дополнительные условия по сроку пребывания ——
        RoadmapTask early = task(
                "Проверить срок законного пребывания",
                "Постановка на учёт",
                "Убедитесь, что не превышен разрешённый срок визита. При необходимости оформите продление.",
                20);
        // Одна группа (И): не граждане Беларуси и не более 90 дней с въезда
        saveRules(early.getId(), nextGroupId++, List.of(
                rule("DAYS_SINCE_ENTRY", "LTE", "90"),
                rule("CITIZENSHIP", "NE", "Беларусь")));
    }

    private void rulesForAll(Long taskId) {
        saveRules(taskId, nextGroupId++, List.of(rule("ALL", "EQ", "true")));
    }

    private void rulesWithoutCertificate(Long taskId) {
        saveRules(taskId, nextGroupId++, List.of(
                rule("HAS_LANGUAGE_CERTIFICATE", "EQ", "false")));
    }

    private void rulesWithoutPatentWithCertificate(Long taskId) {
        saveRules(taskId, nextGroupId++, List.of(
                rule("HAS_LANGUAGE_CERTIFICATE", "EQ", "true"),
                rule("HAS_PATENT", "EQ", "false")));
    }

    private void rulesWithPatent(Long taskId) {
        saveRules(taskId, nextGroupId++, List.of(
                rule("HAS_PATENT", "EQ", "true")));
    }

    private DisplayRule rule(String key, String op, String value) {
        DisplayRule r = new DisplayRule();
        r.setParameterKey(key);
        r.setOperator(op);
        r.setRuleValue(value);
        return r;
    }

    private void saveRules(Long taskId, int groupId, List<DisplayRule> rules) {
        for (DisplayRule r : rules) {
            r.setTaskId(taskId);
            r.setRuleGroupId(groupId);
            displayRuleRepository.save(r);
        }
    }

    private void initPatentTypes() {
        if (patentTypeRepository.count() > 0) {
            return;
        }
        savePatentType("WORK", "Патент на работу");
        savePatentType("SELF_EMPLOYED", "Патент для самозанятых");
        savePatentType("SEASONAL", "Сезонный патент");
        savePatentType("OTHER", "Иной тип патента");
    }

    private void savePatentType(String code, String name) {
        PatentType type = new PatentType();
        type.setCode(code);
        type.setName(name);
        type.setActive(true);
        patentTypeRepository.save(type);
    }

    private RoadmapTask task(String title, String groupName, String description, int days) {
        RoadmapTask task = new RoadmapTask();
        task.setTitle(title);
        task.setGroupName(groupName);
        task.setDescription(description);
        task.setDaysToComplete(days);
        task.setActive(true);
        return taskRepository.save(task);
    }
}
