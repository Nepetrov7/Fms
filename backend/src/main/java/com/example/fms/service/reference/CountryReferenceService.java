package com.example.fms.service.reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Справочник стран для автодополнения полей профиля мигранта.
 *
 * <p>При первом обращении сервис пытается загрузить список стран из REST
 * Countries API. Если внешний сервис недоступен, используется встроенный
 * резервный список, чтобы форма профиля оставалась работоспособной.</p>
 */
@Service
public class CountryReferenceService {

    private static final Logger log = LoggerFactory.getLogger(CountryReferenceService.class);
    private static final URI REST_COUNTRIES_URI = URI.create(
            "https://restcountries.com/v3.1/all?fields=translations,name");

    private static final Pattern RUS_NAME = Pattern.compile(
            "\"rus\"\\s*:\\s*\\{[^{}]*\"common\"\\s*:\\s*\"([^\"\\\\]+)\"");
    private static final Pattern ENG_NAME = Pattern.compile(
            "\"name\"\\s*:\\s*\\{[^{}]*\"common\"\\s*:\\s*\"([^\"\\\\]+)\"");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private volatile List<String> countries = fallbackCountries();
    private volatile boolean initialized;

    /**
     * Принудительно сбрасывает кэш и заново загружает справочник стран.
     *
     * <p>Метод полезен после временного сбоя сети: при следующей загрузке сервис
     * снова попробует получить актуальные данные из внешнего API.</p>
     */
    public synchronized void reload() {
        initialized = false;
        ensureLoaded();
    }

    /**
     * Ищет страны по началу или вхождению подстроки без учета регистра.
     *
     * @param query фрагмент названия страны; пустая строка возвращает первые результаты
     * @param limit максимальное количество результатов, ограничивается диапазоном 1..50
     * @return отсортированный список подходящих названий стран
     */
    public List<String> search(String query, int limit) {
        ensureLoaded();
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        int max = Math.max(1, Math.min(limit, 50));
        return countries.stream()
                .filter(name -> q.isEmpty()
                        || name.toLowerCase(Locale.ROOT).startsWith(q)
                        || name.toLowerCase(Locale.ROOT).contains(q))
                .limit(max)
                .toList();
    }

    /**
     * Лениво и потокобезопасно загружает справочник стран.
     */
    private void ensureLoaded() {
        if (initialized) {
            return;
        }
        synchronized (this) {
            if (initialized) {
                return;
            }
            List<String> loaded = fetchFromRestCountries();
            if (loaded.isEmpty()) {
                countries = fallbackCountries();
                log.warn("REST Countries недоступен - используется встроенный список ({} стран)", countries.size());
            } else {
                countries = List.copyOf(loaded);
                log.info("Загружено {} стран из REST Countries", countries.size());
            }
            initialized = true;
        }
    }

    /**
     * Загружает и разбирает список стран из REST Countries API.
     *
     * @return отсортированный список русских названий стран; если их нет, английских названий
     */
    private List<String> fetchFromRestCountries() {
        try {
            HttpRequest request = HttpRequest.newBuilder(REST_COUNTRIES_URI)
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return List.of();
            }
            String body = response.body();
            TreeSet<String> unique = new TreeSet<>(Comparator.naturalOrder());
            Matcher rus = RUS_NAME.matcher(body);
            while (rus.find()) {
                unique.add(rus.group(1).trim());
            }
            if (unique.isEmpty()) {
                Matcher eng = ENG_NAME.matcher(body);
                while (eng.find()) {
                    unique.add(eng.group(1).trim());
                }
            }
            return new ArrayList<>(unique);
        } catch (Exception e) {
            log.warn("Не удалось загрузить страны: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Возвращает встроенный резервный список стран.
     *
     * @return минимальный набор стран для работы формы без внешней сети
     */
    private static List<String> fallbackCountries() {
        return List.of(
                "Азербайджан", "Армения", "Беларусь", "Грузия", "Казахстан", "Киргизия",
                "Молдова", "Россия", "Таджикистан", "Туркменистан", "Узбекистан", "Украина",
                "Афганистан", "Вьетнам", "Индия", "Китай", "Монголия", "Турция", "Сирия",
                "Германия", "Франция", "Италия", "Испания", "Польша", "США");
    }
}
