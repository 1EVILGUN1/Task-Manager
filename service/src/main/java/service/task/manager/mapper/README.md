# README.md для папки `mapper`

## Описание

Папка `mapper` содержит интерфейсы мапперов, реализованные с использованием библиотеки MapStruct для приложения `service.task.manager`. Мапперы обеспечивают преобразование между объектами передачи данных (DTO) и сущностями базы данных (`Task`, `Epic`, `Subtask`), а также между различными типами DTO. Это позволяет отделить логику преобразования данных от сервисного слоя, упрощая код и повышая его читаемость. Все мапперы интегрированы со Spring и используют аннотации для точной настройки маппинга.

## Структура папки

Папка включает следующие интерфейсы мапперов:

### 1. `EpicMapper.java`
**Описание**: Интерфейс для преобразования данных, связанных с эпиками (`Epic`) и их подзадачами (`Subtask`).  
**Методы**:
- `toEntity(EpicRequestCreatedDto) → Epic` — Преобразует DTO для создания эпика в сущность.
- `toResponseDto(Epic) → EpicResponseDto` — Преобразует сущность эпика в DTO ответа (включает подзадачи).
- `toSubtaskDto(Subtask) → EpicResponseDto.SubtaskDto` — Преобразует подзадачу в DTO для включения в ответ эпика.
- `toEntity(SubtaskRequestUpdatedDto) → Subtask` — Преобразует DTO обновления подзадачи в сущность.
- `toSubtaskRequestUpdatedDto(Subtask) → SubtaskRequestUpdatedDto` — Преобразует подзадачу в DTO обновления.
- `toEntity(EpicRequestUpdatedDto) → Epic` — Преобразует DTO обновления эпика в сущность.
- `toEntity(EpicResponseDto) → Epic` — Преобразует DTO ответа в сущность.
- `toEpicDto(Epic) → EpicRequestUpdatedDto` — Преобразует сущность эпика в DTO обновления.
- `updateTaskFromDto(EpicRequestUpdatedDto, @MappingTarget Epic)` — Обновляет сущность эпика на основе DTO, игнорируя поля `id`, `startTime`, `subtasks`, `endTime` (последнее рассчитывается в `@PreUpdate`).

**Особенности**:
- Игнорирует `endTime`, так как оно вычисляется автоматически.
- Поддерживает вложенный маппинг подзадач для `EpicResponseDto`.

### 2. `SubtaskMapper.java`
**Описание**: Интерфейс для преобразования данных, связанных с подзадачами (`Subtask`).  
**Методы**:
- `toEntity(SubtaskRequestCreatedDto) → Subtask` — Преобразует DTO для создания подзадачи в сущность.
- `toResponseDto(Subtask) → SubtaskResponseDto` — Преобразует сущность подзадачи в DTO ответа.
- `toEntity(SubtaskRequestUpdatedDto) → Subtask` — Преобразует DTO обновления подзадачи в сущность.
- `toEntity(SubtaskResponseDto) → Subtask` — Преобразует DTO ответа в сущность.
- `updateSubtaskFromDto(SubtaskRequestUpdatedDto, @MappingTarget Subtask)` — Обновляет сущность подзадачи на основе DTO, игнорируя поля `id`, `epic`, `startTime`, `endTime` (последнее рассчитывается в `@PreUpdate`).

**Особенности**:
- Игнорирует поле `epic`, чтобы сохранить связь с эпикой из базы данных.
- Поддерживает обновление подзадачи без изменения неизменяемых полей.

### 3. `TaskMapper.java`
**Описание**: Интерфейс для преобразования данных, связанных с задачами (`Task`).  
**Методы**:
- `toEntity(TaskRequestCreatedDto) → Task` — Преобразует DTO для создания задачи в сущность.
- `toResponseDto(Task) → TaskResponseDto` — Преобразует сущность задачи в DTO ответа.
- `toEntity(TaskRequestUpdatedDto) → Task` — Преобразует DTO обновления задачи в сущность.
- `toEntity(TaskResponseDto) → Task` — Преобразует DTO ответа в сущность.
- `toTaskRequestUpdatedDto(Task) → TaskRequestUpdatedDto` — Преобразует сущность задачи в DTO обновления.
- `updateTaskFromDto(TaskRequestUpdatedDto, @MappingTarget Task)` — Обновляет сущность задачи на основе DTO, игнорируя поля `id`, `startTime`, `endTime` (последнее рассчитывается в `@PreUpdate`).

**Особенности**:
- Игнорирует `startTime` и `endTime` при обновлении, чтобы сохранить значения из базы.

## Основные особенности

- **MapStruct**: Все мапперы используют библиотеку MapStruct, которая генерирует реализацию маппинга во время компиляции, обеспечивая высокую производительность и типобезопасность.
- **Интеграция со Spring**: Аннотация `@Mapper(componentModel = "spring")` позволяет инжектировать мапперы как Spring-бины.
- **Аннотации `@Mapping`**:
    - Используются для точной настройки маппинга, например, для игнорирования полей (`id`, `startTime`, `endTime`, `subtasks`, `epic`).
    - Обеспечивают корректное преобразование сложных структур, таких как список подзадач в `EpicResponseDto`.
- **Обновление сущностей**: Методы `update...FromDto` используют `@MappingTarget` для частичного обновления сущностей, сохраняя неизменяемые поля.
- **Документация**: Мапперы косвенно документируются через DTO, которые содержат аннотации Swagger (`@Schema`).

## Использование

Мапперы используются в сервисах (`service`) для преобразования данных между DTO (из контроллеров) и сущностями (для работы с базой данных). Они упрощают обработку запросов, минимизируя ручной код преобразования.

**Пример использования в сервисе**:
```java
Epic epic = epicMapper.toEntity(epicRequestCreatedDto);
epicRepository.save(epic);
EpicResponseDto response = epicMapper.toResponseDto(epic);
```

## Зависимости

- **MapStruct**: Библиотека для генерации мапперов.
- **DTO**: Пакет `service.task.manager.dto` (папки `task`, `epic`, `subtask`).
- **Модели**: Пакет `service.task.manager.model` (классы `Task`, `Epic`, `Subtask`).
- **Spring**: Для интеграции мапперов как бинов.

## Документация API

Мапперы не имеют прямой документации в Swagger, но их использование отражено в DTO, которые описаны в Swagger UI:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Примечания

- **Игнорируемые поля**: Поля, такие как `id`, `startTime`, `endTime`, игнорируются при обновлении, чтобы избежать нежелательных изменений. `endTime` рассчитывается автоматически в сущностях с помощью аннотации `@PreUpdate`.
- **Производительность**: MapStruct генерирует эффективный код, минимизируя накладные расходы на преобразование.
- **Расширяемость**: Для добавления новых маппингов достаточно расширить существующие интерфейсы или создать новые.

## Дополнительно

Для информации о DTO, контроллерах или сервисах см. соответствующие папки (`dto`, `controller`, `service`). По вопросам настройки или расширения мапперов обратитесь к документации проекта или разработчикам.