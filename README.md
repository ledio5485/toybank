# TOY BANK

### Specifications

_The Task_:
Create a small application and design a REST API with the following prerequisites and use
cases. Some tasks are optional, feel free to decide if you want to work on them.

_Prerequisites_:
We have an existing bank customer with money in the following accounts:
• Checking account (functions as a reference account for the savings account)
• Savings account
• Private loan account
Every account has an IBAN assigned and should be referenced by this.

All accounts are having following capabilities:
Checking account - transferring money from and to any account is possible
Savings account - transferring money from any account is possible. Only transferring
money from the savings account to the reference account (checking account) is possible.
Private loan account - transferring money from any account is possible. Withdrawal is not
possible

_Use cases_:
* Deposit money into a specified bank account
  * Enable adding some amount to a specified bank account
* Transfer some money across two bank accounts
  * Imagine this like a regular bank transfer. You should be able to withdraw money
from one account and deposit it to another account
* Show current balance of the specific bank account
  * Show the balance of a specified bank account
* Filter accounts by account type
  * Request account information by account type (could be multiple)
* Show a transaction history
  * For an account, specified by IBAN, show the transaction history
* Bonus - account creation
  * To open an account and assign it with an IBAN an endpoint should be provided.
* Bonus - account locking
  * For security reasons, it might be a good idea to be able to lock a specific account.
For example, if an internal fraud management system spots something suspicious,
it should be able to lock that account. Naturally, if the account can be locked, there
should be an unlock functionality in place

_Extra information_:
  * Think about how to organise account data, and be prepared to explain why you’ve
chosen this approach. Basically the application should be stateless, except that.
  * You should implement your application in one of the JVM languages - preferred are Java
or Kotlin.
  * All the specified functionality must be available through a REST API.
  * Spring-Boot framework should be used.
  * You have your hands free, meaning you choose what build tools, libraries, etc. you want
to use.
  * Although it's a simple API, it should be production-ready. Everybody has a different
opinion on what means so we’re looking forward to speak about that with you.

### Minimum requirements:
* _docker_ installed

### Assumptions
* every request is already authenticated (e.g. there's already an API gateway on top of every service)

### Simplifications
* every TX operation(deposit/withdraw/transfer) is done in the same currency(EUR), so we don't have to deal with currency converter
* a simple pub-sub is implemented to communicate between the modules. in a real world a better system has to be chosen(e.g. rabbitmq, kafka, pulsar, etc)
* the _account_, and the _transaction_ modules are good candidates for microservices
* the country code and the bank code are fixed (see `application.yml`), and a simple account number creation is provided(progressive in-memory counter, instead of a distributed counter)
* the TX process is very simple:
  * every request/command is enqueued, and a 202 _ACCEPTED_ is returned to not block the main process
  * there's a listener of the transfer commands that first validates the request, then if OK save a record in DB and send a balance update event, otherwise raise an error
  * another listener in account module listens to balance update events of previous step, which updates the balances accordingly
  <br /><br />
  a better choice could have been using 2PC protocol or SAGA pattern to tackle the distributed TX problem wisely (but on the other side brings more complexity)
* the `locked` feature is not implemented
* the `annual interest rate` and the `overdraft` are not considered
* The transfer API endpoints (deposit, withdraw, transfer) responds immediately with 202, to not block the main thread. The validation and the transaction might take some time; so in case it fails the user can be notified by email, sms or web app push notification.Somebody may have concerns about this decision (or at least to have the _deposit_ and _withdraw_ operations synchronous), but it's not a big issue to change it.

### Content

* Run the tests: `./gradlew clean test -i` or run the script `./run-tests`

* The following commands are provided to run the applications with`docker-compose`:

Operation | Command
--------- | ----------
Start:    | `./start`
Stop:     | `./stop`
Restart:  | `./restart`

- [actuator](http://localhost:8080/actuato)
- [health check](http://localhost:8080/actuator/health)
- [API docs](http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)

After starting the service, you can start testing it using OpenAPI docs.

### Final note
Please feel free to contact me sending an [email](mailto:led.spaho@gmail.com) for any discussion/doubt/clarification.
