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
    * [422 UnprocessableEntity Errors](#422-unprocessableentity-errors)
    * [Other Error Responses](#other-error-responses)
    * [Scalafmt](#scalafmt)
    * [Testing](#testing-1)
    * [License](#license)

<!-- TOC -->

## Endpoints

### Insert Universal Credit Liability Details

**Endpoint**: `POST /ni/person/{nino}/liability/universal-credit`

**Description**: Provides the capability to insert Universal Credit Liability details for a given individual. This
endpoint requires Mutual Authentication over TLS 1.2

**Path Parameters**: National Insurance Number (NINO)

**Payload**:

```json
{
  "universalCreditLiabilityDetails": {
    "universalCreditRecordType": "LCW/LCWRA",
    "liabilityStartDate": "2015-08-19",
    "liabilityEndDate": "2025-01-04"
  }
}
```

### Terminate Universal Credit Liability Details

**Endpoint**: `POST /ni/person/{nino}/liability/universal-credit/termination`

**Description**: Provides the capability to terminate Universal Credit Liability details for a given individual. This
endpoint requires Mutual Authentication over TLS 1.2

**Path Parameters**: National Insurance Number (NINO)

**Payload**:

```json
{
  "ucLiabilityTerminationDetails": {
    "universalCreditRecordType": "LCW/LCWRA",
    "liabilityStartDate": "2015-08-19",
    "liabilityEndDate": "2025-01-04"
  }
}
```

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

---

## 422 UnprocessableEntity Errors

To get an UnprocessableEntity Error (422) use a National Insurance Number (NINO) with any of the following 5-character
prefixes.

| NINO PREFIX | HTTP Status             | Code  | Description                                                                                                   |
|:------------|-------------------------|-------|---------------------------------------------------------------------------------------------------------------|
| BW130       | 422 UnprocessableEntity | 55006 | Start Date and End Date must be earlier than Date of Death                                                    |
| EZ200       | 422 UnprocessableEntity | 55008 | End Date must be earlier than State Pension Age                                                               |
| BK190       | 422 UnprocessableEntity | 55027 | End Date later than Date of Death                                                                             |
| ET060       | 422 UnprocessableEntity | 55029 | Start Date later than SPA                                                                                     |
| GE100       | 422 UnprocessableEntity | 55038 | A conflicting or identical Liability is already recorded                                                      |
| GP050       | 422 UnprocessableEntity | 55039 | NO corresponding liability found                                                                              |
| EK310       | 422 UnprocessableEntity | 64996 | Start Date is not before date of death                                                                        |
| HS260       | 422 UnprocessableEntity | 64997 | LCW/LCWRA not within a period of UC                                                                           |
| CE150       | 422 UnprocessableEntity | 64998 | LCW/LCWRA Override not within a period of LCW/LCWRA                                                           |
| HC210       | 422 UnprocessableEntity | 65026 | Start date must not be before 16th birthday                                                                   |
| GX240       | 422 UnprocessableEntity | 65536 | Start date before 29/04/2013                                                                                  |
| HT230       | 422 UnprocessableEntity | 65537 | End date before start date                                                                                    |
| EA040       | 422 UnprocessableEntity | 65538 | End date missing but the input was a Termination                                                              |
| BX100       | 422 UnprocessableEntity | 65541 | The NINO input matches a Pseudo Account                                                                       |
| HZ310       | 422 UnprocessableEntity | 65542 | The NINO input matches a non-live account (including redundant, amalgamated and administrative account types) |
| BZ230       | 422 UnprocessableEntity | 65543 | The NINO input matches an account that has been transferred to the Isle of Man                                |
| HG200       | 422 UnprocessableEntity | 65544 | Account held on NPS, but has not gone through adult registration.                                             |
| AB150       | 422 UnprocessableEntity | 99999 | Start Date after Death                                                                                        |

## Other Error Responses

To get a system error use a National Insurance Number (NINO) with any of the following 5-character prefixes.

| NINO PREFIX    | HTTP Status               |
|:---------------|---------------------------|
| XY400          | 400 Bad Request           |
| XY401          | 401 Unauthorized          |
| XY403 or HJ120 | 403 Forbidden             |
| XY404 or CM110 | 404 NotFound              |
| XY500 or HZ020 | 500 Internal Server Error |
| XY503          | 503 Service Unavailable   |

## sbt Aliases

These aliases let you run multiple sbt tasks with a single command.

Checks against the Scalafmt and Scalafix rules. This command is part of the PR Builder Jenkins job and fails the PR if the code is not formatted:
```shell
sbt prePrChecks
```

Checks code coverage (includes both unit and integration tests):

```shell
sbt checkCodeCoverage
```
Formats all project code. Applies Scalafix and Scalafmt rules:

```shell
sbt lintCode
```

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

## Bruno testing
Bruno collection is set up for conducting manual tests and can be found under `bruno`.
It has been developed and tested for `Bruno v2.1.0`.
To use, in Bruno just select open collection, navigate to the `bruno` folder and open.

There are two configured environment profiles, Local and QA.
There are also two auth requests, corresponding to the two environment profiles.

For more info on environment variables in Bruno see https://docs.usebruno.com/get-started/variables/environment-variables

Before you can make requests to the Universal Credit Liability Notification API, a valid auth token is required.
This is obtained by triggering the auth requests corresponding to the environment profile selected (it will fail otherwise).

After a successful authentication request, the obtained auth token will be stored as a secret environment variable and utilised by the Universal Credit Liability Notification API requests.
The auth token may be valid for a different length of time depending on the environment. e.g. QA = 4 hours

# QA auth
For `Auth/QA`, a privileged application subscribing to the API needs to have already been preconfigured.
The Bruno collection also has a dependency on an external JavaScript library `totp-generator` which will require `nodejs`.

After installing node, execute:
```
cd bruno
npm i
```

And then enable [Developer Mode](https://docs.usebruno.com/configure/javascript-sandbox) in Bruno.

For more info on external libraries in Bruno see https://docs.usebruno.com/testing/script/external-libraries

The QA environment is only semi configured, all the fields related to oauth 2.0 are specified as `secrets` which are not commited to the repo and will need to be manually set.
This is done because these secrets can all expire and change.

| Required QA Environment variables | Description                                             |
|-----------------------------------|---------------------------------------------------------|
| clientId                          | The client id to the QA application                     |
| clientSecret                      | any valid client secret generated by the QA application |
| TOTP_SECRET                       | The TOTP Secret for the given QA application            |

The other secret variables are placeholders for temporary variables as part of the authentication process, they do not need to be configured.
These are pre-specified so they also don't get commited to the repo.

For more info on Bruno's secret variables see
https://docs.usebruno.com/secrets-management/secret-variables


## License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
