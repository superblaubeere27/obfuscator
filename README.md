# Obfuscator [![Build Status](https://travis-ci.org/superblaubeere27/obfuscator.svg?branch=master)](https://travis-ci.org/superblaubeere27/obfuscator) [![Join the chat at https://gitter.im/superblaubeere27/obfuscator](https://badges.gitter.im/superblaubeere27/obfuscator.svg)](https://gitter.im/superblaubeere27/obfuscator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
## Obfuscated code
Luyten + Procyon

Without
```Java

public class HelloWorld {
    public HelloWorld() {
        super();
    }
    
    public static void main(final String[] args) {
        System.out.println("Hello World");
        for (int i = 0; i < 10; ++i) {
            System.out.println(i);
        }
    }
}
```

Obfuscated
```Java
public class HelloWorld
{
    private static final /* synthetic */ String[] I;
    private static final /* synthetic */ int[] l;
    
    private static String I(final String s, final String s1) {
        final StringBuilder sb = new StringBuilder();
        final char[] key = s1.toCharArray();
        int i = HelloWorld.l[0];
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        int j = HelloWorld.l[0];
        while (j < length) {
            final char c = charArray[j];
            sb.append((char)(c ^ key[i % key.length]));
            ++i;
            ++j;
            "".length();
            if (0 < 0) {
                throw null;
            }
        }
        return sb.toString();
    }
    
    public static void main(final String[] args) {
        System.out.println(HelloWorld.I[HelloWorld.l[0]]);
        int i = HelloWorld.l[0];
        "".length();
        if (-1 != -1) {
            throw null;
        }
        while (i < HelloWorld.l[1]) {
            System.out.println(i);
            ++i;
        }
    }
    
    public HelloWorld() {
        super();
    }
    
    static {
        l();
        I();
    }
    
    private static void l() {
        (l = new int[3])[0] = "".length();
        HelloWorld.l[1] = (0x75 ^ 0x7F);
        HelloWorld.l[2] = " ".length();
    }
    
    private static void I() {
        (I = new String[HelloWorld.l[2]])[HelloWorld.l[0]] = I("\u0012\u0016!!\u001dz$\"?\u001e>", "ZsMMr");
    }
}

```

Aggressive
```Java
public class HelloWorld
{
    private static /* synthetic */ String[] l;
    private static final /* synthetic */ int[] lI;
    private static final /* synthetic */ String[] I;
    
    private static void lI() {
        (lI = new int[3])[0] = "".length();
        HelloWorld.lI[1] = (0xA ^ 0x0);
        HelloWorld.lI[2] = " ".length();
    }
    
    public HelloWorld() {
        super();
    }
    
    public static void main(final String[] args) {
        System.out.println(HelloWorld.I[HelloWorld.lI[0]]);
        int i = HelloWorld.lI[0];
        "".length();
        if (0 >= 2) {
            throw null;
        }
        while (i < HelloWorld.lI[1]) {
            System.out.println(i);
            ++i;
        }
    }
    
    static {
        lI();
        l();
        I();
    }
    
    private static String I(final String s, final String s1) {
        final StringBuilder sb = new StringBuilder();
        final char[] key = s1.toCharArray();
        int i = HelloWorld.lI[0];
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        int j = HelloWorld.lI[0];
        while (j < length) {
            final char c = charArray[j];
            sb.append((char)(c ^ key[i % key.length]));
            ++i;
            ++j;
            "".length();
            if (1 >= 2) {
                throw null;
            }
        }
        return sb.toString();
    }
    
    private static void I() {
        (I = new String[HelloWorld.lI[2]])[HelloWorld.lI[0]] = I(HelloWorld.l[HelloWorld.lI[0]], HelloWorld.l[HelloWorld.lI[2]]);
    }
    
    private static void l() {
        final String fileName = new Exception().getStackTrace()[HelloWorld.lI[0]].getFileName();
        HelloWorld.l = fileName.substring(fileName.indexOf("\u4590") + HelloWorld.lI[2], fileName.lastIndexOf("\u4592")).split("\u4591");
    }
}


```

Packaged
```Java
import java.lang.reflect.*;
import java.io.*;

public class lIllllIlIl extends ClassLoader
{
    private static final /* synthetic */ int[] ll;
    private static /* synthetic */ String[] lI;
    private static final /* synthetic */ String[] l;
    private static /* synthetic */ byte[] I;
    
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        try {
            final byte[] resource = I(new String(I(name.getBytes(lIllllIlIl.l[lIllllIlIl.ll[5]]), lIllllIlIl.I)));
            return this.defineClass(name, I(resource, lIllllIlIl.I), lIllllIlIl.ll[1], resource.length);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return super.findClass(name);
        }
    }
    
    public lIllllIlIl() {
        super();
    }
    
    private static void I() {
        (l = new String[lIllllIlIl.ll[6]])[lIllllIlIl.ll[1]] = I(lIllllIlIl.lI[lIllllIlIl.ll[1]], lIllllIlIl.lI[lIllllIlIl.ll[3]]);
        lIllllIlIl.l[lIllllIlIl.ll[3]] = I(lIllllIlIl.lI[lIllllIlIl.ll[5]], lIllllIlIl.lI[lIllllIlIl.ll[6]]);
        lIllllIlIl.l[lIllllIlIl.ll[5]] = I(lIllllIlIl.lI[lIllllIlIl.ll[7]], lIllllIlIl.lI[lIllllIlIl.ll[9]]);
    }
    
    static {
        lI();
        lI();
        I();
        final byte[] i = new byte[lIllllIlIl.ll[0]];
        i[lIllllIlIl.ll[1]] = (byte)lIllllIlIl.ll[2];
        i[lIllllIlIl.ll[3]] = (byte)lIllllIlIl.ll[4];
        i[lIllllIlIl.ll[5]] = (byte)lIllllIlIl.ll[6];
        i[lIllllIlIl.ll[6]] = (byte)lIllllIlIl.ll[2];
        i[lIllllIlIl.ll[7]] = (byte)lIllllIlIl.ll[8];
        i[lIllllIlIl.ll[9]] = (byte)lIllllIlIl.ll[10];
        i[lIllllIlIl.ll[11]] = (byte)lIllllIlIl.ll[12];
        i[lIllllIlIl.ll[13]] = (byte)lIllllIlIl.ll[14];
        i[lIllllIlIl.ll[15]] = (byte)lIllllIlIl.ll[16];
        i[lIllllIlIl.ll[17]] = (byte)lIllllIlIl.ll[18];
        i[lIllllIlIl.ll[19]] = (byte)lIllllIlIl.ll[20];
        i[lIllllIlIl.ll[21]] = (byte)lIllllIlIl.ll[22];
        i[lIllllIlIl.ll[23]] = (byte)lIllllIlIl.ll[24];
        i[lIllllIlIl.ll[25]] = (byte)lIllllIlIl.ll[26];
        i[lIllllIlIl.ll[27]] = (byte)lIllllIlIl.ll[28];
        i[lIllllIlIl.ll[29]] = (byte)lIllllIlIl.ll[30];
        i[lIllllIlIl.ll[31]] = (byte)lIllllIlIl.ll[29];
        i[lIllllIlIl.ll[32]] = (byte)lIllllIlIl.ll[33];
        i[lIllllIlIl.ll[34]] = (byte)lIllllIlIl.ll[35];
        i[lIllllIlIl.ll[12]] = (byte)lIllllIlIl.ll[36];
        i[lIllllIlIl.ll[37]] = (byte)lIllllIlIl.ll[38];
        i[lIllllIlIl.ll[39]] = (byte)lIllllIlIl.ll[40];
        i[lIllllIlIl.ll[41]] = (byte)lIllllIlIl.ll[20];
        i[lIllllIlIl.ll[42]] = (byte)lIllllIlIl.ll[43];
        i[lIllllIlIl.ll[44]] = (byte)lIllllIlIl.ll[45];
        i[lIllllIlIl.ll[46]] = (byte)lIllllIlIl.ll[47];
        i[lIllllIlIl.ll[14]] = (byte)lIllllIlIl.ll[48];
        i[lIllllIlIl.ll[49]] = (byte)lIllllIlIl.ll[50];
        i[lIllllIlIl.ll[51]] = (byte)lIllllIlIl.ll[38];
        i[lIllllIlIl.ll[45]] = (byte)lIllllIlIl.ll[45];
        i[lIllllIlIl.ll[52]] = (byte)lIllllIlIl.ll[34];
        i[lIllllIlIl.ll[53]] = (byte)lIllllIlIl.ll[54];
        i[lIllllIlIl.ll[55]] = (byte)lIllllIlIl.ll[7];
        i[lIllllIlIl.ll[43]] = (byte)lIllllIlIl.ll[56];
        i[lIllllIlIl.ll[57]] = (byte)lIllllIlIl.ll[8];
        i[lIllllIlIl.ll[58]] = (byte)lIllllIlIl.ll[57];
        i[lIllllIlIl.ll[28]] = (byte)lIllllIlIl.ll[26];
        i[lIllllIlIl.ll[59]] = (byte)lIllllIlIl.ll[37];
        i[lIllllIlIl.ll[60]] = (byte)lIllllIlIl.ll[33];
        i[lIllllIlIl.ll[10]] = (byte)lIllllIlIl.ll[22];
        i[lIllllIlIl.ll[61]] = (byte)lIllllIlIl.ll[37];
        lIllllIlIl.I = i;
    }
    
    public static void main(final String[] llIIIIlllllIll) {
        try {
            final ClassLoader llllIIlllIIlIIl = new lIllllIlIl();
            final Class<?> loadClass;
            final Class<?> lIIllIIIlIllIIl = loadClass = llllIIlllIIlIIl.loadClass(lIllllIlIl.l[lIllllIlIl.ll[1]]);
            final String s = lIllllIlIl.l[lIllllIlIl.ll[3]];
            final Class[] array = new Class[lIllllIlIl.ll[3]];
            array[lIllllIlIl.ll[1]] = String[].class;
            final Method method = loadClass.getMethod(s, (Class[])array);
            final Object o = null;
            final Object[] array2 = new Object[lIllllIlIl.ll[3]];
            array2[lIllllIlIl.ll[1]] = llIIIIlllllIll;
            method.invoke(o, array2);
            "".length();
            if (true != true) {
                throw null;
            }
        }
        catch (Exception llIlIIIIlIlIl) {
            llIlIIIIlIlIl.printStackTrace();
        }
    }
    
    private static void l() {
        final String fileName = new Exception().getStackTrace()[lIllllIlIl.ll[1]].getFileName();
        lIllllIlIl.lI = fileName.substring(fileName.indexOf("\u4590") + lIllllIlIl.ll[3], fileName.lastIndexOf("\u4592")).split("\u4591");
    }
    
    private static byte[] I(final String name) throws IOException {
        final InputStream is = lIllllIlIl.class.getResourceAsStream(name);
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final byte[] data = new byte[lIllllIlIl.ll[62]];
        "".length();
        if (3 != 3) {
            throw null;
        }
        int nRead;
        while ((nRead = is.read(data, lIllllIlIl.ll[1], data.length)) != lIllllIlIl.ll[63]) {
            buffer.write(data, lIllllIlIl.ll[1], nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
    
    private static byte[] I(final byte[] lIlllIIIIlIlII, final byte[] llIIlIIlllII) {
        final byte[] lIIlIlIlIIIlIl = new byte[lIlllIIIIlIlII.length];
        int llIIIIllIIIIl = lIllllIlIl.ll[1];
        "".length();
        if (-1 < -1) {
            throw null;
        }
        while (llIIIIllIIIIl < lIlllIIIIlIlII.length) {
            lIIlIlIlIIIlIl[llIIIIllIIIIl] = (byte)(lIlllIIIIlIlII[llIIIIllIIIIl] ^ llIIlIIlllII[llIIIIllIIIIl % llIIlIIlllII.length]);
            ++llIIIIllIIIIl;
        }
        return lIIlIlIlIIIlIl;
    }
    
    private static String I(final String s, final String s1) {
        final StringBuilder sb = new StringBuilder();
        final char[] key = s1.toCharArray();
        int i = lIllllIlIl.ll[1];
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        int j = lIllllIlIl.ll[1];
        while (j < length) {
            final char c = charArray[j];
            sb.append((char)(c ^ key[i % key.length]));
            ++i;
            ++j;
            "".length();
            if (1 < 0) {
                throw null;
            }
        }
        return sb.toString();
    }
    
    private static void lI() {
        (ll = new int[64])[0] = (0x5A ^ 0x73);
        lIllllIlIl.ll[1] = "".length();
        lIllllIlIl.ll[2] = (0x4F ^ 0x2D);
        lIllllIlIl.ll[3] = " ".length();
        lIllllIlIl.ll[4] = (0x73 ^ 0x5);
        lIllllIlIl.ll[5] = "  ".length();
        lIllllIlIl.ll[6] = "   ".length();
        lIllllIlIl.ll[7] = (0x6E ^ 0x6A);
        lIllllIlIl.ll[8] = (0x54 ^ 0x25);
        lIllllIlIl.ll[9] = (0xE ^ 0xB);
        lIllllIlIl.ll[10] = (0x92 ^ 0xB5);
        lIllllIlIl.ll[11] = (0x31 ^ 0x37);
        lIllllIlIl.ll[12] = (0x37 ^ 0x24);
        lIllllIlIl.ll[13] = (0x2B ^ 0x2C);
        lIllllIlIl.ll[14] = (0x32 ^ 0x28);
        lIllllIlIl.ll[15] = (0xA7 ^ 0xAF);
        lIllllIlIl.ll[16] = (0xFB ^ 0xAA);
        lIllllIlIl.ll[17] = (0xCC ^ 0xC5);
        lIllllIlIl.ll[18] = (0x4C ^ 0x31);
        lIllllIlIl.ll[19] = (0xAD ^ 0xA7);
        lIllllIlIl.ll[20] = (0x9A ^ 0xC6);
        lIllllIlIl.ll[21] = (0x1C ^ 0x17);
        lIllllIlIl.ll[22] = (0xCD ^ 0x82);
        lIllllIlIl.ll[23] = (0x65 ^ 0x69);
        lIllllIlIl.ll[24] = (0x96 ^ 0xC0);
        lIllllIlIl.ll[25] = (0x32 ^ 0x3F);
        lIllllIlIl.ll[26] = (0x73 ^ 0x27);
        lIllllIlIl.ll[27] = (0x61 ^ 0x6F);
        lIllllIlIl.ll[28] = (0x25 ^ 0x1);
        lIllllIlIl.ll[29] = (0x5E ^ 0x51);
        lIllllIlIl.ll[30] = (0x54 ^ 0x2C);
        lIllllIlIl.ll[31] = (0xD3 ^ 0xC3);
        lIllllIlIl.ll[32] = (0xBC ^ 0xAD);
        lIllllIlIl.ll[33] = (0x39 ^ 0x47);
        lIllllIlIl.ll[34] = (0xA1 ^ 0xB3);
        lIllllIlIl.ll[35] = (0xE2 ^ 0x89);
        lIllllIlIl.ll[36] = (0x22 ^ 0x4E);
        lIllllIlIl.ll[37] = (0x86 ^ 0x92);
        lIllllIlIl.ll[38] = (0x5F ^ 0x1E);
        lIllllIlIl.ll[39] = (0x59 ^ 0x4C);
        lIllllIlIl.ll[40] = (0x4E ^ 0x7E);
        lIllllIlIl.ll[41] = (0x3A ^ 0x2C);
        lIllllIlIl.ll[42] = (0x8A ^ 0x9D);
        lIllllIlIl.ll[43] = (0x5C ^ 0x7D);
        lIllllIlIl.ll[44] = (0x11 ^ 0x9);
        lIllllIlIl.ll[45] = (0x91 ^ 0x8C);
        lIllllIlIl.ll[46] = (0xDC ^ 0xC5);
        lIllllIlIl.ll[47] = (0xA6 ^ 0x9B);
        lIllllIlIl.ll[48] = (0xE0 ^ 0xAC);
        lIllllIlIl.ll[49] = (0x1B ^ 0x0);
        lIllllIlIl.ll[50] = (0xDE ^ 0x9E);
        lIllllIlIl.ll[51] = (0xA8 ^ 0xB4);
        lIllllIlIl.ll[52] = (0x10 ^ 0xE);
        lIllllIlIl.ll[53] = (0x56 ^ 0x49);
        lIllllIlIl.ll[54] = (0x2D ^ 0x66);
        lIllllIlIl.ll[55] = (0x89 ^ 0xA9);
        lIllllIlIl.ll[56] = (0x68 ^ 0x51);
        lIllllIlIl.ll[57] = (0x6A ^ 0x48);
        lIllllIlIl.ll[58] = (0x94 ^ 0xB7);
        lIllllIlIl.ll[59] = (0x6A ^ 0x4F);
        lIllllIlIl.ll[60] = (0x3 ^ 0x25);
        lIllllIlIl.ll[61] = (0xEE ^ 0xC6);
        lIllllIlIl.ll[62] = 6271 + 1742 - 5272 + 13643;
        lIllllIlIl.ll[63] = -" ".length();
    }
}

```

## Usage

`--help` Prints the help page on the screen

`--version` Shows the version of the obfuscator

`--jarIn <input>` Input JAR

`--jarOut <output>` Output JAR

`--package` Encrypts every class

`--packagerMainClass <main-class>` Required if --package is specified. Specify the MainClass which the packager should execute

`--mode <mode>` 0 = Normal, 1 = Aggressive (Might not work)

`--log <file>` Log file

### Examples
`java -jar obfuscator.jar --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`

`java -jar obfuscator.jar --mode 1 --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`
Aggressive

`java -jar obfuscator.jar --package --packagerMainClass HelloWorld --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`
Packager

`java -jar obfuscator.jar --mode 1 --package --packagerMainClass HelloWorld --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`
Aggresive + Packager

## NameObfuscation

USE PROGUARD!!!

## Credits
- MCInjector (FFixer base)
- FFixer (Obfuscator base)
