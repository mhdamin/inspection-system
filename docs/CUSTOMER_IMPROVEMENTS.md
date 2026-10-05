# Customer workflow improvements

Updated 5 October 2026. This implementation spans the Spring backend and the sibling `fleet-manager` React app in the supplied workspace. The original evaluation is retained in the local `PRODUCT_EVALUATION.md`; its findings describe the pre-change version.

## Implemented

| Priority | Customer outcome | Implementation |
| --- | --- | --- |
| P0 | Keep records private | Admin-controlled registration, restrictions on financial actions, customer endpoints scoped to the signed-in account, invoice ownership through rental IDs rather than names. |
| P0 | Preserve inspection evidence | Server drafts and completed snapshots containing 20 exterior points, interior observations, tyres, odometer, fuel, photos, inspector identity and typed customer acknowledgement. Completed snapshots cannot be edited or deleted through checklist endpoints. |
| P0 | Avoid conflicting promises | Date overlap and vehicle-class checks, operational holds, customer eligibility, transactional allocation locks and guarded booking/rental transitions. Conversion and return retries reuse existing records. |
| P0 | Explain charges and deposits | Server-calculated booking prices retained at conversion, extension invoice updates, fuel difference charges, separately collected deposits, reconciled settlement balances, capped manual refunds, references and request keys for payment/refund retries. Monetary persistence uses decimal values rounded to cents. |
| P1 | Work at the vehicle | Mobile navigation, responsive inspection controls, labelled dialogs with keyboard focus management, local draft recovery, server saves and actual error messages. |
| P1 | Connect the workflow | Persistent URLs, rental detail with invoices and inspections, prefilled handover/return links, dashboard queues using real data. |
| P2 | Give renters useful self-service | Linked customer accounts, availability and price preview, booking confirmation, eligible cancellation, own invoices/inspection records and password change. Changed prices require a fresh quote. |

## Operator walkthrough

1. Add a vehicle and a customer. Confirm the customer's identity and licence expiry before handover. Create an active rate plan for each class offered in the customer portal.
2. Create and confirm a booking, assign an available vehicle and prepare its rental. The rental page connects the remaining steps.
3. Record the collected deposit against its separate deposit invoice, with a real receipt/reference. This records a payment already made; it does not charge a card.
4. Complete the pickup inspection with the customer. Each point starts uninspected. Review the observations, photos and typed acknowledgement before completing it.
5. Start the rental. The server requires an eligible customer, the collected deposit when applicable and a completed pickup inspection.
6. Complete a linked return inspection, select it in the return form, review charges and submit. Deposits offset charges once. Any refundable remainder stays pending until a manager records the actual refund with its reference.
7. Administrators can create a customer's portal login from Customers. Refresh the access form's customer list after adding a new customer. Share initial credentials privately; customers can change their password in the portal.

## Permission model

| Role | Intended access |
| --- | --- |
| CUSTOMER | Own portal records, quotes, bookings and eligible cancellation; own password. No staff fleet APIs. |
| USER | Staff read access; no fleet mutations or financial recording. |
| INSPECTOR | Operational fleet actions and inspections; no restricted finance or user administration. |
| MANAGER | Operations plus payment/refund recording, rate changes and approvals. |
| ADMIN / SUPERADMIN | Administration and vehicle management, in addition to operational/finance access. Staff provisioning still respects role hierarchy. |

This is a single-operator deployment model. It does not introduce tenant boundaries between separate rental companies. Frontend navigation is a convenience; the server enforces authorization.

## Setup and upgrade notes

- Demo records and credentials are no longer seeded automatically. `app.seed-demo-data=true` is an explicit development-only opt-in.
- On an empty user database, provision the initial superadmin using `APP_BOOTSTRAP_USERNAME` and `APP_BOOTSTRAP_PASSWORD` (at least 12 characters). Remove the bootstrap password from the runtime environment after provisioning. Existing users are not replaced.
- Business date comparisons accept local date/time values and ISO timestamps with offsets. Offset timestamps are converted using `app.business-zone` (default `Asia/Singapore`). Portal prices currently use SGD. Configure and validate the business timezone before importing timestamps.
- Set a deployment-specific `JWT_SECRET`; the repository's existing fallback is only suitable for local development. Review deployment secrets, TLS, backups and log settings before customer use.
- Apply backend and frontend changes together. Existing clients that attempt public registration or immediate rental closure must move to the controlled account and return flows.
- An existing database needs a reviewed migration before rollout. Add `checklists.rental_id`, `completed_at`, `evidence_json` and `version`; initialize existing checklist versions to `0`. Add nullable, unique `fleet_customers.portal_username`, and nullable `reference`/unique `request_key` on payments and refunds. Permit `NOT_COLLECTED` in deposit-status constraints.
- Convert the monetary fields annotated with `MoneyConverter` in `FleetDomain.java` (including invoice line-item amounts) to decimal columns with two fractional digits, checking precision against existing values. Tax rates remain fractional numeric values. Reconcile totals before and after conversion; do not infer that legacy `HELD` deposits represent real collection.
- Legacy checklists have no complete evidence snapshot and remain incomplete. They must not be silently presented as newly completed inspections. Reconcile old payments, deposits and duplicate rentals before using the new guards on historical records.

No production database was migrated. Hibernate's local schema update is not a substitute for rehearsing an existing-data migration on the deployment database.

## Validation

- Maven integration and unit suite: 33 tests passed, zero failures/errors. See `target/product-tests.log` for the latest result. Tests cover anonymous/role restrictions, same-name customer isolation, superadmin vehicle creation, conflicting and concurrent assignment, adjacent periods, malformed dates/tokens, conversion retries, inspection persistence/immutability/completeness, handover prerequisites, invoice extension, deposit reconciliation, refund caps and changed customer quotes.
- Frontend TypeScript checking and Vite production build.
- Browser smoke checks against an isolated, in-memory backend: sign-in, phone navigation, inspection form sizing, draft recovery, customer entry and vehicle creation. Synthetic test records only.
- The automated database suite uses H2. PostgreSQL migration/locking verification, a complete browser end-to-end suite and representative staff/renter usability sessions remain release validation work.

## Remaining priorities

1. Rehearse migration and full pickup/return accounting on PostgreSQL with representative historical records. Add a durable financial adjustment audit trail and stronger consistency between inspection odometer/fuel snapshots and handover/return values. Monetary calculations still expose Java/JSON numbers despite decimal persistence.
2. Add idempotency to initial customer booking creation, signed/versioned quote acceptance, configurable pickup locations and published rental/fuel/cancellation terms. Current retry guarantees cover conversion, return and manual financial records, not every endpoint.
3. Move photo blobs from bounded JSON snapshots to managed object storage with retention, access-controlled downloads and thumbnail/pagination APIs. Current limit: 12 compressed JPEG/PNG images, each at most 250,000 data-URL characters. Draft recovery uses local storage; it is not a full offline synchronization system and should be used on trusted devices.
4. Add explicit amendments and pickup-versus-return damage comparison. Typed acknowledgement is a retained attestation, not an identity-verified electronic signing service. Customer document downloads are JSON; inspection printing uses the browser.
5. Integrate payment execution, receipts, customer invitations/recovery, support contacts and trip extension requests. Current payment/refund controls record external/manual transactions only.
6. Replace legacy operational shortcuts with stronger domain rules where needed (vehicle holds, maintenance completion and deletions), add immutable audit logs, and introduce organization isolation before serving multiple companies from one instance.
7. Split the frontend bundle and extend accessibility/device testing. The production build reports a large-chunk warning; functionality builds successfully.
