# universal-credit-liability-stubs

The Universal Credit Liability Stubs service provides stubs for the HIP/NPS downstream, to mock the responses.

## Table of Contents

<!-- TOC -->
* [universal-credit-liability-stubs](#universal-credit-liability-stubs)
  * [Table of Contents](#table-of-contents)
  * [Running Locally](#running-locally)
  * [Running with Service Manager](#running-with-service-manager)
  * [Testing](#testing)
  * [Endpoints](#endpoints)
    * [Insert Universal Credit Liability Details](#insert-universal-credit-liability-details)
    * [Terminate Universal Credit Liability Details](#terminate-universal-credit-liability-details)
  * [422 Unprocessable Entity Errors](#422-unprocessable-entity-errors)
  * [Other Error Responses](#other-error-responses)
  * [Scalafmt](#scalafmt)
  * [Testing](#testing-1)
  * [License](#license)
<!-- TOC -->

## Running Locally

Compile the project with:

```shell
sbt clean compile update
```

Run the project locally with:

```shell
sbt run
```

By default, the service runs on port **16108**.

## Running with Service Manager

Use [Service Manager](https://github.com/hmrc/sm2) to start all the services required to run and test Universal Credit
Liability service locally.

Start the **UNIVERSAL_CREDIT_LIABILITY_ALL** profile, responsible for starting up all the services required, with:

```shell
sm2 --start UNIVERSAL_CREDIT_LIABILITY_ALL
```

## Testing

Run unit tests with:

```shell
sbt test
```

Run integration tests with:

```shell
sbt it/test
```

Check code coverage with:

```shell
sbt clean coverage test it/test coverageReport
```

## Endpoints

### Insert Universal Credit Liability Details

**Endpoint**: `POST /person/{nino}/liability/universal-credit`

**Description**: Provides the capability to insert Universal Credit Liability details for a given individual. This
endpoint requires Mutual Authentication over TLS 1.2

**Path Parameters**: National Insurance Number (NINO)

### Terminate Universal Credit Liability Details

**Endpoint**: `POST /person/{nino}/liability/universal-credit/termination`

**Description**: Provides the capability to terminate Universal Credit Liability details for a given individual. This
endpoint requires Mutual Authentication over TLS 1.2

**Path Parameters**: National Insurance Number (NINO)

---

## 422 Unprocessable Entity Errors

To get an Unprocessable Entity Error (422) use a National Insurance Number (NINO) with any of the following 5-character
prefixes.

| NINO PREFIX | HTTP Status              | Code  | Description                                                                                                   |
|:------------|--------------------------|-------|---------------------------------------------------------------------------------------------------------------|
| BW130       | 422 Unprocessable Entity | 55006 | Start Date and End Date must be earlier than Date of Death                                                    |
| EZ200       | 422 Unprocessable Entity | 55008 | End Date must be earlier than State Pension Age                                                               |
| BK190       | 422 Unprocessable Entity | 55027 | End Date later than Date of Death                                                                             |
| ET060       | 422 Unprocessable Entity | 55029 | Start Date later than SPA                                                                                     |
| GE100       | 422 Unprocessable Entity | 55038 | A conflicting or identical Liability is already recorded                                                      |
| GP050       | 422 Unprocessable Entity | 55039 | NO corresponding liability found                                                                              |
| EK310       | 422 Unprocessable Entity | 64996 | Start Date is not before date of death                                                                        |
| HS260       | 422 Unprocessable Entity | 64997 | LCW/LCWRA not within a period of UC                                                                           |
| CE150       | 422 Unprocessable Entity | 64998 | LCW/LCWRA Override not within a period of LCW/LCWRA                                                           |
| HC210       | 422 Unprocessable Entity | 65026 | Start date must not be before 16th birthday                                                                   |
| GX240       | 422 Unprocessable Entity | 65536 | Start date before 29/04/2013                                                                                  |
| HT230       | 422 Unprocessable Entity | 65537 | End date before start date                                                                                    |
| EA040       | 422 Unprocessable Entity | 65538 | End date missing but the input was a Termination                                                              |
| BX100       | 422 Unprocessable Entity | 65541 | The NINO input matches a Pseudo Account                                                                       |
| HZ310       | 422 Unprocessable Entity | 65542 | The NINO input matches a non-live account (including redundant, amalgamated and administrative account types) |
| BZ230       | 422 Unprocessable Entity | 65543 | The NINO input matches an account that has been transferred to the Isle of Man                                |
| AB150       | 422 Unprocessable Entity | 99999 | Start Date after Death                                                                                        |

## Other Error Responses

To get a system error use a National Insurance Number (NINO) with any of the following 5-character
prefixes.

| NINO PREFIX | HTTP Status               |
|:------------|---------------------------|
| XY400       | 400 Bad Request           |
| XY401       | 401 Unauthorized          |
| XY403       | 403 Forbidden             |
| XY404       | 404 NotFound              |
| XY500       | 500 Internal Server Error |
| XY503       | 503 Service Unavailable   |

## Scalafmt

Check all project files are formatted as expected as follows:

```bash
sbt scalafmtCheckAll scalafmtCheck
```

Format `*.sbt` and `project/*.scala` files as follows:

```bash
sbt scalafmtSbt
```

Format all project files as follows:

```bash
sbt scalafmtAll
```

## Testing

Bruno collection can be found in `bruno/SubmitLiabilityDetails.bru` for `Bruno v2.1.0`

## License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
