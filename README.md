# casino-app
Casino Application wich consists of auth service with endpoints /login and /launch. These can be accessed by HTTP POST


ToDo:
1. Test thoroughly end to end
2. Add further unit tests to cover the entire flow
3. Test with multiple datasets in the test db and/or use @TestContainer to create a postgres image for unittesting
4. Add Vault to save the passwords and private key
5. Add Swagger documentation
6. Add Actuator endpoints to measure the performance
7. Add service for docker file and install the service in ECS or Beanstalk

Steps to Test:

use "demo1"	"password1" to access POST /login endpoint. you would recieve a token in response.

use the token from /login call and add gameid “superspins” or “jackpotslots” to make a POST call to /launch endpoint. You would get a URL in the response. if the URL is null, look for the status and message attributes to find out what went wrong.
