import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssembleTextFragment {
    private static final Logger LOGGER = Logger.getLogger(AssembleTextFragment.class.getName());

    AssembleTextFragment() {
    }

    public void assembledText(String filename) {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String fragmentProblem;

            while ((fragmentProblem = in.readLine()) != null) {
                LOGGER.log(Level.INFO, "Assembled text");
                System.out.println(reassemble(fragmentProblem));
            }

        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
            e.printStackTrace();
        }
    }

    private String reassemble(String fragmentProblem) {
        int maxOverlap = 0;
        String other = "";
        int iterations = 0;

        // Break string fragment into a list<String>
        List<String> text = new ArrayList<>(Arrays.asList(fragmentProblem.split(";")));

        /*
            Check if we have data in the arraylist
            else return a empty string
          */
        if (text.isEmpty()) {
            return "";
        }

        return checkForAssembledString(maxOverlap, other, iterations, text);
    }

    private String checkForAssembledString(int maxOverlap, String other, int iterations, List<String> text) {

        /*
            Get first string from the list, then
            remove this from the remaining list
         */
        String referenceString = text.get(0);
        text.remove(referenceString);

        return iterateStringForOverlap(maxOverlap, other, iterations, text, referenceString);
    }

    private String iterateStringForOverlap(int maxOverlap, String other, int iterations, List<String> text, String referenceString) {
        while (text.size() > 0 && iterations < text.size()) {

            for (String fragment : text) {

                /*
                    Check whether the fragment contains the string
                    in both the forward and reverse direction
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
                    Check for an overlap between the reference
                    string and the other strings inside the text:
                    reference + other strings
                 */
                int overlapFound = overlap(referenceString, fragment);
                if (overlapFound > maxOverlap) {
                    maxOverlap = overlapFound;
                    other = fragment;
                }

                /*
                    Check for the overlap between the reverse:
                    other strings + reference string
                 */
                overlapFound = overlap(fragment, referenceString);
                if (overlapFound > maxOverlap) {
                    maxOverlap = overlapFound;
                    other = fragment;
                }

            }

            referenceString = assembledString(referenceString, other, maxOverlap);
            text.remove(other);
            maxOverlap = 0;
            other = "";
            iterations++;
        }
        return referenceString;
    }

    private String assembledString(String referenceString, String other, int maxOverlap) {

        boolean forwardMatch = referenceString.substring((referenceString.length() - maxOverlap), referenceString.length())
                .equals(other.substring(0, maxOverlap));

        if (forwardMatch) {
            other = other.substring(maxOverlap, other.length());
            return referenceString + other;
        } else {
            boolean reverseMatch = other.substring((other.length() - maxOverlap), other.length())
                    .equals(referenceString.substring(0, maxOverlap));

            if (reverseMatch) {
                referenceString = referenceString.substring(maxOverlap, referenceString.length());
                return other + referenceString;
            } else {
                return referenceString;
            }
        }
    }

    private int overlap(final String referenceString, final String other) {

        int maxOverlap = other.length() - 1;

        /*
            Find the longest (maximum) match by starting from
            the longest none match to the final maximum match
         */
        while (!referenceString.regionMatches(true,referenceString.length() - maxOverlap, other, 0,maxOverlap)) {
            maxOverlap--;
        }

        return maxOverlap;
    }
}
