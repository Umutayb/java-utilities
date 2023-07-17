# Java Utilities Library
[![Maven Central](https://img.shields.io/maven-central/v/io.github.umutayb/Utilities?color=brightgreen&label=Utilities)](https://mvnrepository.com/artifact/io.github.umutayb/Utilities/latest)

Java-Utilities simplifies common tasks and enhances productivity for Java developers by providing utility classes and functions. This repository serves as a toolkit that can be utilized in various Java projects.

### Features

- **BooleanUtilities** provides methods for modifying boolean values. A random boolean value can be generated using this function.

- **DBUtilities** provides utility methods for working with a database. It includes functionality for establishing a database connection and retrieving results from SQL queries.

- **DateUtilities** provides methods related to date and time manipulation.

- **EmailUtilities** provides utility methods for email-related operations, including sending emails and interacting with email inboxes within Java projects.

- **FileUtilities** provides utility methods for working with files. It includes methods for retrieving the absolute path of a file, reading the contents of a file as a string, creating a file if it does not exist, writing a string to a file, deleting a directory and its contents, and verifying the presence of a file at a given directory. Zip class is a static subclass that handles zip-related operations. Excel class provides methods for retrieving Excel sheet data. Json class provides methods for working with JSON objects.

- **LogUtilities** provides methods for logging messages in Java using the java.util.logging.Logger framework. It has the ability to create a logging file using a SimpleFormatter and add it to the handlers for the logger. It also offers a technique for getting the log level from a given string format. Java programs can utilize this class to make logging processes easier.

- **MappingUtilities** provides useful methods for object mapping using the Jackson library. It has a nested class Json that equips an ObjectMapper object for handling JSON operations with particular visibility settings and serialization features.

- **NumericUtilities** provides methods for creating random numbers, arranging lists of integers, and working with double values. These techniques can be applied in a variety of circumstances where numerical operations are necessary.

- **Printer** is a utility used to log messages with various formatting and priority levels. It offers ways to record essential messages, informational messages, success messages, warning messages, and error messages in addition to plain text messages. A specific hue can be used to highlight a particular message type.

- **PropertiesReader** reads properties from a file. It lets you load a property file and get property values by name. It simplifies the access and management of properties in your Java program.

- **PropertyUtility** is a utility for loading and accessing properties. It has methods for loading properties from a file, retrieving property values by key, and setting property default values. It simplifies working with properties in Java programs.

- **ReflectionUtilities** is a collection of tools for dealing with reflection. It allows you to compare objects and JSON structures, access and change methods and fields, and retrieve class information. It facilitates introspection and working with objects at runtime.

- **StringUtilities** is a collection of utility methods for working with strings. It has methods for determining whether a string is blank, highlighting text with colors, converting strings to camel case format, cleaning strings, normalizing strings, shortening strings, generating random strings, measuring the distance between keywords in a string, converting strings to maps, and performing context checks on strings.

- **SystemUtilities** contains methods for performing system-related activities. It contains a mechanism for determining whether a given port on the local host is available. It also has a nested class TerminalUtilities, which allows you to perform terminal commands as new processes and wait for them to finish.

- **TextParser** is a utility that allows you to extract keywords-based substrings from text.extParser is a utility class that allows you to extract keywords-based substrings from text.

### Dependency 
Add the following maven dependencies to your pom.xml file

```xml
<dependencies>
    <!-- Utilities -->
    <dependency>
        <groupId>io.github.umutayb</groupId>
        <artifactId>Utilities</artifactId>
        <version>1.x.x</version>
    </dependency>
</dependencies>
```

### Acknowledgments

This project was inspired by the common tasks and functionalities Java developers encounter in their projects. For their contributions and for providing a powerful and flexible platform, we would like to thank the open-source community and the Java language developers. 

### License

This project is licensed under the MIT License.
