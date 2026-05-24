# ЖКХ Связь — Мобильное приложение

Android-приложение для взаимодействия жильцов с управляющей компанией.

## Функциональность

### Для жильца
| UC | Описание |
|----|----------|
| UC-01 | Просмотр сводки начислений (ЖКХ-квитанции) |
| UC-02 | Получение уведомлений от УК |
| UC-03 | Подача обращения на устранение неисправности |
| UC-04 | Отслеживание статуса обращения |
| UC-05 | Переход в мессенджер (Telegram / ВКонтакте) |
| UC-06 | Просмотр журнала технических работ |

### Для администратора (сотрудника УК)
| UC | Описание |
|----|----------|
| UC-07 | Рассылка уведомлений жильцам |
| UC-08 | Обработка обращений жильцов |
| UC-09 | Назначение услуги жильцу |
| UC-10 | Внесение записи в журнал работ |

## Архитектура

**MVVM** + Repository Pattern + Clean Architecture (Use Cases)

```
app/src/main/java/com/example/communication/
├── data/
│   ├── models/          # User, Request, Receipt, Notification, Service, WorkLogEntry
│   ├── repositories/    # Interfaces + Mock implementations
│   └── mock/            # MockData (замена API на этапе разработки)
├── domain/
│   └── usecases/        # Login, Logout
└── presentation/
    ├── auth/            # AuthActivity, AuthViewModel
    └── regular/         # CoreActivity, ResidentViewModel, AdminViewModel
```

### Паттерны проектирования
- **MVVM** — разделение UI и бизнес-логики
- **Repository** — абстракция источника данных
- **Observer** — уведомления через StateFlow / LiveData
- **Chain of Responsibility** — обработка обращений (ValidationHandler → DuplicateCheckHandler → SaveRequestHandler)
- **Decorator** — отправка уведомлений (LoggingNotificationDecorator, PushDecorator)
- **State** — статусы обращения (NewState → InProgressState → DoneState / RejectedState)

## Стек технологий

- **Kotlin** + Android Jetpack
- **ViewModel** + **StateFlow** / **LiveData**
- **Coroutines** — асинхронность
- **Material Design 3**
- **Gradle** (Kotlin DSL)

## Запуск

1. Открыть проект в Android Studio
2. Синхронизировать Gradle
3. Запустить на эмуляторе или устройстве (minSdk 24)

### Тестовые учётные данные

| Роль | Логин | Пароль |
|------|-------|--------|
| Жилец | `89603568729` | серия+номер паспорта: `2411222333` |
| Жилец | `89271672730` | `52676942` |
| Администратор | `admin` | `admin321` |

## Диаграммы

В папке `/docs/diagrams/` хранятся:
- `use-case.drawio` — диаграмма вариантов использования
- `activity.drawio` — диаграммы деятельности (UC-01…UC-10)
- `sequence.drawio` — диаграммы взаимодействия
- `class.drawio` — диаграмма классов (MVVM + паттерны)
