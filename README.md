# Obfuscator [![Build Status](https://travis-ci.org/superblaubeere27/obfuscator.svg?branch=master)](https://travis-ci.org/superblaubeere27/obfuscator) [![Join the chat at https://gitter.im/superblaubeere27/obfuscator](https://badges.gitter.im/superblaubeere27/obfuscator.svg)](https://gitter.im/superblaubeere27/obfuscator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Donate](https://img.shields.io/badge/patreon-donate-green.svg)](http://patreon.com/superblaubeere27)

A Java bytecode obfuscator supporting
* Flow Obfuscation
* Line Number Removal
* Number Obfuscation
* Optimisation
* Name Obfuscation (Classes, methods and fields) with custom dictionaries
* Deobfuscator crasher
* String Encryption
* Inner Class Removal
* HWID Locking
* Invoke Dynamic
* Reference Proxy
* Member Shuffling & Hiding

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

### Excluding Classes

In some situations you need to prevent certain classes from being obfuscated, such as dependencies packaged with your jar or mixins in a forge mod.

You will need to exclude in two places.

##### Scripting Tab

Here is an example script that will obfuscate and remap all classes except the org.json dependency and mixins.
```javascript
function isRemappingEnabledForClass(node) {
    var flag1 = !node.name.startsWith("org/json");
    var flag2 = !node.name.startsWith("com/client/mixin");
    return flag1 && flag2;
}
function isObfuscatorEnabledForClass(node) {
    var flag1 = !node.name.startsWith("org/json");
    var flag2 = !node.name.startsWith("com/client/mixin");
    return flag1 && flag2;
}
```

##### Name Obfuscation

If you also want to exclude these classes from name obfuscation you will need to go to Transformers -> Name Obfuscation and add these exclusions there.

To Exclude the same classes as we did above, we would need to add the following to Excluded classes, methods and fields.
```regexp
org.json.**
com.client.mixin.**
```


If your classes are still being obfuscated after applyinng both of these exclusions please open an issue.

## Contributing

##### 1. Fork the repository

- Click the "Fork" button at the top right hand corner of this page
- Then run `git clone https://github.com/[your github username]/obfuscator.git`

##### 2. Import into IntelliJ

- File -> New -> Project From Existing Sources
- Select `C:\[Path To]\obfuscator\pom.xml`
- Set "Search for projects recursively" and "Import Maven projects automatically" to true and click next
- Make sure all maven projects are ticked
- Select the correct Java SDK and go with all the default options for the next pages

##### 3. Editing

- Make any edits
- Package the project to make sure that the project is still functional:
  - `java -Dmaven.multiModuleProjectDirectory=D:\Computing\obfuscator\obfuscator-core "-Dmaven.home=C:\Program Files\JetBrains\IntelliJ IDEA 2019.1.3\plugins\maven\lib\maven3" "-Dclassworlds.conf=C:\Program Files\JetBrains\IntelliJ IDEA 2019.1.3\plugins\maven\lib\maven3\bin\m2.conf" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2019.1.3\lib\idea_rt.jar=18889:C:\Program Files\JetBrains\IntelliJ IDEA 2019.1.3\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files\JetBrains\IntelliJ IDEA 2019.1.3\plugins\maven\lib\maven3\boot\plexus-classworlds-2.5.2.jar" org.codehaus.classworlds.Launcher -Didea.version2019.1.3 package` (Change any paths to be relevant to your system)

##### 3. Commit

- Run `git status` to see which files you've changed
- Run `git add [file name]` for each of the files you want to submit your changes to
- Do `git commit -m "[A description of the changes]"`
- And finally `git push` to upload your changes to GitHub

##### 4. Pull Request

- Go to `https://github.com/[your github username]/obfuscator` and click "Pull Requests" and then "New Pull Request"
- Make sure all your changes are included then submit the Pull Request.

## Credits
- MCInjector (FFixer base)
- FFixer (Obfuscator base)
- SmokeObfuscator (Some ideas)
- MarcoMC (Some ideas)
- ItzSomebody (NameUtils.crazyString(), Crasher)
