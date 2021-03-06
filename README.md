#FileEncrypt Class

##Description:
1. Encrypts / Decrypts a file using symmetric algorithm of your choice.
2. Signs and validates file's contents.
3. Encrypts the symmetric key + signature using asymmetric algorithm of your choice.
4. Prepares a JSON with all the relevant information for the receiver of how to decrypt the file.

## Run
### Using JAR:
```
java -jar <jar_file> [params]
```

### Class file:
```
java EncryptTool [params]
```

According to the params below described in the below documentation.

##Documentation

```
FileEncrypt tool

Run:
java EncryptTool -encrypt -keystore <keystore file> -password <pass> -myAlias <your keystore alias> -myAliasPassword <your alias password> -file <file path>
java EncryptTool -decrypt -keystore <keystore file> -password <pass> -myAlias <your keystore alias> -myAliasPassword <your alias password> -file <file path>

Options:
    -encrypt            Encrypts the file and creates a signature
    -decrypt            Encrypts the file validates signature
    -keystore           Key Store file path
    -password           Key Store password
    -myAlias            Key Store alias for my cert with private key
    -myAliasPassword    alias password for my cert with private key (if not defined, using keystore password)
    -recAlias           Key Store alias for recipient cert
    -file               File to encrypt and sign
```

## Contribution
* Reorganize constants and strings used around the code
* Unit test coverage
* Improve usage interface
    * Better parameter names
    * Support running this tool with a configuration file instead of long parameters list