package model;

import Database.PetDatabase;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PetSorter implements IPetSorter {

    private final User user;
    private final ICompatibilityCalculator calculator;

    public PetSorter(User user, ICompatibilityCalculator calculator) {
        this.user = user;
        this.calculator = calculator;
    }

    @Override
    public void sortAndExportToCSV(List<Pet> pets, String outputCsvPath) {
        List<PetWithScore> scoredPets = new ArrayList<>();

        // Calculate compatibility score for each pet
        for (Pet pet : pets) {
            double score = calculator.calculate(user, pet);
            scoredPets.add(new PetWithScore(pet, score));
        }

        // Sort pets with custom tie-breaking logic
        scoredPets.sort(Comparator
                .comparing(PetWithScore::getScore).reversed() // Highest score first
                .thenComparing(this::genderMatchPriority)
                .thenComparing(this::mbtiMatchCount)
                .thenComparing(this::spaceRatio)
                .thenComparing(this::energyDiff)
                .thenComparing(this::timeRatio)
                .thenComparing(this::budgetRatio)
                .thenComparing(this::yardMatch)
        );

        // Write to CSV
        try (FileWriter writer = new FileWriter(outputCsvPath)) {
            writer.write("Name,Breed,Type,Score,ImagePath\n");
            for (PetWithScore p : scoredPets) {
                Pet pet = p.getPet();
                writer.write(String.format(
                        "%s,%s,%s,%s,%s\n",
                        pet.getName(),
                        pet.getBreed(),
                        pet.getType(),
                        formatPercentage(p.getScore()),
                        pet.getImagePath()
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to CSV: " + e.getMessage(), e);
        }
    }

    // Format the score as percentage (e.g., 85% instead of 0.85)
    private String formatPercentage(double score) {
        return String.format("%.0f%%", score * 100);
    }

    // Tie-breaking helpers
    private int genderMatchPriority(PetWithScore p) {
        String pref = user.getPreferredPetGender();
        if (pref.equalsIgnoreCase("Any")) return 0;
        return pref.equalsIgnoreCase(p.getPet().getGender()) ? -1 : 1;
    }

    private int mbtiMatchCount(PetWithScore p) {
        String userMBTI = user.getMbti();
        String petMBTI = p.getPet().getMbti();
        int match = 0;
        for (int i = 0; i < 4; i++) {
            if (i < userMBTI.length() && i < petMBTI.length() &&
                    Character.toUpperCase(userMBTI.charAt(i)) == Character.toUpperCase(petMBTI.charAt(i))) {
                match++;
            }
        }
        return -match; // more matches = higher priority
    }

    private double spaceRatio(PetWithScore p) {
        double space = user.getSpace();
        double required = p.getPet().getRequiredSpace();
        return -Math.min(space / required, 1.0); // higher space ratio preferred
    }

    private double energyDiff(PetWithScore p) {
        int userEnergy = user.getEnergyLevel();
        int petEnergy = p.getPet().getEnergyLevel();
        return Math.abs(userEnergy - petEnergy); // smaller diff is better
    }

    private double timeRatio(PetWithScore p) {
        double userTime = user.getTimePerDay();
        double petTime = p.getPet().getTimeNeededPerDay();
        return -Math.min(userTime / petTime, 1.0); // higher ratio is better
    }

    private double budgetRatio(PetWithScore p) {
        double userBudget = user.getBudget();
        double petCost = p.getPet().getMonthlyCost();
        return -Math.min(userBudget / petCost, 1.0); // higher ratio is better
    }

    private int yardMatch(PetWithScore p) {
        boolean hasYard = user.hasYard();
        boolean needsYard = p.getPet().requiresYard();
        return (!needsYard || hasYard) ? -1 : 1; // -1 if yard matches
    }
}
