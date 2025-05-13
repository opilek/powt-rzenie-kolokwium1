import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Klasa przechowująca listę statystyk przyczyn zgonów i operacje na niej
public class DeathCauseStatisticList
{
    // Lista obiektów przechowujących dane statystyczne o zgonach
    private List<DeathCauseStatistic> stats = new ArrayList<DeathCauseStatistic>();

    // Prywatna metoda sprawdzająca, czy kod ICD-10 jest poprawny (np. A12, B99, C03.4 itd.)
    private boolean isValidICD10(String code)
    {
        //^ → początek tekstu (czyli sprawdzamy od początku ciągu znaków)
        //
        //[A-Z] → jedna wielka litera (od A do Z)
        //
        //\\d{2} → dokładnie dwie cyfry (\\d oznacza cyfrę, {2} – dokładnie 2 razy)
        //
        //.* → dowolne znaki (kropka . oznacza "dowolny znak", a * – zero lub więcej razy)
        return code.matches("^[A-Z]\\d{2}.*");
    }

    // Metoda wczytująca dane z pliku CSV i uzupełniająca listę stats
    public void repopulate(String file_path)
    {
        this.stats.clear(); // Czyści obecną listę statystyk

        try (BufferedReader br = new BufferedReader(new FileReader(file_path)))
        {
            br.readLine(); // Pomija pierwszy wiersz (np. nagłówki kolumn)
            br.readLine(); // Pomija drugi wiersz (np. opis)

            String line;

            // Wczytujemy kolejne linie
            while ((line = br.readLine()) != null)
            {
                // Tworzymy obiekt DeathCauseStatistic z jednej linii CSV
                DeathCauseStatistic stat = DeathCauseStatistic.fromCsvLine(line);

                // Dodajemy tylko poprawne kody ICD-10
                if (isValidICD10(stat.getIcd10Code()))
                {
                    this.stats.add(stat);
                }
            }
        }
        catch (IOException e)
        {
            // W razie błędu odczytu pliku rzucamy wyjątek
            throw new RuntimeException();
        }
    }

    // Metoda zwracająca n najbardziej śmiercionośnych chorób dla danego wieku
    public List<DeathCauseStatistic> mostDeadlyDiseases(int age, int n)
    {
        List<DeathCauseStatistic> vaildStats = new ArrayList<>(); // statystyki z poprawnymi danymi dla danego wieku
        List<Integer> deathCount = new ArrayList<>(); // odpowiadające liczby zgonów

        // Przechodzimy przez wszystkie statystyki
        for (DeathCauseStatistic stat : this.stats)
        {
            DeathCauseStatistic.AgeBracketDeaths bracket = stat.getAgeBracket(age);
            if (bracket != null)
            {
                vaildStats.add(stat);                 // dodajemy statystykę
                deathCount.add(bracket.deathCount);   // dodajemy liczbę zgonów w danym przedziale
            }
        }

        // BŁĄD: Pętla wewnętrzna nie działa poprawnie – `i++` zamiast `j++`
        for (int i = 0; i < deathCount.size() - 1; i++)
        {
            for (int j = i + 1; j < deathCount.size(); j++) // poprawka: `j++`, nie `i++`
            {
                // Sortujemy malejąco – największe liczby zgonów na początku
                if (deathCount.get(i) < deathCount.get(j))
                {
                    // Zamiana miejscami w tablicy zgonów
                    int temp = deathCount.get(i);
                    deathCount.set(i, deathCount.get(j));
                    deathCount.set(j, temp);

                    // Zamiana odpowiadających statystyk
                    DeathCauseStatistic tmp = vaildStats.get(i);
                    vaildStats.set(i, vaildStats.get(j));
                    vaildStats.set(j, tmp);
                }
            }
        }

        // Tworzymy listę n najgroźniejszych chorób
        List<DeathCauseStatistic> sortedStats = new ArrayList<>();

        for (int i = 0; i < n && i < vaildStats.size(); i++)
        {
            sortedStats.add(vaildStats.get(i));
        }

        return sortedStats;
    }
}