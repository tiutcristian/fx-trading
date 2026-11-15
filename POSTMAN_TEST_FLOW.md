# FX Trading API – Postman Test Flow

Nov 15, 2026 · @Cristian

## Prerequisites

Both services must be running before you start. Log in through user-administration, then call fx-trading with the token it returns.

| Service | Base URL |
| --- | --- |
| user-administration | http://localhost:8200 |
| fx-trading | http://localhost:8210 |

Test user: `test` / `testPassword` (already registered).

## Postman steps

Run these four requests in order. Only step 1 has a body.

**1. Log in (get the token)**

`POST http://localhost:8200/user/authenticate`

Body: raw, JSON:

```json
{
    "username": "test",
    "password": "testPassword"
}
```

Copy the `token` value from the response.

**2. Call fx-trading without a token**

`GET http://localhost:8210/transaction`

No body. Authorization tab: No Auth.

**3. Call fx-trading with the token**

`GET http://localhost:8210/transaction`

No body. Authorization tab: Bearer Token, paste the token from step 1.

**4. Call fx-trading with a tampered token**

`GET http://localhost:8210/transaction`

No body. Authorization tab: Bearer Token, paste the token with its last character changed.

| Step | Request | Expected status |
| --- | --- | --- |
| 1 | POST /user/authenticate | 200, response contains `token` |
| 2 | GET /transaction, no token | 401 |
| 3 | GET /transaction, valid token | 200, list of transactions |
| 4 | GET /transaction, tampered token | 401 |

## Notes

- Tokens expire 3 hours after login. After that, step 3 returns 401; repeat step 1 for a new token.
- On a 401, the `WWW-Authenticate` response header says why the token was rejected.
- `POST /transaction` also needs quote-service running on port 8220.
