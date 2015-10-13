# Html Battle Window

###Requirements

On Linux:

```
sudo apt-get install gradle
```

On Windows, install `chocolatey` and then in administrative command line:

```
choco install gradle
```

###Build

On Linux:
```
./gradlew html:dist
```

On Windows, in **powershell**:
```
gradlew.bat html:dist
```

###Run

This is not a stand-alone repository. It is used as a submodule of `po-devs/po-web`.
