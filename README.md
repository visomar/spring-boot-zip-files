# spring-boot-zip-files

This project is a proof of concept of an application that accepts multipart files, compress them into a zip file, and returns it as downloadable content.

## Getting Started

With this instructions you will be able to set up the project locally, and test it within a real scenario.

### Prerequisites

It is required to have installed in your system and updated to the last version these products:
* git
* docker
* maven

### Installation and execution

1. Clone the repo
   ```sh
   git clone git@github.com:visomar/spring-boot-zip-files.git
   ```
2. Create the docker image
   ```sh
   mvn spring-boot:build-image
   ```
3. Run the package
   ```sh
   docker run -it -p8080:8080 zipping:0.0.1-SNAPSHOT
   ```

### Usage

If the previous steps are followed, and nothing breaks, you have available and endpoint at
```
http://localhost:8080/api/zip
```
to perform POST requests.

#### Example with Postman

The recommended way to -visually- test it is with Postman. This is a screenshot of the needed steps:

1. Select POST as the method
2. Write the URL: http://localhost:8080/api/zip
3. Select **Body** from the options below the URL
4. Select **form-data** from the options that appear below
5. Use **files** as the name of the *key* for each file
6. Once every key has been filled, a dropdown appears when hovering the field. Select **File**
7. Choose one file for every field declared
8. Click on **Send**
9. Save response as a zip file

#### Example with CURL

With a sincle CURL command we can see the results too:

```sh
curl -X POST -H "Content-Type: multipart/form-data" -F "files=@yepe1.txt" -F "files=@../../bin/yepe/yepe2.txt" http://localhost:8080/api/zip --output response.zip
```

For each file that the user wants to compress we have to identify it as **files**, and then point to the path where that file is located.

