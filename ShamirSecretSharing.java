import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    // Function to decode the value based on the given base
    public static BigInteger decodeValue(String value, int base) {
        return new BigInteger(value, base);  // Use BigInteger for large numbers
    }

    // Function to perform Lagrange interpolation and calculate the constant term
    public static BigInteger lagrangeInterpolation(List<BigInteger[]> points, int k) {
        BigInteger constantTerm = BigInteger.ZERO;
        BigInteger mod = BigInteger.valueOf(Long.MAX_VALUE);  // You can adjust this mod as per the requirements

        // Iterate through each point to calculate Lagrange basis polynomials
        for (int i = 0; i < k; i++) {
            BigInteger xi = points.get(i)[0];
            BigInteger yi = points.get(i)[1];

            BigInteger basis = BigInteger.ONE;

            // Compute the basis polynomial for this point
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = points.get(j)[0];

                    // Numerator: 0 - xj = -xj
                    BigInteger numerator = BigInteger.ZERO.subtract(xj);

                    // Denominator: xi - xj
                    BigInteger denominator = xi.subtract(xj);

                    // Modular inverse of denominator
                    BigInteger denominatorInv = denominator.modInverse(mod);

                    // Basis term = basis * (numerator * denominatorInv) % mod
                    basis = basis.multiply(numerator).multiply(denominatorInv).mod(mod);
                }
            }

            // Add the contribution of the current point to the constant term
            constantTerm = constantTerm.add(yi.multiply(basis)).mod(mod);
        }

        return constantTerm.mod(mod);  // Return result mod M
    }

    // Function to parse input string and extract the values
    public static void parseInput(String jsonInput, List<BigInteger[]> points) {
        String[] lines = jsonInput.split("\n");
        int n = 0, k = 0;

        // Iterate over the input lines
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // Extract n and k values
            if (line.contains("\"n\"")) {
                n = Integer.parseInt(line.replaceAll("[^0-9]", ""));
            } else if (line.contains("\"k\"")) {
                k = Integer.parseInt(line.replaceAll("[^0-9]", ""));
            }

            // Extract x, base, and value pairs
            else if (line.matches("\"\\d+\": \\{")) {
                int x = Integer.parseInt(line.replaceAll("[^0-9]", ""));  // Extract x value

                // Find the base and value in subsequent lines
                String baseLine = lines[++i].trim();  // Go to the next line for base
                int base = Integer.parseInt(baseLine.replaceAll("[^0-9]", ""));  // Extract base

                String valueLine = lines[++i].trim();  // Go to the next line for value
                String value = valueLine.split(":")[1].replaceAll("[^0-9A-Fa-f]", "");  // Extract value

                // Decode value and add the point
                BigInteger y = decodeValue(value, base);
                points.add(new BigInteger[]{BigInteger.valueOf(x), y});
            }
        }
    }

    public static void main(String[] args) {
        // First test case input
        String jsonInput1 = """
        {
            "keys": {
                "n": 4,
                "k": 3
            },
            "1": {
                "base": "10",
                "value": "4"
            },
            "2": {
                "base": "2",
                "value": "111"
            },
            "3": {
                "base": "10",
                "value": "12"
            },
            "6": {
                "base": "4",
                "value": "213"
            }
        }
        """;

        // Second test case input
        String jsonInput2 = """
        {
            "keys": {
                "n": 9,
                "k": 6
            },
            "1": {
                "base": "10",
                "value": "28735619723837"
            },
            "2": {
                "base": "16",
                "value": "1A228867F0CA"
            },
            "3": {
                "base": "12",
                "value": "32811A4AA0B7B"
            },
            "4": {
                "base": "11",
                "value": "917978721331A"
            },
            "5": {
                "base": "16",
                "value": "1A22886782E1"
            },
            "6": {
                "base": "10",
                "value": "28735619654702"
            },
            "7": {
                "base": "14",
                "value": "71AB5070CC4B"
            },
            "8": {
                "base": "9",
                "value": "122662581541670"
            },
            "9": {
                "base": "8",
                "value": "642121030037605"
            }
        }
        """;

        // Initialize a list to store the points for both test cases
        List<BigInteger[]> points1 = new ArrayList<>();
        List<BigInteger[]> points2 = new ArrayList<>();

        // Parse both inputs
        parseInput(jsonInput1, points1);
        parseInput(jsonInput2, points2);

        // Perform Lagrange interpolation for both cases
        BigInteger constantTerm1 = lagrangeInterpolation(points1, 3);
        BigInteger constantTerm2 = lagrangeInterpolation(points2, 6);

        // Print the results
        System.out.println("Constant term (Test Case 1): " + constantTerm1);
        System.out.println("Constant term (Test Case 2): " + constantTerm2);
    }
}
