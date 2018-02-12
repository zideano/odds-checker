import com.sun.istack.internal.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssembleTextFragment {
    private static final Logger LOGGER = Logger.getLogger(AssembleTextFragment.class.getName());

    public void assembledText(String filename) {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String fragmentProblem;

            while ((fragmentProblem = in.readLine()) != null) {
                System.out.println(reassemble(fragmentProblem));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param fragmentProblem is the text body that we will reassemble
     * @return the assembled string
     */
    private String reassemble(final @NotNull String fragmentProblem) {
        int maxOverlap = 0;
        String other = "";

        List<String> text = new ArrayList<>(Arrays.asList(fragmentProblem.split(";")));

        // Return a empty string if the list is empty
        if (text.isEmpty()) {
            return "";
        }

        return checkForAssembledString(text, maxOverlap, other);
    }

    /**
     * @param text list of text fragments to be compared for overlap
     * @param maxOverlap is the maximum overlap between the reference and other text
     * @param other the other text fragment to compare to the reference string
     * @return the assembled string
     */
    private String checkForAssembledString(@NotNull List<String> text, int maxOverlap, String other) {

        /*
            Get reference string and update the list
         */
        String referenceString = text.get(0);
        text.remove(referenceString);

        return iterateForStringOverlap(text, maxOverlap, other, referenceString);
    }

    /**
     * @param text list of text fragments to be compared for overlap
     * @param maxOverlap is the maximum overlap between the reference and other text
     * @param other the other text fragment to compare to the reference string
     * @param referenceString the starting string to be compared
     * @return the assembled string
     */
    private String iterateForStringOverlap(@NotNull List<String> text, int maxOverlap, String other, String referenceString) {

        while (text.size() > 0) {
            for (String fragment : text) {
                int overlap;

                /*
                    First check if the reference string is already composed of the
                    other text fragments in both the forward and reverse directions
                 */
                if (referenceString.contains(fragment)) {
                    maxOverlap = fragment.length();
                    other = fragment;
                } else if (fragment.contains(referenceString)) {
                    maxOverlap = referenceString.length();
                    referenceString = fragment;
                    other = fragment;
                }

                /*
                    Check for overlap between the reference string and the text
                    fragments in both the forward and reverse directions
                 */
                overlap = overlap(referenceString, fragment);
                if (overlap > maxOverlap) {
                    maxOverlap = overlap;
                    other = fragment;
                }

                overlap = overlap(fragment, referenceString);
                if (overlap > maxOverlap) {
                    maxOverlap = overlap;
                    other = fragment;
                }

            }

            /*
               Get updated reference string and update the list again
            */
            referenceString = concatenateString(referenceString, other, maxOverlap);
            text.remove(other);
            maxOverlap = 0;
            other = "";
        }

        return referenceString;
    }

    /**
     * @param reference starting string to be compared with
     * @param other the other strings to be compared against reference string
     * @param overlap find the maximum overlap between reference & other
     * @return concatenated string: reference + other or just reference when no overlap
     */
    private String concatenateString(@NotNull String reference, @NotNull String other, int overlap) {

        boolean forwardMatch = reference.substring((reference.length() - overlap), reference.length())
                .equals(other.substring(0, overlap));
        boolean reverseMatch = other.substring((other.length() - overlap), other.length())
                .equals(reference.substring(0, overlap));

        if (forwardMatch) {
            other = other.substring(overlap, other.length());
            return reference + other;
        } else if (reverseMatch) {
            reference = reference.substring(overlap, reference.length());
            return other + reference;
        } else {
            return reference;
        }
    }

    /**
     * @param referenceString the starting string to be compared
     * @param other the string to compare the reference string against
     * @return the maximum overlap between the reference string and other
     */
    private int overlap(final @NotNull String referenceString, final @NotNull String other) {
        int maxOverlap = other.length() - 1;

        /*
            Find the region of maximum overlap or reduce maxOverlap
            to zero otherwise when there is no overlap between reference
            and other.
         */
        while (!referenceString.regionMatches(true,referenceString.length() - maxOverlap, other, 0,
                maxOverlap)) {
            maxOverlap--;
        }

        return maxOverlap;
    }
}

