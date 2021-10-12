### Java Exercise
This project was create as part of the hiring process from Sytac, the instruction are in the INSTRUCTIONS.md file

## Running the project
 - Run maven
```
mvn clean package
```
 - Run Docker Build
```
docker build --tag java-exercise:1.0.0 .
```
 - Launch the application
```
docker run -i -t -v "YOUR_DIRECTORY_PATH":/usr/app --name java-exercise java-exercise:1.0.0
```
Note: Replace YOUR_DIRECTORY_PATH in the command line above and replace it with the path where the files will be exported.
