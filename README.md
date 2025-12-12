# universal-credit-liability-stubs

## 422 Errors

| NINO     | HTTP Status              | Code  | Description                                                                                                   |
|:---------|--------------------------|-------|---------------------------------------------------------------------------------------------------------------|
| AA010000 | 422 Unprocessable Entity | 55006 | Start Date and End Date must be earlier than Date of Death                                                    |
| AA020000 | 422 Unprocessable Entity | 55008 | End Date must be earlier than State Pension Age                                                               |
| AA030000 | 422 Unprocessable Entity | 55027 | End Date later than Date of Death                                                                             |
| AA040000 | 422 Unprocessable Entity | 55029 | Start Date later than SPA                                                                                     |
| AA050000 | 422 Unprocessable Entity | 55038 | A conflicting or identical Liability is already recorded                                                      |
| AA060000 | 422 Unprocessable Entity | 55039 | NO corresponding liability found                                                                              |
| AA070000 | 422 Unprocessable Entity | 64996 | Start Date is not before date of death                                                                        |
| AA080000 | 422 Unprocessable Entity | 64997 | LCW/LCWRA not within a period of UC                                                                           |
| AA090000 | 422 Unprocessable Entity | 64998 | LCW/LCWRA Override not within a period of LCW/LCWRA                                                           |
| AA100000 | 422 Unprocessable Entity | 65026 | Start date must not be before 16th birthday                                                                   |
| AA110000 | 422 Unprocessable Entity | 65536 | Start date before 29/04/2013                                                                                  |
| AA120000 | 422 Unprocessable Entity | 65537 | End date before start date                                                                                    |
| AA130000 | 422 Unprocessable Entity | 65541 | The NINO input matches a Pseudo Account                                                                       |
| AA140000 | 422 Unprocessable Entity | 65542 | The NINO input matches a non-live account (including redundant, amalgamated and administrative account types) |
| AA150000 | 422 Unprocessable Entity | 65543 | The NINO input matches an account that has been transferred to the Isle of Man                                |
| AA160000 | 422 Unprocessable Entity | 99999 | Start Date after Death                                                                                        |

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

### License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").