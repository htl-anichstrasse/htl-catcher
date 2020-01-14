# HTL Catcher Leaderboard API

We have set up a [RESTful API](https://en.wikipedia.org/wiki/Representational_state_transfer) in order to access leaderboard data inside the HTL Catcher Android app. This document lists all existing API nodes/resources and describes how to use them.

All API URLs are prefixed with `/api/`. Due to the fact that this project is a very small and is maybe used just once in production, the API does not support versions.

## Table of contents

* [Authorization](#authorization)
    * [Example](#authorization-example)
* [Nodes](#nodes)
    * [Add](#add)
    * [Remove](#remove)
* [Contribute](#contribute)

---

## Authorization

Some API nodes (especially the POST ones) require authorization by the user. The API uses Basic auth, usernames and salted SHA-512 password hashes for API users are stored in `/webserver/static/users.json`. After you have added a user plus a password hash generated using the `generate_hash.py` script there, you can use Basic auth.

Nodes which require authorization are marked as such.

### Authorization Example

    Username: peter
    Password: supersecurepassword

1. Generate a hash for the chosen password

```console
user@pc:/htl-catcher/server$ py generate_hash.py supersecurepassword
```

2. Enter the user data in `users.json`

```json
[
    {
        "name": "peter",
        "password_hash": "c18c24d6a99817142d20db895b1b478cd0d0af16bc09e6d1adae33795afbd357633fc57549b19c61bee7e6dd30871c43c2d9bfe818323e6e432fd5ce876a4ab0bc992d18bc82330b46860e5045700349ab82715576e8524e545983454e06e366"
    }
]
```

3. Use Basic authorization header

`peter:supersecretpassword` encoded in Base64 is `cGV0ZXI6c3VwZXJzZWNyZXRwYXNzd29yZA==`

```
Authorization: Basic cGV0ZXI6c3VwZXJzZWNyZXRwYXNzd29yZA==
```

---

## Nodes

### Add

Modifies the remote leaderboard data by adding a new player entry to the leaderboard.

    POST /add

**Requires Authorization:** Yes

**Headers**

```
Authorization: Basic b64<username:password>
Content-Type: application/json
```

**Payload**

* `name: string` — required
* `score: integer` — required
* `message: string`

```json
{
    "name": "Freddie Mercury",
    "score": 1970,
    "message": "Don't stop me now"
}
```

**Success response**
```json
{
    "message": "Successfully added new leaderboard entry."
}
```

**Example call**

```console
user@pc:~$ curl --location --request POST '/api/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic cGV0ZXI6c3VwZXJzZWNyZXRwYXNzd29yZA==' \
--data-raw '{
    "name": "Freddie Mercury",
    "score": 1970,
    "message": "Don'\''t stop me now"
}'
```

### Remove

Modifies the remote leaderboard data by removing all occurrences of a received player name.

    POST /remove

**Requires Authorization:** Yes

**Headers**

```
Authorization: Basic b64<username:password>
Content-Type: application/json
```

**Payload**

* `name: string` — required

```json
{
    "name": "Freddie Mercury"
}
```

**Success response**
```json
{
    "message": "Successfully removed leaderboard entry / entries."
}
```

**Example call**

```console
user@pc:~$ curl --location --request POST '/api/remove' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic eWVldDpuaWNvbGF1cw==' \
--data-raw '{
    "name": "Freddie Mercury"
}'
```
## Contribute

Documentation is important. If you contribute to this project and want to add a new API node / resource, please add appropriate documentation for it to this file.
