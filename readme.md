# ATM



## Problem Statement



You are asked to develop a Command Line Interface (CLI) to simulate an interaction of an ATM with a retail bank.



## Commands



* `login [name]` - Logs in as this customer and creates the customer if not exist

* `deposit [amount]` - Deposits this amount to the logged in customer

* `withdraw [amount]` - Withdraws this amount from the logged in customer

* `transfer [target] [amount]` - Transfers this amount from the logged in customer to the target customer

* `logout` - Logs out of the current customer



## Example Session



Your console output should contain at least the following output depending on the scenario and commands. But feel free

to add extra output as you see fit.



```bash

$ login Alice

Hello, Alice!

Your balance is $0



$ deposit 100

Your balance is $100



$ logout

Goodbye, Alice!



$ login Bob

Hello, Bob!

Your balance is $0



$ deposit 80

Your balance is $80



$ transfer Alice 50

Transferred $50 to Alice

Your balance is $30



$ transfer Alice 100

Transferred $30 to Alice

Your balance is $0

Owed $70 to Alice



$ deposit 30

Transferred $30 to Alice

Your balance is $0

Owed $40 to Alice



$ logout

Goodbye, Bob!



$ login Alice

Hello, Alice!

Your balance is $210

Owed $40 from Bob



$ transfer Bob 30

Your balance is $210

Owed $10 from Bob



$ logout

Goodbye, Alice!



$ login Bob

Hello, Bob!

Your balance is $0

Owed $10 to Alice



$ deposit 100

Transferred $10 to Alice

Your balance is $90



$ logout

Goodbye, Bob!

```

## Notes

### Prerequisites

- Java 11
- Gradle 7.2

### Build

Gradle is used as build system.

```bash
> ./gradlew clean build
```

Executable jar can be found in `./build/libs/atm-1.0.jar`.

#### Build with test coverage report

To build with pitest mutational test coverage report use (takes ~1.5 min on my mac):

```bash
> ./gradlew clean build pitest
```

Test coverage report can be found in `./build/reports/pitest/{datetime}/index.html`

### Run

To run the program use:

```bash
> java -jar ./build/libs/atm-1.0.jar
```
### Logs

A log file can be found here `./logs/atm.log`

### Help

To see all available commands use:

```bash
> help
```

### Some development points and design rationale
* Spring-boot framework is used just to quickly prepare the app skeleton. There is no special need to use it for this task.
* ATM commands are implemented using 'Command' design pattern.
* Business logic and state placed in both 'service' package and 'domain' package (aka rich domain model objects).
* To represent money BigDecimal class is used (as more suitable for strict calculation with decimal point that is 
money in general case) although required scenario can be covered with using just int type.
* Project contains unit and BDD scenario-based tests. 
