# Free demo deployment

The demo uses Cloudflare Workers Static Assets, the existing Render Free web service, and the Supabase Free project `fleet-manager`. Do not enable paid plans or add-ons for this deployment.

## Resources

- Frontend: https://fleetguard-demo.ohameng13.workers.dev
- Backend: https://fleetguard-backend-1-0-0.onrender.com
- Backend health: https://fleetguard-backend-1-0-0.onrender.com/actuator/health
- Render service: `srv-d5p6qc14tr6s73aksav0`, plan `free`, repository branch `prod`.
- Supabase project: `rdqdswrbcasxzncrsqku`, Singapore. Application schema: `fleet_app`. Application login: `fleet_demo`.

## Backend configuration

Activate `SPRING_PROFILES_ACTIVE=docker,render`. The render profile configures a three-connection pool, private schema, restricted health endpoint and reduced logging. `JAVA_OPTS=-Xms64m -Xmx256m -XX:+UseSerialGC` leaves memory for the JVM outside its heap.

Set `SPRING_DATASOURCE_URL` to the project's session-pooler JDBC address with `sslmode=require&currentSchema=fleet_app`, and `SPRING_DATASOURCE_USERNAME` to `fleet_demo.PROJECT_REF`. Store its password and `JWT_SECRET` only in Render's environment settings. These are generated secrets, not the Supabase administrator password.

`APP_CORS_ALLOWED_ORIGINS` is a comma-separated list of exact frontend origins. Do not use a wildcard. Default development origins remain available when this setting is absent locally.

`APP_BOOTSTRAP_USERNAME` and `APP_BOOTSTRAP_PASSWORD` provision the first administrator only when the user database is empty. Remove the bootstrap password after confirming access and retaining it in a private password manager. Never enable the legacy sample-user seeder on a public deployment; it includes publicly known passwords.

## Database isolation

The application role owns only `fleet_app` and has no superuser, role-creation, database-creation or RLS-bypass permission. The schema denies access to PUBLIC, anon and authenticated; new application objects do not grant those roles access. Keep this schema out of Supabase Data API exposed schemas. The frontend uses the Spring API and does not require Supabase publishable or service-role keys.

Schema update is enabled for this new demo database. Existing-data production deployment needs reviewed migrations and backups instead. No other Supabase projects are part of this deployment.

## Frontend deployment

The frontend repository contains `wrangler.jsonc` and `.env.production`. The latter contains only the public backend URL. From that repository, run `npm ci`, `npm run build`, then `npx wrangler deploy`. Deploying assets does not automatically rebuild them. Node 22+ is recommended for the deployment CLI.

The Cloudflare frontend was deployed through Wrangler. Git pushes alone do not update it until Workers Builds/Git integration is separately configured. Render's existing backend tracks `prod`; a push can trigger a deployment. Environment updates made via Render's API require an explicit deploy.

## Demo operation

Use fictional customers and a small photo collection. Render Free sleeps after inactivity, so visit the health endpoint before a presentation and allow time for startup. Supabase Free may pause inactive projects; resume in its dashboard if needed. Keep all services on their Free plans and review included usage, particularly if payment details are already attached to the accounts.

## Access and validation

Administrator credentials are delivered privately on the local machine, not committed. Database and signing secrets are stored in Render; the local deployment helper retains a Windows-user-encrypted copy under the ignored `target/deploy-tools` directory for recovery.

Deployment checks should confirm backend health, a successful login, authenticated frontend API requests, denied unauthorized origins, persistence in `fleet_app`, and no anonymous schema privileges. Hosting URLs are public; application data requires login.
