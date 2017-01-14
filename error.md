# Error message 

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