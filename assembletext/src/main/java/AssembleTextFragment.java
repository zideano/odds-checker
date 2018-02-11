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
                LOGGER.log(Level.INFO, "Assembled text");
                System.out.println(reassemble(fragmentProblem));
            }

        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
            e.printStackTrace();
        }
    }

    private String reassemble(final String fragmentProblem) {

        List<String> text = new ArrayList<>(Arrays.asList(fragmentProblem.split(";")));

        if (text.isEmpty()) {
            return "";
        }

        int maxOverlap = 0;
        String overlapping = "";

        return checkForAssembledString(text, maxOverlap, overlapping);
    }

    private String checkForAssembledString(List<String> text, int maxOverlap, String overlapping) {

        String referenceString = text.get(0);
        text.remove(referenceString);

        return iterateForStringOverlap(text, maxOverlap, overlapping, referenceString);
    }

    private String iterateForStringOverlap(List<String> text, int maxOverlap, String other, String referenceString) {

        while (text.size() > 0) {
            for (final String fragment : text) {

                if (referenceString.contains(fragment)) {
                    maxOverlap = fragment.length();
                    other = fragment;
                } else if (fragment.contains(referenceString)) {
                    maxOverlap = referenceString.length();
                    referenceString = fragment;
                    other = fragment;
                }

                int overlap = overlap(referenceString, fragment);
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
            referenceString = recombine(referenceString, other, maxOverlap);
            text.remove(other);
            maxOverlap = 0;
            other = "";
        }

        return referenceString;
    }

    private String recombine(String reference, String other, int overlap) {

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

    private int overlap(final String str1, final String str2) {
        int maxOverlap = str2.length() - 1;

        while (!str1.regionMatches(str1.length() - maxOverlap, str2, 0,
                maxOverlap)) {
            maxOverlap--;
        }

        return maxOverlap;
    }
}

