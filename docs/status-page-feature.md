# Status Tracking Feature

## Purpose

The Patent Disclosure Workflow provides a dedicated status tracking page that allows an applicant to check the current state of a submitted patent disclosure.

## Workflow

1. Applicant enters the disclosure ID.
2. The frontend sends a request to the status API.
3. The backend retrieves the disclosure record.
4. The current status is displayed to the applicant.

## Supported Statuses

- SUBMITTED
- APPROVED
- REJECTED

## Validation

The status page validates the disclosure ID before requesting the record and displays an appropriate message when the disclosure cannot be found.

## Verification

The status tracking page is covered by the Selenium test:

`testStatusTrackingPageLoads`

The test verifies that the status tracking page loads successfully and that the expected page heading is present.

## API Endpoint Example

The status tracking feature uses the disclosure API base endpoint:

`GET /api/disclosures/{disclosureId}`

For example:

`GET /api/disclosures/PD-20260929-45788404`
