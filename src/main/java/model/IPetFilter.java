package model;

import Database.PetDatabase;
import java.util.List;

/**
 * Interface for filtering pets based on compatibility score.
 * This interface provides a method to filter pets with compatibility scores above 85%.
 */
public interface IPetFilter {

    /**
     * Filters the pets based on their compatibility score.
     * Only pets with a compatibility score higher than 80% will be included.
     *
     * @param pets the list of pets to be filtered.
     * @return a list of pets with compatibility scores over 85%.
     */
    List<Pet> filterPetsByCompatibility(List<Pet> pets);
}

