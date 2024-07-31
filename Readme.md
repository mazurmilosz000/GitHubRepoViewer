# GitHub User Repo Viewer

## Overview

GitHub User Repo Viewer is a Spring Boot application designed to fetch and display repository details of a specified GitHub user. The application interacts with the GitHub API to retrieve repository information and filters out forked repositories.

## Features

- Fetch repository details of a GitHub user.
- Display only non-forked repositories.
- Handles API errors gracefully.
- Configurable via application.yml file.

## Prerequisites

- Java 21
- Maven 3.6.3 or later
- A GitHub personal access token with necessary permissions to access the GitHub API.

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/yourusername/github-user-repo-viewer.git
cd github-user-repo-viewer
````

### Create your GitHub API token

To create an individual API token, follow the instructions [here](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-fine-grained-personal-access-token).

### Configurations

Go to the **src/main/resources/application.yml** file and paste your generated token here:

```yaml
app:
  token: <paste_your_token_here> # GitHub app token
````

### Build and Run the Application

To build and run your application use following commands:

```bash
mvn clean install
mvn spring-boot:run
````

Now application should be running on default port **8080**

## Making API Calls

To make an API call you can use [Postman](https://www.postman.com/) or curl command:

```bash
curl --location 'http://localhost:8080/repositories/<username>'
````

## Response

### Success

If successful, the response should look something like this:

```json
[
  {
    "repositoryName": "Chrome_Dino",
    "ownerLogin": "mazurmilosz000",
    "branches": [
      {
        "branchName": "main",
        "lastCommitSHA": "89ea117d6e01f6691f86430a00ba331df9602c27"
      }
    ]
  },
  {
    "repositoryName": "CurrentWeatherAPI",
    "ownerLogin": "mazurmilosz000",
    "branches": [
      {
        "branchName": "master",
        "lastCommitSHA": "e747be07c9f15555642327bfbcde88c3a6b929a6"
      }
    ]
  }
  ...
]
````

### Username not found

If no user was found with the given username, the response will look like this:
```json
{
  "status": "NOT_FOUND",
  "message": "Cannot found user with username: mazurmilosz23479"
}
````

### Incorrect API Token


If the API token has not been configured, or it is incorrect, the response will look like this:
```json
{
  "status": "UNAUTHORIZED",
  "message": "An error occurred during API authorization. Verify your API token and try again."
}
````

## Contributing
Feel free to submit issues, fork the repository, and send pull requests. Contributions are always welcome.

## License
This project is licensed under the MIT License.

