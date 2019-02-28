# Obfuscator [![Build Status](https://travis-ci.org/superblaubeere27/obfuscator.svg?branch=master)](https://travis-ci.org/superblaubeere27/obfuscator) [![Join the chat at https://gitter.im/superblaubeere27/obfuscator](https://badges.gitter.im/superblaubeere27/obfuscator.svg)](https://gitter.im/superblaubeere27/obfuscator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Donate](https://img.shields.io/badge/patreon-donate-green.svg)](http://patreon.com/superblaubeere27)

Feel free to join my discord server: [![Discord Chat](https://img.shields.io/discord/468320443682521089.svg)](https://discord.gg/zQmAChK)  


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

Obfuscated (short version for full code visit https://pastebin.com/RFHtgPtX)
```Java

public class HelloWorld {
    
    public static void main(final String[] array) {
        // invokedynamic(1:(Ljava/io/PrintStream;Ljava/lang/String;)V, invokedynamic(0:()Ljava/io/PrintStream;), HelloWorld.llII[HelloWorld.lllI[0]])
        float lllllllIlIllIII = HelloWorld.lllI[0];
        while (llIll((int)lllllllIlIllIII, HelloWorld.lllI[1])) {
            // invokedynamic(2:(Ljava/io/PrintStream;I)V, invokedynamic(0:()Ljava/io/PrintStream;), lllllllIlIllIII)
            ++lllllllIlIllIII;
            "".length();
            if (" ".length() == (" ".length() << ("   ".length() << " ".length()) & ~(" ".length() << ("   ".length() << " ".length())))) {
                throw null;
            }
        }
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

`--hwid [<hwid>]` Enabled Hardware ID bound. If `hwid` isn't provided it will use the HWID of your system.

### Examples
`java -jar obfuscator.jar --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`

`java -jar obfuscator.jar --mode 1 --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`
Aggressive

`java -jar obfuscator.jar --package --packagerMainClass HelloWorld --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`
Packager

`java -jar obfuscator.jar --hwid 5614147245AD3553FE5B7FE0259BC886 --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`
HWID bound

`java -jar obfuscator.jar --package --packagerMainClass HelloWorld --hwid 5614147245AD3553FE5B7FE0259BC886 --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`
HWID bound + Packager

`java -jar obfuscator.jar --mode 1 --package --packagerMainClass HelloWorld --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`
Aggresive + Packager

## NameObfuscation

USE PROGUARD!!!

## Credits
- MCInjector (FFixer base)
- FFixer (Obfuscator base)
- SmokeObfuscator (Some ideas)
- MarcoMC (Some ideas)
- ItzSomebody (NameUtils.crazyString(), Crasher)
