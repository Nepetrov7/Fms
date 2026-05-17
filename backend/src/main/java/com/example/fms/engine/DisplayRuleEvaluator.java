package com.example.fms.engine;

import com.example.fms.entity.DisplayRule;
import com.example.fms.entity.Migrant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Проверяет, должна ли задача дорожной карты отображаться для профиля мигранта.
 *
 * <p>Правила хранятся отдельными строками {@link DisplayRule}. Строки с одним
 * {@code ruleGroupId} образуют одну группу условий, внутри которой все условия
 * должны выполниться одновременно, то есть работают через логическое И. Разные
 * группы являются альтернативами и работают через логическое ИЛИ: если подошла
 * хотя бы одна группа, задача считается видимой.</p>
 *
 * <p>Задача без правил не отображается. Чтобы показать задачу всем пользователям
 * с заполненным профилем, нужно добавить правило с параметром
 * {@link DisplayRuleParameter#ALL}.</p>
 */
@Component
@RequiredArgsConstructor
public class DisplayRuleEvaluator {

    /**
     * Проверяет, подходит ли мигрант хотя бы под одну группу правил задачи.
     *
     * @param migrant профиль мигранта, из которого берутся значения для проверки условий
     * @param rules все правила отображения, привязанные к одной задаче дорожной карты
     * @return {@code true}, если подошла хотя бы одна группа правил; {@code false}, если правил нет
     */
    public boolean isTaskVisible(Migrant migrant, List<DisplayRule> rules) {
        if (rules == null || rules.isEmpty()) {
            return false;
        }
        Map<Integer, List<DisplayRule>> byGroup = rules.stream()
                .collect(Collectors.groupingBy(DisplayRule::getRuleGroupId));
        return byGroup.values().stream().anyMatch(group -> matchesGroup(migrant, group));
    }

    /**
     * Проверяет одну группу правил, внутри которой условия объединены через И.
     *
     * <p>Специальный параметр {@code ALL} сразу делает группу подходящей,
     * потому что он означает безусловное отображение задачи.</p>
     *
     * @param migrant профиль мигранта
     * @param group список правил с одинаковым {@code ruleGroupId}
     * @return {@code true}, если все условия группы выполнены или в группе есть {@code ALL}
     */
    private boolean matchesGroup(Migrant migrant, List<DisplayRule> group) {
        for (DisplayRule rule : group) {
            if (DisplayRuleParameter.fromKey(rule.getParameterKey()) == DisplayRuleParameter.ALL) {
                return true;
            }
        }
        return group.stream().allMatch(rule -> matchesCondition(migrant, rule));
    }

    /**
     * Проверяет одно условие правила по данным профиля мигранта.
     *
     * <p>Параметр правила определяет, какое поле профиля нужно прочитать.
     * Оператор определяет, как это значение сравнивается со значением из
     * {@link DisplayRule#getRuleValue()}.</p>
     *
     * @param migrant профиль мигранта
     * @param rule одно сохраненное правило отображения
     * @return {@code true}, если условие выполнено
     */
    private boolean matchesCondition(Migrant migrant, DisplayRule rule) {
        DisplayRuleParameter param = DisplayRuleParameter.fromKey(rule.getParameterKey());
        DisplayRuleOperator op = DisplayRuleOperator.fromString(rule.getOperator());
        String value = rule.getRuleValue();

        return switch (param) {
            case ALL -> true;
            case DAYS_SINCE_ENTRY -> compareLong(daysSinceEntry(migrant), parseLong(value), op);
            case HAS_PATENT -> compareBoolean(hasPatent(migrant), parseBoolean(value), op);
            case HAS_LANGUAGE_CERTIFICATE -> compareBoolean(
                    Boolean.TRUE.equals(migrant.getHasLanguageCertificate()), parseBoolean(value), op);
            case COUNTRY_OF_ARRIVAL -> compareString(migrant.getCountryOfArrival(), value, op);
            case CITIZENSHIP -> compareString(migrant.getCitizenship(), value, op);
        };
    }

    /**
     * Считает количество календарных дней с даты въезда мигранта.
     *
     * @param migrant профиль мигранта
     * @return количество дней от даты въезда до текущей даты или {@code 0}, если дата въезда не указана
     */
    private long daysSinceEntry(Migrant migrant) {
        if (migrant.getArrivalDate() == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(migrant.getArrivalDate(), LocalDate.now());
    }

    /**
     * Возвращает признак наличия патента, используемый в правилах отображения.
     *
     * @param migrant профиль мигранта
     * @return {@code true}, только если в профиле явно указано наличие патента
     */
    private boolean hasPatent(Migrant migrant) {
        return Boolean.TRUE.equals(migrant.getHasPatent());
    }

    /**
     * Сравнивает строковые значения на равенство или неравенство.
     *
     * <p>Сравнение не учитывает регистр и пробелы по краям. Операторы
     * числового сравнения для строк не применяются и возвращают {@code false}.</p>
     *
     * @param actual значение из профиля мигранта
     * @param expected значение, сохраненное в правиле
     * @param op оператор сравнения
     * @return результат сравнения
     */
    private boolean compareString(String actual, String expected, DisplayRuleOperator op) {
        String a = actual == null ? "" : actual.trim().toLowerCase();
        String e = expected == null ? "" : expected.trim().toLowerCase();
        return switch (op) {
            case EQ -> a.equals(e);
            case NE -> !a.equals(e);
            default -> false;
        };
    }

    /**
     * Сравнивает логические значения.
     *
     * <p>Для boolean-условий имеют смысл только операторы {@code EQ} и {@code NE}.
     * Числовые операторы сравнения возвращают {@code false}.</p>
     *
     * @param actual логическое значение, вычисленное по профилю мигранта
     * @param expected логическое значение, прочитанное из правила
     * @param op оператор сравнения
     * @return результат сравнения
     */
    private boolean compareBoolean(boolean actual, boolean expected, DisplayRuleOperator op) {
        return switch (op) {
            case EQ -> actual == expected;
            case NE -> actual != expected;
            default -> false;
        };
    }

    /**
     * Сравнивает числовые значения.
     *
     * @param actual число, вычисленное по профилю мигранта
     * @param expected число, прочитанное из правила
     * @param op оператор сравнения
     * @return результат сравнения для равенства, неравенства и операторов порядка
     */
    private boolean compareLong(long actual, long expected, DisplayRuleOperator op) {
        return switch (op) {
            case EQ -> actual == expected;
            case NE -> actual != expected;
            case GT -> actual > expected;
            case LT -> actual < expected;
            case GTE -> actual >= expected;
            case LTE -> actual <= expected;
        };
    }

    /**
     * Преобразует строковое значение правила в число.
     *
     * @param value исходное значение из {@link DisplayRule#getRuleValue()}
     * @return число из правила или {@code 0}, если значение пустое
     * @throws NumberFormatException если значение непустое, но не является числом
     */
    private long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Long.parseLong(value.trim());
    }

    /**
     * Преобразует строковое значение правила в boolean.
     *
     * @param value исходное значение из {@link DisplayRule#getRuleValue()}
     * @return {@code true} только для строки {@code "true"} без учета регистра
     */
    private boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value != null ? value.trim() : "");
    }
}
