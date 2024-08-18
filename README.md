Для проверки тестов:
1. Ввести в терминале:
docker-compose -f docker-compose-prod.yml --env-file .env up
2. Остановить контейнер самого приложения spring_app.
3. Открыть код в среде разработки, закоментировать опцию -
   profiles:
     active: prod
4. Запустить тесты.