# File Checksum service
## Requirements
„Napisz usługę REST, która umożliwi wczytanie jednego lub więcej plików, obliczy ich sumę kontrolną (digest) oraz rozmiar, a następnie zapisze te informacje, wraz z nazwą pliku, w bazie danych. Kolejnym krokiem będzie przekazanie pliku dalej, wykorzystując metodę void store(String location, InputStream content) (metoda ta nie musi być implementowana w ramach zadania). Pliki mogą być bardzo duże. Implementację wykonaj przy użyciu Spring WebFlux oraz R2DBC do obsługi baz danych. Dodatkowo przygotuj testy jednostkowe oraz integracyjne, aby zapewnić poprawność działania rozwiązania.”

## Commands

start up application:
./gradlew bootRun

perform tests:
./gradlew test
