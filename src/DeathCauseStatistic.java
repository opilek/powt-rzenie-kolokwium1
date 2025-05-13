import java.io.BufferedReader;
import java.io.FileReader;

// Klasa reprezentująca statystyki przyczyn zgonów wg kodu ICD-10 i przedziałów wiekowych
public class DeathCauseStatistic
{
    // Kod ICD-10 opisujący przyczynę zgonu
    private String icd10Code;

    // Tablica przechowująca liczbę zgonów w różnych przedziałach wiekowych
    private int[] deathsByAgeGroup;

    // Getter zwracający kod ICD-10
    public String getIcd10Code() { return icd10Code; }

    // Getter zwracający tablicę zgonów w przedziałach wiekowych
    public int[] getDeathsByAgeGroup() { return deathsByAgeGroup; }

    // Konstruktor klasy przypisujący kod ICD-10 i dane o zgonach
    public DeathCauseStatistic(String icd10Code, int[] deathsByAgeGroup)
    {
        this.icd10Code = icd10Code;
        this.deathsByAgeGroup = deathsByAgeGroup;
    }

    // Wewnętrzna klasa reprezentująca zgony w konkretnym przedziale wiekowym
    public class AgeBracketDeaths
    {
        public final int young;      // dolna granica wieku
        public final int old;        // górna granica wieku
        public final int deathCount; // liczba zgonów w tym przedziale

        public AgeBracketDeaths(int young, int old, int deathCount)
        {
            this.young = young;
            this.old = old;
            this.deathCount = deathCount;
        }

        // Zwraca tekstową reprezentację obiektu (np. do wypisania)
        @Override
        public String toString()
        {
            return String.format("Wiek: %d-%d, Zgony: %d\n", young, old, deathCount);
        }
    }

    // Statyczna tablica przedziałów wiekowych – odpowiada indeksom w tabeli danych
    private static final int[][] AGE_BRACKETS = {
            {0, 4}, {5, 9}, {10, 14}, {15, 19}, {20, 24},
            {25, 29}, {30, 34}, {35, 39}, {40, 44}, {45, 49},
            {50, 54}, {55, 59}, {60, 64}, {65, 69}, {70, 74},
            {75, 79}, {80, 84}, {85, 89}, {90, 94}, {95, 200}
    };

    // Metoda tworząca obiekt klasy z jednej linii pliku CSV
    public static DeathCauseStatistic fromCsvLine(String line)
    {
        // Dzielimy linię po przecinkach
        String[] parts = line.split(",");

        // Pierwszy element to kod ICD-10
        String icd10Code = parts[0].trim();

        // Inicjalizacja tablicy zgonów dla każdej grupy wiekowej
        int[] deathsByAgeGroup = new int[parts.length - 1];

        // Iteracja po pozostałych elementach (zgony)
        for (int i = 1; i < parts.length; i++)
        {
            // Jeśli jest "-", to traktujemy jako 0
            if (parts[i].equals("-"))
            {
                deathsByAgeGroup[i - 1] = 0;
            }
            else
            {
                // W przeciwnym razie parsujemy liczbę całkowitą
                deathsByAgeGroup[i - 1] = Integer.parseInt(parts[i]);
            }
        }

        // Zwracamy nowy obiekt z wczytanymi danymi
        return new DeathCauseStatistic(icd10Code, deathsByAgeGroup);
    }

    // Zwraca dane o zgonach w odpowiednim przedziale wiekowym dla podanego wieku
    public AgeBracketDeaths getAgeBracket(int age)
    {
        // Szukamy przedziału wiekowego, do którego pasuje wiek
        for (int i = 0; i < AGE_BRACKETS.length; i++)
        {
            int[] brackets = AGE_BRACKETS[i];

            // Jeśli wiek mieści się w danym przedziale
            if (age >= brackets[0] && age <= brackets[1])
            {
                // Zwracamy nowy obiekt z danymi o zgonach w tym przedziale
                return new AgeBracketDeaths(brackets[0], brackets[1], this.deathsByAgeGroup[i]);
            }
        }

        // Jeśli wiek nie pasuje do żadnego przedziału – zwracamy null
        return null;
    }
}