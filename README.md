# MailBreeze Java SDK

Official Java SDK for the [MailBreeze](https://mailbreeze.com) email platform.

## Installation

### Gradle (Kotlin DSL)

Add the GitHub Packages repository and dependency to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/mailbreeze/mailbreeze-java")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("com.mailbreeze:mailbreeze-java:0.1.0")
}
```

### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/mailbreeze/mailbreeze-java")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'com.mailbreeze:mailbreeze-java:0.1.0'
}
```

### Maven

Add to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/mailbreeze/mailbreeze-java</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.mailbreeze</groupId>
        <artifactId>mailbreeze-java</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

Configure authentication in `~/.m2/settings.xml`:

```xml
<servers>
    <server>
        <id>github</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_GITHUB_TOKEN</password>
    </server>
</servers>
```

## Quick Start

```java
import com.mailbreeze.MailBreeze;
import com.mailbreeze.models.*;

// Initialize the client
MailBreeze mailbreeze = MailBreeze.builder()
    .apiKey("sk_live_your_api_key")
    .build();

// Send an email
SendEmailResult result = mailbreeze.emails().send(
    SendEmailParams.builder()
        .from("hello@yourdomain.com")
        .to("user@example.com")
        .subject("Welcome!")
        .html("<h1>Hello World</h1>")
        .build()
);

System.out.println("Email sent: " + result.getEmailId());
```

## Features

### Emails

```java
// Send email
SendEmailResult result = mailbreeze.emails().send(
    SendEmailParams.builder()
        .from("hello@yourdomain.com")
        .to("user@example.com")
        .subject("Hello")
        .html("<p>Content</p>")
        .build()
);

// Send with idempotency key (prevents duplicate sends)
SendEmailResult result = mailbreeze.emails().send(params, "unique-key-123");

// List emails
PaginatedResponse<Email> emails = mailbreeze.emails().list();

// Get email by ID
Email email = mailbreeze.emails().get("email_abc123");

// Get email stats
EmailStats stats = mailbreeze.emails().stats();
```

### Contact Lists

```java
// Create a list
ContactList list = mailbreeze.lists().create(
    CreateListParams.builder()
        .name("Newsletter Subscribers")
        .description("Main newsletter list")
        .build()
);

// List all lists
PaginatedResponse<ContactList> lists = mailbreeze.lists().list();

// Update a list
ContactList updated = mailbreeze.lists().update("list_123",
    UpdateListParams.builder()
        .name("Updated Name")
        .build()
);

// Delete a list
mailbreeze.lists().delete("list_123");
```

### Contacts

```java
// Create a contact in a list
Contact contact = mailbreeze.contacts("list_123").create(
    CreateContactParams.builder()
        .email("user@example.com")
        .firstName("John")
        .lastName("Doe")
        .customField("company", "Acme Inc")
        .build()
);

// List contacts
PaginatedResponse<Contact> contacts = mailbreeze.contacts("list_123").list();

// Update a contact
Contact updated = mailbreeze.contacts("list_123").update("contact_abc",
    UpdateContactParams.builder()
        .firstName("Jane")
        .build()
);

// Suppress a contact
mailbreeze.contacts("list_123").suppress("contact_abc", SuppressReason.UNSUBSCRIBED);
```

### Email Verification

```java
// Verify single email
VerifyEmailResult result = mailbreeze.verification().verify("test@example.com");

// Batch verification
BatchVerifyResult batch = mailbreeze.verification().batch(
    BatchVerifyParams.builder()
        .email("user1@example.com")
        .email("user2@example.com")
        .build()
);

// Get verification stats
VerificationStats stats = mailbreeze.verification().stats();
```

### Attachments

```java
// Step 1: Create upload URL
CreateAttachmentUploadResult upload = mailbreeze.attachments().createUpload(
    CreateAttachmentUploadParams.builder()
        .fileName("document.pdf")
        .contentType("application/pdf")
        .fileSize(1024L)
        .build()
);

// Step 2: Upload file to the provided URL (use your HTTP client)
// PUT upload.getUploadUrl() with file content

// Step 3: Confirm upload
Attachment attachment = mailbreeze.attachments().confirm(
    ConfirmAttachmentParams.builder()
        .attachmentId(upload.getAttachmentId())
        .uploadToken(upload.getUploadToken())
        .build()
);
```

## Error Handling

```java
import com.mailbreeze.exceptions.*;

try {
    mailbreeze.emails().send(params);
} catch (ValidationException e) {
    // Invalid parameters (400)
    System.err.println("Validation error: " + e.getMessage());
    System.err.println("Details: " + e.getDetails());
} catch (AuthenticationException e) {
    // Invalid API key (401)
    System.err.println("Auth error: " + e.getMessage());
} catch (NotFoundException e) {
    // Resource not found (404)
    System.err.println("Not found: " + e.getMessage());
} catch (RateLimitException e) {
    // Rate limited (429)
    System.err.println("Rate limited. Retry after: " + e.getRetryAfter() + "s");
} catch (ServerException e) {
    // Server error (5xx)
    System.err.println("Server error: " + e.getMessage());
} catch (MailBreezeException e) {
    // Generic error
    System.err.println("Error: " + e.getMessage());
}
```

## Configuration

```java
MailBreeze mailbreeze = MailBreeze.builder()
    .apiKey("sk_live_...")
    .baseUrl("https://api.mailbreeze.com")  // Optional: custom base URL
    .timeout(Duration.ofSeconds(30))         // Optional: request timeout
    .maxRetries(3)                           // Optional: retry count
    .build();
```

## Requirements

- Java 17 or higher
- OkHttp 4.x (included as dependency)
- Jackson 2.x (included as dependency)

## License

MIT License - see [LICENSE](LICENSE) for details.
