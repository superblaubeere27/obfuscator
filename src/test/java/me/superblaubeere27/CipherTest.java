package me.superblaubeere27;

import me.superblaubeere27.jobf.processors.encryption.string.AESEncryptionAlgorithm;
import me.superblaubeere27.jobf.processors.encryption.string.BlowfishEncryptionAlgorithm;
import me.superblaubeere27.jobf.processors.encryption.string.DESEncryptionAlgorithm;
import me.superblaubeere27.jobf.processors.encryption.string.XOREncryptionAlgorithm;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CipherTest {

    @Test
    public void testDES() {
        String encrypt = "Encryption works.";
        String key = "123456 is a safe key";

        DESEncryptionAlgorithm algorithm = new DESEncryptionAlgorithm();

        String enc = algorithm.encrypt(encrypt, key);


        assertEquals(encrypt, DESEncryptionAlgorithm.decrypt(enc, key));
    }


    @Test
    public void testXOR() {
        String encrypt = "Encryption works.";
        String key = "123456 is a safe key";

        XOREncryptionAlgorithm algorithm = new XOREncryptionAlgorithm();

        String enc = algorithm.encrypt(encrypt, key);


        assertEquals(encrypt, XOREncryptionAlgorithm.decrypt(enc, key));
    }

    @Test
    public void testAES() {
        String encrypt = "Encryption works.";
        String key = "123456 is a safe key";

        AESEncryptionAlgorithm algorithm = new AESEncryptionAlgorithm();

        String enc = algorithm.encrypt(encrypt, key);

        assertEquals(encrypt, AESEncryptionAlgorithm.decrypt(enc, key));
    }

    @Test
    public void testBlowfish() {
        String encrypt = "Encryption works.";
        String key = "123456 is a safe key";

        BlowfishEncryptionAlgorithm algorithm = new BlowfishEncryptionAlgorithm();

        String enc = algorithm.encrypt(encrypt, key);

        assertEquals(encrypt, BlowfishEncryptionAlgorithm.decrypt(enc, key));
    }

}
