package pw.kanavis.forgelogin.auth;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Authentication data provider
 */
public class AuthProvider {

    public static final Path PATH = Paths.get("forgelogin_data/auth");

    // Constructor
    public AuthProvider() {
        // Ensure directory existence
        File dir = new File(PATH.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Check auth
    public synchronized AuthResult checkAuth(String login, String password)
            throws IOException, NoSuchAlgorithmException {
        // Resolve login file
        login = login.toLowerCase();
        Path loginFilePath = PATH.resolve(login.concat(".data"));
        File loginFile = loginFilePath.toFile();

        // Read login file and check for password hash
        BufferedReader loginFileReader = null;
        try {
            loginFileReader = new BufferedReader(new FileReader(loginFile));
        } catch (FileNotFoundException e) {
            return new AuthResult(false, false, false, "");
        }
        String hashHex = loginFileReader.readLine();
        if (hashHex == null) {
            // Initial state: empty file
            return new AuthResult(true, false, true, "");
        }

        // Check password hash
        hashHex = hashHex.trim();
        byte[] providedHash = MessageDigest.getInstance("SHA-256").digest(password.getBytes());
        String providedHashHex = String.format("%064x", new BigInteger(1, providedHash));
        String debugStr = String.format("got \"%s\" expect \"%s\"", providedHashHex, hashHex);
        return new AuthResult(true, providedHashHex.equals(hashHex),false, debugStr);
    }

    // Set password
    public synchronized void setPassword(String login, String password) throws IOException, NoSuchAlgorithmException {
        // Resolve login file
        login = login.toLowerCase();
        Path loginFilePath = PATH.resolve(login.concat(".data"));
        File loginFile = loginFilePath.toFile();

        // Compute password hash
        byte[] providedHash = MessageDigest.getInstance("SHA-256").digest(password.getBytes());
        String providedHashHex = String.format("%064x", new BigInteger(1, providedHash));


        // Write to login file
        BufferedWriter loginFileWriter = new BufferedWriter(new FileWriter(loginFile));
        loginFileWriter.write(providedHashHex);
        loginFileWriter.close();
    }

    // Auth result class
    public class AuthResult {

        private boolean exists;
        private boolean ok;
        private boolean initial;
        private String debug;

        public AuthResult(boolean exists, boolean ok, boolean initial, String debug) {
            this.exists = exists;
            this.ok = ok;
            this.initial = initial;
            this.debug = debug;
        }

        public boolean isOk() {
            return this.ok;
        }

        public boolean isInitial() {
            return this.initial;
        }

        public boolean exists() {
            return this.exists;
        }

        public String getDebug() {
            return this.debug;
        }
    }

}
