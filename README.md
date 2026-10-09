# IT-355---Group-Project

Java programs demonstrating rules and recommendations from the
[SEI CERT Oracle Coding Standard for Java](https://cmu-sei.github.io/secure-coding-standards/sei-cert-oracle-coding-standard-for-java/).

| Folder | Contents |
|---|---|
| `src/rules/` | One package for each CERT **rule** demonstrated |
| `src/recommendations/` | One package for each CERT **recommendation** demonstrated |
| `src/securestudentvault/` | **Secure Student Vault**: A larger exmaple that shows all rules from `src/rules/` in a single program. |

## Requirements

- **JDK 17 or newer.**
- **`h2.jar` (included, in the repository root).** The H2 in-memory database for SQL-related rules. Keep `h2.jar` in the repository root, the program instructions expect it to be there.

  h2.jar is needed for:
  - `rules.ids00j.SqlInjection`
  - `securestudentvault.SecureStudentVault` (runs without it, but skips the SQL section)


## Running in Eclipse

1. **File > Import... > General > Existing Projects into Workspace**, and select the repository
2. For each package, open the .java file with the main method and run

`h2.jar` is already on the project's build path (`src/.classpath` refers to it as `../h2.jar`). If you are getting errors with H2, try right-clicking the project and clicking **Refresh**.

Eclipse runs programs from the `src` folder, so files the demos create (see the table below) appear there.

## Running programs from the command line

Run all commands from the **repository root** (the folder containing `h2.jar`)

The only hardware difference in running programs is that the classpath separator is **`;` on Windows** and **`:` on macOS and Linux**.

Compile the desired program's main file. `-sourcepath src` makes `javac` find the other classes it uses.
Then run it by its full class name (from the table below):

**Windows (PowerShell or Command Prompt)**
```
javac -cp h2.jar -sourcepath src -d out src/securestudentvault/SecureStudentVault.java
java -cp "out;h2.jar" securestudentvault.SecureStudentVault
```

**macOS / Linux**
```
javac -cp h2.jar -sourcepath src -d out src/securestudentvault/SecureStudentVault.java
java -cp "out:h2.jar" securestudentvault.SecureStudentVault
```

For another program, change the file path and the class name, for example
`src/rules/fio01j/FileAccessModifiers.java` and `rules.fio01j.FileAccessModifiers`.
