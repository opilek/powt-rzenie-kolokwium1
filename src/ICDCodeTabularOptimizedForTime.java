import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Klasa implementująca interfejs ICDCodeTabular – przechowuje opisy kodów ICD-10 w sposób zoptymalizowany pod kątem szybkości (HashMap)
public class ICDCodeTabularOptimizedForTime implements ICDCodeTabular
{
    // Mapa odwzorowująca kod ICD-10 na jego opis (np. "A00" → "Cholera")
    private Map<String, String> codeToDescription = new HashMap<>();

    // Konstruktor wczytujący dane z pliku – każda linia zawiera kod ICD-10 i jego opis
    public ICDCodeTabularOptimizedForTime(String filePath) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) // automatyczne zamknięcie pliku
        {
            String line;
            int lineNumber = 0; // numer aktualnie wczytywanej linii

            // Wczytujemy linia po linii
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                if (lineNumber < 88) continue; // pomijamy pierwsze 87 linii (np. nagłówki, metadane)

                line = line.trim(); // usuwamy spacje z początku i końca

                /*
                Wyrażenie regularne sprawdza, czy linia pasuje do formatu kodu ICD-10 i opisu.
                Wyrażenie: [A-Z][0-9]{2}(\\.[0-9]+)?\\s+.+

                - [A-Z]         → jedna wielka litera (np. A)
                - [0-9]{2}      → dokładnie dwie cyfry (np. 01, 99)
                - (\\.[0-9]+)?  → opcjonalna część z kropką i cyframi (np. .0, .23)
                - \\s+          → co najmniej jedna spacja
                - .+            → co najmniej jeden dowolny znak (czyli opis choroby)
                 */

                if (line.matches("[A-Z][0-9]{2}(\\.[0-9]+)?\\s+.+")) {
                    // Dzielimy linię na dwie części: kod i opis (pierwsze wystąpienie spacji jako separator)
                    String[] parts = line.split("\\s+", 2);

                    if (parts.length == 2) {
                        // Dodajemy kod i jego opis do mapy
                        this.codeToDescription.put(parts[0], parts[1]);
                    }
                }
            }
        }
    }

    // Zwraca opis choroby dla danego kodu ICD-10
    @Override
    public String getDescription(String code)
    {
        // Jeśli kod nie istnieje w mapie, rzucamy wyjątek
        if (!this.codeToDescription.containsKey(code))
        {
            throw new IndexOutOfBoundsException("Kod nieznaleziony: " + code);
        }

        // Zwracamy opis z mapy
        return this.codeToDescription.get(code);
    }
}
