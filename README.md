# Java HTTP/1.1 Server

A multi-threaded HTTP/1.1 server built from scratch in Java, featuring TLS/HTTPS support, persistent connections, and full CRUD file operations.

---

## Features

- **Multi-threaded** request handling via a fixed thread pool
- **TLS/HTTPS** support using Java `SSLContext` and PKCS12 keystore
- **Persistent connections** (HTTP keep-alive) per HTTP/1.1 spec
- **Full CRUD file operations** — GET, POST, PUT, DELETE, PATCH
- **Custom HTTP parser** with request validation and error handling
- **Request routing** to appropriate handlers based on path
- **Structured logging** with SLF4J and Logback

---

## Tech Stack

- Java 17
- Maven
- SLF4J / Logback
- JUnit 5

---

## Setup & Running

### Prerequisites
- Java 17
- Maven
- A PKCS12 keystore (`keystore.p12`) — generate one with:
```bash
keytool -genkeypair -alias myserver -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365
```

### Environment Variables
Set the keystore password as a system environment variable:
```bash
# Windows
setx KEYSTORE_PASSWORD "your_password"

# Linux/Mac
export KEYSTORE_PASSWORD=your_password
```

### Run
```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.tamir.Main"
```

Server starts on port `4221` by default.

---

## Testing with curl

```bash
# Root endpoint
curl -k https://localhost:4221/

# Echo endpoint
curl -k https://localhost:4221/echo/hello

# File operations
curl -k -X POST https://localhost:4221/files/test.txt -d "hello world"
curl -k https://localhost:4221/files/test.txt
curl -k -X PUT https://localhost:4221/files/test.txt -d "updated content"
curl -k -X PATCH https://localhost:4221/files/test.txt -d " appended"
curl -k -X DELETE https://localhost:4221/files/test.txt

# Keep-alive (multiple requests on same connection)
curl -k --http1.1 https://localhost:4221/ https://localhost:4221/echo/test
```

The `-k` flag is required because the server uses a self-signed certificate.

---

## Architecture

An incoming request is accepted by `Main` and handed off to a `ConnectionHandler` running on a thread pool worker. The `ConnectionHandler` reads the raw request from the socket, passes it to `Parser` which validates and parses it into an `HttpRequest` object, then `Router` inspects the path and returns the appropriate `Handler` (root, echo, or file). The handler processes the request and returns an `HttpResponse`, which `ResponseBuilder` serializes into a raw HTTP response string and writes back to the client. The socket stays open for subsequent requests unless the client sends `Connection: close`.

```
Client → ConnectionHandler → Parser → Router → Handler → ResponseBuilder → Client
```

---

## Known Limitations

- Uses a self-signed certificate — browsers will show a security warning
- Working directory for file operations is hardcoded in `Main.java`
- Supports HTTP/1.1 only
- Thread pool is fixed at 2 threads