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

`--config <configFile>` Config File

`--cp <classPath>` Class Path

`--scriptFile <scriptFile>` A JS file to script certain parts of the obfuscation

`--threads` Sets the number of threads the obfuscator should use

`--verbose` Sets logging to verbose mode

### Examples

`java -jar obfuscator.jar --jarIn helloWorld.jar --jarOut helloWorld-obf.jar`

`java -jar obfuscator.jar --jarIn helloWorld.jar --jarOut helloWorld-obf.jar --config obfConfig`

### Example Config

```
{
  "input": "D:\\Computing\\HelloWorld\\out\\artifacts\\HelloWorld_jar\\HelloWorld.jar",
  "output": "D:\\Computing\\HelloWorld\\out\\artifacts\\HelloWorld_jar\\HelloWorld-obf.jar",
  "script": "function isRemappingEnabledForClass(node) {\n    return true;\n}\nfunction isObfuscatorEnabledForClass(node) {\n    return true;\n}",
  "libraries": [
    "C:\\Program Files\\Java\\jre1.8.0_211\\lib",
    "D:\\Computing\\backdoored_old\\dependencies",
    "D:\\Computing\\backdoored\\libs"
  ],
  "Crasher": {
    "Enabled": false,
    "Invalid Signatures": true,
    "Empty annotation spam": true
  },
  "InvokeDynamic": {
    "Enabled": true
  },
  "HWIDPRotection": {
    "Enabled": false,
    "HWID": ""
  },
  "Optimizer": {
    "Enabled": true,
    "Replace String.equals()": true,
    "Replace String.equalsIgnoreCase()": true,
    "Optimize static string calls": true
  },
  "LineNumberRemover": {
    "Enabled": true,
    "Rename local variables": true,
    "Remove Line Numbers": true,
    "Remove Debug Names": true,
    "Add Local Variables": true,
    "New SourceFile Name": ""
  },
  "StringEncryption": {
    "Enabled": true,
    "HideStrings": true,
    "AES": true
  },
  "NumberObfuscation": {
    "Enabled": true,
    "Extract to Array": true,
    "Obfuscate Zero": true,
    "Shift": false,
    "And": false,
    "Multiple Instructions": true
  },
  "ReferenceProxy": {
    "Enabled": false
  },
  "ShuffleMembers": {
    "Enabled": true
  },
  "InnerClassRemover": {
    "Enabled": true,
    "Remap": true,
    "Remove Metadata": true
  },
  "NameObfuscation": {
    "Enabled": true,
    "Excluded classes": "HelloWorld",
    "Excluded methods": "",
    "Excluded fields": ""
  },
  "General Settings": {
    "Custom dictionary": true,
    "Name dictionary": "hello,world"
  },
  "Packager": {
    "Enabled": false,
    "Use MainClass from the JAR manifest": true,
    "Main class": "HelloWorld"
  },
  "FlowObfuscator": {
    "Enabled": true,
    "Mangle Comparisons": true,
    "Replace GOTO": true,
    "Replace If": true,
    "Bad POP": true,
    "Bad Concat": true,
    "Mangle Switches": false,
    "Mangle Return": false,
    "Mangle Local Variables": false
  },
  "HideMembers": {
    "Enabled": true
  },
  "Inlining": {
    "Enabled": false
  }
}
```

## Credits
- MCInjector (FFixer base)
- FFixer (Obfuscator base)
- SmokeObfuscator (Some ideas)
- MarcoMC (Some ideas)
- ItzSomebody (NameUtils.crazyString(), Crasher)
