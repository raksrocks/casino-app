@TestPostEndpoint
Feature: Creates an auth request with /login endpoint
  Create a token with given details

  @CreateWithDataTable
  Scenario Outline: Create a token
    Given API is ready
    When  I send a POST request to API with user as <username> and password as <password>
    Then verify the token as <token>
    Examples:

      | username | password  | token |
      | demo1    | password1 | "66d488ba-cf4a-11ed-afa1-0242ac120002" |