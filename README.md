# Java Utilities Library
[![Maven Central](https://img.shields.io/maven-central/v/io.github.umutayb/Utilities?color=brightgreen&label=Utilities)](https://mvnrepository.com/artifact/io.github.umutayb/Utilities/latest)

Java-Utilities is a collection of utility classes and functions for Java developers, aimed at simplifying common tasks and enhancing productivity. This repository serves as a toolkit that can be utilized in various Java projects.

### Features

- **BooleanUtilities** provides methods for working with boolean values. It includes functionality for generating a random boolean value.
- **DBUtilities** provides utility methods for working with a database. It includes functionality for establishing a database connection and retrieving results from SQL queries.
- **DateUtilities** provides methods related to date and time manipulation.
- **EmailUtilities** provides utility methods for email-related operations, including sending emails and interacting with email inboxes within Java projects.
- **FileUtilities** provides utility methods for working with files. It includes methods for retrieving the absolute path of a file, reading the contents of a file as a string, creating a file if it does not exist, writing a string to a file, deleting a directory and its contents, and verifying the presence of a file at a given directory. **Zip** class is a static subclass that handles zip-related operations. **Excel** class provides methods for retrieving Excel sheet data. **Json** class provides methods for working with JSON objects.
- **LogUtilities** is a utility class that provides methods for logging messages using the java.util.logging.Logger framework. It includes functionality to set up a logging file with a **SimpleFormatter** and add it to the logger's handlers. Additionally, it provides a method to retrieve the log level based on a given string representation. This class can be used to facilitate logging operations in Java applications.
- **MappingUtilities** provides utility methods for mapping objects using the Jackson library. It includes a nested class **Json** that configures an **ObjectMapper** object with specific visibility settings and serialization features for handling JSON operations.
- **NumericUtilities** is a utility class that provides methods for generating random numbers, sorting lists of integers, and manipulating double values. These methods can be used in various scenarios where numeric operations are required.
- **Printer** is a utility class used for logging messages with different levels of importance and formatting. It provides methods for logging plain text messages, important messages, informational messages, success messages, warning messages, and error messages. Each message type can be highlighted with a specified color.
- **PropertiesReader** is used to read properties from a property file. It allows you to load a property file and retrieve the values of properties by their names. It simplifies the process of accessing and managing properties in your Java application.
- **PropertyUtility** is a utility class for loading and accessing properties. It provides methods for loading properties from a file, retrieving property values by key, and setting default values for properties. It simplifies the process of working with properties in Java applications.
- **ReflectionUtilities** is a utility class that provides methods for working with reflection in Java. It includes functionality for comparing objects and JSON structures, accessing and manipulating methods and fields, and retrieving information about classes. It simplifies the process of introspecting and interacting with objects at runtime.
- **StringUtilities** provides utility methods for working with strings. It includes methods for checking if a string is blank, highlighting text with colors, reversing strings, capitalizing or de-capitalizing the first letter of a string, converting strings to camel case format, cleaning strings, normalizing strings, shortening strings, generating random strings, measuring the distance between keywords in a string, converting strings to maps, and performing context checks on strings.
- **SystemUtilities** is a utility class that provides methods for system-related tasks. It includes a method for checking the availability of a specific port on the local host. Additionally, it contains a nested class **TerminalUtilities**, which allows running terminal commands as new processes and waiting for them to complete.
- **TextParser** is a utility class that allows you to extract substrings from text using keywords.

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

### License

This project is licensed under the MIT License. Feel free to use the utility classes and functions in your own projects, modify them, or distribute them as needed.

### Acknowledgments

- This project was inspired by the common tasks and functionalities that Java developers frequently encounter in their projects.
- We would like to thank the open-source community for their contributions and the Java language developers for providing a powerful and flexible platform.

