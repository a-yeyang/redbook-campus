# Redbook Campus Web

The `vue3` workspace contains the browser application for Redbook Campus. It is built with Vue 3, Vite, TypeScript, and Axios, and talks to the backend through the Spring Cloud Gateway.

## Features

- Campus feed by channel.
- Image note publishing with file upload.
- Note detail modal with comments.
- Note search and user search.
- Like and unlike interactions.
- Follow and unfollow interactions.
- Token bootstrap through the `token` query parameter for local testing.

## Development

```bash
npm install
npm run dev
```

Default URL:

```text
http://127.0.0.1:5173
```

Default backend gateway:

```text
http://127.0.0.1:8000
```

Override the gateway with:

```bash
VITE_API_BASE_URL=http://127.0.0.1:8000 npm run dev
```

## Build

```bash
npm run build
```

## Token Bootstrap

For local testing, open the app with:

```text
http://127.0.0.1:5173/?token=<access-token>
```

The app stores the token in `localStorage` and sends it as a Bearer token on subsequent API requests.
