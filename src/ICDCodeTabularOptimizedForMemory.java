import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Klasa implementująca interfejs ICDCodeTabular – zoptymalizowana pod kątem zużycia pamięci
// Dane nie są trzymane w pamięci – każdorazowo są odczytywane z pliku
public class ICDCodeTabularOptimizedForMemory implements ICDCodeTabular
{
    // Ścieżka do pliku z danymi ICD-10
    private String filePath;

    // Konstruktor – tylko zapamiętuje ścieżkę do pliku
    public ICDCodeTabularOptimizedForMemory(String filePath)
    {
        this.filePath = filePath;
    }

    // Metoda zwracająca opis kodu ICD-10
    // Wyszukuje kod "na żywo" w pliku (bez wcześniejszego wczytywania całego pliku do pamięci)
    @Override
    public String getDescription(String code) throws IndexOutOfBoundsException
    {
        // Otwieramy plik do odczytu
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            int lineNumber = 0;

            // Wczytujemy plik linia po linii
            while ((line = br.readLine()) != null)
            {
                lineNumber++;

                // Pomijamy pierwsze 87 linii (np. nagłówki)
                if (lineNumber < 88) continue;

                line = line.trim(); // usuwamy białe znaki z początku i końca

                // Sprawdzamy, czy linia zaczyna się od szukanego kodu i spacji
                // Zapobiega np. błędnemu dopasowaniu "A00" do "A001"
                if (line.startsWith(code + " "))
                {
                    // Zwracamy resztę linii jako opis
                    return line.substring(code.length()).trim();
                }
            }
        }
        catch (IOException e)
        {
            // Jeśli wystąpi błąd I/O – np. plik nie istnieje – rzucamy wyjątek z informacją
            throw new RuntimeException("Błąd odczytu pliku", e);
        }

        // Jeśli nie znaleziono kodu – rzucamy wyjątek
        throw new IndexOutOfBoundsException("Kod nieznaleziony: " + code);
    }
}