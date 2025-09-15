package model;

import Database.PetDatabase;
import java.util.List;

public interface IPetSorter {
    /**
     * Sorts the given list of pets based on compatibility with the user.
     * The result is exported to a CSV file.
     *
     * @param pets the list of pets to sort
     * @param outputCsvPath the path to the CSV file to export
     */
    void sortAndExportToCSV(List<Pet> pets, String outputCsvPath);
}
