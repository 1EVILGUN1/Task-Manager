# README.md для папки `repository`

## Описание

Папка `repository` содержит интерфейсы репозиториев для работы с базой данных в приложении `service.task.manager`. Репозитории реализованы с использованием Spring Data JPA и предоставляют методы для выполнения CRUD-операций (создание, чтение, обновление, удаление) над сущностями `Task`, `Epic` и `Subtask`. Каждый репозиторий расширяет интерфейс `JpaRepository`, что обеспечивает стандартные методы для работы с данными, а также включает дополнительные методы для специфичных проверок.

## Структура папки

### 1. `EpicRepository.java`
**Описание**: Интерфейс репозитория для работы с сущностью `Epic` (эпики).  
**Методы**:
- Наследуемые от `JpaRepository<Epic, Long>`:
    - `save`, `findById`, `findAll`, `deleteById`, и др. — стандартные CRUD-операции.
- Специфичный метод:
    - `boolean existsByName(String name)` — Проверяет, существует ли эпик с указанным именем.  
      **Использование**: Используется для создания, получения, обновления и удаления эпиков, а также для проверки уникальности имени эпика.

### 2. `SubtaskRepository.java`
**Описание**: Интерфейс репозитория для работы с сущностью `Subtask` (подзадачи).  
**Методы**:
- Наследуемые от `JpaRepository<Subtask, Long>`:
    - `save`, `findById`, `findAll`, `deleteById`, и др. — стандартные CRUD-операции.
- Специфичный метод:
    - `boolean existsByName(String name)` — Проверяет, существует ли подзадача с указанным именем.  
      **Использование**: Используется для управления подзадачами, связанными с эпиками, и проверки уникальности имени подзадачи.

### 3. `TaskRepository.java`
**Описание**: Интерфейс репозитория для работы с сущностью `Task` (задачи).  
**Методы**:
- Наследуемые от `JpaRepository<Task, Long>`:
    - `save`, `findById`, `findAll`, `deleteById`, и др. — стандартные CRUD-операции.
- Специфичный метод:
    - `boolean existsByName(String name)` — Проверяет, существует ли задача с указанным именем.  
      **Использование**: Используется для управления независимыми задачами и проверки уникальности имени задачи.

## Основные особенности

- **Spring Data JPA**: Репозитории используют Spring Data JPA для автоматической генерации запросов к базе данных на основе имен методов и аннотаций.
- **Аннотация `@Repository`**: Помечает интерфейсы как Spring-бины, интегрируемые с контекстом приложения.
- **Уникальность имен**: Метод `existsByName` в каждом репозитории позволяет проверять уникальность имени сущности, что предотвращает создание дубликатов (например, используется для генерации исключения `ConflictException`).
- **Типизация**: Репозитории строго типизированы для работы с соответствующими сущностями (`Epic`, `Subtask`, `Task`) и их идентификаторами (`Long`).
- **Каскадные операции**: Для `EpicRepository` операции над эпиками автоматически применяются к связанным подзадачам благодаря настройке `cascade = CascadeType.ALL` в модели `Epic`.

## Использование

Репозитории используются в сервисах (`service`) для взаимодействия с базой данных. Они предоставляют удобный интерфейс для выполнения операций без необходимости написания SQL-запросов.

**Пример использования в сервисе**:
```java
@Service
@RequiredArgsConstructor
public class EpicService {
    private final EpicRepository epicRepository;

    public void create(Epic epic) {
        if (epicRepository.existsByName(epic.getName())) {
            throw new ConflictException("Эпик с именем " + epic.getName() + " уже существует");
        }
        epicRepository.save(epic);
    }
}
```

## Зависимости

- **Spring Data JPA**: Для реализации репозиториев и взаимодействия с базой данных.
- **Hibernate**: Как реализация JPA для маппинга сущностей на таблицы.
- **Модели**: Пакет `service.task.manager.model` (классы `Task`, `Epic`, `Subtask`).
- **Исключения**: Пакет `service.task.manager.exception` (например, `ConflictException` для обработки ошибок).

## Документация API

Репозитории не документируются напрямую в Swagger, но их функциональность отражена в эндпоинтах контроллеров, которые используют сервисы и, соответственно, репозитории. Документация доступна по:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Примечания

- **Автоматические методы**: `JpaRepository` предоставляет множество встроенных методов, таких как `findAll`, `save`, `delete`, что минимизирует необходимость написания пользовательских запросов.
- **Проверка уникальности**: Метод `existsByName` используется для предотвращения дублирования имен, что является частью бизнес-логики (например, валидация перед сохранением).
- **Производительность**: Spring Data JPA оптимизирует запросы, но для сложных операций рекомендуется использовать `@Query` или другие механизмы, если потребуется.
- **Расширяемость**: Для добавления новых методов достаточно расширить интерфейсы репозиториев, используя соглашения об именовании Spring Data или аннотацию `@Query`.

## Дополнительно

Для информации о моделях, сервисах, мапперах или контроллерах см. соответствующие папки (`model`, `service`, `mapper`, `controller`). По вопросам настройки или расширения репозиториев обратитесь к документации проекта или разработчикам.